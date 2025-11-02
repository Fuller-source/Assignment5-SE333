package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BarnesAndNobleTest {

    /**
     *     public PurchaseSummary getPriceForCart(Map<String, Integer> order) {
     *         if(order==null)
     *             return null;
     *
     *         PurchaseSummary purchaseSummary = new PurchaseSummary();
     *         for (String ISBN : order.keySet())
     *             retrieveBook(ISBN, order.get(ISBN), purchaseSummary);
     *         return purchaseSummary;
     *     }
     *
     *     Partitioning:
     *          - order: null
     *          - order: empty
     *          - order: has items
     *
     *
     */
    @DisplayName("Specification-Based")

    @Test
    public void getPriceForCartReturnNullWhenOrderIsNull() {
        Map<String, Integer> order = null;
        BookDatabase bookDatabase = mock(BookDatabase.class);
        BuyBookProcess process = mock(BuyBookProcess.class);

        BarnesAndNoble barnesAndNoble = new BarnesAndNoble(bookDatabase, process);

        PurchaseSummary purchaseSummary = barnesAndNoble.getPriceForCart(order);

        assertNull(purchaseSummary);
    }

    @Test
    public void getPriceForCartReturnEmptyObjectWhenOrderIsEmpty() {
        Map<String, Integer> order = new HashMap<>();
        BookDatabase bookDatabase = mock(BookDatabase.class);
        BuyBookProcess process = mock(BuyBookProcess.class);

        BarnesAndNoble barnesAndNoble = new BarnesAndNoble(bookDatabase, process);

        PurchaseSummary purchaseSummary = barnesAndNoble.getPriceForCart(order);

        assertEquals(0, purchaseSummary.getTotalPrice());
        assertEquals(new HashMap<>(),  purchaseSummary.getUnavailable());
    }

    @Test
    public void getPriceForCartReturnPopulatedObjectWhenOrderIsPopulatedButNotEnough() {
        Map<String, Integer> order = new HashMap<>();
        order.put("123", 2);
        BookDatabase bookDatabase = mock(BookDatabase.class);
        BuyBookProcess process = mock(BuyBookProcess.class);
        Book book = new Book("123", 10, 1);

        when(bookDatabase.findByISBN("123")).thenReturn(book);

        BarnesAndNoble barnesAndNoble = new BarnesAndNoble(bookDatabase, process);
        PurchaseSummary purchaseSummary = barnesAndNoble.getPriceForCart(order);
        assertEquals(10, purchaseSummary.getTotalPrice());
        assertEquals(1, purchaseSummary.getUnavailable().get(book));
    }

    @DisplayName("Structural-Based")
    @Test
    public void getPriceForCartReturnPopulatedObjectWhenOrderIsPopulatedAndEnough() {
        Map<String, Integer> order = new HashMap<>();
        order.put("123", 1);
        BookDatabase bookDatabase = mock(BookDatabase.class);
        BuyBookProcess process = mock(BuyBookProcess.class);
        Book book = new Book("123", 10, 10);
        when(bookDatabase.findByISBN("123")).thenReturn(book);

        BarnesAndNoble barnesAndNoble = new BarnesAndNoble(bookDatabase, process);
        PurchaseSummary purchaseSummary = barnesAndNoble.getPriceForCart(order);
        assertEquals(10, purchaseSummary.getTotalPrice());
        assertNull(purchaseSummary.getUnavailable().get(book));
    }

    @Test
    public void equalsReturnsTrueForEqualObjects() {
        Book book = new Book("123", 10, 10);
        Book book2 = new Book("123", 10, 10);
        assertTrue(book.equals(book));
        assertTrue(book.equals(book2));
    }

    @Test
    public void equalsReturnsFalseForDifferentBooks() {
        Book book = new Book("123", 10, 10);
        Book book2 = new Book("124", 10, 10);
        assertNotEquals(book2, book);
    }

    @Test
    public void equalsReturnsFalseForDifferentObjects() {
        Book book = new Book("123", 10, 10);
        Integer i = 123;
        assertFalse(book.equals(i));
    }

    @Test
    public void equalsReturnsFalseForNullBook() {
        Book book = new Book("123", 10, 10);
        Book book2 = null;
        assertFalse(book.equals(book2));
    }
}
