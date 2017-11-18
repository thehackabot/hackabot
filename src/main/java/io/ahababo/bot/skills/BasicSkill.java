package io.ahababo.bot.skills;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class BasicSkill implements Skill {
    private User user;
    private Bot context;

    /**
     * Initialize the skill.
     * @param user
     */
    public void init(Bot context, User user) {
        this.context = context;
        this.user = user;
    }

    /**
     * Get user returns the skills user.
     * @return
     */
    public final User getUser() {
        return user;
    }

    public final Bot getContext() {
        return context;
    }

    /**
     * handle handles a incoming message.
     * @return
     */
    public SendMessage handle(Message msg) {
        return new SendMessage().setChatId(msg.getChatId()).setText(msg.getText());
    }

    /**
     * isFinished returns true if the conversation with the bot is finished.
     * @return
     */
    public boolean isFinished() {
        return true;
    }
}
