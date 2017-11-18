package io.ahababo.bot;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

public class SelfieContext {
    private static final SelfieContext context = new SelfieContext();

    private SelfieContext(){

    }

    public static SelfieContext getInstance(){
        return context;
    }
}
