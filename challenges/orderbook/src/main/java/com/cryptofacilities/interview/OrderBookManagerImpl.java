package com.cryptofacilities.interview;

import java.util.*;
import java.util.stream.Collectors;
import static java.lang.Math.toIntExact;

/**
 * This implementation will assume no parallel use-case.
 */

/**
 * Created by CF-8 on 6/27/2017.
 */
public class OrderBookManagerImpl implements OrderBookManager {

    private static final int NO_PRICES = -1;
    private static final int NO_ORDERS = -1;

    /**
        1. EnumMap: very efficient for enum keys
        2. HashMap: as efficient as possible, (String) instrument -> price/level
        3. HashMap: price/level -> (OrderList) orders # order lists can store asset quantity
            A list was chosen instead of a Queue due to the requirement of modifying an item's position in the "Queue"
            Also, it has O(1) complexity for adding items
     **/
    private EnumMap<Side, // Buy or Sell
                    HashMap<String, // Instrument code
                            HashMap<Long, // Price
                                    OrderList<Order> // Orders
                            >>> orderBook;

    /**
      Keep track of every order object by ID, so we don't need to incur O(n) remove or linear O find-and-modify via Id
     **/
    private HashMap<String, Order> orderById;

    public OrderBookManagerImpl() {
        this.orderBook = new EnumMap<>(Side.class);
        this.orderById = new HashMap<>();

        // Populate enum map
        this.orderBook.put(Side.buy, new HashMap<>());
        this.orderBook.put(Side.sell, new HashMap<>());
    }

    /**
     * It costs a little bit more to write, but it really pays off in the long run for readers
     * @param order new order to add <br/>
     *
     */
    public void addOrder(Order order) {
        if (this.orderById.containsKey(order.getOrderId())) {
            throw new IllegalArgumentException("Order already processed");
        }
        this.orderById.put(order.getOrderId(), order);

        HashMap<String, HashMap<Long, OrderList<Order>>> sideBook = this.orderBook.get(order.getSide());

        if (!sideBook.containsKey(order.getInstrument())) {
            System.out.println("New instrument:");
            System.out.println(order.getInstrument());
            sideBook.put(order.getInstrument(), new HashMap<>());
        }

        HashMap<Long, OrderList<Order>> instrumentBook = sideBook.get(order.getInstrument());

        if (!instrumentBook.containsKey(order.getPrice())) {
            System.out.println("New price:");
            System.out.println(order.getPrice());
            instrumentBook.put(order.getPrice(), new OrderList<>());
        }

        OrderList<Order> orderList = instrumentBook.get(order.getPrice());

        // head of list are latest orders submitted, while tail (index 0) is the first one
        orderList.addOrder(order);
        System.out.println("Added:");
        System.out.println(order);
    }

    public void modifyOrder(String orderId, long newQuantity) {
        if (!this.orderById.containsKey(orderId)) {
            throw new IllegalArgumentException("Order does not exist");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("newQuantity must be a positive and absolute value");
        }

        Order modifyOrder = this.orderById.get(orderId);
        OrderList<Order> orderList = this.orderBook
                .get(modifyOrder.getSide())
                .get(modifyOrder.getInstrument())
                .get(modifyOrder.getPrice());

        // We deliberately ignore use-case of newQuantity == current quantity, as position in order list does not change
        if (modifyOrder.getQuantity() > newQuantity) {
            // Move order to the last position in order list - we only iterate because quantity required so
            ListIterator<Order> iter = orderList.listIterator();
            while (iter.hasNext()) {
                Order iterOrder = iter.next();

                // Take the opportunity to remove deleted items if found any
                if (iterOrder.getIsDeleted()) {
                    iter.remove();
                    System.out.println("Item deleted:");
                    System.out.println(iterOrder);
                    continue;
                }

                // reorder as latest order added for given price
                if (iterOrder.getOrderId().equals(orderId)) {
                    iter.remove();
                    System.out.println("Item reordered:");
                    System.out.println(iterOrder);
                    orderList.addOrder(iterOrder);
                    break;
                }
            }
        }

        Integer quantityDelta = toIntExact(newQuantity) - toIntExact(modifyOrder.getQuantity());
        System.out.println("Modify quantity delta:");
        System.out.println(quantityDelta);

        modifyOrder.setQuantity(newQuantity);
        orderList.applyAssetQuantityDelta(quantityDelta);
    }

