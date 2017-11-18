package io.ahababo.bot.skills;

import io.ahababo.bot.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class BasicSkill implements Skill {
    private User user;

    /**
     * Initialize the skill.
     * @param user
     */
    public void init(User user) {
        this.user = user;
    }

    /**
     * Get user returns the skills user.
     * @return
     */
    public final User getUser() {
        return user;
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
