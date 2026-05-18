package com.javatraining.m02.streams;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M02-T2: Sales Report Pipeline — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SalesReportServiceTest {

    private SalesReportService service;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        service = new SalesReportService();
        transactions = List.of(
            new Transaction("T01", "C001", "LAPTOP",  "Electronics", 75000.0, Month.JANUARY),
            new Transaction("T02", "C002", "PHONE",   "Electronics", 25000.0, Month.JANUARY),
            new Transaction("T03", "C001", "CHAIR",   "Furniture",   8000.0,  Month.FEBRUARY),
            new Transaction("T04", "C003", "LAPTOP",  "Electronics", 72000.0, Month.FEBRUARY),
            new Transaction("T05", "C002", "DESK",    "Furniture",   15000.0, Month.MARCH),
            new Transaction("T06", "C003", "PHONE",   "Electronics", 24000.0, Month.MARCH),
            new Transaction("T07", "C001", "LAPTOP",  "Electronics", 80000.0, Month.MARCH),
            new Transaction("T08", "C004", "MUG",     "Accessories", 500.0,   Month.JANUARY),
            new Transaction("T09", "C004", "CABLE",   "Accessories", 300.0,   Month.FEBRUARY),
            new Transaction("T10", "C002", "LAPTOP",  "Electronics", 70000.0, Month.APRIL)
        );
    }

    @Test @Order(1)
    @DisplayName("totalRevenue() sums all amounts correctly")
    void totalRevenue() {
        double expected = 75000 + 25000 + 8000 + 72000 + 15000 + 24000 + 80000 + 500 + 300 + 70000;
        assertThat(service.totalRevenue(transactions)).isEqualTo(expected);
    }

    @Test @Order(2)
    @DisplayName("totalRevenue() returns 0 for empty list")
    void totalRevenueEmpty() {
        assertThat(service.totalRevenue(List.of())).isEqualTo(0.0);
    }

    @Test @Order(3)
    @DisplayName("revenueByMonth() correct totals per month")
    void revenueByMonth() {
        Map<Month, Double> result = service.revenueByMonth(transactions);
        assertThat(result.get(Month.JANUARY)).isEqualTo(100500.0);  // 75000+25000+500
        assertThat(result.get(Month.FEBRUARY)).isEqualTo(80300.0);  // 8000+72000+300
        assertThat(result.get(Month.MARCH)).isEqualTo(119000.0);    // 15000+24000+80000
        assertThat(result.get(Month.APRIL)).isEqualTo(70000.0);
    }

    @Test @Order(4)
    @DisplayName("revenueByMonth() does not include months with no transactions")
    void revenueByMonthNoEmptyMonths() {
        Map<Month, Double> result = service.revenueByMonth(transactions);
        assertThat(result).doesNotContainKey(Month.MAY);
        assertThat(result).doesNotContainKey(Month.DECEMBER);
    }

    @Test @Order(5)
    @DisplayName("topNProductsByRevenue() returns correct order")
    void topNProducts() {
        // LAPTOP: 75000+72000+80000+70000 = 297000 (top)
        // PHONE:  25000+24000             = 49000
        // DESK:   15000
        // CHAIR:  8000
        // CABLE:  300
        // MUG:    500
        List<String> top3 = service.topNProductsByRevenue(transactions, 3);
        assertThat(top3).containsExactly("LAPTOP", "PHONE", "DESK");
    }

    @Test @Order(6)
    @DisplayName("topNProductsByRevenue() tie broken alphabetically")
    void topNProductsTieBreak() {
        List<Transaction> tieData = List.of(
            new Transaction("X1", "C1", "BETA", "Cat", 100.0, Month.JANUARY),
            new Transaction("X2", "C1", "ALPHA","Cat", 100.0, Month.JANUARY)
        );
        assertThat(service.topNProductsByRevenue(tieData, 2))
            .containsExactly("ALPHA", "BETA");
    }

    @Test @Order(7)
    @DisplayName("topNProductsByRevenue() returns fewer than N when not enough products")
    void topNFewerProducts() {
        assertThat(service.topNProductsByRevenue(transactions, 100)).hasSize(6);
    }

    @ParameterizedTest @Order(8)
    @DisplayName("topNProductsByRevenue() throws for n <= 0")
    @ValueSource(ints = {0, -1})
    void topNInvalidThrows(int n) {
        assertThatThrownBy(() -> service.topNProductsByRevenue(transactions, n))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(9)
    @DisplayName("averageAmountByCategory() correct per category, rounded to 2dp")
    void averageByCategory() {
        Map<String, Double> result = service.averageAmountByCategory(transactions);
        // Electronics: 75000+25000+72000+24000+80000+70000 = 346000 / 6 = 57666.67
        assertThat(result.get("Electronics")).isEqualTo(57666.67);
        // Furniture: 8000+15000 = 23000 / 2 = 11500.0
        assertThat(result.get("Furniture")).isEqualTo(11500.0);
        // Accessories: 500+300 = 800 / 2 = 400.0
        assertThat(result.get("Accessories")).isEqualTo(400.0);
    }

    @Test @Order(10)
    @DisplayName("highValueCustomers() returns customers above threshold, sorted")
    void highValueCustomers() {
        // C001: 75000+8000+80000 = 163000
        // C002: 25000+15000+70000 = 110000
        // C003: 72000+24000 = 96000
        // C004: 500+300 = 800
        List<String> result = service.highValueCustomers(transactions, 100_000.0);
        assertThat(result).containsExactly("C001", "C002");
    }

    @Test @Order(11)
    @DisplayName("highValueCustomers() returns empty list when none qualify")
    void highValueCustomersNone() {
        assertThat(service.highValueCustomers(transactions, 1_000_000.0)).isEmpty();
    }

    @Test @Order(12)
    @DisplayName("hasHighValueTransactionInMonth() returns true when match exists")
    void hasHighValueTrue() {
        assertThat(service.hasHighValueTransactionInMonth(transactions, Month.MARCH, 79_000.0))
            .isTrue();
    }

    @Test @Order(13)
    @DisplayName("hasHighValueTransactionInMonth() returns false when no match")
    void hasHighValueFalse() {
        assertThat(service.hasHighValueTransactionInMonth(transactions, Month.JANUARY, 100_000.0))
            .isFalse();
    }

    @Test @Order(14)
    @DisplayName("partitionByAboveAverage() correctly splits around mean")
    void partitionByAboveAverage() {
        // avg = 369800 / 10 = 36980
        Map<Boolean, List<Transaction>> result = service.partitionByAboveAverage(transactions);
        // Above: T01(75000), T03_WAIT... let's just verify sizes and no overlap
        assertThat(result).containsKeys(true, false);
        assertThat(result.get(true).size() + result.get(false).size())
            .isEqualTo(transactions.size());
        // All items in true group must have amount >= mean
        double mean = transactions.stream().mapToDouble(Transaction::amount).average().orElse(0);
        result.get(true).forEach(t ->
            assertThat(t.amount()).isGreaterThanOrEqualTo(mean));
        result.get(false).forEach(t ->
            assertThat(t.amount()).isLessThan(mean));
    }

    @Test @Order(15)
    @DisplayName("customersByCategory() returns comma-separated sorted customer IDs")
    void customersByCategory() {
        Map<String, String> result = service.customersByCategory(transactions);
        // Electronics: C001(T01,T07), C002(T02,T10), C003(T04,T06) → "C001,C002,C003"
        assertThat(result.get("Electronics")).isEqualTo("C001,C002,C003");
        // Furniture: C001(T03), C002(T05) → "C001,C002"
        assertThat(result.get("Furniture")).isEqualTo("C001,C002");
        // Accessories: C004 → "C004"
        assertThat(result.get("Accessories")).isEqualTo("C004");
    }

    @Test @Order(16)
    @DisplayName("All methods handle empty transaction list without throwing")
    void emptyListHandledGracefully() {
        List<Transaction> empty = List.of();
        assertThatNoException().isThrownBy(() -> {
            service.totalRevenue(empty);
            service.revenueByMonth(empty);
            service.topNProductsByRevenue(empty, 5);
            service.averageAmountByCategory(empty);
            service.highValueCustomers(empty, 1000);
            service.hasHighValueTransactionInMonth(empty, Month.JANUARY, 100);
            service.partitionByAboveAverage(empty);
            service.customersByCategory(empty);
        });
    }
}
