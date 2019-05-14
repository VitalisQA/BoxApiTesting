package resources;

// Define a class that we can parse
// the json into
class Config {
    class BoxAppSettings {
        class AppAuth {
            String privateKey;
            String passphrase;
            String publicKeyID;
        }

        String clientID;
        String clientSecret;
        AppAuth appAuth;
    }

    BoxAppSettings boxAppSettings;
    String enterpriseID;
}
