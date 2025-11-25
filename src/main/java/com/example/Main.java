package com.example;

import com.example.config.MinioConfig;
import com.example.service.DuckDBService;
import com.example.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Data Lake Application - MinIO + DuckDB
 * Demonstrating CSV storage in MinIO and querying with DuckDB
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Data Lake Application - MinIO + DuckDB");
        System.out.println("=================================================\n");

        try {
            // Initialize services
            MinioConfig config = MinioConfig.getDefaultConfig();
            MinioService minioService = new MinioService(config);
            DuckDBService duckDBService = new DuckDBService();

            // Demo 1: Create and upload sample CSV data
            System.out.println("--- DEMO 1: Upload CSV Data to MinIO ---");
            createAndUploadSampleData(minioService);

            // Demo 2: List files in MinIO
            System.out.println("\n--- DEMO 2: List CSV Files in MinIO ---");
            listMinioFiles(minioService);

            // Demo 3: Query CSV data with DuckDB
            System.out.println("\n--- DEMO 3: Query CSV Data with DuckDB ---");
            queryCsvWithDuckDB(minioService, duckDBService);

            // Demo 4: Advanced queries
            System.out.println("\n--- DEMO 4: Advanced Analytics with DuckDB ---");
            advancedAnalytics(minioService, duckDBService);

            // Cleanup
            duckDBService.close();
            System.out.println("\n=================================================");
            System.out.println("   Data Lake Demo Completed Successfully!");
            System.out.println("=================================================");

        } catch (Exception e) {
            logger.error("Error in main application", e);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create sample CSV data and upload to MinIO
     */
    private static void createAndUploadSampleData(MinioService minioService) throws Exception {
        // Sample Sales Data
        String salesCsv = """
            order_id,customer_name,product,quantity,price,order_date
            1,Nguyen Van A,Laptop,2,15000000,2024-01-15
            2,Tran Thi B,Mouse,5,200000,2024-01-16
            3,Le Van C,Keyboard,3,500000,2024-01-16
            4,Pham Thi D,Monitor,1,3000000,2024-01-17
            5,Nguyen Van A,USB Drive,10,100000,2024-01-18
            6,Hoang Van E,Laptop,1,15000000,2024-01-18
            7,Tran Thi B,Headphone,2,800000,2024-01-19
            8,Le Van C,Webcam,1,1200000,2024-01-20
            9,Nguyen Van A,Mouse,3,200000,2024-01-21
            10,Pham Thi D,Keyboard,2,500000,2024-01-22
            """;

        // Sample Products Data
        String productsCsv = """
            product_id,product_name,category,stock,supplier
            1,Laptop,Electronics,50,Tech Supplier A
            2,Mouse,Accessories,200,Tech Supplier B
            3,Keyboard,Accessories,150,Tech Supplier B
            4,Monitor,Electronics,80,Tech Supplier A
            5,USB Drive,Accessories,500,Tech Supplier C
            6,Headphone,Accessories,100,Tech Supplier B
            7,Webcam,Electronics,75,Tech Supplier A
            """;

        // Upload to MinIO
        minioService.uploadCsvContent("sales/sales_data.csv", salesCsv);
        System.out.println("✓ Uploaded sales_data.csv");

        minioService.uploadCsvContent("products/products_data.csv", productsCsv);
        System.out.println("✓ Uploaded products_data.csv");

        // Create a larger dataset for analytics
        StringBuilder largeCsv = new StringBuilder();
        largeCsv.append("id,name,age,city,salary\n");
        String[] cities = {"Ha Noi", "Ho Chi Minh", "Da Nang", "Can Tho", "Hai Phong"};
        for (int i = 1; i <= 100; i++) {
            largeCsv.append(String.format("%d,Employee_%d,%d,%s,%d\n",
                i, i, 20 + (i % 40), cities[i % 5], 5000000 + (i * 100000)));
        }
        minioService.uploadCsvContent("employees/employees_data.csv", largeCsv.toString());
        System.out.println("✓ Uploaded employees_data.csv (100 records)");
    }

    /**
     * List all CSV files in MinIO
     */
    private static void listMinioFiles(MinioService minioService) {
        List<String> files = minioService.listCsvFiles();
        System.out.println("CSV files in MinIO Data Lake:");
        for (String file : files) {
            System.out.println("  - " + file);
        }
        System.out.println("Total files: " + files.size());
    }

    /**
     * Query CSV data using DuckDB
     */
    private static void queryCsvWithDuckDB(MinioService minioService, DuckDBService duckDBService) throws Exception {
        // Download CSV from MinIO and load into DuckDB
        File tempFile = File.createTempFile("sales_", ".csv");
        tempFile.deleteOnExit();

        try (InputStream is = minioService.getCsvFileStream("sales/sales_data.csv");
             FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // Load into DuckDB
        duckDBService.loadCsvFromFile("sales", tempFile.getAbsolutePath());

        // Execute queries
        System.out.println("\nQuery 1: All sales records");
        duckDBService.printQueryResults("SELECT * FROM sales LIMIT 5");

        System.out.println("\nQuery 2: Total sales by customer");
        duckDBService.printQueryResults(
            "SELECT customer_name, COUNT(*) as total_orders, SUM(quantity * price) as total_amount " +
            "FROM sales GROUP BY customer_name ORDER BY total_amount DESC"
        );

        System.out.println("\nQuery 3: Sales by product");
        duckDBService.printQueryResults(
            "SELECT product, SUM(quantity) as total_quantity, SUM(quantity * price) as total_revenue " +
            "FROM sales GROUP BY product ORDER BY total_revenue DESC"
        );
    }

    /**
     * Advanced analytics with DuckDB
     */
    private static void advancedAnalytics(MinioService minioService, DuckDBService duckDBService) throws Exception {
        // Load employees data
        File tempFile = File.createTempFile("employees_", ".csv");
        tempFile.deleteOnExit();

        try (InputStream is = minioService.getCsvFileStream("employees/employees_data.csv");
             FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        duckDBService.loadCsvFromFile("employees", tempFile.getAbsolutePath());

        // Advanced queries
        System.out.println("\nAdvanced Query 1: Average salary by city");
        duckDBService.printQueryResults(
            "SELECT city, COUNT(*) as employee_count, " +
            "AVG(salary) as avg_salary, MIN(salary) as min_salary, MAX(salary) as max_salary " +
            "FROM employees GROUP BY city ORDER BY avg_salary DESC"
        );

        System.out.println("\nAdvanced Query 2: Age distribution");
        duckDBService.printQueryResults(
            "SELECT " +
            "CASE " +
            "  WHEN age < 25 THEN '20-24' " +
            "  WHEN age < 30 THEN '25-29' " +
            "  WHEN age < 35 THEN '30-34' " +
            "  ELSE '35+' " +
            "END as age_group, " +
            "COUNT(*) as count, AVG(salary) as avg_salary " +
            "FROM employees GROUP BY age_group ORDER BY age_group"
        );

        System.out.println("\nAdvanced Query 3: Top 10 highest paid employees");
        duckDBService.printQueryResults(
            "SELECT name, city, age, salary FROM employees ORDER BY salary DESC LIMIT 10"
        );

        // Export query results back to MinIO
        File exportFile = File.createTempFile("analytics_", ".csv");
        exportFile.deleteOnExit();

        duckDBService.executeQueryToCSV(
            "SELECT city, AVG(salary) as avg_salary FROM employees GROUP BY city",
            exportFile.getAbsolutePath()
        );

        minioService.uploadCsvFile("analytics/city_salary_summary.csv", exportFile);
        System.out.println("\n✓ Exported analytics results to MinIO: analytics/city_salary_summary.csv");
    }
}