package com.cryptofacilities.interview;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OrderBookManagerImplTest {
    public static final String VOD_L = "VOD.L";
    public static final String VOD_X = "VOD.X";
    OrderBookManager underTest;


    @Before
    public void setUp() throws Exception {
        // reset order book
        underTest = new OrderBookManagerImpl();
    }

    @Test
    public void addOrder() throws Exception {
        // create orders
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );
        Order buy2 = new Order("order2", VOD_L, Side.buy, 200, 10 );

        // send orders
        underTest.addOrder( buy1 );
        underTest.addOrder( buy2 );

        // check that second order is at head
        List orders = underTest.getOrdersAtLevel(VOD_L, Side.buy, 200 );
        assertEquals( "Orders are in order", buy1, orders.get(0));
        assertEquals( "Orders are in order", buy2, orders.get(1));
    }

    @Test
    public void modifyOrderKeepPosition() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );
        Order buy2 = new Order("order2", VOD_L, Side.buy, 200, 10 );
        Order buy3 = new Order("order3", VOD_L, Side.buy, 200, 11 );

        // send
        underTest.addOrder( buy1 );
        underTest.addOrder( buy2 );
        underTest.addOrder( buy3 );

        // modify
        underTest.modifyOrder( buy1.getOrderId(), 15 );

        // check that order quantity is correct
        List<Order> orders = underTest.getOrdersAtLevel(VOD_L, Side.buy, 200 );
        assertEquals( "Order has correct quantity", 15, orders.get(0).getQuantity());
        assertEquals( "Order has correct id", "order1", orders.get(0).getOrderId());
    }

    @Test
    public void modifyOrderResetPosition() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );
        Order buy2 = new Order("order2", VOD_L, Side.buy, 200, 10 );
        Order buy3 = new Order("order3", VOD_L, Side.buy, 200, 11 );

        // send
        underTest.addOrder( buy1 );
        underTest.addOrder( buy2 );
        underTest.addOrder( buy3 );

        // modify
        underTest.modifyOrder( buy1.getOrderId(), 5 );

        // check that order quantity is correct
        List<Order> orders = underTest.getOrdersAtLevel(VOD_L, Side.buy, 200 );
        assertEquals( "Order has correct quantity", 10, orders.get(0).getQuantity());
        assertEquals( "Order has correct id", "order2", orders.get(0).getOrderId());
    }

    @Test
    public void modifyOrderNonExistent() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );

        // send
        underTest.addOrder( buy1 );

        // modify
        try {
            underTest.modifyOrder( "order5", 5 );
            fail("Should have thrown");
        } catch(IllegalArgumentException iae ) {
            assertEquals("Order does not exist", iae.getMessage());
        }
    }

    @Test
    public void deleteOrder() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );

        // send
        underTest.addOrder( buy1 );

        // delete
        underTest.deleteOrder( buy1.getOrderId() );

        // check that order list is empty
        long orderNum = underTest.getOrderNumAtLevel(VOD_L, Side.buy, 200 );
        assertEquals( "Order list is empty", -1, orderNum);

        List<Order> orders = underTest.getOrdersAtLevel(VOD_L, Side.buy, 200 );
        assertEquals( "Order list is empty", 0, orders.size());
    }

    @Test
    public void deleteOrderNonExistent() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );

        // send
        underTest.addOrder( buy1 );

        // delete
        try {
            underTest.deleteOrder( "order5");
            fail("Should have thrown");
        } catch(IllegalArgumentException iae ) {
            assertEquals("Order does not exist", iae.getMessage());
        }
    }

    @Test
    public void getBestPriceBuy() throws Exception {
        // create
        Order buy1 = new Order("order1", VOD_L, Side.buy, 200, 10 );
        Order buy2 = new Order("order2", VOD_L, Side.buy, 400, 12 );
        Order buy3 = new Order("order3", VOD_L, Side.buy, 300, 11 );

        // send
        underTest.addOrder( buy1 );
        underTest.addOrder( buy2 );
        underTest.addOrder( buy3 );

        long bestPrice = underTest.getBestPrice(VOD_L, Side.buy);
        assertEquals( "Best buy price", 400, bestPrice);
    }

    @Test
    public void getBestPriceSell() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 400, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 300, 11 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );

        long bestPrice = underTest.getBestPrice(VOD_L, Side.sell);
        assertEquals( "Best sell price", 200, bestPrice);
    }


    @Test
    public void getBestPriceNone() throws Exception {
        long actual = underTest.getBestPrice(VOD_L, Side.buy);
        assertEquals( "Best price is -1", -1, actual);
    }

    @Test
    public void getOrderNumAtLevel() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 200, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 500, 11 );
        Order buy1 = new Order("order4", VOD_L, Side.buy, 200, 51 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );
        underTest.addOrder( buy1 );

        long orderNum = underTest.getOrderNumAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Order num", 2, orderNum);
    }

    @Test
    public void getOrderNumAtLevelNone() throws Exception {
        long orderNum = underTest.getOrderNumAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Order num", -1, orderNum);
    }

    @Test
    public void getTotalQuantityAtLevel() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 200, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 200, 11 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );

        long qtty = underTest.getTotalQuantityAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Quantity", 33, qtty);
    }

    @Test
    public void getTotalQuantityAtLevelMix() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 200, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 300, 11 );
        Order sell4 = new Order("order6", VOD_X, Side.sell, 200, 15 );
        Order buy1 = new Order("order4", VOD_L, Side.buy, 200, 10 );
        Order buy2 = new Order("order5", VOD_L, Side.buy, 400, 12 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );
        underTest.addOrder( sell4 );
        underTest.addOrder( buy1 );
        underTest.addOrder( buy2 );

        // modify
        underTest.modifyOrder(sell2.getOrderId(), 51);

        long qtty = underTest.getTotalQuantityAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Quantity", 61, qtty);
    }

    @Test
    public void getTotalQuantityAtLevelNone() throws Exception {
        long qtty = underTest.getTotalQuantityAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Quantity", -1, qtty);
    }

    @Test
    public void getTotalVolumeAtLevel() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 200, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 200, 11 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );

        long volume = underTest.getTotalVolumeAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Volume", 200*33, volume);
    }

    @Test
    public void getTotalVolumeAtLevelMix() throws Exception {
        // create
        Order sell1 = new Order("order1", VOD_L, Side.sell, 200, 10 );
        Order sell2 = new Order("order2", VOD_L, Side.sell, 200, 12 );
        Order sell3 = new Order("order3", VOD_L, Side.sell, 300, 11 );
        Order sell4 = new Order("order4", VOD_L, Side.sell, 200, 5 );
        Order buy1 = new Order("order5", VOD_L, Side.buy, 200, 14 );

        // send
        underTest.addOrder( sell1 );
        underTest.addOrder( sell2 );
        underTest.addOrder( sell3 );
        underTest.addOrder( sell4 );
        underTest.addOrder( buy1 );

        // delete
        underTest.deleteOrder(sell4.getOrderId());

        long volume = underTest.getTotalVolumeAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Volume", 200*22, volume);
    }

    @Test
    public void getTotalVolumeAtLevelNone() throws Exception {
        long volume = underTest.getTotalVolumeAtLevel(VOD_L, Side.sell, 200);
        assertEquals( "Volume", -1, volume);
    }

    @Test
    public void getOrdersAtLevelNoOrders() throws Exception {
        List orders = underTest.getOrdersAtLevel(VOD_L, Side.buy, 200 );
        assertEquals("Empty list", 0, orders.size());
    }

}