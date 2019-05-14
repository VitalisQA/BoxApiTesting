package boxApiTests;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CreateCommentAPI extends ParentApiTest{

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
    public void deleteCommentToFile(){
        boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,messageText),false);

    }

}
