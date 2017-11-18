package io.ahababo.bot.skills.games;

import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Random;

public class RockPaperScissorSkill extends BasicSkill{
    private int state;

    public RockPaperScissorSkill(){ super();}

    public void init(User user){
        super.init(user);
        state = 0;
    }

    @Override
    public SendMessage handle(Message incoming) {
        String botmsg = "";
        String botchoice = "";
        switch (state) {
            case 0:
                state++;
                botmsg = "So you decided to play against the evil super AI. Good Luck." +
                                "What do you chose? Rock, Paper or Scissor";
                return new SendMessage().setChatId(incoming.getChatId()).setText(botmsg);
            case 1:
                int random = new Random().nextInt() % 2;
                switch (random){
                    case 0: botchoice += "Rock";
                        break;
                    case 1: botchoice += "Paper";
                        break;
                    case 2: botchoice += "Scissor";
                        break;
                    default:botchoice += "Internal faillure";
                        break;
                }
                if(incoming.getText().equals(botchoice)) {
                    botmsg = "Good choice. We both picked the same. I win";
                    state++;
                    return new SendMessage().setChatId(incoming.getChatId()).setText(botmsg);
                }
                switch(incoming.getText()) {
                    case "Rock":
                        if(botchoice.equals("Scissor")) {
                            botmsg = "-Scissor\n"+"Well played. You win";
                        }else{
                            botmsg = "-Paper\n"+"HAHAHA I WIN!";
                        }
                        state++;
                        break;
                    case "Paper":
                        if(botchoice.equals("Rock")) {
                            botmsg = "-Rock\n"+"Well played. You win";
                        }else{
                            botmsg = "-Scissor\n"+"HAHAHA I WIN!";
                        }
                        state++;
                        break;
                    case "Scissor":
                        if(botchoice.equals("Paper")) {
                            botmsg = "-Paper\n"+"Well played. You win";
                        }else{
                            botmsg = "-Rock\n"+"HAHAHA I WIN!";
                        }
                        state++;
                        break;
                    default:botmsg = "Do you even know how to play this game?";
                        break;
                }
                return new SendMessage().setChatId(incoming.getChatId()).setText(botmsg);
        }
        return null;
    }

    public boolean isFinished(){return state >=2;}
}
