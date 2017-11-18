package io.ahababo.bot.skills.games;

import io.ahababo.bot.Bot;
import io.ahababo.bot.User;
import java.util.ArrayList;
import java.util.Random;
import io.ahababo.bot.skills.BasicSkill;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class HangmanSkill extends BasicSkill {
  private int state;

  public HangmanSkill() {
    super();

  }

  //Array

  private static final String[] wordsLevel1 = {"bier","oida","fesch","brezn","gaudi"};
  private static final String[] wordsLevel2 = {"semmel","biergarten","weißwurst","dahoam","leberkas","lederhos","geschaid","schmarrn"};
  private static final String[] wordsLevel3 = {"leberkassemmel","fleischpfanzl","oachkatzlschwoaf"};
  private String word ;
  private ArrayList<Character> hiddenWord = new ArrayList<Character>();
  private ArrayList<Character> lettersGuessed = new ArrayList<Character>();
  private int level = 0;


  @Override
  public void init(Bot context, User user) {
    super.init(context, user);
    state = 0;
  }

  public ArrayList<Character> getLettersGuessed() {
    return lettersGuessed;
  }

  private void setHiddenWord() {
    setWord();
    for (char letter: word.toCharArray()) {
      hiddenWord.add('-');
    }
  }

  private void setWord() {

    if(level==1){
      word = wordsLevel1[new Random().nextInt(4)];
    }

    if(level==2){
      word = wordsLevel2[new Random().nextInt(7)];
    }

    if(level==3){
      word = wordsLevel3[new Random().nextInt(2)];
    }

  }

  public ArrayList<Character> getHiddenWord() {
    return hiddenWord;
  }

  public String getWord() {
    return word;
  }


  @Override
  public SendMessage handle(Message incoming){
    switch (state){
      case 0:
        state++;
        return new SendMessage().setChatId(incoming.getChatId()).setText("Now, let's play Hangman! Guess my word ;) Note: Typical Bavarian Wörds");
      case 1:
        try{
          new SendMessage().setChatId(incoming.getChatId()).setText("Set the Level: 1 to 3");
        int level = Integer.parseInt(incoming.getText());
        while(level > 3||level < 1){
          new SendMessage().setChatId(incoming.getChatId()).setText("There are only Levels 1 to 3");
          level = Integer.parseInt(incoming.getText());
        }
        state++;
        this.level=level;
      }catch (Exception e) {
          return new SendMessage().setChatId(incoming.getChatId()).setText("You should enter a number!");
        }
      //wird es
        // case 2:
    }
    return null;
  }

  @Override
  public boolean isFinished() {
    return false ;
  } /// Anzahl herausfinden

}
