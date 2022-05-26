# kinesis-glue-test

### Build and Start

Run the following commands to start the application

```
1.  mvn clean install
2.  java -jar target/kinesis-glue*.jar 
```

Sample `CURL` to test the application

```
curl --location --request POST 'http://localhost:8080/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": 319,
    "firstName": "Albin",
    "lastName": "Murray",
    "address": "20650 Ramiro Corners"
}'
```

AWS Setup

* Create a gule schema registry with name `glue-registry-test` and schema with name `customer-schema`
* Use the following as schema version definition
```
{
  "namespace": "com.jaychapani.kinesisglue.model",
  "type": "record",
  "name": "Customer",
  "fields": [
    {
      "name": "id",
      "type": [
        "int",
        "null"
      ]
    },
    {
      "name": "firstName",
      "type": "string"
    },
    {
      "name": "lastName",
      "type": "string"
    },
    {
      "name": "address",
      "type": "string"
    }
  ]
}
```