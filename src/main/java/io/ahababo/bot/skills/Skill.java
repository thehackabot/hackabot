package io.ahababo.bot.skills;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public interface Skill {
    SendMessage handle(Message msg);
}
