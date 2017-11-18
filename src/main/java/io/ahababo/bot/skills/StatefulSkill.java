package io.ahababo.bot.skills;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public abstract class StatefulSkill extends BasicSkill {
    private int state;
    private int maxStates;

    public void init(Bot context, User user, int maxStates) {
        super.init(context, user);
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
