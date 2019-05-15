package boxAPI;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import io.restassured.response.ResponseBody;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class BoxRestSteps {

    private Logger logger = Logger.getLogger(getClass());
    private BoxApi boxApi = new BoxApi();

    public void uploadFileIfNotExist(int folderId, String fileName, String accessToken) throws IOException {

        String fileId = getFileIdRest(folderId, fileName, accessToken);
        if (fileId.equals("")) {
            logger.info("File " + fileName + " doesn't exist in the parent folder");

            BoxFile.Info fileInfo = uploadFileRest(folderId, fileName, accessToken);
            logger.info("File " + fileName + " was uploaded to the parent folder");
        }

    }

    public String getFileIdRest(int folderId, String fileName, String accessToken) {
        String fileId = "";
        ResponseBody folderItems = getFolderItemsRest(folderId, accessToken);
        List<Map<String, Object>> entries = folderItems.jsonPath().getList("entries");

        for (Map<String, Object> map : entries) {
            if (map.containsKey("type") && map.get("type").equals("file") && map.get("name").equals(fileName)) {
                fileId = (String) map.get("id");
//                logger.info("File " + fileName + " exist in the folder with ID: " + folderId + ". File ID: " + fileId);
            }
        }
        if (!fileId.equals("")) {
            return fileId;
        } else {
//            logger.info("File " + fileName + " doesn't exist in the folder whith ID: "+folderId);
            return fileId;
        }
    }

    public ResponseBody getFolderItemsRest(int folderId, String accessToken) {
        return boxApi.getFolderItems(folderId, accessToken);
    }


    public BoxFile.Info uploadFileRest(int folderId, String fileName, String accessToken) throws IOException {

        String filePath = ".//src//main//java//libs//bfoto_ru_2385.jpg";
// Select Box folder
        BoxFolder folder = new BoxFolder(new BoxAPIConnection(accessToken), String.valueOf(folderId));
// Upload file
        FileInputStream stream = new FileInputStream(filePath);
        BoxFile.Info newFileInfo = folder.uploadFile(stream, fileName);
        stream.close();
        return newFileInfo;
//        JSONObject parent = new JSONObject();
//        JSONObject requestParams = new JSONObject();
//
//        parent.put("id", folderId);
//        requestParams.put("name","bfoto_ru_2385.jpg")
//                .put("parent", parent);
//
//        return boxApi.uploadFile(accessToken,requestParams);
    }

    public void deleteAnyCommentToFileIfExist(int folderId, String fileName, String accessToken) {
        ResponseBody folderItems = getFolderItemsRest(folderId, accessToken);
        List<Map<String, Object>> entries = folderItems.jsonPath().getList("entries");

        String fileId = "";
        List<BigInteger> commentsId = new ArrayList<>();

        for (Map<String, Object> map : entries) {
            if (map.containsKey("type") && map.get("type").equals("file") && map.get("name").equals(fileName)) {
                fileId = (String) map.get("id");
                logger.info("File " + fileName + " exist in the folder with ID: " + folderId + ". File ID: " + fileId);
            }
        }
        if (fileId.equals("")) {
            logger.info("File " + fileName + " doesn't exist in folder whith folderID: " + folderId);
        } else {
            ResponseBody commentsItems = getFileCommentsRest(fileId, accessToken);
            List<Map<String, Object>> commentsEntries = commentsItems.jsonPath().getList("entries");
            for (Map<String, Object> map : commentsEntries) {
                if (map.containsKey("type") && map.get("type").equals("comment")) {

                    commentsId.add(new BigInteger((String) map.get("id")));
                    logger.info("Comment with id " + map.get("id") + " to file " + fileName + " exists in the folder with ID: " + folderId);
                }
            }
            if (!commentsId.isEmpty()) {
                Collections.sort(commentsId);
                Collections.reverse(commentsId);

                for (BigInteger commentId : commentsId) {
                    deleteACommentRest(String.valueOf(commentId), accessToken);
                }
            }
        }
    }

    private void deleteACommentRest(String commentId, String accessToken) {
        boxApi.deleteAComment(commentId, accessToken);
        logger.info("Comment with id " + commentId + " deleted");
    }

    private ResponseBody getFileCommentsRest(String fileId, String accessToken) {
        return boxApi.getFileComments(fileId, accessToken);
    }

    public String createCommentToFile(int folderID, String fileName, String accessToken, String messageText) {

        String fileId = getFileIdRest(folderID, fileName, accessToken);
        if (fileId.equals("")) {
            logger.info("File " + fileName + " doesn't exist in the parent folder");
            return "";
        } else {
            JSONObject item = new JSONObject();
            JSONObject requestParams = new JSONObject();

            item.put("type", "file")
                    .put("id", fileId);
            requestParams.put("item", item)
                    .put("message", messageText);
            ResponseBody commentsItems = boxApi.createComment(accessToken, requestParams);
            Map<String, Object> commentsEntry = commentsItems.jsonPath().get();
            if (commentsEntry.containsKey("type") && commentsEntry.get("type").equals("comment")) {
                logger.info("Comment to file created");
                return ((String) commentsEntry.get("id"));
            } else {
                return "";
            }
        }
    }

    public String createToggledCommentToFile(int folderID, String fileName, String accessToken, String toggledMessageText) {

        String fileId = getFileIdRest(folderID, fileName, accessToken);
        if (fileId.equals("")) {
            logger.info("File " + fileName + " doesn't exist in the parent folder");
            return "";
        } else {
            JSONObject item = new JSONObject();
            JSONObject requestParams = new JSONObject();

            item.put("type", "file")
                    .put("id", fileId);
            requestParams.put("item", item)
                    .put("tagged_message", toggledMessageText);
            ResponseBody commentsItems = boxApi.createComment(accessToken, requestParams);
            Map<String, Object> commentsEntry = commentsItems.jsonPath().get();
            if (commentsEntry.containsKey("type") && commentsEntry.get("type").equals("comment")) {
                logger.info("Comment to file created");
                return ((String) commentsEntry.get("id"));
            } else {
                return "";
            }
        }
    }

    public boolean isCommentToFileWithMessageTextGivenExist(int folderID, String fileName, String accessToken, String messageText) {
        String fileId = getFileIdRest(folderID, fileName, accessToken);
        List<String> commentsId = new ArrayList<>();

        if (!fileId.equals("")) {
            ResponseBody commentsInfo = getFileCommentsRest(fileId, accessToken);
            List<Map<String, Object>> commentsEntries = commentsInfo.jsonPath().getList("entries");
            for (Map<String, Object> map : commentsEntries) {
                if (map.containsKey("type") && map.get("type").equals("comment") && map.get("message").equals(messageText)) {
                    commentsId.add((String) map.get("id"));
                }

            }
            return !commentsEntries.isEmpty();

        } else {
            return false;
        }
    }

    public String createCommentToComment(String commentId, String accessToken, String messageText) {
        JSONObject item = new JSONObject();
        JSONObject requestParams = new JSONObject();

        item.put("type", "comment")
                .put("id", commentId);
        requestParams.put("item", item)
                .put("message", messageText);
        ResponseBody commentsItems = boxApi.createComment(accessToken, requestParams);
        Map<String, Object> commentsEntry = commentsItems.jsonPath().get();
        if (commentsEntry.containsKey("type") && commentsEntry.get("type").equals("comment")) {
            logger.info("Comment to comment was created");
            return ((String) commentsEntry.get("id"));
        } else {
            logger.info("Comment to comment was not created");
            return "";
        }
    }

    public boolean isCommentExist(String commentToCommentID, String accessToken, String messageText) {
        if (!commentToCommentID.equals("")) {
            ResponseBody commentInfo = boxApi.getComment(commentToCommentID, accessToken);
            Map<String, Object> map = commentInfo.jsonPath().get();
            if (map.containsKey("type") && map.get("type").equals("comment")
                    && map.get("id").equals(commentToCommentID) && map.get("message").equals(messageText)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
