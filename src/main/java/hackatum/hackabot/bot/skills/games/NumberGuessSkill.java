package hackatum.hackabot.bot.skills.games;

import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.User;
import hackatum.hackabot.bot.skills.StatefulSkill;
import hackatum.hackabot.bot.Bot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class NumberGuessSkill extends StatefulSkill {
    private final static Localization local = Localization.getInstance();

    @Override
    public void init(Bot context, User user) {
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message incoming, int state) {
        switch (state) {
            case 0:
                increaseState();
                return new SendMessage()
                        .setChatId(incoming.getChatId())
                        .setText(local.get("Guess", "Intro"));
            case 1:
                try {
                    int guess = Integer.parseInt(incoming.getText());
                    if (guess < 0 || guess > 100){
                        throw new Exception("Bad input");
                    }
                    int computer = new Random().nextInt(100) + 1;
                    increaseState();
                    if (state == computer) {
                        return new SendMessage()
                                .setChatId(incoming.getChatId())
                                .setText(local.get("Guess", "Success"));
                    } else {
                        return new SendMessage()
                                .setChatId(incoming.getChatId())
                                .setText(String.format(local.get("Guess", "Missed"), Math.abs(computer - guess)));
                    }
                } catch (Exception e) {
                    return new SendMessage()
                            .setChatId(incoming.getChatId())
                            .setText(local.get("Guess", "Intro"));
                }
        }
        return null;
    }
}
