import connections.DynamoConn;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DBHandler {

  private DynamoDbClient dynamoConn;

  public DBHandler() {
    this.dynamoConn = DynamoConn.getDbClient();
  }

  void storeReviewInDb(ReviewMessage message) {
    // get the json message from RMQ
    String likeOrNot = message.getLikeOrNot();
    System.out.println("albumID: " + message.getAlbumID() + "; likeOrNot: " + likeOrNot);
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("id", AttributeValue.builder().s(message.getAlbumID()).build());
    if (likeOrNot.equals("dislike")) {
      item.put("likeOrNot", AttributeValue.builder().s("dislike").build());
      PutItemRequest putItemRequest = PutItemRequest.builder()
          .tableName("DislikeTable")
          .item(item)
          .build();
      dynamoConn.putItem(putItemRequest);
    } else if (likeOrNot.equals("like")) {
      item.put("likeOrNot", AttributeValue.builder().s("like").build());
      PutItemRequest putItemRequest = PutItemRequest.builder()
          .tableName("LikeTable")
          .item(item)
          .build();
      dynamoConn.putItem(putItemRequest);
    }
  }
}
