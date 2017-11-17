package io.ahababo.bot.skills;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class HelloWorld implements Skill {
    public SendMessage handle(Message message) {
        SendMessage msg = new SendMessage();
        msg.setChatId(message.getChatId());
        msg.setText("hello, world");
        return msg;
    }
}
