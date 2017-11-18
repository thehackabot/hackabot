package io.ahababo.bot.skills.party;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import io.ahababo.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class PowerPointKaraokeSkill extends StatefulSkill{


    public void init(Bot context, User user, int maxStates){
        super.init(context, user);
            maxStates = maxStates;
            maxStates = 0;
    }

    public SendMessage transition(Message message, int state) {
        switch (state){
            case 0:
                String botmsg = "You started a round of PowerPointKaraoke. Have fun! \n";
                botmsg += "Selected random player to present a slide\n";
                int playerIndex = new Random().nextInt() % getContext().getUserPhotosLength();
                User player = getContext().getActiveUsers().get(playerIndex);
                getContext().publish(new SendPhoto().setChatId(message.getChatId()).setPhoto(getContext().getUserPhoto(player)));
                state++;
                break;
            case 1:
                break;
            default:
                break;
        }
        return null;
    }
}
