package BoxApiTesting;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class Authorisation {

    public String getAccessToken() throws IOException, OperatorCreationException, PKCSException, JoseException {
        // Create a file reader
        FileReader reader = new FileReader(".//src//main//java//libs//config.json");

        // Use the powerful GSON library (github.com/google/gson)
        // to covert the string into a Config object
        Gson gson = new GsonBuilder().create();
        Config config = (Config) gson.fromJson(reader, Config.class);

        // We use BouncyCastle to handle the decryption
        // (https://www.bouncycastle.org/java.html)
        Security.addProvider(new BouncyCastleProvider());

// Using BouncyCastle's PEMParser we convert the
// encrypted private key into a keypair object
        PEMParser pemParser = new PEMParser(
                new StringReader(config.boxAppSettings.appAuth.privateKey)
        );
        Object keyPair = pemParser.readObject();
        pemParser.close();

// Finally, we decrypt the key using the passphrase
        char[] passphrase = config.boxAppSettings.appAuth.passphrase.toCharArray();
        JceOpenSSLPKCS8DecryptorProviderBuilder decryptBuilder =
                new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider("BC");
        InputDecryptorProvider decryptProvider
                = decryptBuilder.build(passphrase);
        PrivateKeyInfo keyInfo
                = ((PKCS8EncryptedPrivateKeyInfo) keyPair).decryptPrivateKeyInfo(decryptProvider);

// In the end, we will use this key in the next steps
        PrivateKey key = (new JcaPEMKeyConverter()).getPrivateKey(keyInfo);



// We will need the authenticationUrl  again later,
// so it is handy to define here
        String authenticationUrl = "https://api.box.com/oauth2/token";

// Rather than constructing the JWT assertion manually, we are
// using the org.jose4j.jwt library.
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(config.boxAppSettings.clientID);
        claims.setAudience(authenticationUrl);
        claims.setSubject(config.enterpriseID);
        claims.setClaim("box_sub_type", "enterprise");
// This is an identifier that helps protect against
// replay attacks
        claims.setGeneratedJwtId(64);
// We give the assertion a lifetime of 45 seconds
// before it expires
        claims.setExpirationTimeMinutesInTheFuture(0.75f);


        // With the claims in place, it's time to sign the assertion
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key);
// The API support "RS256", "RS384", and "RS512" encryption
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
        jws.setHeader("typ", "JWT");
        jws.setHeader("kid", config.boxAppSettings.appAuth.publicKeyID);
        String assertion = jws.getCompactSerialization();


        // We are using the excellent org.apache.http package
        // to simplify the API call

// Create the params for the request
        List<NameValuePair> params = new ArrayList<NameValuePair>();
// This specifies that we are using a JWT assertion
// to authenticate
        params.add(new BasicNameValuePair(
                "grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
// Our JWT assertion
        params.add(new BasicNameValuePair(
                "assertion", assertion));
// The OAuth 2 client ID and secret
        params.add(new BasicNameValuePair(
                "client_id", config.boxAppSettings.clientID));
        params.add(new BasicNameValuePair(
                "client_secret", config.boxAppSettings.clientSecret));

// Make the POST call to the authentication endpoint
        CloseableHttpClient httpClient =
                HttpClientBuilder.create().disableCookieManagement().build();
        HttpPost request = new HttpPost(authenticationUrl);
        request.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String response = EntityUtils.toString(entity);
        httpClient.close();

// Parse the JSON using Gson to a Token object
        class Token {
            String access_token;
        }

        Token token = (Token) gson.fromJson(response, Token.class);
        String accessToken = token.access_token;
        return accessToken;
    }

}