    /**
     * Array Iterator will then have to consider if Order is deleted and dereference it the next time it iterates
     * @param orderId unique identifier of existing order
     */
    public void deleteOrder(String orderId) {
        Order removedOrder = this.orderById.remove(orderId);
        if (removedOrder == null) {
            throw new IllegalArgumentException("Order does not exist");
        }

        removedOrder.markAsDeleted();
        System.out.println("Removed:");
        System.out.println(orderId);

        OrderList<Order> orderList = this.orderBook
                .get(removedOrder.getSide())
                .get(removedOrder.getInstrument())
                .get(removedOrder.getPrice());

        orderList.applyAssetQuantityDelta(-toIntExact(removedOrder.getQuantity()));
    }

    /**
     * Recurse until found a valid price with orders
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @return highest if on buy side, lowest if on sell side
     */
    public long getBestPrice(String instrument, Side side) {
        HashMap<Long, OrderList<Order>> priceMap = this.orderBook
                .getOrDefault(side, new HashMap<>())
                .getOrDefault(instrument, null);
        if (priceMap == null) {
            return NO_ORDERS;
        }

        Set<Long> prices = priceMap.keySet();

        if (prices.size() == 0) {
            System.out.println("No more prices / orders for instrument / side");
            return NO_PRICES;
        }

        List<Long> sortedPrices = prices.stream().sorted().collect(
                Collectors.toCollection(ArrayList::new));

        Long lowest = sortedPrices.get(0);
        Long highest = sortedPrices.get(sortedPrices.size() - 1);

        Long bestPrice = side == Side.buy ? highest : lowest;

        // Make sure there is still at least 1 order not marked as deleted in the order list
        List<Order> orderList = priceMap.get(bestPrice);
        ListIterator<Order> iter = orderList.listIterator();
        while (iter.hasNext()) {
            Order iterOrder = iter.next();

            // Take the opportunity to remove deleted items if found any
            if (iterOrder.getIsDeleted()) {
                iter.remove();
                System.out.println("Item deleted:");
                System.out.println(iterOrder);
            } else {
                // Found an order that is not deleted, enough to claim it to be the best price
                System.out.println("Found valid order:");
                System.out.println(iterOrder);
                break;
            }
        }

        // check list still has elements, otherwise remove price and recurse until found best price
        if (orderList.size() == 0) {
            System.out.println("Removed price as no more valid orders:");
            System.out.println(priceMap.remove(bestPrice));

            bestPrice = getBestPrice(instrument, side);
        }

        return bestPrice;
    }

    /**
     * An optimisation could be returning an approximate number, if considering the deleted ones.
     * Or even a precise one, if storing the number of deleted items in OrderList.
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @param price requested price level
     * @return
     */
    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        List orders = getOrdersAtLevel(instrument, side, price);
        long totalOrders = orders.size();
        return totalOrders == 0 ? NO_ORDERS : totalOrders;
    }

    /**
     * This function does not need to consider removed items
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @param price requested price level
     * @return
     */
    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
        OrderList<Order> orderList = getOrderList(instrument, side, price);
        return orderList.getAssetQuantity() == 0 ? NO_ORDERS : orderList.getAssetQuantity();
    }

    /**
     * This function does not need to consider removed items
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @param price requested price level
     * @return
     */
    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        OrderList<Order> orderList = getOrderList(instrument, side, price);
        if (orderList.getAssetQuantity() == 0) {
            return NO_ORDERS;
        } else {
            return orderList.getAssetQuantity() * price;
        }
    }

    /**
     * Pay the price of removing items marked for delete.
     * An optimisation could be returning a Stream that can be consumed on-the-fly for non-deleted items.
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @param price requested price level
     * @return
     */
    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        OrderList<Order> orderList = getOrderList(instrument, side, price);

        // Must now remove items marked as deleted
        ListIterator<Order> iter = orderList.listIterator();
        while (iter.hasNext()) {
            Order iterOrder = iter.next();

            // remove deleted item
            if (iterOrder.getIsDeleted()) {
                iter.remove();
                System.out.println("Item deleted:");
                System.out.println(iterOrder);
            }
        }

        return orderList;
    }

    private OrderList<Order> getOrderList(String instrument, Side side, long price) {
        return this.orderBook
                .getOrDefault(side, new HashMap<>())
                .getOrDefault(instrument, new HashMap<>())
                .getOrDefault(price, new OrderList<>());
    }
}
