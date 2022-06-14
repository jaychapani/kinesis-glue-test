package com.jaychapani.kinesisglue.stream;

import com.jaychapani.kinesisglue.model.User;

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
public class UserStreamConfiguration {

    private final Log logger = LogFactory.getLog(UserStreamConfiguration.class);

    private BlockingQueue<User> userQueue = new LinkedBlockingQueue<>();

    @Bean
    public Supplier<User> userProducer() {
        return () -> {
            return this.userQueue.poll();
        };
    }

    @Bean
    public Consumer<User> userConsumer() {
        return (value) -> {
            //logger.info("Consumer Received : " + Customer.fromByteBuffer(value.getData()));
            logger.info("Consumer received user : " + value);
        };
    }

    public void addUser(User user) throws IOException {

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

        this.userQueue.offer(user);
        logger.info("Producer added user : " + user.toString());
    }

}
