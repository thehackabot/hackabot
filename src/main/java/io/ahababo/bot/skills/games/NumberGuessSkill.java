package io.ahababo.bot.skills.games;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class NumberGuessSkill extends StatefulSkill {
    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message incoming, int state) {
        switch (state) {
            case 0:
                increaseState();
                return new SendMessage().setChatId(incoming.getChatId()).setText("Please guess a number between 0 and 100.");
            case 1:
                try {
                    int guess = Integer.parseInt(incoming.getText());
                    int computer = new Random().nextInt(100) + 1;
                    increaseState();
                    if (state == computer) {
                        return new SendMessage().setChatId(incoming.getChatId()).setText("Oh well, you we're right!");
                    } else {
                        return new SendMessage()
                                .setChatId(incoming.getChatId())
                                .setText("Bad luck, you missed it by " + Math.abs(computer - guess));
                    }
                } catch (Exception e) {
                    return new SendMessage().setChatId(incoming.getChatId()).setText("You should enter a number!");
                }
        }
        return null;
    }
}
