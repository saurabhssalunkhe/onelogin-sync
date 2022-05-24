package com.icahn;

import java.io.IOException;
import java.util.Properties;

public class Config {

    private static Config instance = null;
    private Properties properties;
   


    protected Config() throws IOException{

        properties = new Properties();
        properties.load(getClass().getResourceAsStream("../../config.properties"));

    }

    public static Config getInstance() {
        if(instance == null) {
            try {
                instance = new Config();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

}