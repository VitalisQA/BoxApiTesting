package boxApiTests;

import org.junit.Before;
import org.junit.Test;
import parentTests.ParentApiTest;

import java.io.IOException;

public class DeleteCommentAPI extends ParentApiTest {

    @Before
    public void setupFolderAndFileAndDeleteAllComments() throws IOException {
        boxRestSteps.uploadFileIfNotExist(folderID,fileName,accessToken);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);
    }

    @Test
    public void deleteCommentToFile(){
        boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);

        checkExpectedResult("Comment was not deleted!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,messageText),false);

    }
}
