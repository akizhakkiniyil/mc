<!-- 
============================================================================
pom.xml: Maven Project Configuration
This file defines the project structure and dependencies. It includes
Spring Boot 3.x, Spring Batch 5.x, JDBC for database interaction, and H2
as the in-memory database.
(No changes from previous version)
============================================================================
-->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Using a stable Spring Boot 3.x version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>blazing-fast-batch</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>blazing-fast-batch</name>
    <description>Blazing Fast Spring Batch 5.x Example</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <!-- Core dependency for Spring Batch -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-batch</artifactId>
        </dependency>
        <!-- Dependency for JDBC data access -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!-- In-memory H2 Database for demonstration -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Testing dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.batch</groupId>
            <artifactId>spring-batch-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

<!-- 
============================================================================
src/main/resources/application.properties
This file contains the application configuration, including database
connection details, batch job settings, and H2 console access.
(No changes from previous version)
============================================================================
-->
# Spring Batch Settings
# We disable the job execution on startup to trigger it manually.
spring.batch.job.enabled=false 
# Initialize the Spring Batch metadata schema on startup.
spring.batch.jdbc.initialize-schema=always

# H2 Database Settings
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
# Enable the H2 web console to inspect the database.
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.org.springframework.batch=INFO
logging.level.com.example.blazingfastbatch=DEBUG


<!-- 
============================================================================
src/main/resources/schema.sql
This SQL script creates the target database tables. Spring Boot will
automatically execute this script on startup against the H2 database.
(No changes from previous version)
============================================================================
-->
DROP TABLE IF EXISTS CUSTOMERS;
DROP TABLE IF EXISTS PRODUCTS;

CREATE TABLE CUSTOMERS (
    id INT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE PRODUCTS (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price DECIMAL(10, 2),
    created_at TIMESTAMP
);

<!-- 
============================================================================
src/main/resources/data/customers-01.csv
Sample data for the first customer CSV file.
(No changes from previous version)
============================================================================
-->
id,firstName,lastName,email
1,John,Doe,john.doe@example.com
2,Jane,Smith,jane.smith@example.com
3,Michael,Brown,michael.brown@example.com

<!-- 
============================================================================
src/main/resources/data/customers-02.csv
Sample data for the second customer CSV file.
(No changes from previous version)
============================================================================
-->
id,firstName,lastName,email
4,Emily,Davis,emily.davis@example.com
5,Chris,Wilson,chris.wilson@example.com

<!-- 
============================================================================
src/main/resources/data/products-01.csv
Sample data for the first product CSV file.
(No changes from previous version)
============================================================================
-->
id,name,description,price
101,Laptop Pro,High-end developer laptop,2499.99
102,Wireless Mouse,Ergonomic wireless mouse,79.50

<!-- 
============================================================================
src/main/resources/data/products-02.csv
Sample data for the second product CSV file.
(No changes from previous version)
============================================================================
-->
id,name,description,price
103,Mechanical Keyboard,RGB Mechanical Keyboard,150.00
104,4K Monitor,27-inch 4K UHD Monitor,625.00
105,Webcam HD,1080p HD Webcam,89.99

<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/BlazingFastBatchApplication.java
The main entry point for the Spring Boot application.
**MODIFIED**: Added @EnableScheduling to activate the scheduler.
============================================================================
-->
package com.example.blazingfastbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // This annotation enables Spring's scheduled task execution
public class BlazingFastBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlazingFastBatchApplication.class, args);
    }

}

<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/model/Customer.java
POJO representing a Customer record.
(No changes from previous version)
============================================================================
-->
package com.example.blazingfastbatch.model;

import java.time.LocalDateTime;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/model/Product.java
POJO representing a Product record.
(No changes from previous version)
============================================================================
-->
package com.example.blazingfastbatch.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/processor/TimestampProcessor.java
A simple ItemProcessor to add a creation timestamp to each record.
(No changes from previous version)
============================================================================
-->
package com.example.blazingfastbatch.processor;

import com.example.blazingfastbatch.model.Customer;
import com.example.blazingfastbatch.model.Product;
import org.springframework.batch.item.ItemProcessor;
import java.time.LocalDateTime;

public class TimestampProcessor implements ItemProcessor<Object, Object> {

    @Override
    public Object process(Object item) throws Exception {
        if (item instanceof Customer customer) {
            customer.setCreatedAt(LocalDateTime.now());
            return customer;
        } else if (item instanceof Product product) {
            product.setCreatedAt(LocalDateTime.now());
            return product;
        }
        return item;
    }
}


<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/config/BatchConfiguration.java
This is the core configuration for the Spring Batch job.
(No changes from previous version)
============================================================================
-->
package com.example.blazingfastbatch.config;

