package io.ahababo.bot.skills.drinking;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.ArrayList;

public class DareSkill extends StatefulSkill {
    private String description;

    private SendMessage handleWelcomeState(Message incoming) {
        if (CommonContext.getInstance().exists(getUser())) {
            increaseState(); increaseState();
            return new SendMessage().setChatId(incoming.getChatId()).setText("Sorry, but you are already looking for partner.");
        } else {
            increaseState();
            return new SendMessage().setChatId(incoming.getChatId()).setText("Please describe yourself in one sentence.");
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
            CommonContext.getInstance().accept(entry);
            return new SendMessage().setChatId(incoming.getChatId()).setText("I may have found a match. It describes itself as '" + entry.description + "'.");
        } else {
            CommonContext.getInstance().enqueue(getUser(), description);
            return new SendMessage().setChatId(incoming.getChatId()).setText("Hackabot strong, Hackabot looking!");
        }
    }

    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message message, int state) {
        switch (state) {
            case 0:
                return handleWelcomeState(message);
            case 1:
                return handleDescribeState(message);
            default:
                return null;
        }
    }

    private static class CommonContext {
        private class MatchEntry {
            final User match;
            final String description;

            public MatchEntry(User match, String description) {
                this.match = match;
                this.description = description;
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
        public void enqueue(User user, String description) {
            openMatches.add(new MatchEntry(user, description));

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
