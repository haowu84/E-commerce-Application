package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.OrderCompleteRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.ItemDetailResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderPaymentResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.core.base.ResultResponse;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@RestController
public class OrderController
{
    private final BillingRepo repo;

    @Autowired
    public OrderController(BillingRepo repo)
    {
        this.repo = repo;
    }

    @GetMapping("/order/payment")
    public ResponseEntity<OrderPaymentResponse> orderPayment(@AuthenticationPrincipal SignedJWT user)
    {
        Long userId = getUserId(user);

        List<Item> items = repo.selectCart(userId);
        if (items.isEmpty())
        {
            OrderPaymentResponse response = new OrderPaymentResponse()
                    .setResult(BillingResults.CART_EMPTY);

            return response.toResponse();
        }

        StringBuilder description = new StringBuilder();

        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items)
        {
            int discount = applyDiscount(user) ? repo.selectPremiumDiscount(item.getMovieId()) : 0;
            BigDecimal discounted = item.getUnitPrice().multiply(BigDecimal.valueOf(1 - (discount / 100.0)))
                    .setScale(2, RoundingMode.DOWN);
            item.setUnitPrice(discounted);
            total = total.add(discounted.multiply(BigDecimal.valueOf(item.getQuantity())));

            description.append(item.getMovieTitle()).append(", ");
        }

        description.delete(description.length() - 2, description.length());

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD")
                        .setDescription(description.toString())
                        .setAmount(total.movePointRight(2).longValue())
                        .putMetadata("userId", Long.toString(userId))
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        try
        {
            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

            OrderPaymentResponse response = new OrderPaymentResponse()
                    .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED)
                    .setPaymentIntentId(paymentIntent.getId())
                    .setClientSecret(paymentIntent.getClientSecret());

            return response.toResponse();
        }
        catch (StripeException e)
        {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }
    }

    @PostMapping("/order/complete")
    public ResponseEntity<ResultResponse> orderComplete(@AuthenticationPrincipal SignedJWT user, @RequestBody OrderCompleteRequest request)
    {
        Long userId = getUserId(user);

        try
        {
            PaymentIntent retrievedPaymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());
            if (!retrievedPaymentIntent.getStatus().equals("succeeded"))
            {
                throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
            }

            if (!retrievedPaymentIntent.getMetadata().get("userId").equals(Long.toString(userId)))
            {
                throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
            }

            repo.insertSale(userId, BigDecimal.valueOf(retrievedPaymentIntent.getAmount()).movePointLeft(2), Instant.now());
            repo.insertAllSaleItem(repo.selectLastSale(), repo.selectCart(userId));
            repo.clearCart(userId);

            return ResultResponse.response(BillingResults.ORDER_COMPLETED);
        }
        catch (StripeException e)
        {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }
    }

    @GetMapping("/order/list")
    public ResponseEntity<OrderListResponse> orderList(@AuthenticationPrincipal SignedJWT user)
    {
        List<Sale> sales = repo.selectSaleByUserId(getUserId(user));
        if (sales.isEmpty())
        {
            OrderListResponse response = new OrderListResponse()
                    .setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND);

            return response.toResponse();
        }

        OrderListResponse response = new OrderListResponse()
                .setResult(BillingResults.ORDER_LIST_FOUND_SALES)
                .setSales(sales);

        return response.toResponse();
    }

    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<ItemDetailResponse> orderDetail(@AuthenticationPrincipal SignedJWT user, @PathVariable Long saleId)
    {
        List<Item> items = repo.selectSaleItem(getUserId(user), saleId);
        if (items.isEmpty())
        {
            ItemDetailResponse response = new ItemDetailResponse()
                    .setResult(BillingResults.ORDER_DETAIL_NOT_FOUND);

            return response.toResponse();
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items)
        {
            int discount = applyDiscount(user) ? repo.selectPremiumDiscount(item.getMovieId()) : 0;
            BigDecimal discounted = item.getUnitPrice().multiply(BigDecimal.valueOf(1 - (discount / 100.0)))
                    .setScale(2, RoundingMode.DOWN);
            item.setUnitPrice(discounted);
            total = total.add(discounted.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        ItemDetailResponse response = new ItemDetailResponse()
                .setResult(BillingResults.ORDER_DETAIL_FOUND)
                .setItems(items)
                .setTotal(total);

        return response.toResponse();
    }


    private Long getUserId(@AuthenticationPrincipal SignedJWT user)
    {
        try
        {
            return user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        }
        catch (ParseException ignored)
        {
        }
        return null;
    }

    private boolean applyDiscount(@AuthenticationPrincipal SignedJWT user)
    {
        try
        {
            for (String role: user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES))
            {
                if (role.equalsIgnoreCase("Premium"))
                {
                    return true;
                }
            }
        }
        catch (ParseException ignored)
        {
        }
        return false;
    }

}