import com.example.blazingfastbatch.model.Customer;
import com.example.blazingfastbatch.model.Product;
import com.example.blazingfastbatch.processor.TimestampProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchConfiguration {

    @Value("classpath:data/*.csv")
    private Resource[] inputResources;

    @Bean
    public Job multiFileToMultiTableJob(JobRepository jobRepository, Step parallelMultiFileStep) {
        return new JobBuilder("multiFileToMultiTableJob", jobRepository)
                .start(parallelMultiFileStep)
                .build();
    }

    @Bean
    public Step parallelMultiFileStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      MultiResourceItemReader<Object> multiFileReader,
                                      ItemProcessor<Object, Object> timestampProcessor,
                                      ClassifierCompositeItemWriter<Object> classifierWriter,
                                      TaskExecutor taskExecutor) {
        return new StepBuilder("parallelMultiFileStep", jobRepository)
                .<Object, Object>chunk(100, transactionManager)
                .reader(multiFileReader)
                .processor(timestampProcessor)
                .writer(classifierWriter)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }

    @Bean
    public MultiResourceItemReader<Object> multiFileReader() {
        MultiResourceItemReader<Object> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(classifierFlatFileItemReader());
        resourceItemReader.setStrict(true); 
        return resourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Object> classifierFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<Object>()
                .name("classifierFlatFileItemReader")
                .lineMapper(classifierLineMapper())
                .build();
    }
    
    @Bean
    public LineMapper<Object> classifierLineMapper() {
        return (line, lineNumber) -> {
            if (line.contains("@")) {
                return customerLineMapper().mapLine(line, lineNumber);
            } else {
                return productLineMapper().mapLine(line, lineNumber);
            }
        };
    }

    @Bean
    public DefaultLineMapper<Object> customerLineMapper() {
        DefaultLineMapper<Object> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(createTokenizer("id,firstName,lastName,email"));
        lineMapper.setFieldSetMapper(createFieldSetMapper(Customer.class));
        return lineMapper;
    }

    @Bean
    public DefaultLineMapper<Object> productLineMapper() {
        DefaultLineMapper<Object> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(createTokenizer("id,name,description,price"));
        lineMapper.setFieldSetMapper(createFieldSetMapper(Product.class));
        return lineMapper;
    }
    
    private LineTokenizer createTokenizer(String names) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(names.split(","));
        tokenizer.setIncludedFields(0, 1, 2, 3);
        return tokenizer;
    }

    private FieldSetMapper<Object> createFieldSetMapper(Class<?> targetType) {
        BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(targetType);
        return fieldSetMapper;
    }

    @Bean
    public ItemProcessor<Object, Object> timestampProcessor() {
        return new TimestampProcessor();
    }

    @Bean
    public ClassifierCompositeItemWriter<Object> classifierWriter(
            JdbcBatchItemWriter<Customer> customerWriter,
            JdbcBatchItemWriter<Product> productWriter) {

        Classifier<Object, ItemWriter<? super Object>> classifier = classifiable -> {
            if (classifiable instanceof Customer) {
                return customerWriter;
            } else if (classifiable instanceof Product) {
                return productWriter;
            }
            throw new IllegalArgumentException("Unknown type: " + classifiable.getClass().getName());
        };

        ClassifierCompositeItemWriter<Object> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(classifier);
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> customerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO CUSTOMERS (id, first_name, last_name, email, created_at) VALUES (:id, :firstName, :lastName, :email, :createdAt)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Product> productWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(dataSource)
                .sql("INSERT INTO PRODUCTS (id, name, description, price, created_at) VALUES (:id, :name, :description, :price, :createdAt)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}

<!-- 
============================================================================
src/main/java/com/example/blazingfastbatch/scheduler/JobScheduler.java
**NEW FILE**: This component replaces the JobRunner. It uses @Scheduled
to launch the batch job on a defined schedule (e.g., every minute).
============================================================================
-->
package com.example.blazingfastbatch.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job multiFileToMultiTableJob;

    /**
     * This method is scheduled to run at a fixed interval defined by the cron expression.
     * Cron Expression: "0 * * * * ?" means the job will run at the start of every minute.
     * You can customize this for different schedules (e.g., "0 0 2 * * ?" for 2 AM daily).
     */
    @Scheduled(cron = "0 * * * * ?")
    public void launchJobPeriodically() {
        logger.info("Scheduler is triggering the batch job...");
        try {
            // Use a timestamp to ensure each job instance is unique, allowing it to be re-run.
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(multiFileToMultiTableJob, jobParameters);
            logger.info("Batch job has been launched successfully by the scheduler.");

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            logger.error("Scheduler failed to launch the batch job", e);
        }
    }
}
