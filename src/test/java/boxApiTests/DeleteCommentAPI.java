package boxApiTests;

import org.junit.Before;
import org.junit.Test;
import parentTests.ParentApiTest;

import java.io.IOException;

public class DeleteCommentAPI extends ParentApiTest {

    /**
     * Preconditions:
     * Перед выполнением теста
     * 1. Проверить есть ли файл, для добавления комментария к нему, если нет, то загрузить файл в корневую папку
     * 2. Проверить есть ли комментарии к файлу. Если есть, удалить все комментарии
     **/

    @Before
    public void setupFolderAndFileAndDeleteAllComments() throws IOException {
        boxRestSteps.uploadFileIfNotExist(folderID,fileName,accessToken);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);
    }
    /**
     * Тест-кейс4: Удалить комментарий к файлу.
     *
     * Шаги:
     * 1. Создать комментарий к файлу через Box_API
     * 2. Удалить созданный комментарий к файлу через Box_API
     * 3. Проверить наличие созданного в 1 шаге комментария
     *
     * Ожидаемый результат:
     * Созданный комментарий к файлу удален.
     */

    @Test
    public void deleteCommentToFile(){
        boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);

        checkExpectedResult("Comment was not deleted!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,messageText),false);

    }
}
