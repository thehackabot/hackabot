package io.ahababo.bot.skills.party;

import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.util.Random;

public class PowerPointKaraokeSkill extends BasicSkill {
    private final String KARAOKE_MESSAGE = "You started a round of PowerPointKaraoke. Have fun!\nSelected random player to present a slide!";

    private File getRandomFile(){
        File[] files = new File("~/OpenSource/ahababo/PowerPointKaraokeSlides/").listFiles();
        int randomSlideIndex = new Random().nextInt()%files.length;
        return files[randomSlideIndex];
    }

    public SendMessage handle(Message message) {
        int playerIndex = new Random().nextInt() % getContext().getUserPhotosLength();
        User player = getContext().getActiveUsers().get(playerIndex);
        // Send player photo
        getContext().publish(new SendPhoto().setChatId(message.getChatId()).setPhoto(getContext().getUserPhoto(player)));
        // Send slide photo
        SendPhoto photo = new SendPhoto();
        photo.setChatId(message.getChatId());
        photo.setNewPhoto(getRandomFile());
        getContext().publish(photo);
        return new SendMessage().setChatId(message.getChatId()).setText(KARAOKE_MESSAGE);
    }
}
