package io.ahababo.bot.skills;

import io.ahababo.bot.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public interface Skill {
    void init(User user);
    SendMessage handle(Message message);
    boolean isFinished();
}
