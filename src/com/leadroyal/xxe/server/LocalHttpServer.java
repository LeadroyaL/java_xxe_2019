package com.leadroyal.xxe.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class LocalHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(1234), 100);
        httpserver.createContext("/", new MyResponseHandler());
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");
    }

    public static class MyResponseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Receive Request Start");
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                System.out.println(URLDecoder.decode(httpExchange.getRequestURI().getPath(), "UTF-8"));
                String response = "";
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream responseBody = httpExchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(responseBody, StandardCharsets.UTF_8);
                writer.write(response);
                writer.close();
                responseBody.close();
            }
            System.out.println("Receive Request End");
        }
    }
}
