package hackatum.hackabot.bot.skills.examples;


import hackatum.hackabot.bot.Localization;
import hackatum.hackabot.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.stream.Collectors;

public class HelpSkill extends BasicSkill{
    private final static Localization local = Localization.getInstance();
    @Override
    public SendMessage handle(Message incoming) {
        String triggers = getContext().getPrivateFactory().list().stream()
                .map(v -> String.format("'%s' (%s)", v, v.enableGroup ?
                        local.get("Help", "Public"):
                        local.get("Help", "Private")))
                .collect(Collectors.joining(", "));
        return new SendMessage().setChatId(incoming.getChatId()).setText(String.format(local.get("Help", "Format"), triggers));
    }
}
