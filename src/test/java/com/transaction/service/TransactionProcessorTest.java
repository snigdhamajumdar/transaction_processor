package com.transaction.service;

import com.transaction.Demo;
import com.transaction.model.Transaction;
import com.transaction.service.CSVProcessor;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static com.transaction.util.TransactionType.PAYMENT;
import static com.transaction.util.TransactionType.REVERSAL;
import static org.junit.Assert.assertEquals;

public class TransactionProcessorTest {

    @Test
    public void givenReverseTransactionOutsideTimeRange_CalculateBalance() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("20/10/2018 19:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect balance", -25.00, result.getSum(), 0.0);
    }

    @Test
    public void givenReverseTransactionOutsideTimeRange_CountEligibleTransactions() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("20/10/2018 19:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect number transactions", 1, result.getCount(), 0.0);
    }

    @Test
    public void givenReverseTransactionWithinTimeRange_CalculateBalance() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("20/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect balance", -33.00, result.getSum(), 0.0);
    }

    @Test
    public void givenReverseTransactionWithinTimeRange_CountEligibleTransactions() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("20/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect number transactions", 4, result.getCount(), 0.0);
    }

    @Test
    public void givenTransactions_CalculateBalances() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("21/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect number transactions", -40.25, result.getSum(), 0.0);
    }

    @Test
    public void givenTransactions_CountEligibleTransactions() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("21/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect number transactions", 5, result.getCount(), 0.0);
    }

    @Test
    public void givenIncorrectAccountNumber_DisplayZeroStats() throws Exception {
        List<Transaction> transactionList = createTestData();
        String accountNumber = "BAD_ACCOUNT_NUMBER";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("21/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        DoubleSummaryStatistics result = TransactionProcessor.findTransactionStats(transactionList, accountNumber, start, end);
        assertEquals("Incorrect number transactions", 0, result.getCount(), 0.0);
    }

    @Test(expected = Exception.class)
    public void givenNoTransactions_throwException() throws Exception {
        String accountNumber = "ACC334455";
        Instant start = LocalDateTime.parse("20/10/2018 00:00:00", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        Instant end = LocalDateTime.parse("21/10/2018 23:59:59", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant();
        TransactionProcessor.findTransactionStats(Collections.EMPTY_LIST, accountNumber, start, end);
    }

    private static List<Transaction> createTestData() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("TX10001",
                "ACC334455",
                "ACC778899",
                LocalDateTime.parse("20/10/2018 12:47:55", Demo.df)
                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                .toInstant(),
                -25.00,
                PAYMENT,
                null
                ));
        transactions.add(new Transaction("TX10002",
                "ACC334455",
                "ACC998877",
                LocalDateTime.parse("20/10/2018 17:33:43", Demo.df)
                        .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                        .toInstant(),
                -10.50,
                PAYMENT,
                null
        ));
        transactions.add(new Transaction("TX10003",
                "ACC998877",
                "ACC778899",
                LocalDateTime.parse("20/10/2018 18:00:00", Demo.df)
                        .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                        .toInstant(),
                -5.00,
                PAYMENT,
                null
        ));
        transactions.add(new Transaction("TX10004",
                "ACC334455",
                "ACC998877",
                LocalDateTime.parse("20/10/2018 19:45:00", Demo.df)
                        .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                        .toInstant(),
                10.50,
                REVERSAL,
                "TX10002"
        ));
        transactions.add(new Transaction("TX10005",
                "ACC334455",
                "ACC998877",
                LocalDateTime.parse("20/10/2018 20:00:00", Demo.df)
                        .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                        .toInstant(),
            -8.00,
                PAYMENT,
                null
        ));
        transactions.add(new Transaction("TX10006",
                "ACC334455",
                "ACC778899",
                LocalDateTime.parse("21/10/2018 09:30:00", Demo.df)
                        .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                        .toInstant(),
                -7.25,
                PAYMENT,
                null
        ));
        return transactions;
    }
}
