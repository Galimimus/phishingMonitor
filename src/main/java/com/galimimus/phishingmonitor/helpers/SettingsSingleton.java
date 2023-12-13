package com.galimimus.phishingmonitor.helpers;


import com.galimimus.phishingmonitor.StartApplication;
import org.yaml.snakeyaml.Yaml;
import lombok.*;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
@Getter
public class SettingsSingleton {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());
    private String WORKING_DIRECTORY;
    private String USER_EMAIL;
    private String USER_APP_PASS;
    private String DB_NAME;
    private String DB_HOST;
    private String DB_USERNAME;
    private String DB_PASS;
    private String HTTP_SERVER_HOST;
    private Integer HTTP_SERVER_PORT;
    private String HTTP_SERVER_URL_HANDLE;
    private String HTTP_SERVER_EXE_HANDLE;
    private String HTTP_SERVER_DOWNLOAD_HANDLE;
    private String MAIL_SMTP_SERVER;
    private Integer MAIL_SMTP_PORT;
    private String MINGW_COMMAND;
    private String RAR_COMMAND;
    private String CP_COMMAND;


    private SettingsSingleton() {
        loadSettingsFromYamlFile(Paths.get("settings.yaml").toAbsolutePath().normalize().toString());
    }

    private static class SingletonHolder {
        public static final SettingsSingleton HOLDER_INSTANCE = new SettingsSingleton();
    }

    public static SettingsSingleton getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
    public void loadSettingsFromYamlFile(String filePath) {
        try {
            Yaml yaml = new Yaml();
            FileInputStream input = new FileInputStream(filePath);
            Map<String, Object> data = yaml.load(input);
            if (data != null) {
                WORKING_DIRECTORY = (String) data.get("WORKING_DIRECTORY");
                USER_EMAIL = (String) data.get("USER_EMAIL");
                USER_APP_PASS = (String)data.get("USER_APP_PASS");
                DB_NAME = (String)data.get("DB_NAME");
                DB_HOST = (String)data.get("DB_HOST");
                DB_USERNAME = (String)data.get("DB_USERNAME");
                DB_PASS =(String) data.get("DB_PASS");
                HTTP_SERVER_HOST = (String)data.get("HTTP_SERVER_HOST");
                HTTP_SERVER_PORT = (Integer) data.get("HTTP_SERVER_PORT");
                HTTP_SERVER_URL_HANDLE = (String)data.get("HTTP_SERVER_URL_HANDLE");
                HTTP_SERVER_EXE_HANDLE = (String)data.get("HTTP_SERVER_EXE_HANDLE");
                HTTP_SERVER_DOWNLOAD_HANDLE = (String)data.get("HTTP_SERVER_DOWNLOAD_HANDLE");
                MAIL_SMTP_SERVER = (String)data.get("MAIL_SMTP_SERVER");
                MAIL_SMTP_PORT = (Integer) data.get("MAIL_SMTP_PORT");
                MINGW_COMMAND = (String)data.get("MINGW_COMMAND");
                RAR_COMMAND = (String)data.get("RAR_COMMAND");
                CP_COMMAND = (String)data.get("CP_COMMAND");
            }else {
                log.logp(Level.WARNING, "SettingsSingleton",
                        "loadSettingsFromYamlFile", "File settings.yaml not found. path = " + filePath);
                System.exit(404);
            }

            input.close();
        } catch (Exception e) {
            log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
            throw new RuntimeException(e);
        }
    }
}