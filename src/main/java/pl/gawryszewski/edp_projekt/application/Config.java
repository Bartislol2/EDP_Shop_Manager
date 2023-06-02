package pl.gawryszewski.edp_projekt.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config instance;
    private String dbUrl;
    private String clientId;
    private String clientSecret;
    private String dbUserName;
    private String dbPassword;
    private Config(){
        try(InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")){
            Properties properties = new Properties();
            properties.load(inputStream);
            dbUrl = properties.getProperty("db.url");
            dbUserName = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            clientId = properties.getProperty("allegro.client.id");
            clientSecret = properties.getProperty("allegro.client.secret");
        } catch (IOException e){
            System.out.println("Could not get properties");
        }
    }

    public static Config getInstance() {
        if(instance == null){
            synchronized (Config.class){
                if(instance == null){
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
