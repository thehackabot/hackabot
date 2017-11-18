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

public class PowerPointKaraokeSkill extends BasicSkill{

    private void sendDocUploadingAFile(Long chatId, java.io.File save, String caption) throws TelegramApiException{
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setNewDocument(save);
        sendDocumentRequest.setCaption(caption);
        getContext().sendDocument(sendDocumentRequest);
    }

    private File getRandomFile(){
        File[] files = new File("~/OpenSource/ahababo/PowerPointKaraokeSlides/").listFiles();
        int randomSlideIndex = new Random().nextInt()%files.length;
        return files[randomSlideIndex];
    }

    public SendMessage handle(Message message) {
        String botmsg = "You started a round of PowerPointKaraoke. Have fun! \n";
        botmsg += "Selected random player to present a slide\n";

        int playerIndex = new Random().nextInt() % getContext().getUserPhotosLength();
        User player = getContext().getActiveUsers().get(playerIndex);
        getContext().publish(new SendPhoto().setChatId(message.getChatId()).setPhoto(getContext().getUserPhoto(player)));
        try {
            sendDocUploadingAFile(message.getChatId(), getRandomFile(), "Slide");
        }catch(TelegramApiException e){
        }
        return new SendMessage().setChatId(message.getChatId()).setText(botmsg);
    }
}
