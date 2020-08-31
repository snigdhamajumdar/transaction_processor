package com.transaction;

import com.transaction.model.Transaction;
import com.transaction.service.CSVProcessor;
import com.transaction.service.TransactionProcessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class Demo {

    private CSVProcessor csvProcessor = new CSVProcessor();
    private TransactionProcessor transactionProcessor = new TransactionProcessor();
    private static final String SAMPLE_CSV_FILE_PATH = "./account_txn.csv";
    public static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Demo demo = new Demo();
        List<Transaction> transactionRecords = demo.csvProcessor.readCSV(SAMPLE_CSV_FILE_PATH);

        String accountNumber = args[0];
        String sdt = args[1];
        String edt = args[2];

        Instant startDateTime = LocalDateTime.parse(sdt, df)
                .atZone(ZoneId.of("Australia/Sydney"))
                .toInstant();
        Instant endDateTime = LocalDateTime.parse(edt, df)
                .atZone(ZoneId.of("Australia/Sydney"))
                .toInstant();

        DoubleSummaryStatistics result = demo.transactionProcessor.findTransactionStats(transactionRecords, accountNumber, startDateTime, endDateTime);
        System.out.println("Relative Balance For the Period Is:  " + result.getSum());
        System.out.println("Number of Transactions included is:  " + result.getCount());
    }
}
