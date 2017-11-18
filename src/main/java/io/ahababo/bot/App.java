package io.ahababo.bot;

import io.ahababo.bot.webserver.WebServer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class App 
{
    public static void main( String[] args )
    {
        WebServer webserver = WebServer.getInstance();
        webserver.run();

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
