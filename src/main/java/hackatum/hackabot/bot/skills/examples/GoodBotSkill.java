package hackatum.hackabot.bot.skills.examples;

import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class GoodBotSkill extends BasicSkill {
    @Override
    public SendMessage handle(Message incoming) {
        return new SendMessage().setChatId(incoming.getChatId()).setText(Localization.getInstance().get("GoodBot", "Message"));
    }
}
