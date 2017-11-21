package io.ahababo.bot.skills.social;

import io.ahababo.bot.Bot;
import io.ahababo.bot.Localization;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import io.ahababo.bot.skills.util.EmotionDetector;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

import java.util.ArrayList;

public class MatchSkill extends StatefulSkill {
    private final static Localization local = Localization.getInstance();
    private final int MIN_MATCHES = 3;

    private String description, image;
    private EmotionDetector.Result emotions;

    private SendMessage handleWelcomeState(Message incoming) {
        if (CommonContext.getInstance().exists(getUser())) {
            increaseState(); increaseState(); increaseState();
            return new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText(local.get("Match", "AlreadyActive"));
        } else {
            increaseState();
            return new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText(local.get("Match", "TakePhoto"));
        }
    }

    private SendMessage handleSelfieState(Message incoming) {
        if (incoming.getPhoto() != null) {
            image = incoming.getPhoto().get(incoming.getPhoto().size()-1).getFileId();
            getContext().setUserPhoto(getUser(),incoming.getPhoto().get(incoming.getPhoto().size()-1).getFileId());
            try {
                EmotionDetector.Result[] output = EmotionDetector.detect(getContext(), image);
                if (output.length > 1) {
                    return new SendMessage()
                            .setChatId(incoming.getChatId())
                            .setText(local.get("Match", "TooManyHumans"));
                } else if (output.length < 1) {
                    return new SendMessage()
                            .setChatId(incoming.getChatId())
                            .setText(local.get("Match", "NoHumans"));
                }
                emotions = output[0];
            } catch (Exception e) {
                increaseState();
                increaseState();
                return new SendMessage()
                        .setChatId(incoming.getChatId())
                        .setText(local.get("Match", "Error"));
            }
            increaseState();
            return new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText(local.get("Match", "Describe"));
        } else {
            increaseState();
            increaseState();
            return new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText(local.get("Match", "Ignore"));
        }
    }

    private SendMessage handleDescribeState(Message incoming) {
        increaseState();
        description = incoming.getText();
        CommonContext.MatchEntry entry = CommonContext.getInstance().lookForMatch(getUser(), emotions);
        if (entry != null && CommonContext.getInstance().openMatches.size() >= MIN_MATCHES) {
            double similarity = (entry.emotions.similarTo(emotions) / 2.0 + 0.5) * 100.0;
            getContext().publish(new SendMessage()
                    .setChatId(entry.match.getChatId())
                    .setText(String.format(local.get("Match", "Found"), similarity, description)));
            getContext().publish(new SendPhoto()
                    .setChatId(entry.match.getChatId())
                    .setPhoto(image));
            CommonContext.getInstance().accept(entry);
            getContext().publish(new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText(String.format(local.get("Match", "Found"), similarity, entry.description)));
            getContext().publish(new SendPhoto()
                    .setChatId(incoming.getChatId())
                    .setPhoto(entry.image));
        } else {
            CommonContext.getInstance().enqueue(getUser(), description, image, emotions);
            return new SendMessage().setChatId(incoming.getChatId()).setText(local.get("Match", "InQueue"));
        }
        return null;
    }

    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 3);
    }

    @Override
    public SendMessage transition(Message message, int state) {
        switch (state) {
            case 0:
                return handleWelcomeState(message);
            case 1:
                return handleSelfieState(message);
            case 2:
                return handleDescribeState(message);
            default:
                return null;
        }
    }

    private static class CommonContext {
        private class MatchEntry {
            final User match;
            final String description, image;
            final EmotionDetector.Result emotions;

            public MatchEntry(User match, String description, String image, EmotionDetector.Result emotions) {
                this.match = match;
                this.description = description;
                this.image = image;
                this.emotions = emotions;
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof MatchEntry)) {
                    return false;
                }
                return ((MatchEntry) o).match.equals(match);
            }
        }
        private ArrayList<MatchEntry> openMatches;
        private CommonContext() {
            openMatches = new ArrayList<>();
        }
        public void enqueue(User user, String description, String image, EmotionDetector.Result emotions) {
            openMatches.add(new MatchEntry(user, description, image, emotions));

        }
        public boolean exists(User match) {
            for (MatchEntry entry : openMatches) {
                if (entry.match.equals(match)) return true;
            }
            return false;
        }
        public MatchEntry lookForMatch(User partner, EmotionDetector.Result emotions) {
            MatchEntry max = null;
            double maxScore = -1;
            for (MatchEntry entry : openMatches) {
                if (!entry.match.equals(partner) && entry.emotions.similarTo(emotions) > maxScore) {
                    maxScore = entry.emotions.similarTo(emotions);
                    max = entry;
                }
            }
            return max;
        }
        public void accept(MatchEntry match) {
            openMatches.remove(match);
        }
        private static CommonContext instance;
        public static CommonContext getInstance() {
            if (instance == null) instance = new CommonContext();
            return instance;
        }
    }
}
