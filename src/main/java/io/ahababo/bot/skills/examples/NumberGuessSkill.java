package io.ahababo.bot.skills.examples;

import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class NumberGuessSkill extends BasicSkill {
    private int state;

    public NumberGuessSkill() {
        super();
    }

    @Override
    public void init(User user) {
        super.init(user);
        state = 0;
    }

    @Override
    public SendMessage handle(Message incoming) {
        switch (state) {
            case 0:
                state++;
                return new SendMessage().setChatId(incoming.getChatId()).setText("Please guess a number between 0 and 100.");
            case 1:
                try {
                    int guess = Integer.parseInt(incoming.getText());
                    int computer = new Random().nextInt() % 100;
                    state++;
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

    @Override
    public boolean isFinished() {
        return state >= 2;
    }
}
