package hackatum.hackabot.bot.skills.party;

import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.User;
import hackatum.hackabot.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

import java.io.File;
import java.util.Random;

public class Karaoke extends BasicSkill {
    private File getRandomFile() throws Exception {
        File[] files = new File(System.getenv("SLIDESHOW_PATH")).listFiles();
        int randomSlideIndex = new Random().nextInt(files.length);
        return files[randomSlideIndex];
    }

    public SendMessage handle(Message message) {
        int photoIndex = new Random().nextInt(getContext().getUserPhotosLength());
        User player = getContext().getPhotoUsers().get(photoIndex);
        try {
            // Send slide photo
            getContext().publish(new SendPhoto()
                    .setChatId(message.getChatId())
                    .setNewPhoto(getRandomFile()));

            // Send player photo
            getContext().publish(new SendPhoto()
                    .setChatId(message.getChatId())
                    .setPhoto(getContext().getUserPhoto(player)));
        } catch (Exception e) {
            return new SendMessage()
                    .setChatId(message.getChatId())
                    .setText(Localization.getInstance().get("Karaoke", "Error"));
        }
        return new SendMessage()
                .setChatId(message.getChatId())
                .setText(Localization.getInstance().get("Karaoke", "Round"));
    }
}
