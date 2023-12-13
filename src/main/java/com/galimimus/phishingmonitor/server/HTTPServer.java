package com.galimimus.phishingmonitor.server;
import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
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
import static com.galimimus.phishingmonitor.logic.Statistic.countEmployeeRaiting;

public class HTTPServer {
    private static boolean IS_ACTIVE = false;
    private static HttpServer server;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());
    static final SettingsSingleton ss = SettingsSingleton.getInstance();

    public static void startHttpServer() {
        try {
            if (!IS_ACTIVE) {
                server = HttpServer.create(new InetSocketAddress(ss.getHTTP_SERVER_HOST(),ss.getHTTP_SERVER_PORT()), 0);
                server.createContext("/"+ss.getHTTP_SERVER_DOWNLOAD_HANDLE(), new DownloadHandler());
                server.createContext("/"+ss.getHTTP_SERVER_EXE_HANDLE(), new MailSecureHandler());
                server.createContext("/"+ss.getHTTP_SERVER_URL_HANDLE(), new Handler());
                server.setExecutor(null); // TODO: Проверить потом на множестве одновременных запросов, возможно запись в бд нужно будет делать не из хэндлера
                //server.setExecutor(new ThreadPoolExecutor(THREADS_AMOUNT, MAX_THREADS_AMOUNT, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY)));
                server.start();
                IS_ACTIVE = true;
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server started");
            } else {
                log.logp(Level.INFO, "HTTPServer", "startHttpServer", "HTTP server is already running");
            }

        } catch (Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "startHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }

/*    public static void stopHttpServer() {//TODO: сделать кнопку для остановки?
        try {
            if (IS_ACTIVE) {
                server.stop(10);
                IS_ACTIVE = false;
                log.logp(Level.INFO, "HTTPServer", "stopHttpServer", "HTTP server stopped");
            }
        } catch (Exception e) {
            log.logp(Level.SEVERE, "HTTPServer", "stopHttpServer", e.toString());
            throw new RuntimeException(e);
        }
    }*/


    static class Handler implements HttpHandler {
        final Logger log = Logger.getLogger(StartApplication.class.getName());

        private String[] handleGetRequest(HttpExchange t) {
            String[] args = new String[2];

            args[0] = t.getRequestURI().toString().split("\\?")[1].split("&")[0].split("=")[1];
            args[1] = t.getRequestURI().toString().split("\\?")[1].split("&")[1].split("=")[1];

            return args;
        }

        @Override
        public void handle(HttpExchange t) {
            try {
                log.logp(Level.INFO, "Handler", "handle", "Handle request = " + t.getRequestURI());
                String[] args = handleGetRequest(t);
                if (args[0] == null || args[1] == null) {
                    log.logp(Level.SEVERE, "Handler", "handle", "Token or mailing id is null." +
                            "token = " + args[0] + " mailing_id = " + args[1]);
                    return;
                }
                String token = java.net.URLDecoder.decode(args[0], StandardCharsets.UTF_16);
                System.out.println("token decoded " + token);

                t.getResponseHeaders().set("Location", "https://habr.com/ru/articles/427995/");
                t.sendResponseHeaders(303, 0);

                String ip = validateToken(token);

                if (ip != null) {
                    DB db = new DB();
                    db.connect();
                    db.logLastMailing(ip, Integer.parseInt(args[1]));
                    db.IncrementTotalUsed(Integer.parseInt(args[1]), ip);
                    db.close();
                    countEmployeeRaiting(ip);
                } else {
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
        public void handle(HttpExchange t) {
            try {
                if (t.getRequestMethod().equalsIgnoreCase("GET")) {
                    log.logp(Level.INFO, "DownloadHandler", "handle", "Handle request = " + t.getRequestURI());

                    String filename = java.net.URLDecoder.decode(t.getRequestURI().toString().split("\\?")[1].split("=")[1], StandardCharsets.UTF_16);
                    SettingsSingleton ss = SettingsSingleton.getInstance();
                    String requestedFile = ss.getWORKING_DIRECTORY()+"/files/" + filename + "\u202etxt.exe";//Document_\u202Excod.exe";

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

    static class MailSecureHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            Path resp = Paths.get("index.html");
            try {
                t.sendResponseHeaders(200, Files.size(resp));
                OutputStream output = t.getResponseBody();
                Files.copy(resp, output);
                output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}