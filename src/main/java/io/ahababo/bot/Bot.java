package io.ahababo.bot;

import io.ahababo.bot.skills.Skill;
import io.ahababo.bot.skills.SkillFactory;
import io.ahababo.bot.skills.examples.HelloWorldSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    private final static Logger logger = Logger.getLogger(Bot.class.getName());
    private ConcurrentHashMap<User, Skill> activeSkills;
    private SkillFactory groupFactory, privateFactory;

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() || update.hasChannelPost()) {
            logger.info("Received update " + update.getUpdateId());

            Message incoming = null;
            User user = null;
            SkillFactory selectedChannel = null;
            if (update.hasMessage() && update.getMessage().getChat().isUserChat()) {
                logger.info("Selected private factory for user chat");
                selectedChannel = privateFactory;
                incoming = update.getMessage();
                user = new User(incoming.getFrom().getId());
            } else if (update.hasChannelPost()) {
                logger.info("Selected public factory for channel chat");
                selectedChannel = groupFactory;
                incoming = update.getChannelPost();
                user = new User(-1);
            } else {
                logger.warning("Received group chat message");
                // TODO: Handle group chats
                return;
            }

            Skill active = activeSkills.get(user);
            logger.info("Incoming message from user " + user.getUserId() + ": " + incoming.getText());
            if (active == null || (active != null && active.isFinished())) {
                logger.info("Searching for new skill");
                try {
                    active = selectedChannel.makeSkill(incoming.getText());
                    if (active == null) {
                        logger.warning("Failed to handle command");
                        // TODO: Show useful 'command not found' message
                        return;
                    }
                    active.init(user);
                    activeSkills.put(user, active);
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: Handle error
                    return;
                }
            }

            SendMessage reply = active.handle(incoming);
            try {
                execute(reply); // Call method to send the message
            } catch (TelegramApiException e) {
                logger.warning(e.getMessage());
            }
        }
    }

    public String getBotUsername() {
        return "ahababot";
    }

    @Override
    public String getBotToken() {
        return "***REMOVED***";
    }

    public Bot() {

        activeSkills = new ConcurrentHashMap<>();
        groupFactory = new SkillFactory();
        privateFactory = new SkillFactory();

        privateFactory.register("hello", HelloWorldSkill.class);
        groupFactory.register("hello", HelloWorldSkill.class);
    }
}