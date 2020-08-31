package com.transaction.service;

import com.transaction.Demo;
import com.transaction.model.Transaction;
import com.transaction.util.TransactionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

public class CSVProcessor {

    /**
     * Reads the CSV given in the sample csv file path
     * @param sampleCsvFilePath
     * @return List of Transactions{@link Transaction}
     * @throws IOException
     */
    public static List<Transaction> readCSV(String sampleCsvFilePath) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(sampleCsvFilePath));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withHeader("transactionId", "fromAccountId", "toAccountId", "createdAt", "amount", "transactionType", "relatedTransaction")
                .withIgnoreHeaderCase()
                .withTrim());
        List<CSVRecord> csvRecords = csvParser.getRecords();

        return csvRecords
                .stream()
                .skip(1) //Skipping the header line
                .map(record -> new Transaction(record.get("transactionId"),
                        record.get("fromAccountId").trim(),
                        record.get("toAccountId").trim(),
                        LocalDateTime.parse(record.get("createdAt").trim(), Demo.df)
                                .atZone(ZoneId.of( ZoneId.SHORT_IDS.get("AET")))
                                .toInstant(),
                        Double.parseDouble(record.get("amount").trim()),
                        TransactionType.valueOf(record.get("transactionType").trim()),
                        record.get("relatedTransaction").trim()))
                .collect(collectAfterTransform()); //Using custom collector to negate payment transactions
    }

    /**
     * Custom collector to transform payment transactions to hold negative value
     * @return {@link Collector}
     */
    private static Collector<Transaction, ?, List<Transaction>> collectAfterTransform() {
        return Collector.of(
                () -> new ArrayList<Transaction>(),
                (list, value) -> list.add(transform(value)),
                (first, second) -> { first.addAll(second); return first; });
    }

    /**
     * Transforms the given transaction by negating the amount if it of type payment
     * @param a {@link Transaction}
     * @return
     */
    private static Transaction transform(Transaction a) {
        if(a.getTransactionType() == TransactionType.PAYMENT) {
            a.setAmount(a.getAmount() * -1);
        }
        return a;
    }

}
