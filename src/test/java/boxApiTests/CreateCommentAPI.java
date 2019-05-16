package boxApiTests;

import org.junit.Before;
import org.junit.Test;
import parentTests.ParentApiTest;

import java.io.IOException;



public class CreateCommentAPI extends ParentApiTest {

    /**
     * Preconditions:
     * Перед выполнением каждого теста
     * 1. Проверить есть ли файл, для добавления комментария к нему, если нет, то загрузить файл в корневую папку
     * 2. Проверить есть ли комментарии к файлу. Если есть, удалить все комментарии
     **/

    @Before
    public void setupFolderAndFileAndDeleteAllComments() throws IOException {
        boxRestSteps.uploadFileIfNotExist(folderID,fileName,accessToken);
        boxRestSteps.deleteAnyCommentToFileIfExist(folderID,fileName,accessToken);
    }

    /**
     * Тест-кейс1: Создать комментарий к файлу с обычным сообщением (message)
     *
     * Шаги:
     * 1. Создать комментарий к файлу через Box_API
     *
     * Ожидаемый результат:
     * Комментарий к файлу c обычным сообщением создан
     */

    @Test
    public void createCommentToFile(){
        boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,messageText));

    }


    /**
     * Тест-кейс2: Создать комментарий к файлу с toggled сообщением (toggled_message)
     *
     * Шаги:
     * 1. Создать комментарий с toggled сообщением к файлу через Box_API
     *
     * Ожидаемый результат:
     * Комментарий с toggled сообщением к файлу создан
     */
    @Test
    public void createToggledCommentToFile(){
        boxRestSteps.createToggledCommentToFile(folderID,fileName,accessToken, toggedMessageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentToFileWithMessageTextGivenExist(folderID,fileName,accessToken,toggedMessageText));

    }
    /**
     * Тест-кейс3: Создать комментарий к комментарию с обычным сообщением (message)
     *
     * Шаги:
     * 1. Создать комментарий к файлу через Box_API
     * 2. Создать комментарий к созданному комментарию Box_API
     *
     * Ожидаемый результат:
     * Комментарий к комментарию c обычным сообщением создан
     */

    @Test
    public void createCommentToComment(){
        String commentId = boxRestSteps.createCommentToFile(folderID,fileName,accessToken,messageText);
        String commentToCommentID = boxRestSteps.createCommentToComment(commentId,accessToken,messageText);

        checkExpectedResult("Comment was not created!",
                boxRestSteps.isCommentExist(commentToCommentID,accessToken,messageText));

    }
}
