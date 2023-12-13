package com.galimimus.phishingmonitor.helpers;


import com.galimimus.phishingmonitor.StartApplication;
import org.yaml.snakeyaml.Yaml;
import lombok.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;
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
    private SettingsSingleton(){
        loadSettingsFromYamlFile(Paths.get("settings.yaml").toString());
    }

    private static class SingletonHolder {
        public static final SettingsSingleton HOLDER_INSTANCE = new SettingsSingleton();
    }

    public static SettingsSingleton getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
    public void loadSettingsFromYamlFile(String fileName) {
        String home = System.getProperty("user.home");
        if (home == null) {
            home = System.getenv("HOME");
        }
        Path workingDir = Paths.get(home,"PhishingMonitor");
        if(!Files.exists(workingDir)){
            try {
                Files.createDirectory(workingDir);
                Path dropperFilesDir = Paths.get(home, "PhishingMonitor", "dropper_files");
                Files.createDirectory(dropperFilesDir);
                Files.createDirectory(Paths.get(home,"PhishingMonitor", "dropper_files","dropper_outs"));
                Files.createDirectory(Paths.get(home,"PhishingMonitor", "dropper_files", "tmp"));
                Files.createDirectory(Paths.get(home, "PhishingMonitor","files"));
                Files.createDirectory(Paths.get(home, "PhishingMonitor","qrcode"));

                Files.copy(Objects.requireNonNull(StartApplication.class.getResourceAsStream("index.html")),
                        Paths.get(home, "PhishingMonitor","index.html"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Objects.requireNonNull(StartApplication.class.getResourceAsStream("settings.yaml"))
                        , Paths.get(home, "PhishingMonitor","settings.yaml"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Objects.requireNonNull(StartApplication.class.getResourceAsStream("archive-original.exe"))
                        , Paths.get(home, "PhishingMonitor","dropper_files", "archive-original.exe"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Objects.requireNonNull(StartApplication.class.getResourceAsStream("dropper-original.cpp"))
                        , Paths.get(home, "PhishingMonitor","dropper_files", "dropper-original.cpp"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Objects.requireNonNull(StartApplication.class.getResourceAsStream("file.txt"))
                        , Paths.get(home, "PhishingMonitor","dropper_files", "file.txt"), StandardCopyOption.REPLACE_EXISTING);
                Files.createFile(Paths.get(home, "PhishingMonitor", "app_log.txt"));


            } catch (IOException e) {
                log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
                throw new RuntimeException(e);
            }
            try {
                Yaml yaml = new Yaml();
                Path settingsPath = Paths.get(home,"PhishingMonitor","settings.yaml");
                FileInputStream input = new FileInputStream(settingsPath.toAbsolutePath().normalize().toString());
                Map<String, String> data = yaml.load(input);
                data.put("WORKING_DIRECTORY", workingDir.toAbsolutePath().normalize().toString());
                System.out.println(data);
                PrintWriter writer = new PrintWriter(settingsPath.toAbsolutePath().normalize().toFile());
                yaml.dump(data, writer);
            }catch (FileNotFoundException e){
                log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
                throw new RuntimeException(e);
            }
        }


        try {
            Yaml yaml = new Yaml();
            FileInputStream input = new FileInputStream(Paths.get(home,"PhishingMonitor",fileName).toString());
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
                        "loadSettingsFromYamlFile", "File settings.yaml not found. path = " + fileName);
                System.exit(404);
            }

            input.close();
        } catch (Exception e) {
            log.logp(Level.SEVERE, "SettingsSingleton", "loadSettingsFromYamlFile", e.toString());
            throw new RuntimeException(e);
        }
    }
}