package com.example.receiptprinting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileLoader {

    private Properties properties;
    private static PropertyFileLoader instance;

    public static PropertyFileLoader getInstance(){
        if(instance == null){
            try{
                instance = new PropertyFileLoader();
            }catch (Exception e){
                throw new RuntimeException("Failed to load property file", e);
            }
        }
        return instance;
    }
    public void loadProperty(String fileName) throws IOException {
        File directoryPath = new File(System.getProperty("user.dir"));
        File propertyFile = new File(directoryPath, fileName+".properties");

        if(!propertyFile.exists()){
            throw new IOException("Configuration file not found: " + propertyFile.getAbsolutePath());
        }

        properties = new Properties();
        try{
            FileInputStream fis = new FileInputStream(propertyFile);
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
