package io.ahababo.bot.webserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class WebServer implements Runnable {
    private final long BEER_TIMEOUT = 10000;

    private HttpServer server;
    private long beerTimer;

    private WebServer() {
        beerTimer = 0;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start beer server");
        }
        server.createContext("/beer", exchange -> {
            String response = "BEER";
            if (System.currentTimeMillis() - beerTimer > BEER_TIMEOUT) {
                response = "NOBEER";
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        });
        server.setExecutor(null);
    }

    public void enableBeer() {
        beerTimer = System.currentTimeMillis();
    }

    @Override
    public void run() {
        server.start();
    }

    private static WebServer _instance;
    public static WebServer getInstance() {
        if (_instance == null) {
            _instance = new WebServer();
        }
        return _instance;
    }
}
