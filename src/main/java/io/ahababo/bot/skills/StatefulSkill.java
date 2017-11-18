package io.ahababo.bot.skills;

import io.ahababo.bot.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public abstract class StatefulSkill implements Skill {
    private User user;
    private int state;
    private int maxStates;

    public User getUser() {
        return user;
    }

    public void init(User user, int maxStates) {
        this.user = user;
        this.maxStates = maxStates;
        this.state = 0;
    }

    public void increaseState() {
        state++;
    }

    @Override
    public SendMessage handle(Message message) {
        return transition(message, state);
    }

    public abstract SendMessage transition(Message message, int state);

    @Override
    public boolean isFinished() {
        return state >= maxStates;
    }
}
