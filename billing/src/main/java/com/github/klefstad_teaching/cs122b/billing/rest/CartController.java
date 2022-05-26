package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.billing.model.request.CartInsertAndUpdateRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.ItemDetailResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.base.ResultResponse;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<ResultResponse> cartInsert(@AuthenticationPrincipal SignedJWT user, @RequestBody CartInsertAndUpdateRequest request)
    {
        validate.validateQuantity(request.getQuantity());

        repo.insertCart(request, getUserId(user));

        return ResultResponse.response(BillingResults.CART_ITEM_INSERTED);
    }

    @PostMapping("/cart/update")
    public ResponseEntity<ResultResponse> cartUpdate(@AuthenticationPrincipal SignedJWT user, @RequestBody CartInsertAndUpdateRequest request)
    {
        validate.validateQuantity(request.getQuantity());

        repo.updateCart(request, getUserId(user));

        return ResultResponse.response(BillingResults.CART_ITEM_UPDATED);
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<ResultResponse> cartDelete(@AuthenticationPrincipal SignedJWT user, @PathVariable Long movieId)
    {
        repo.deleteCart(getUserId(user), movieId);

        return ResultResponse.response(BillingResults.CART_ITEM_DELETED);
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<ResultResponse> cartClear(@AuthenticationPrincipal SignedJWT user)
    {
        repo.clearCart(getUserId(user));

        return ResultResponse.response(BillingResults.CART_CLEARED);
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<ItemDetailResponse> cartRetrieve(@AuthenticationPrincipal SignedJWT user)
    {
        List<Item> items = repo.selectCart(getUserId(user));
        if (items.isEmpty())
        {
            ItemDetailResponse response = new ItemDetailResponse()
                    .setResult(BillingResults.CART_EMPTY);

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
                .setResult(BillingResults.CART_RETRIEVED)
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
