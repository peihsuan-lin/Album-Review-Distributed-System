package dao;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;

public class DynamoManager {
  private static final DynamoDbClient db = DynamoDbClient.builder()
      .region(Region.US_WEST_2)
      .build();

  public static void main(String[] args) {
    deleteTable("AlbumsTable");
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
}
