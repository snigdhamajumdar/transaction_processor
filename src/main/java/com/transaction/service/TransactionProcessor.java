package com.transaction.service;

import com.transaction.model.Transaction;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionProcessor {

    /**
     * Method to calculate account balance based on transactions within a time range
     * This method takes into account both payment and reversal transactions if both fall within the time range specified
     * This method ignores payment transactions whose reversal transactions are outside the time range specified
     * @param transactionRecords List of Transactions{@link Transaction}
     * @param accountNumber Account Number whose transactions are being processed
     * @param startDateTime Start Date and Time
     * @param endDateTime End Date and Time
     * @return {@link DoubleSummaryStatistics}
     * @throws Exception
     */
    public static DoubleSummaryStatistics findTransactionStats(List<Transaction> transactionRecords, String accountNumber, Instant startDateTime, Instant endDateTime) throws Exception {
        if(transactionRecords.size() == 0) {
            throw new Exception("No transactions found");
        }
        Map<String, Instant> mapOfReversedTransactions = transactionRecords.stream()
                .filter(transaction -> transaction.getRelatedTransaction()!= null && !transaction.getRelatedTransaction().isEmpty())
                .collect(Collectors.toMap(transaction -> transaction.getRelatedTransaction(), transaction -> transaction.getCreatedAt()));

        return   transactionRecords.stream()
                .filter(transaction -> transaction.getFromAccountId().equalsIgnoreCase(accountNumber))
                .filter(transaction ->
                        !mapOfReversedTransactions.containsKey(transaction.getTransactionId())
                                ||
                                !( mapOfReversedTransactions.containsKey(transaction.getTransactionId())
                                        &&
                                        (
                                                mapOfReversedTransactions.get(transaction.getTransactionId()).isAfter(endDateTime) ||
                                                        mapOfReversedTransactions.get(transaction.getTransactionId()).isBefore(startDateTime)
                                        )
                                ))
                .filter(transaction -> transaction.getCreatedAt().isAfter(startDateTime) && transaction.getCreatedAt().isBefore(endDateTime))
                .mapToDouble(transaction -> transaction.getAmount())
                .summaryStatistics();
    }
}
