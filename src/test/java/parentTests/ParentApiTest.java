package parentTests;

import boxAPI.BoxRestSteps;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import resources.Authorisation;

import java.io.IOException;

public class ParentApiTest {

    protected BoxRestSteps boxRestSteps;
    protected int folderID;
    protected String fileName;
    protected String accessToken;
    protected String messageText;
    protected String toggedMessageText;

    @Before
    public void setAuthorisation() throws OperatorCreationException, PKCSException, JoseException, IOException {
        Authorisation authorisation = new Authorisation();
        accessToken = authorisation.getAccessToken();
        boxRestSteps = new BoxRestSteps();
        folderID = 0;
        fileName = "bfoto_ru_2385.jpg";
        messageText = "Test message 1234567890";
        toggedMessageText = "Test message 1234567890 @[8210220790:Vitalii Fedorenko]";
    }

    public void checkExpectedResult(String message, boolean actualResult, boolean expectedResult) {
        Assert.assertEquals(message, expectedResult, actualResult);
    }

    public void checkExpectedResult(String mesage, boolean actualResult) {
        checkExpectedResult(mesage, actualResult, true);
    }
}
