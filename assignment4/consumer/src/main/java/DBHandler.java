import connections.DynamoConn;
import java.util.HashMap;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class DBHandler {

  private DynamoDbClient dynamoConn;

  public DBHandler() {
    this.dynamoConn = DynamoConn.getDbClient();
  }

  void storeReviewInDb(ReviewMessage message) {
    // get json message from Kafka
    String id = message.getAlbumID();
    String likeOrNot = message.getLikeOrNot();
    String tableName = "like".equals(likeOrNot) ? "LikeTable" : "DislikeTable";

    UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
        .tableName(tableName)
        .key(Map.of("id", AttributeValue.builder().s(id).build()))
        .updateExpression("SET review_count = if_not_exists(review_count, :start) + :inc")
        .expressionAttributeValues(Map.of(
            ":inc", AttributeValue.builder().n("1").build(),
            ":start", AttributeValue.builder().n("1").build()
        ))

        .build();
    dynamoConn.updateItem(updateItemRequest);
  }
}
