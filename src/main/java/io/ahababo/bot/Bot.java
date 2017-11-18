package io.ahababo.bot;

import io.ahababo.bot.skills.examples.*;
import io.ahababo.bot.skills.Skill;
import io.ahababo.bot.skills.SkillFactory;
import io.ahababo.bot.skills.drinking.BeerSkill;
import io.ahababo.bot.skills.examples.GoodBotSkill;
import io.ahababo.bot.skills.examples.HelloWorldSkill;
import io.ahababo.bot.skills.examples.NumberGuessSkill;
import io.ahababo.bot.skills.games.RockPaperScissorSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.concurrent.ConcurrentHashMap;

public class Bot extends TelegramLongPollingBot {
    private final static Logger logger = LoggerFactory.getLogger(Bot.class);
    private ConcurrentHashMap<User, Skill> activeSkills;
    private SkillFactory groupFactory, privateFactory;

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            logger.info("Received update #" + update.getUpdateId());

            Message incoming = update.getMessage();
            User user = new User(incoming.getFrom().getId());
            SkillFactory selectedChannel = privateFactory;

            Skill active = activeSkills.get(user);
            logger.info("Incoming message from user #" + user.getUserId() + ": " + incoming.getText());
             if (active == null || (active != null && active.isFinished())) {
                logger.info("Searching for new skill");
                try {
                    active = selectedChannel.makeSkill(incoming.getText());
                    if (active == null) {
                        logger.error("Failed to handle command");
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
                logger.error("Could not reply to message", e);
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
        privateFactory.register("guess", NumberGuessSkill.class);
        privateFactory.register("good bot", GoodBotSkill.class);
        privateFactory.register("beer", BeerSkill.class);
        privateFactory.register("help", help.class);

        privateFactory.register("RockPaperScissor", RockPaperScissorSkill.class);
        groupFactory.register("hello", HelloWorldSkill.class);
    }
}