package hackatum.hackabot.bot.skills.drinking;

import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.skills.BasicSkill;
import hackatum.hackabot.bot.webserver.WebServer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class BeerSkill extends BasicSkill {
    @Override
    public SendMessage handle(Message msg) {
        WebServer.getInstance().enableBeer();
        return new SendMessage()
                .setText(Localization.getInstance().get("Beer", "Ready"))
                .setChatId(msg.getChatId());
    }
}
