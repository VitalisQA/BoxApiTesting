package BoxApiTesting;

import static org.junit.Assert.assertTrue;

import com.box.sdk.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        BoxAPIConnection api = new BoxAPIConnection("zOkBdE36hMtef28cKO9BDQNBB22hfm1M");
        String folderID = null;
        String fileID = null;
        BoxFolder rootFolder = BoxFolder.getRootFolder(api);
        for (BoxItem.Info itemInfo : rootFolder) {
            System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());
            if (itemInfo.getName().equals("Vit")){
                folderID=itemInfo.getID();
            }
        }
        if (folderID==null){
            BoxFolder.Info childFolderInfo = rootFolder.createFolder("Vit");
            folderID = childFolderInfo.getID();

            BoxFolder vit = new BoxFolder(api,folderID);
            FileInputStream stream = new FileInputStream("bfoto_ru_2385.jpg");
            BoxFile.Info newFileInfo = vit.uploadFile(stream, "bfoto_ru_2385.jpg");
            stream.close();

            fileID = newFileInfo.getID();

        }else {
            BoxFolder vitFolder = new BoxFolder(api,folderID);
            for (BoxItem.Info itemInfo: vitFolder) {
                System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());

                if (itemInfo.getName().equals("Mbfoto_ru_2385.jpg")){
                    fileID = itemInfo.getID();
                }
            }
            if (fileID==null){
                FileInputStream stream = new FileInputStream(".//src//main//java//libs//bfoto_ru_2385.jpg");
                BoxFile.Info newFileInfo = vitFolder.uploadFile(stream, "bfoto_ru_2385.jpg");
                stream.close();
                fileID = newFileInfo.getID();
            }
        }

        BoxFile file = new BoxFile(api, fileID);
        String commentID = file.addComment("Test comment").getID();

        assertTrue("Comment was not created :( ", isCommentExist(commentID));
    }

    private boolean isCommentExist(String commentID) {
        BoxAPIConnection api = new BoxAPIConnection("zOkBdE36hMtef28cKO9BDQNBB22hfm1M");
        BoxComment comment = new BoxComment(api, commentID);
        BoxComment.Info info = comment.getInfo();
        return (info.getMessage().equals("Test comment"));
        }
    }



