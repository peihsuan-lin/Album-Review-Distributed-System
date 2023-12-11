package dao;

import dto.ReviewResponse;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

public interface AlbumDao {
  GetItemResponse getItemById(String id);
  String putNewItem(byte[] imageByteArray, String profileJson);
  ReviewResponse getReviewCountById(String id);

}
