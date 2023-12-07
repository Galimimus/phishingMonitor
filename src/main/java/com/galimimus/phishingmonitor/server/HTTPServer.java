package com.galimimus.phishingmonitor.server;
import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.galimimus.phishingmonitor.helpers.Validation.validateToken;

public class HTTPServer{
    private static boolean IS_ACTIVE = false;
    private static HttpServer server;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());


    public void startHttpServer(){
        try {
            if(!IS_ACTIVE) {
                server = HttpServer.create(new InetSocketAddress(8000), 0);
                server.createContext("/download", new DownloadHandler());
                server.createContext("/loh", new Handler());
                server.setExecutor(null); // TODO: Проверить потом на множестве одновременных запросов, возможно запись в бд нужно будет делать не из хэндлера
                //server.setExecutor(new ThreadPoolExecutor(THREADS_AMOUNT, MAX_THREADS_AMOUNT, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY)));
                server.start();
                IS_ACTIVE = true;
                log.logp(Level.INFO, "HTTPServer","startHttpServer", "HTTP server started");
            }else {
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server is already running");
            }

        }catch(Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "startHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }
    public void stopHttpServer(){
        try {
            if(IS_ACTIVE) {
                server.stop(10);
                IS_ACTIVE = false;
                log.logp(Level.INFO, "HTTPServer", "stopHttpServer", "HTTP server stopped");
            }
        }catch(Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "stopHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }


    static class Handler implements HttpHandler {
        Logger log = Logger.getLogger(StartApplication.class.getName());

        private String[] handleGetRequest(HttpExchange t) {
            String[] args = new String[2];
            args[0] = t.getRequestURI().toString().split("\\?")[1].split("&")[0].split("=")[1];
            args[1] = t.getRequestURI().toString().split("\\?")[1].split("&")[1].split("=")[1];
            return args;
        }
        @Override
        public void handle(HttpExchange t){
            try {
                log.logp(Level.INFO, "Handler", "handle", "Handle request = " + t.getRequestURI());
                String[] args = handleGetRequest(t);
                if(args[0] == null || args[1] == null){
                    log.logp(Level.SEVERE, "Handler", "handle", "Token or mailing id is null." +
                            "token = " + args[0] + " mailing_id = " + args[1]);
                    return;
                }
                String token = java.net.URLDecoder.decode(args[0], StandardCharsets.UTF_8);
                t.getResponseHeaders().set("Location", "https://habr.com/ru/articles/427995/");
                t.sendResponseHeaders(303, 0);

                String ip = validateToken(token);
                if(ip != null) {
                    DB db = new DB();
                    db.connect();
                    db.logLastMailing(ip, Integer.parseInt(args[1]));
                    db.IncrementTotalUsed(Integer.parseInt(args[1]));
                    db.close();
                }else{
                    log.logp(Level.SEVERE, "Handler", "handle", "Token не совпадает ни с одним ip." +
                            "token = " + args[0] + " mailing_id = " + args[1]);
                }
            } catch (IOException e) {
                log.logp(Level.SEVERE, "Handler", "handle", e.toString());
                throw new RuntimeException(e);
            }
        }
    }

    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t){
            try {
                if (t.getRequestMethod().equalsIgnoreCase("GET")) {
                log.logp(Level.INFO, "DownloadHandler", "handle", "Handle request = " + t.getRequestURI());

                String filename = java.net.URLDecoder.decode(t.getRequestURI().toString().split("\\?")[1].split("=")[1], StandardCharsets.UTF_8);

                String requestedFile = "/home/galimimus/IdeaProjects/phishingMonitor/src/main/java/com/galimimus/phishingmonitor/server/files/"+filename;
            //String requestedFile = "/files/";
                Path fileToSend = Paths.get(requestedFile);
                if (Files.exists(fileToSend)) {
                    t.getResponseHeaders().add("Content-Disposition", "attachment; filename=" + fileToSend.getFileName());
                    t.sendResponseHeaders(200, Files.size(fileToSend));
                    OutputStream output = t.getResponseBody();
                    Files.copy(fileToSend, output);
                    output.close();
                    log.logp(Level.INFO, "DownloadHandler", "handle", "File " + filename + " sent");
                } else {
                    log.logp(Level.WARNING, "DownloadHandler", "handle", "File " + filename + " not found");
                    t.sendResponseHeaders(404, 0); // File Not Found
                }
            } else {
                log.logp(Level.WARNING, "DownloadHandler", "handle", "Method not allowed ");
                t.sendResponseHeaders(405, 0); // Method Not Allowed
            }
            t.getResponseBody().close();
            } catch (IOException e) {
                log.logp(Level.SEVERE, "DownloadHandler", "handle", e.toString());
                throw new RuntimeException(e);
            }
        }

    }
}