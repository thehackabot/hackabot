package io.ahababo.bot.skills.social;

import io.ahababo.bot.Bot;
import io.ahababo.bot.Localization;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import io.ahababo.bot.skills.util.EmotionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class EmotionSkill extends StatefulSkill {
    private final static Localization local = Localization.getInstance();
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
                return new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(local.get("Emotion", "TakePhoto"));
            case 1:
                increaseState();
                if (message.getPhoto() == null) {
                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText(local.get("Emotion", "NoPhoto"));
                }
                String fileId = message.getPhoto().get(message.getPhoto().size()-1).getFileId();
                try {
                    EmotionDetector.Result[] emotions = EmotionDetector.detect(getContext(), fileId);
                    for (int i = 0; i < emotions.length; i++) {
                        getContext().publish(new SendMessage()
                                .setChatId(message.getChatId())
                                .setText(String.format(local.get("Emotion", "Description"),
                                        i, emotions[i].toString())));
                    }
                } catch (Exception e) {
                    logger.error("Error while analyzing emotions", e);
                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText(local.get("Emotion", "Error"));
                }

        }
        return null;
    }
}
