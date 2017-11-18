package io.ahababo.bot.skills.examples;

import io.ahababo.bot.User;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class help extends BasicSkill{

  public help(){ super();}

  public void init(User user){
    super.init(user);
  }

  String message = "hello\nguess\ngood bot\nbeer\nRockPaperScissor";

  @Override
  public SendMessage handle(Message incoming) {


    return  new SendMessage().setChatId(incoming.getChatId()).setText(message);
  }

  public boolean isFinished(){return true;}

}
