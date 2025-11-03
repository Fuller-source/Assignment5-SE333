package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @DisplayName("Specification-Testing")

    @Test
    void calculateReturnsZeroWhenCartIsNull() {
        ShoppingCart cart = null;
        List<PriceRule> ruleList = new ArrayList<>();
        Amazon amazon = new Amazon(cart, ruleList);
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    void calculateReturnsZeroWhenCartIsEmpty() {
        ShoppingCart cart = mock(ShoppingCart.class);
        List<Item>  itemsList = new ArrayList<>();
        List<PriceRule> ruleList = new ArrayList<>();
        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);
        PriceRule rule3 = mock(PriceRule.class);
        ruleList.add(rule1);
        ruleList.add(rule2);
        ruleList.add(rule3);
        when(cart.getItems()).thenReturn(itemsList);
        when(rule1.priceToAggregate(itemsList)).thenReturn(0.0);
        when(rule2.priceToAggregate(itemsList)).thenReturn(0.0);
        when(rule3.priceToAggregate(itemsList)).thenReturn(0.0);
        Amazon amazon = new Amazon(cart, ruleList);
        assertEquals(0, amazon.calculate());
    }

    @Test
    void calculateReturnsPriceWhenCartHasItem() {
        ShoppingCart cart = mock(ShoppingCart.class);
        List<Item> itemsList = new ArrayList<>();
        Item item1 = mock(Item.class);
        itemsList.add(item1);
        List<PriceRule> ruleList = new ArrayList<>();
        PriceRule deliveryPrice = mock(PriceRule.class);
        PriceRule extraCostForElectronics = mock(PriceRule.class);
        PriceRule regularCost = mock(PriceRule.class);
        ruleList.add(deliveryPrice);
        ruleList.add(extraCostForElectronics);
        ruleList.add(regularCost);
        when(cart.getItems()).thenReturn(itemsList);
        when(deliveryPrice.priceToAggregate(itemsList)).thenReturn(5.0);
        when(extraCostForElectronics.priceToAggregate(itemsList)).thenReturn(0.0);
        when(regularCost.priceToAggregate(itemsList)).thenReturn(10.0);
        Amazon amazon = new Amazon(cart, ruleList);
        amazon.addToCart(item1);
        assertEquals(15.0, amazon.calculate());
    }

    @Test
    void addToCart() {
        ShoppingCart cart = mock(ShoppingCart.class);
        Item item1 = mock(Item.class);
        Amazon amazon = new Amazon(cart, new ArrayList<>());
        amazon.addToCart(item1);
        verify(cart).add(item1);
    }

    @DisplayName("Structural Based")
    // My Spec based testing merged with struct based, since there is only ever one method in the other classes.
    @Test
    void deliveryPriceZeroItems() {
        List<Item> itemsList = new ArrayList<>();
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        assertEquals(0, deliveryPrice.priceToAggregate(itemsList));
    }

    @Test
    void deliveryPriceOneItem() {
        List<Item> itemsList = new ArrayList<>();
        Item item1 = mock(Item.class);
        itemsList.add(item1);
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        assertEquals(5.0, deliveryPrice.priceToAggregate(itemsList));
    }

    @Test
    void deliveryPriceFiveItems() {
        List<Item> itemsList = mock(List.class);
        when(itemsList.size()).thenReturn(5);
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        assertEquals(12.5, deliveryPrice.priceToAggregate(itemsList));
    }

    @Test
    void deliveryPriceElevenItems() {
        List<Item> itemsList = mock(List.class);
        when(itemsList.size()).thenReturn(11);
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        assertEquals(20, deliveryPrice.priceToAggregate(itemsList));
    }

    @Test
    void extraCostElectricItems() {
        List<Item> itemsList = new ArrayList<>();
        Item item1 = mock(Item.class);
        when(item1.getType()).thenReturn(ItemType.ELECTRONIC);
        itemsList.add(item1);
        ExtraCostForElectronics extraCostForElectronics = new ExtraCostForElectronics();
        assertEquals(7.50, extraCostForElectronics.priceToAggregate(itemsList));
    }

    @Test
    void extraCostNoElectricItems() {
        List<Item> itemsList = new ArrayList<>();
        Item item1 = mock(Item.class);
        when(item1.getType()).thenReturn(ItemType.OTHER);
        itemsList.add(item1);
        ExtraCostForElectronics extraCostForElectronics = new ExtraCostForElectronics();
        assertEquals(0, extraCostForElectronics.priceToAggregate(itemsList));
    }

    @Test
    void regularCostZeroItems() {
        List<Item> itemsList = new ArrayList<>();
        RegularCost regularCost = new RegularCost();
        assertEquals(0, regularCost.priceToAggregate(itemsList));
    }

    @Test
    void regularCostOneItem() {
        List<Item> itemsList = new ArrayList<>();
        Item item1 = mock(Item.class);
        itemsList.add(item1);
        RegularCost regularCost = new RegularCost();
        when(item1.getPricePerUnit()).thenReturn(1.0);
        when(item1.getQuantity()).thenReturn(10);
        assertEquals(10.0, regularCost.priceToAggregate(itemsList));
    }

    @Test
    void databaseConstruct(){
        Database database = new Database();
        assertNotNull(database);
    }

    @Test
    void databaseConstruct2(){
        Database database = new Database();
        database = new Database();
        assertNotNull(database);
    }

    @Test
    void databaseConnection(){
        Database database = new Database();
        assertNotNull(database.getConnection());
    }

    @Test
    void databaseWrongSQL(){
        Database database = new Database();
        assertThrows(NullPointerException.class, () -> database.resetDatabase());
    }

    @Test
    void databaseClose(){
        Database database = new Database();
        database.close();
        assertThrows(RuntimeException.class, () -> database.resetDatabase());
    }

    @Test
    void databaseClose2(){
        Database database = null;
        database.close();
        database.close();
        assertEquals(null, database.getConnection());
    }

    @Test
    void itemCreation(){
        ItemType itemType = mock(ItemType.class);
        Item item = new Item(itemType, "name", 1, 1);
        assertEquals(item.getType(), itemType);
        assertEquals("name", item.getName());
        assertEquals(1, item.getQuantity());
        assertEquals(1.0, item.getPricePerUnit());
    }

    @Test
    void shoppingCartCreation(){
        Database database = mock(Database.class);
        ShoppingCartAdaptor shoppingCartAdaptor = new ShoppingCartAdaptor(database);
        assertNotNull(shoppingCartAdaptor);
    }

    @Test //
    void shoppingCartAddItem_DelegatesCorrectly() throws SQLException {
        Database database = mock(Database.class);
        Item item = mock(Item.class);
        ItemType itemType = mock(ItemType.class);

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(item.getName()).thenReturn("TestItem");
        when(item.getQuantity()).thenReturn(1);
        when(item.getPricePerUnit()).thenReturn(10.0);
        when(item.getType()).thenReturn(itemType);

        when(database.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        when(database.withSql(any(Database.SqlSupplier.class)))
                .thenAnswer((InvocationOnMock invocation) -> {
                    Database.SqlSupplier supplier = invocation.getArgument(0, Database.SqlSupplier.class);
                    return supplier.doSql();
                });

        ShoppingCartAdaptor shoppingCartAdaptor = new ShoppingCartAdaptor(database);

        shoppingCartAdaptor.add(item);

        verify(item).getName();
        verify(item).getQuantity();
        verify(item).getPricePerUnit();
        verify(item).getType();
        verify(database).withSql(any());
        verify(database, times(2)).getConnection();
        verify(mockPreparedStatement).setString(1, "TestItem");
        verify(mockPreparedStatement).setInt(3, 1);
        verify(mockPreparedStatement).setDouble(4, 10.0);
        verify(mockPreparedStatement).execute();
        verify(mockConnection).commit();
    }
}
