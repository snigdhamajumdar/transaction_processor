# Getting Started

### Prerequisites
To run this code, you would need Java 8 and Gradle installed on your machine. Please refer to the official documentation links below

* [How do I intall Java](https://java.com/en/download/help/download_options.xml)
* [Official Gradle documentation](https://docs.gradle.org)

### Steps to run the code
* Clone the project from github.com. Open terminal and enter command:  
 ``git clone https://github.com/snigdhamajumdar/transaction_processor.git``
* cd into the project directory
* Run  
 ``gradle clean build``
* If input csv needs to be modified, then add transactions to the file    
 ``./account_txn.csv``
* In order to run the main class, provide arguments for account number, start date/time and end date/time. Note that the date time arguments have to be enclosed within double quotes and escape the double quotes    
 ``gradle run --args="ACC334455 \"20/10/2018 00:00:00\" \"20/10/2018 18:59:59\"" ``
* To run the Junit    
 ``gradle test``  
* To view the test report open in browser  
  `./build/reports/tests/test/index.html`
  
### Notes on Code Structure
* The application is a stand-alone java app integrated with gradle for easy execution. 
* The main class is `com.transaction.Demo`
* The package `com.transaction.service` contains services used by the main class 
* `com.transaction.service.CSVProcessor` is a service class used to read the CSV file.
* `com.transaction.service.TransactionProcessor` is a service class used to calculate transaction stats for an account.  
    * Calculation takes into account both payment and reversal transactions **IF** both fall **WITHIN** the time range specified
    * Calculation **IGNORES** payment transactions whose reversal transactions are **OUTSIDE** the time range specified
* `com.transaction.model.Transaction` is a simple POJO that represent a single transaction row of input CSV.
* `com.transaction.util.TransactionType` is an enum representing types of transaction
    
