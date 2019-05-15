package boxApiTests;

import org.junit.Before;
import org.junit.Test;
import parentTests.ParentApiTest;

import java.io.IOException;



public class CreateCommentAPI extends ParentApiTest {

    @Before
    public void setupFolderAndFileAndDeleteAllComments() throws IOException {
        boxRestSteps.uploadFileIfNotExist(folderID,fileName,accessToken);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);
    }

    @Test
    public void createCommentToFile(){
        boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,messageText));

    }

    @Test
    public void createToggledCommentToFile(){
        boxRestSteps.createToggledCommentToFile(folderID,fileName,accessToken, toggedMessageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,toggedMessageText));

    }

    @Test
    public void createCommentToComment(){
        String commentId = boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);
        String commentToCommentID = boxRestSteps.createCommentToComment(commentId,accessToken,messageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentExist(commentToCommentID,accessToken,messageText));

    }
}
