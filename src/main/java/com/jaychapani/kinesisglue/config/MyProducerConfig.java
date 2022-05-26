package com.jaychapani.kinesisglue.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaServiceManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

import software.amazon.awssdk.services.glue.model.DataFormat;

@Component
public class MyProducerConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "spring.cloud.stream.kinesis.binder.kpl-kcl-enabled")
    public KinesisProducerConfiguration kinesisProducerConfiguration() {
        KinesisProducerConfiguration kinesisProducerConfiguration = new KinesisProducerConfiguration();
        kinesisProducerConfiguration.setCredentialsProvider(new DefaultAWSCredentialsProviderChain());
        kinesisProducerConfiguration.setRegion("us-east-1");
        kinesisProducerConfiguration.setGlueSchemaRegistryConfiguration(new GlueSchemaRegistryConfiguration(getProducerConfig()));
        return kinesisProducerConfiguration;
    }

    private Properties getProducerConfig() {
        Properties props = new Properties();
        props.setProperty(AWSSchemaRegistryConstants.AWS_REGION, "us-east-1");
        props.setProperty(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.AVRO.name());
        props.setProperty(AWSSchemaRegistryConstants.REGISTRY_NAME, "glue-registry-test");
        props.setProperty(AWSSchemaRegistryConstants.SCHEMA_NAME, "customer-schema");
        props.setProperty(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE,
                  AvroRecordType.SPECIFIC_RECORD.getName());
        props.setProperty(AWSSchemaRegistryConstants.USER_AGENT_APP ,"KPL");
        props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, false);

        return props;
    }


    @Bean
    public MessageConverter customerMessageConverter() throws IOException {
        MessageConverter avroSchemaMessageConverter = new AvroSchemaMessageConverter(new AvroSchemaServiceManagerImpl());

        ((AvroSchemaMessageConverter) avroSchemaMessageConverter).setSchemaLocation(new FileSystemResource("src/main/avro/customer.avsc"));
        return avroSchemaMessageConverter;
    }

}
