package com.jaychapani.kinesisglue.config;

import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializerImpl;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializerImpl;
import com.amazonaws.services.schemaregistry.utils.AVROUtils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaServiceManagerImpl;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.glue.model.DataFormat;

public class GlueAvroMessageConverter extends AvroSchemaMessageConverter {


    @Value("custom meta tag")
    String x_amz_meta_trans;

//    @Autowired
//    @Qualifier("custRegistryConfiguration")
    private GlueSchemaRegistryConfiguration glueSchemaRegistryConfig;

    public GlueAvroMessageConverter() {
        super(new MimeType("application", "avro"), new AvroSchemaServiceManagerImpl());
        GlueSchemaRegistryConfiguration configs = new GlueSchemaRegistryConfiguration("us-east-1");
        configs.setRegistryName("glue-registry-test");
        configs.setSchemaAutoRegistrationEnabled(false);
        this.glueSchemaRegistryConfig = configs;
    }


    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass,
                                         @Nullable Object conversionHint) {

        Object result;
        byte[] payload = (byte[]) message.getPayload();
        //The following lines remove the schema registry header
        GlueSchemaRegistryDeserializerImpl glueSchemaRegistryDeserializer =
            new GlueSchemaRegistryDeserializerImpl(DefaultCredentialsProvider.builder()
                                                                             .build(),
                                                   glueSchemaRegistryConfig);

        byte[] record = glueSchemaRegistryDeserializer.getData(payload);

        com.amazonaws.services.schemaregistry.common.Schema awsSchema;

        Schema avroSchema;
        awsSchema = glueSchemaRegistryDeserializer.getSchema(payload);
        avroSchema = new Schema.Parser().parse(awsSchema.getSchemaDefinition());

        //The following lines serialize an AVRO schema record
        try {
            result =
                avroSchemaServiceManager().readData(targetClass, record, avroSchema, avroSchema);
        } catch (IOException e) {
            throw new MessageConversionException(message, "Failed to read payload", e);
        }
        return result;
    }

    @Override
    protected Object convertToInternal(Object payload, @Nullable MessageHeaders headers,
                                       @Nullable Object conversionHint) {

        byte[] recordAsBytes;
        recordAsBytes = convertRecordToBytes(payload);

        String
            schemaDefinition =
            AVROUtils.getInstance()
                     .getSchemaDefinition(payload);
        String schema_name = //"customer-schema";
            AVROUtils.getInstance().getSchema(payload).getFullName();

        //The following lines add a Schema Header to a record
        com.amazonaws.services.schemaregistry.common.Schema awsSchema;
        awsSchema =
            new com.amazonaws.services.schemaregistry.common.Schema(schemaDefinition,
                                                                    DataFormat.AVRO.name(),
                                                                    schema_name);
        GlueSchemaRegistrySerializerImpl glueSchemaRegistrySerializer =
            new GlueSchemaRegistrySerializerImpl(DefaultCredentialsProvider.builder()
                                                                           .build(),
                                                 glueSchemaRegistryConfig);
        byte[] recordWithSchemaHeader =
            glueSchemaRegistrySerializer.encode(x_amz_meta_trans, awsSchema, recordAsBytes);
        return recordWithSchemaHeader;
    }

    private byte[] convertRecordToBytes(final Object record) throws MessageConversionException {
        ByteArrayOutputStream recordAsBytes = new ByteArrayOutputStream();
        try {
            Encoder
                encoder =
                EncoderFactory.get()
                              .directBinaryEncoder(recordAsBytes, null);
            GenericDatumWriter
                datumWriter =
                new GenericDatumWriter<>(AVROUtils.getInstance()
                                                  .getSchema(record));
            datumWriter.write(record, encoder);
            encoder.flush();
        } catch (IOException e) {
            throw new MessageConversionException("Failed to write payload", e);
        }
        return recordAsBytes.toByteArray();
    }

    @Override
    protected boolean supports(Class<?> aClass) {

        return true;
    }
}

