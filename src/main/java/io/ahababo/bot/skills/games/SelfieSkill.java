package io.ahababo.bot.skills.games;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class SelfieSkill extends StatefulSkill {
    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message message, int state) {
        switch (state) {
            case 0:
                increaseState();
                return new SendMessage().setChatId(message.getChatId()).setText("Please send me your selfie.");
            case 1:
                if (message.getPhoto().size() != 1) {
                    return new SendMessage().setChatId(message.getChatId()).setText("Just send me a selfie.");
                }
                increaseState();
                return new SendMessage().setChatId(message.getChatId()).setText("I normally would answer 'Oh boy' but well.");
        }
        return null;
    }
}
