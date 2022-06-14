package com.jaychapani.kinesisglue.config;

import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class MyProducerConfig {
    @Bean
    public MessageConverter myAvroMessageConverter() {
        return new GlueAvroMessageConverter() ;
    }

    @Bean
    public MessageConverter myJsonMessageConverter() {
        return new GlueJsonMessageConverter() ;
    }
//    @Bean
//    public MessageConverter myUserMessageConverter() {
//        return new GlueAvroUserMessageConverter() ;
//    }

//    @Bean
//    public GlueSchemaRegistryConfiguration custRegistryConfiguration() {
//        GlueSchemaRegistryConfiguration configs = new GlueSchemaRegistryConfiguration("us-east-1");
//        //Optional setting to enable auto-registration.
//        configs.setRegistryName("glue-registry-test");
//        //configs.setSchemaAutoRegistrationEnabled(true);
//        return configs;
//    }
//
//    @Bean
//    public GlueSchemaRegistryConfiguration userRegistryConfiguration() {
//        GlueSchemaRegistryConfiguration configs = new GlueSchemaRegistryConfiguration("us-east-1");
//        //Optional setting to enable auto-registration.
//        configs.setRegistryName("default-registry");
//        //configs.setSchemaAutoRegistrationEnabled(true);
//        return configs;
//    }


//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(name = "spring.cloud.stream.kinesis.binder.kpl-kcl-enabled")
//    public KinesisProducerConfiguration kinesisProducerConfiguration() {
//        KinesisProducerConfiguration kinesisProducerConfiguration = new KinesisProducerConfiguration();
//        kinesisProducerConfiguration.setCredentialsProvider(new DefaultAWSCredentialsProviderChain());
//        kinesisProducerConfiguration.setRegion("us-east-1");
//        kinesisProducerConfiguration.setGlueSchemaRegistryConfiguration(new GlueSchemaRegistryConfiguration(getProducerConfig()));
//        return kinesisProducerConfiguration;
//    }
//
//    private Properties getProducerConfig() {
//        Properties props = new Properties();
//        props.setProperty(AWSSchemaRegistryConstants.AWS_REGION, "us-east-1");
//        props.setProperty(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.AVRO.name());
//        props.setProperty(AWSSchemaRegistryConstants.REGISTRY_NAME, "glue-registry-test");
//        props.setProperty(AWSSchemaRegistryConstants.SCHEMA_NAME, "customer-schema");
//        props.setProperty(AWSSchemaRegistryConstants.AVRO_RECORD_TYPE,
//                  AvroRecordType.SPECIFIC_RECORD.getName());
//        props.setProperty(AWSSchemaRegistryConstants.USER_AGENT_APP ,"KPL");
//        props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, false);
//
//        return props;
//    }
//
//
//    @Bean
//    public MessageConverter customerMessageConverter() throws IOException {
//        MessageConverter avroSchemaMessageConverter = new AvroSchemaMessageConverter(new AvroSchemaServiceManagerImpl());
//
//        ((AvroSchemaMessageConverter) avroSchemaMessageConverter).setSchemaLocation(new FileSystemResource("src/main/avro/customer.avsc"));
//        return avroSchemaMessageConverter;
//    }
//
//    @Bean
//    ProducerMessageHandlerCustomizer<KplMessageHandler> kplMessageHandlerCustomizer() {
//        return (handler, destinationName) -> handler.setMessageConverter(new MessageConverter() {
//
//            @Override
//            public Object fromMessage(Message<?> message, Class<?> targetClass) {
//                return ((UserRecord) message.getPayload()).getData().array();
//            }
//
//            @Override
//            public Message<?> toMessage(Object payload, MessageHeaders headers) {
//                return null;
//            }
//
//        });
//    }
}
