package io.ahababo.bot.skills.social;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import io.ahababo.bot.skills.util.EmotionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class EmotionSkill extends StatefulSkill {
    private final static Logger logger = LoggerFactory.getLogger(EmotionSkill.class);

    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message message, int state) {
        switch (state) {
            case 0:
                increaseState();
                return new SendMessage().setChatId(message.getChatId()).setText("I want to know your emotions. I want to understand them.");
            case 1:
                increaseState();
                if (message.getPhoto() == null) {
                    return new SendMessage().setChatId(message.getChatId()).setText("Well, bad things will happen to you.");
                }
                String fileId = message.getPhoto().get(message.getPhoto().size()-1).getFileId();
                try {
                    EmotionDetector.Result[] emotions = EmotionDetector.detect(getContext(), fileId);
                    for (int i = 0; i < emotions.length; i++) {
                        String msgText = "You are number " + i + "." + emotions[i].toString();
                        getContext().publish(new SendMessage().setChatId(message.getChatId()).setText(msgText));
                    }
                } catch (Exception e) {
                    logger.error("Error while analyzing emotions", e);
                    return new SendMessage().setChatId(message.getChatId()).setText("Sorry, I am a bit confused right now.");
                }

        }
        return null;
    }
}
