package com.cryptofacilities.interview;

import java.util.ArrayList;

public class OrderList<T extends Order> extends ArrayList<T> {
    private long assetQuantity = 0;

    public boolean addOrder(Order order) {
        boolean added = super.add((T) order);

        if (added) {
            this.assetQuantity += order.getQuantity();
        }

        return added;
    }

    public long getAssetQuantity() {
        return assetQuantity;
    }

    public void applyAssetQuantityDelta(Integer delta) {
        if (this.assetQuantity + delta < 0) {
            throw new IllegalArgumentException("Invalid delta");
        }
        this.assetQuantity += delta;
    }
}
