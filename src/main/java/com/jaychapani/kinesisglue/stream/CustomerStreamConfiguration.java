package com.jaychapani.kinesisglue.stream;

import com.amazonaws.services.kinesis.producer.UserRecord;
import com.amazonaws.services.schemaregistry.common.Schema;
import com.jaychapani.kinesisglue.model.Customer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import software.amazon.awssdk.services.glue.model.DataFormat;

@Configuration
public class CustomerStreamConfiguration {

    private final Log logger = LogFactory.getLog(CustomerStreamConfiguration.class);

    private BlockingQueue<UserRecord> customerQueue = new LinkedBlockingQueue<>();

    @Bean
    public Supplier<UserRecord> customerProducer() {
        return () -> {
            return this.customerQueue.poll();
        };
    }

    @Bean
    public Consumer<UserRecord> customerConsumer() {
        return (value) -> {
            try {
                logger.info("Consumer Received : " + Customer.fromByteBuffer(value.getData()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public void addCustomer(Customer customer) throws IOException {

        Schema gsrSchema = null;
        gsrSchema =
            new Schema(new String(Files.readAllBytes(Paths.get(("src/main/avro/customer.avsc")))),
                       DataFormat.AVRO.toString(), "customer-schema");

        UserRecord userRecord = new UserRecord();
        userRecord.setData(customer.toByteBuffer());
        userRecord.setPartitionKey("12345678");
        userRecord.setStreamName("test_stream_jc_glue");
        userRecord.setSchema(gsrSchema);

        this.customerQueue.offer(userRecord);
        logger.info("Producer added : " + customer.toString());
    }

}
