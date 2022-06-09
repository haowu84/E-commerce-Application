package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.request.CartInsertAndUpdateRequest;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template) {this.template = template;}

    public void insertCart(CartInsertAndUpdateRequest request, Long user_id)
    {
        try
        {
            template.update(
                    "INSERT INTO billing.cart (user_id, movie_id, quantity) " +
                            "Values (:user_id, :movie_id, :quantity);",
                    new MapSqlParameterSource()
                            .addValue("user_id", user_id, Types.INTEGER)
                            .addValue("movie_id", request.getMovieId(), Types.INTEGER)
                            .addValue("quantity", request.getQuantity(), Types.INTEGER)
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }
    }

    public void updateCart(CartInsertAndUpdateRequest request, Long userId)
    {
        int rowsUpdated = template.update(
                "UPDATE billing.cart " +
                        "SET quantity = :quantity " +
                        "WHERE user_id = :user_id AND movie_id = :movie_id;",
                new MapSqlParameterSource()
                        .addValue("quantity", request.getQuantity(), Types.INTEGER)
                        .addValue("user_id", userId, Types.INTEGER)
                        .addValue("movie_id", request.getMovieId(), Types.INTEGER)
        );

        if (rowsUpdated == 0)
        {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void deleteCart(Long userId, Long movieId)
    {
        int rowsUpdated = template.update(
                "DELETE FROM billing.cart " +
                        "WHERE user_id = :user_id AND movie_id = :movie_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER)
                        .addValue("movie_id", movieId, Types.INTEGER)
        );

        if (rowsUpdated == 0)
        {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void clearCart(Long userId)
    {
        int rowsUpdated = template.update(
                "DELETE FROM billing.cart " +
                        "WHERE user_id = :user_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER)
        );

        if (rowsUpdated == 0)
        {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
    }

    public List<Item> selectCart(Long userId)
    {
        return template.query(
                "SELECT mp.unit_price AS unitPrice, c.quantity AS quantity, m.id AS movieId, title as movieTitle, backdrop_path AS backdropPath, poster_path AS posterPath " +
                        "FROM movies.movie m " +
                        "JOIN billing.movie_price mp " +
                        "ON m.id = mp.movie_id " +
                        "JOIN billing.cart c " +
                        "ON m.id = c.movie_id " +
                        "WHERE c.user_id = :user_id;",
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER),
                (rs, rowNum) -> new Item()
                        .setUnitPrice(rs.getBigDecimal("unitPrice").setScale(2, RoundingMode.DOWN))
                        .setQuantity(rs.getInt("quantity"))
                        .setMovieId(rs.getLong("movieId"))
                        .setMovieTitle(rs.getString("movieTitle"))
                        .setBackdropPath(rs.getString("backdropPath"))
                        .setPosterPath(rs.getString("posterPath"))
        );
    }

    public Integer selectPremiumDiscount(Long movieId)
    {
        return template.queryForObject(
                "SELECT premium_discount " +
                        "FROM billing.movie_price " +
                        "WHERE movie_id = :movie_id;",
                new MapSqlParameterSource()
                        .addValue("movie_id", movieId, Types.INTEGER),
                (rs, rowNum)
                        -> rs.getInt("premium_discount")
        );
    }

    public void insertSale(Long userId, BigDecimal total, Instant order_date)
    {
        template.update(
                "INSERT INTO billing.sale (user_id, total, order_date) " +
                        "Values (:user_id, :total, :order_date);",
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER)
                        .addValue("total", total, Types.DECIMAL)
                        .addValue("order_date", Timestamp.from(order_date), Types.TIMESTAMP)
        );
    }

    public Integer selectLastSale()
    {
        return template.queryForObject(
                "SELECT id " +
                        "FROM billing.sale " +
                        "ORDER BY order_date DESC " +
                        "LIMIT 1;",
                new MapSqlParameterSource(),
                (rs, rowNum)
                        -> rs.getInt("id")
        );
    }

    public void insertAllSaleItem(int saleId, List<Item> items)
    {
        MapSqlParameterSource[] sources = items
                .stream()
                .map(
                        item ->
                                new MapSqlParameterSource()
                                        .addValue("sale_id", saleId, Types.INTEGER)
                                        .addValue("movie_id", item.getMovieId(), Types.INTEGER)
                                        .addValue("quantity", item.getQuantity(), Types.INTEGER)
                )
                .toArray(MapSqlParameterSource[]::new);

        template.batchUpdate(
                "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                        "Values (:sale_id, :movie_id, :quantity);",
                sources
        );
    }

    public List<Sale> selectSaleByUserId(Long userId)
    {
        return template.query(
                "SELECT id, total, order_date " +
                        "FROM billing.sale " +
                        "WHERE user_id = :user_id " +
                        "ORDER BY id DESC " +
                        "LIMIT 5;",
                new MapSqlParameterSource()
                        .addValue("user_id", userId, Types.INTEGER),
                (rs, rowNum) -> new Sale()
                        .setSaleId(rs.getLong("id"))
                        .setTotal(rs.getBigDecimal("total").setScale(2, RoundingMode.DOWN))
                        .setOrderDate(rs.getTimestamp("order_date").toInstant())
        );
    }

    public List<Item> selectSaleItem(Long userId, Long saleId)
    {
        return template.query(
                "SELECT mp.unit_price AS unitPrice, si.quantity AS quantity, m.id AS movieId, title as movieTitle, backdrop_path AS backdropPath, poster_path AS posterPath " +
                        "FROM billing.sale s " +
                        "JOIN billing.sale_item si " +
                        "ON s.id = si.sale_id " +
                        "JOIN billing.movie_price mp " +
                        "ON si.movie_id = mp.movie_id " +
                        "JOIN movies.movie m " +
                        "ON si.movie_id = m.id " +
                        "WHERE s.id = :sale_id AND s.user_id = :user_id;",
                new MapSqlParameterSource()
                        .addValue("sale_id", saleId, Types.INTEGER)
                        .addValue("user_id", userId, Types.INTEGER),
                (rs, rowNum) -> new Item()
                        .setUnitPrice(rs.getBigDecimal("unitPrice").setScale(2, RoundingMode.DOWN))
                        .setQuantity(rs.getInt("quantity"))
                        .setMovieId(rs.getLong("movieId"))
                        .setMovieTitle(rs.getString("movieTitle"))
                        .setBackdropPath(rs.getString("backdropPath"))
                        .setPosterPath(rs.getString("posterPath"))
        );
    }

}
