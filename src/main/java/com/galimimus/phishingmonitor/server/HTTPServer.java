package com.galimimus.phishingmonitor.server;
import com.galimimus.phishingmonitor.helpers.DB;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.galimimus.phishingmonitor.helpers.Validation.validatePattern;
import static com.galimimus.phishingmonitor.helpers.Validation.validateToken;

public class HTTPServer{

    public void startHttpServer(){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", new Handler());
            server.setExecutor(null); // TODO: Проверить потом на множестве одновременных запросов, возможно запись в бд нужно будет делать не из хэндлера
            //server.setExecutor(new ThreadPoolExecutor(THREADS_AMOUNT, MAX_THREADS_AMOUNT, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY)));
            server.start();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    static class Handler implements HttpHandler {
        private String handleGetRequest(HttpExchange httpExchange) {
            return httpExchange.
                    getRequestURI()
                    .toString()
                    .split("\\?")[1]
                    .split("=")[1];
        }
        @Override
        public void handle(HttpExchange t){
            try {

                String token = java.net.URLDecoder.decode(handleGetRequest(t), StandardCharsets.UTF_8);
                //(String) t.getAttribute("token");//TODO: Validate ip
                if(token == null){
                    System.out.println("token is null");
                    return;
                }
                t.getResponseHeaders().set("Location", "https://habr.com/ru/articles/427995/");
                t.sendResponseHeaders(303, 0);

                String ip = validateToken(token);
                if(ip != null) {

                    DB db = new DB();
                    db.connect();
                    db.logIP(ip);
                    db.close();

                    System.out.println("logged ip = " + ip);
                }else{
                    System.out.println("hash не совпадает ни с одним ip");
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}