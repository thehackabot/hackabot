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
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    private final static Logger logger = Logger.getLogger(Bot.class.getName());
    private ConcurrentHashMap<User, Skill> activeSkills;
    private SkillFactory groupFactory, privateFactory;

    static {
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);
        logger.addHandler(consoleHandler);
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            logger.fine("Received update " + update.getUpdateId());

            SkillFactory selectedChannel = null;
            if (update.getMessage().getChat().isUserChat()) {
                logger.fine("Selected private factory for user chat");
                selectedChannel = privateFactory;
            } else if (update.getMessage().getChat().isChannelChat()) {
                logger.fine("Selected public factory for channel chat");
                selectedChannel = groupFactory;
            } else {
                logger.warning("Received group chat message");
                // TODO: Handle group chats
                return;
            }

            Message incoming = update.getMessage();
            User user = new User(incoming.getFrom().getId());
            Skill active = activeSkills.get(user);

            logger.fine("Incoming message from user " + user.getUserId() + ": " + incoming.getText());
            if (active == null || (active != null && active.isFinished())) {
                logger.fine("Searching for new skill");
                try {
                    active = selectedChannel.makeSkill(update.getMessage().getText());
                    if (active == null) {
                        logger.warning("Failed to handle command");
                        // TODO: Show useful 'command not found' message
                        return;
                    }
                    active.init(user);
                    activeSkills.put(user, active);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
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
    }
}