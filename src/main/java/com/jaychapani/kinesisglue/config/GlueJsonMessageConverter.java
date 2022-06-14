package com.jaychapani.kinesisglue.config;

import com.amazonaws.services.schemaregistry.common.GlueSchemaRegistryDataFormatDeserializer;
import com.amazonaws.services.schemaregistry.common.GlueSchemaRegistryDataFormatSerializer;
import com.amazonaws.services.schemaregistry.common.Schema;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializer;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializerFactory;
import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializerImpl;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializer;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializerFactory;
import com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistrySerializerImpl;
import com.amazonaws.services.schemaregistry.serializers.json.JsonDataWithSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.function.context.converter.avro.AvroSchemaServiceManagerImpl;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.glue.model.DataFormat;

public class GlueJsonMessageConverter extends MappingJackson2MessageConverter {


    @Value("custom meta tag")
    String x_amz_meta_trans;

//    @Autowired
//    @Qualifier("custRegistryConfiguration")
    private GlueSchemaRegistryConfiguration glueSchemaRegistryConfig;

    public GlueJsonMessageConverter() {
        super();
        GlueSchemaRegistryConfiguration configs = new GlueSchemaRegistryConfiguration("us-east-1");
        configs.setRegistryName("glue-registry-test");
        this.glueSchemaRegistryConfig = configs;
    }


    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass,
                                         @Nullable Object conversionHint) {

        Object result;
        byte[] payload = (byte[]) message.getPayload();
        //The following lines remove the schema registry header
        GlueSchemaRegistryDeserializer glueSchemaRegistryDeserializer =
            new GlueSchemaRegistryDeserializerImpl(DefaultCredentialsProvider.builder()
                                                                             .build(),
                                                   glueSchemaRegistryConfig);

        GlueSchemaRegistryDataFormatDeserializer gsrDataFormatDeserializer =
            new GlueSchemaRegistryDeserializerFactory().getInstance(DataFormat.JSON, glueSchemaRegistryConfig);

        Schema awsSchema = glueSchemaRegistryDeserializer.getSchema(payload);
        result = gsrDataFormatDeserializer.deserialize(ByteBuffer.wrap(payload), awsSchema);


        return result;
    }

    @Override
    protected Object convertToInternal(Object payload, @Nullable MessageHeaders headers,
                                       @Nullable Object conversionHint) {

        String schemaString = null;
        try {
            //schemaString = new JSONObject(new JSONTokener(new FileReader("src/main/json/customer.json"))).toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

            JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(payload.getClass());

            schemaString = objectMapper.writeValueAsString(jsonSchema);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonDataWithSchema
            record = null;
        try {
            record =
                JsonDataWithSchema.builder(schemaString, (new ObjectMapper()).writeValueAsString(payload)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        GlueSchemaRegistrySerializer glueSchemaRegistrySerializer =
            new GlueSchemaRegistrySerializerImpl(DefaultCredentialsProvider.builder()
                                                                           .build(),
                                                 glueSchemaRegistryConfig);

        GlueSchemaRegistryDataFormatSerializer dataFormatSerializer =
            new GlueSchemaRegistrySerializerFactory().getInstance(DataFormat.JSON, glueSchemaRegistryConfig);

        String schemaDefinition = dataFormatSerializer.getSchemaDefinition(record);
        String schema_name = "test-json"; //"customer-schema";
            //AVROUtils.getInstance().getSchema(payload).getFullName();

        //The following lines add a Schema Header to a record
        Schema awsSchema;
        awsSchema = new Schema(schemaDefinition, DataFormat.JSON.name(),
                                                                    schema_name);

        byte[] serializedBytes = dataFormatSerializer.serialize(record);

        byte[] recordWithSchemaHeader =
            glueSchemaRegistrySerializer.encode(x_amz_meta_trans, awsSchema, serializedBytes);

        return recordWithSchemaHeader;
    }

    @Override
    protected boolean supports(Class<?> aClass) {

        return true;
    }
}

