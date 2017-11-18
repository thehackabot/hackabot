package io.ahababo.bot.skills.examples;


import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.stream.Collectors;

public class HelpSkill extends BasicSkill{
    @Override
    public SendMessage handle(Message incoming) {
        String triggers = getContext().getPrivateFactory().list().stream()
                .map(v -> "'" + v.trigger + "' + (" + (v.enableGroup ? "public": "private") + ")").collect(Collectors.joining(", "));
        return new SendMessage().setChatId(incoming.getChatId()).setText("You could use " + triggers + ".");
    }
}
