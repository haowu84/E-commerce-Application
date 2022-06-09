package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

public class ItemDetailResponse extends ResponseModel<ItemDetailResponse> {
    private BigDecimal total;
    private List<Item> items;

    public BigDecimal getTotal() {
        return total;
    }

    public ItemDetailResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public ItemDetailResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
