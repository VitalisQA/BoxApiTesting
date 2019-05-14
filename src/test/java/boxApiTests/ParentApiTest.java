package boxApiTests;

import boxAPI.BoxRestSteps;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import resources.Authorisation;

import java.io.IOException;

public class ParentApiTest {

    BoxRestSteps boxRestSteps;
    int folderID;
    String fileName;
    String accessToken;
    String messageText;

    @Before
    public void setAuthorisation() throws OperatorCreationException, PKCSException, JoseException, IOException {
        Authorisation authorisation = new Authorisation();
        accessToken = authorisation.getAccessToken();
        boxRestSteps = new BoxRestSteps();
        folderID = 0;
        fileName = "bfoto_ru_2385.jpg";
        messageText = "Test message 1234567890";
    }

    public void checkExpectedResult(String message, boolean actualResult, boolean expectedResult) {
        Assert.assertEquals(message, expectedResult, actualResult);
    }

    public void checkExpectedResult(String mesage, boolean actualResult) {
        checkExpectedResult(mesage, actualResult, true);
    }
}
