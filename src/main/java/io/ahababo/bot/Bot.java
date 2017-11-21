package io.ahababo.bot;

import io.ahababo.bot.skills.Skill;
import io.ahababo.bot.skills.SkillFactory;
import io.ahababo.bot.skills.drinking.BeerSkill;
import io.ahababo.bot.skills.social.EmotionSkill;
import io.ahababo.bot.skills.social.MatchSkill;
import io.ahababo.bot.skills.examples.GoodBotSkill;
import io.ahababo.bot.skills.examples.HelpSkill;
import io.ahababo.bot.skills.games.NumberGuessSkill;
import io.ahababo.bot.skills.games.RockPaperScissorSkill;
import io.ahababo.bot.skills.games.SelfieSkill;
import io.ahababo.bot.skills.party.Karaoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {
    private final static long USER_TIMEOUT = 300000;
    private final static Logger logger = LoggerFactory.getLogger(Bot.class);
    private ConcurrentHashMap<User, Long> privateUsers;
    private ConcurrentHashMap<User, Skill> activeSkills;
    private SkillFactory skillFactory;
    private ConcurrentHashMap<User, String> userPhotos;

    public List<User> getActiveUsers() {
        return privateUsers.keySet().stream()
                .filter(v -> System.currentTimeMillis() - privateUsers.get(v) <= USER_TIMEOUT)
                .collect(Collectors.toList());
    }

    public String getUserPhoto(User user) {
        return userPhotos.get(user);
    }

    public List<User> getPhotoUsers() {
        return userPhotos.keySet().stream().collect(Collectors.toList());
    }

    public int getUserPhotosLength() {
        return userPhotos.size();
    }

    public void setUserPhoto(User user, String fileId){
        userPhotos.put(user, fileId);
    }

    public SkillFactory getPrivateFactory() {
        return skillFactory;
    }

    public void publish(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Could not reply to message", e);
        }
    }

    public void publish(SendPhoto photo) {
        try {
            sendPhoto(photo);
        } catch (TelegramApiException e) {
            logger.error("Could not public photo", e);
        }
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            logger.info("Received update #" + update.getUpdateId());

            Message incoming = update.getMessage();
            User user = new User(incoming.getFrom().getId(), incoming.getChatId());
            privateUsers.put(user, System.currentTimeMillis());
            if (update.getMessage().isGroupMessage()) {
                user = new User(-incoming.getChatId().intValue(), incoming.getChatId());
            }

            Skill active = activeSkills.get(user);
            SendMessage reply = null;
            logger.info("Incoming message from user #" + user.getUserId() + ": " + incoming.getText());
            if (active == null || (active != null && active.isFinished())) {
                logger.info("Searching for new skill");
                try {
                    active = skillFactory.makeSkill(incoming.getText(), update.getMessage().isGroupMessage());
                    if (active == null) {
                        logger.error("Failed to classify command '" + incoming.getText() + "'");
                        return;
                    } else {
                        active.init(this, user);
                        activeSkills.put(user, active);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reply = new SendMessage()
                            .setChatId(incoming.getChatId())
                            .setText(Localization.getInstance().get("Bot.Error"));
                }
            }

            if (reply == null) reply = active.handle(incoming);
            if (reply != null) publish(reply);
        }
    }

    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    public Bot() {
        privateUsers = new ConcurrentHashMap<>();
        activeSkills = new ConcurrentHashMap<>();
        userPhotos = new ConcurrentHashMap<>();
        skillFactory = new SkillFactory();
        skillFactory.register("GoodBot", GoodBotSkill.class, true);
        skillFactory.register("Guess", NumberGuessSkill.class, true);
        skillFactory.register("Beer", BeerSkill.class, true);
        skillFactory.register("Help", HelpSkill.class, true);
        skillFactory.register("RPS", RockPaperScissorSkill.class, true);
        skillFactory.register("Match", MatchSkill.class, false);
        skillFactory.register("Selfie", SelfieSkill.class, true);
        skillFactory.register("Karaoke", Karaoke.class, true);
        skillFactory.register("Emotion", EmotionSkill.class, true);
    }
}