package io.ahababo.bot.skills.party;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Random;

public class PowerPointKaraokeSkill extends BasicSkill{
    public SendMessage handle(Message message) {
        String botmsg = "You started a round of PowerPointKaraoke. Have fun! \n";
        botmsg += "Selected random player to present a slide\n";

        int playerIndex = new Random().nextInt() % getContext().getUserPhotosLength();
        User player = getContext().getActiveUsers().get(playerIndex);
        getContext().publish(new SendPhoto().setChatId(message.getChatId()).setPhoto(getContext().getUserPhoto(player)));

        try{
            getContext().sendPhoto(new SendPhoto().setChatId(message.getChatId()).setPhoto("ahababo/PowerpointKaraoke/Folie1.JPG"));
        }catch(TelegramApiException e){

        }

        return new SendMessage().setChatId(message.getChatId()).setText(botmsg);
    }
}
