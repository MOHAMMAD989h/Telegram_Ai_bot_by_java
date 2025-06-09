package com.bank;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(new JavaBot());
                System.out.println("Telegram Bot is running in the background...");
            } catch (TelegramApiException e) {
                System.err.println("Failed to start Telegram Bot:");
                e.printStackTrace();
            }
        }).start();

        try {
            int port = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/", exchange -> {
                String response = "Telegram Bot is alive!";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("Health check server started on port " + port);

        } catch (IOException e) {
            System.err.println("Failed to start health check server:");
            e.printStackTrace();
        }
    }
}