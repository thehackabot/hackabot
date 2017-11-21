package hackatum.hackabot.bot.skills.games;

import hackatum.hackabot.bot.Bot;
import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.User;
import hackatum.hackabot.bot.skills.StatefulSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class RockPaperScissorSkill extends StatefulSkill {
    private final static Localization local = Localization.getInstance();

    public void init(Bot context, User user){
        super.init(context, user, 2);
    }

    @Override
    public SendMessage transition(Message message, int state) {
        switch (state) {
            case 0:
                increaseState();
                return new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(local.get("RPS", "Intro"));
            case 1:
                String botChoice;
                int random = new Random().nextInt() % 3;
                switch (random) {
                    case 0: botChoice = local.get("RPS", "Rock"); break;
                    case 1: botChoice = local.get("RPS", "Paper"); break;
                    case 2: botChoice = local.get("RPS", "Scissor"); break;
                    default: botChoice = "invalid";
                }
                if (message.getText().equals(botChoice)) {
                    increaseState();
                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText(local.get("RPS", "Draw"));
                }

                boolean didBotWin;
                if (message.getText().equals(local.get("RPS", "Rock"))) {
                    didBotWin = botChoice.equals(local.get("RPS", "Rock"));
                } else if (message.getText().equals(local.get("RPS", "Paper"))) {
                    didBotWin = botChoice.equals(local.get("RPS", "Rock"));
                } else if (message.getText().equals(local.get("RPS", "Scissor"))) {
                    didBotWin = botChoice.equals(local.get("RPS", "Scissor"));
                } else {
                    return new SendMessage().setChatId(message.getChatId()).setText(local.get("Skill", "BadInput"));
                }
                increaseState();
                return new SendMessage()
                        .setText(String.format(local.get("RPS", didBotWin ? "BotWin": "UserWin"), botChoice))
                        .setChatId(message.getChatId());
        }
        return null;
    }
}
