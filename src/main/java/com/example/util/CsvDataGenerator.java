package com.example.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class to generate sample CSV data for testing
 */
public class CsvDataGenerator {
    private static final Random random = new Random();

    /**
     * Generate sales data CSV
     */
    public static String generateSalesData(int numRecords) throws IOException {
        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
            .withHeader("order_id", "customer_name", "product", "quantity", "price", "order_date"));

        String[] customers = {"Nguyen Van A", "Tran Thi B", "Le Van C", "Pham Thi D", "Hoang Van E"};
        String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor", "USB Drive", "Headphone", "Webcam"};
        int[] prices = {15000000, 200000, 500000, 3000000, 100000, 800000, 1200000};

        for (int i = 1; i <= numRecords; i++) {
            int productIndex = random.nextInt(products.length);
            csvPrinter.printRecord(
                i,
                customers[random.nextInt(customers.length)],
                products[productIndex],
                random.nextInt(10) + 1,
                prices[productIndex],
                LocalDate.now().minusDays(random.nextInt(30)).toString()
            );
        }

        csvPrinter.flush();
        return sw.toString();
    }

    /**
     * Generate employee data CSV
     */
    public static String generateEmployeeData(int numRecords) throws IOException {
        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
            .withHeader("id", "name", "age", "city", "salary", "department"));

        String[] cities = {"Ha Noi", "Ho Chi Minh", "Da Nang", "Can Tho", "Hai Phong"};
        String[] departments = {"IT", "Sales", "Marketing", "HR", "Finance"};

        for (int i = 1; i <= numRecords; i++) {
            csvPrinter.printRecord(
                i,
                "Employee_" + i,
                20 + random.nextInt(40),
                cities[random.nextInt(cities.length)],
                5000000 + random.nextInt(15000000),
                departments[random.nextInt(departments.length)]
            );
        }

        csvPrinter.flush();
        return sw.toString();
    }

    /**
     * Generate product data CSV
     */
    public static String generateProductData() throws IOException {
        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
            .withHeader("product_id", "product_name", "category", "stock", "supplier", "unit_price"));

        Object[][] products = {
            {1, "Laptop Dell XPS", "Electronics", 50, "Tech Supplier A", 15000000},
            {2, "Logitech Mouse", "Accessories", 200, "Tech Supplier B", 200000},
            {3, "Mechanical Keyboard", "Accessories", 150, "Tech Supplier B", 500000},
            {4, "LG Monitor 27\"", "Electronics", 80, "Tech Supplier A", 3000000},
            {5, "SanDisk USB 64GB", "Accessories", 500, "Tech Supplier C", 100000},
            {6, "Sony Headphone", "Accessories", 100, "Tech Supplier B", 800000},
            {7, "Logitech Webcam", "Electronics", 75, "Tech Supplier A", 1200000}
        };

        for (Object[] product : products) {
            csvPrinter.printRecord(product);
        }

        csvPrinter.flush();
        return sw.toString();
    }

    /**
     * Generate time series data CSV
     */
    public static String generateTimeSeriesData(int numDays) throws IOException {
        StringWriter sw = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
            .withHeader("date", "metric", "value"));

        LocalDate startDate = LocalDate.now().minusDays(numDays);
        String[] metrics = {"revenue", "users", "orders", "pageviews"};

        for (int i = 0; i < numDays; i++) {
            LocalDate date = startDate.plusDays(i);
            for (String metric : metrics) {
                int baseValue = switch (metric) {
                    case "revenue" -> 10000000;
                    case "users" -> 1000;
                    case "orders" -> 500;
                    case "pageviews" -> 50000;
                    default -> 0;
                };
                csvPrinter.printRecord(
                    date.toString(),
                    metric,
                    baseValue + random.nextInt(baseValue / 2)
                );
            }
        }

        csvPrinter.flush();
        return sw.toString();
    }

    /**
     * Save CSV to file
     */
    public static void saveCsvToFile(String csvContent, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent);
        }
    }
}

