package io.ahababo.bot.skills.examples;

import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class GoodBotSkill extends BasicSkill {
    @Override
    public SendMessage handle(Message incoming) {
        return new SendMessage().setChatId(incoming.getChatId()).setText("Oh, how nice of you!");
    }
}
