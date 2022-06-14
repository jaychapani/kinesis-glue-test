package com.jaychapani.kinesisglue.stream;

import com.jaychapani.kinesisglue.model.Customer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class CustomerStreamConfiguration {

    private final Log logger = LogFactory.getLog(CustomerStreamConfiguration.class);

    private BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();

    @Bean
    public Supplier<Customer> customerProducer() {
        return () -> {
            return this.customerQueue.poll();
        };
    }

    @Bean
    public Consumer<Customer> customerConsumer() {
        return (value) -> {
            //logger.info("Consumer Received : " + Customer.fromByteBuffer(value.getData()));
            logger.info("Consumer Received : " + value);
        };
    }

    public void addCustomer(Customer customer) throws IOException {

//        Schema gsrSchema = null;
//        gsrSchema =
//            new Schema(new String(Files.readAllBytes(Paths.get(("src/main/avro/customer.avsc")))),
//                       DataFormat.AVRO.toString(), "customer-schema");
//
//        UserRecord userRecord = new UserRecord();
//        userRecord.setData(customer.toByteBuffer());
//        userRecord.setPartitionKey("12345678");
//        userRecord.setStreamName("test_stream_jc_glue");
//        userRecord.setSchema(gsrSchema);

        this.customerQueue.offer(customer);
        logger.info("Producer added : " + customer.toString());
    }

}
