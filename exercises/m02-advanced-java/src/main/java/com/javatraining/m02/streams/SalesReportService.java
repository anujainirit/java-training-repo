package com.javatraining.m02.streams;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EXERCISE M02-T2: Sales Report Pipeline
 *
 * Use the Streams API to compute a sales report from a list of transactions.
 * DO NOT use for/while loops — every method must use streams/lambdas internally.
 * DO NOT modify method signatures or the Transaction record.
 */
public record Transaction(
    String id,
    String customerId,
    String productId,
    String category,
    double amount,
    Month month
) {}

class SalesReportService {

    /**
     * Total revenue across all transactions.
     */
    public double totalRevenue(List<Transaction> transactions) {
        // TODO: use streams
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Total revenue grouped by month, sorted by month order (Jan first).
     * Months with no transactions must NOT appear in the result.
     */
    public Map<Month, Double> revenueByMonth(List<Transaction> transactions) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return the top N products by total revenue, sorted descending.
     * Ties broken alphabetically ascending by productId.
     *
     * @param n must be > 0
     * @return list of productIds (may be shorter than n if fewer products exist)
     */
    public List<String> topNProductsByRevenue(List<Transaction> transactions, int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return the average transaction amount per category.
     * Result rounded to 2 decimal places per category.
     */
    public Map<String, Double> averageAmountByCategory(List<Transaction> transactions) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return customers who spent more than the given threshold in total.
     * Result sorted alphabetically by customerId.
     */
    public List<String> highValueCustomers(List<Transaction> transactions, double threshold) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return true if any transaction in the given month exceeds the threshold amount.
     */
    public boolean hasHighValueTransactionInMonth(List<Transaction> transactions,
                                                   Month month, double threshold) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Partition transactions into two groups:
     *   true  → amount >= avgAmount of all transactions
     *   false → amount <  avgAmount
     *
     * @return map with Boolean keys
     */
    public Map<Boolean, List<Transaction>> partitionByAboveAverage(List<Transaction> transactions) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * For each category return a comma-separated string of unique customer IDs
     * who purchased in that category, sorted alphabetically.
     * Example: "Electronics" -> "C001,C003,C007"
     */
    public Map<String, String> customersByCategory(List<Transaction> transactions) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
