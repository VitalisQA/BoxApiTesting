package boxAPI;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;

import java.io.File;

import static io.restassured.RestAssured.given;

class BoxApi {

    public ResponseBody getFolderItems(int folderId, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)    // Header
                .when()
                .get("https://api.box.com/2.0/folders/" + folderId + "/items")
                .then()
                .statusCode(200)
                .extract().response().getBody();
    }

    public ResponseBody uploadFile(String accessToken, JSONObject attributes) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("uploadFile", ".//src//main//java//libs//bfoto_ru_2385.jpg", "image/png")
                .multiPart("attributes", attributes)
                .when()
                .post("https://upload.box.com/api/2.0/files/content")
                .then()
                .extract()
                .response().getBody();
    }

    public ResponseBody getFileComments(String fileId, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)    // Header
                .when()
                .get("https://api.box.com/2.0/files/" + fileId + "/comments")
                .then()
                .statusCode(200)
                .extract().response().getBody();
    }

    public void deleteAComment(String commentId, String accessToken) {
        given()
                .header("Authorization", "Bearer " + accessToken)    // Header
                .when()
                .delete("https://api.box.com/2.0/comments/" + commentId)
                .then()
                .statusCode(204);
    }

    public void createCommentToAFile(String accessToken, JSONObject requestParams) {
        given()
                .contentType(ContentType.JSON)
                .body(requestParams.toMap())
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("https://api.box.com/2.0/comments")
                .then()
                .statusCode(201);
    }
}
