package io.ahababo.bot;

import io.ahababo.bot.skills.HelloWorld;
import io.ahababo.bot.skills.Skill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private Map<String, Skill> skills;

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String[] command = update.getMessage().getText().toLowerCase().split(" ");
            SendMessage reply = new SendMessage().setText("Sorry, I do not understand you.").setChatId(update.getMessage().getChatId());
            if (command.length > 0) {
                Skill sk = skills.get(command[0]);
                if (sk != null) {
                    reply = skills.get(command[0]).handle(update.getMessage());
                }
            } else { }
            try {
                execute(reply); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "ahababot";
    }

    @Override
    public String getBotToken() {
        return "***REMOVED***";
    }

    public Bot() {
        skills = new HashMap<String, Skill>();
        skills.put("hello", new HelloWorld());
        skills.put("hello1", new HelloWorld());
        skills.put("hello2", new HelloWorld());
        skills.put("hello3", new HelloWorld());
    }
}