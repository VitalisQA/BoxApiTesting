package BoxApiTesting;

import static org.junit.Assert.assertTrue;

import com.box.sdk.*;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class CreateComment {



    @Test
    public void createCommentToFile() throws IOException, JoseException, OperatorCreationException, PKCSException {
        Authorisation authorisation = new Authorisation();
        String accessToken = authorisation.getAccessToken();

        BoxAPIConnection api = new BoxAPIConnection(accessToken);
        String folderID = "";
        String fileID = "";
        BoxFolder rootFolder = BoxFolder.getRootFolder(api);
        for (BoxItem.Info itemInfo : rootFolder) {
            System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());
            if (itemInfo.getName().equals("Vit")) {
                folderID = itemInfo.getID();
            }
        }
        if (folderID.equals("")) {
            BoxFolder.Info childFolderInfo = rootFolder.createFolder("Vit");
            folderID = childFolderInfo.getID();

            BoxFolder vit = new BoxFolder(api, folderID);
            FileInputStream stream = new FileInputStream(".//src//main//java//libs//bfoto_ru_2385.jpg");
            BoxFile.Info newFileInfo = vit.uploadFile(stream, "bfoto_ru_2385.jpg");
            stream.close();

            fileID = newFileInfo.getID();

        } else {
            BoxFolder vitFolder = new BoxFolder(api, folderID);
            for (BoxItem.Info itemInfo : vitFolder) {
                if (itemInfo instanceof BoxFile.Info) {
                    BoxFile.Info fileInfo = (BoxFile.Info) itemInfo;
                    // Do something with the file.
                    if (fileInfo.getName().equals("bfoto_ru_2385.jpg")){
                        fileID = fileInfo.getID();
                    }

                } else if (itemInfo instanceof BoxFolder.Info) {
                    BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
                    // Do something with the folder.
                }
            }




//            BoxFolder vitFolder = new BoxFolder(api, folderID);
//            for (BoxItem.Info itemInfo : vitFolder) {
//                System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());
//                if (itemInfo.getName().equals("Mbfoto_ru_2385.jpg")) {
//                    System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());
//                    fileID = itemInfo.getID();
//                }
//            }
        }
        if (fileID.equals("")) {
            BoxFolder vitFolder = new BoxFolder(api, folderID);
            FileInputStream stream = new FileInputStream(".//src//main//java//libs//bfoto_ru_2385.jpg");
            BoxFile.Info newFileInfo = vitFolder.uploadFile(stream, "bfoto_ru_2385.jpg");
            stream.close();
            fileID = newFileInfo.getID();
        }

        BoxFile file = new BoxFile(api, fileID);
        String commentID = file.addComment("Test comment").getID();

        assertTrue("Comment was not created :( ", isCommentExist(commentID));

        BoxComment comment = new BoxComment(api, commentID);
        comment.delete();
    }

    private boolean isCommentExist(String commentID) throws OperatorCreationException, PKCSException, JoseException, IOException {
        Authorisation authorisation = new Authorisation();
        String accessToken = authorisation.getAccessToken();
        BoxAPIConnection api = new BoxAPIConnection(accessToken);
        BoxComment comment = new BoxComment(api, commentID);
        BoxComment.Info info = comment.getInfo();
        return (info.getMessage().equals("Test comment"));
    }
}



