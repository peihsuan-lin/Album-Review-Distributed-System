package dao;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class DynamoManager {
  private static final DynamoDbClient db = DynamoDbClient.builder()
      .region(Region.US_WEST_2)
      .build();

  public static void main(String[] args) {
//    deleteTable("AlbumsTable");
//    deleteTable("DislikeTable");
//    deleteTable("LikeTable");
    createTable("AlbumsTable");
    createTable("DislikeTable");
    createTable("LikeTable");
  }
  public static void describeTableAndItemCount(String tableName) {
    DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
        .tableName(tableName)
        .build();
    DescribeTableResponse describeTableResponse = db.describeTable(describeTableRequest);
    Long itemCount = describeTableResponse.table().itemCount();
    System.out.println("Item count: " + itemCount);
  }
  public static DynamoDbClient getDbClient() {
    return db;
  }
  public static void deleteTable(String tableName) {
    DeleteTableRequest request = DeleteTableRequest.builder()
        .tableName(tableName)
        .build();
    db.deleteTable(request);
    System.out.println("Table deleted: " + tableName);
  }
  public static void createTable(String tableName) {
    List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
    keySchema.add(KeySchemaElement.builder()
        .attributeName("id")
        .keyType("HASH")
        .build());
    List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
    attributeDefinitions.add(AttributeDefinition.builder()
        .attributeName("id")
        .attributeType(ScalarAttributeType.S)
        .build());

    CreateTableRequest request = CreateTableRequest.builder()
        .tableName(tableName)
        .keySchema(keySchema)
        .attributeDefinitions(attributeDefinitions)
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build();
    CreateTableResponse table = db.createTable(request);
    System.out.println(table);

  }
}
