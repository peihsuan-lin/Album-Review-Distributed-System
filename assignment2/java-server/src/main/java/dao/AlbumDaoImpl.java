package dao;

import com.google.gson.Gson;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class AlbumDaoImpl implements AlbumDao {
  private DynamoDbClient dynamoDbClient;

  public AlbumDaoImpl() {
    this.dynamoDbClient = DynamoManager.getDbClient();
  }
  @Override
  public GetItemResponse getItemById(String id) {
    HashMap<String, AttributeValue> keyToGet = new HashMap<>();
    keyToGet.put("id", AttributeValue.builder().s(id).build());

    GetItemRequest request = GetItemRequest.builder()
        .key(keyToGet)
        .tableName("AlbumsTable")
        .build();

    return dynamoDbClient.getItem(request);
  }

  @Override
  public String putNewItem(byte[] imageByteArray, String profileJson) {
    Map<String, AttributeValue> item = new HashMap<>();
    String generatedUUID = UUID.randomUUID().toString();
    item.put("id", AttributeValue.builder().s(generatedUUID).build());
    item.put("imageFile",
        AttributeValue.builder().b(SdkBytes.fromByteArray(imageByteArray)).build());
    Gson gson = new Gson();
    String profileJsonString = gson.toJson(profileJson);
    item.put("profile", AttributeValue.builder().s(profileJsonString).build());
    PutItemRequest putItemRequest = PutItemRequest.builder()
        .tableName("AlbumsTable")
        .item(item)
        .build();
    dynamoDbClient.putItem(putItemRequest);
    return generatedUUID;
  }
}
