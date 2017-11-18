package io.ahababo.bot.skills.drinking;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

import java.util.ArrayList;

public class MatchSkill extends StatefulSkill {
    private String description, image;

    private SendMessage handleWelcomeState(Message incoming) {
        if (CommonContext.getInstance().exists(getUser())) {
            increaseState(); increaseState(); increaseState();
            return new SendMessage().setChatId(incoming.getChatId()).setText("Sorry, but you are already looking for partner.");
        } else {
            increaseState();
            return new SendMessage().setChatId(incoming.getChatId()).setText("Please take a photo of yourself.");
        }
    }

    private SendMessage handleSelfieState(Message incoming) {
        if (incoming.getPhoto() != null) {
            increaseState();
            image = incoming.getPhoto().get(incoming.getPhoto().size()-1).getFileId();
            System.out.println(incoming.getPhoto().get(incoming.getPhoto().size()-1));
            return new SendMessage().setChatId(incoming.getChatId()).setText("Please describe yourself.");
        } else {
            increaseState();
            increaseState();
            return new SendMessage().setChatId(incoming.getChatId()).setText("Well, if you don't want to.");
        }
    }

    private SendMessage handleDescribeState(Message incoming) {
        increaseState();
        description = incoming.getText();
        CommonContext.MatchEntry entry = CommonContext.getInstance().lookForMatch(getUser());
        if (entry != null) {
            getContext().publish(new SendMessage()
                    .setChatId(entry.match.getChatId())
                    .setText("Your search was successful! Your match describes itself as '" + description + "'."));
            getContext().publish(new SendPhoto()
                    .setChatId(entry.match.getChatId())
                    .setPhoto(image));
            CommonContext.getInstance().accept(entry);
            getContext().publish(new SendMessage()
                    .setChatId(incoming.getChatId())
                    .setText("I may have found a match. It describes itself as '" + entry.description + "'."));
            getContext().publish(new SendPhoto()
                    .setChatId(incoming.getChatId())
                    .setPhoto(entry.image));
            } else {
            CommonContext.getInstance().enqueue(getUser(), description, image);
            return new SendMessage().setChatId(incoming.getChatId()).setText("Hackabot strong, Hackabot looking!");
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

            public MatchEntry(User match, String description, String image) {
                this.match = match;
                this.description = description;
                this.image = image;
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
        public void enqueue(User user, String description, String image) {
            openMatches.add(new MatchEntry(user, description, image));

        }
        public boolean exists(User match) {
            return lookForMatch(match) == null && openMatches.size() > 0;
        }
        public MatchEntry lookForMatch(User partner) {
            for (MatchEntry entry : openMatches) {
                if (!entry.match.equals(partner)) return entry;
            }
            return null;
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
