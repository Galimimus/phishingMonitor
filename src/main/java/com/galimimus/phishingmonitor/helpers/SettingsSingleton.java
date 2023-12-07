package com.galimimus.phishingmonitor.helpers;


import com.galimimus.phishingmonitor.StartApplication;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int HTTP_SERVER_PORT;
    private String HTTP_SERVER_URL_HANDLE;
    private String HTTP_SERVER_EXE_HANDLE;
    private String HTTP_SERVER_DOWNLOAD_HANDLE;
    private String MAIL_SMTP_SERVER;
    private int MAIL_SMTP_PORT;
    private String MINGW_COMMAND;

    private SettingsSingleton() {
        loadSettingsFromYamlFile(String.valueOf(Paths.get("settings.yaml").toAbsolutePath()));
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

            // Чтение данных из YAML файла
            Map<String, String> data = yaml.load(input);

            // Установка значений из YAML файла в соответствующие поля
            if (data != null) {
                WORKING_DIRECTORY = data.get("WORKING_DIRECTORY");
                USER_EMAIL = data.get("USER_EMAIL");
                USER_APP_PASS = data.get("USER_APP_PASS");
                DB_NAME = data.get("DB_NAME");
                DB_HOST = data.get("DB_HOST");
                DB_USERNAME = data.get("DB_USERNAME");
                DB_PASS = data.get("DB_PASS");
                HTTP_SERVER_HOST = data.get("HTTP_SERVER_HOST");
                HTTP_SERVER_PORT = Integer.parseInt(data.get("HTTP_SERVER_PORT"));
                HTTP_SERVER_URL_HANDLE = data.get("HTTP_SERVER_URL_HANDLE");
                HTTP_SERVER_EXE_HANDLE = data.get("HTTP_SERVER_EXE_HANDLE");
                HTTP_SERVER_DOWNLOAD_HANDLE = data.get("HTTP_SERVER_DOWNLOAD_HANDLE");
                MAIL_SMTP_SERVER = data.get("MAIL_SMTP_SERVER");
                MAIL_SMTP_PORT = Integer.parseInt(data.get("MAIL_SMTP_PORT"));
                MINGW_COMMAND = data.get("MINGW_COMMAND");
            }

            input.close();
        } catch (FileNotFoundException e) {
            log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
            throw new RuntimeException(e);
            // Обработка ошибки, если файл не найден
        } catch (Exception e) {
            log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
            throw new RuntimeException(e);
            // Обработка других исключений, если они возникнут при чтении файла
        }
    }
}