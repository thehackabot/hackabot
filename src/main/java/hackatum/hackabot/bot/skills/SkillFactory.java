package hackatum.hackabot.bot.skills;

import hackatum.hackabot.bot.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SkillFactory {
    private final static Logger logger = LoggerFactory.getLogger(SkillFactory.class);
    private ArrayList<FactoryItem> factoryItems;

    public class FactoryItem {
        public final Class<? extends Skill> skill;
        public final ArrayList<String> buzzwords;
        public final String trigger;
        public final boolean enableGroup;

        public FactoryItem(Class<? extends Skill> skill, ArrayList<String> buzzwords, String trigger, boolean enableGroup) {
            this.skill = skill;
            this.buzzwords = buzzwords;
            this.enableGroup = enableGroup;
            this.trigger = trigger;
        }

        public float match(String sentence, boolean isGroup) {
            if (!enableGroup && isGroup) return -1.f;

            String[] words = sentence.toLowerCase().split(" ");
            HashMap<String, Boolean> existing = new HashMap<String, Boolean>();
            for (String w : words) {
                existing.put(w, true);
            }
            int sum = 0;
            for (String w : buzzwords) {
                if (existing.get(w) != null) sum++;
            }
            return ((float) sum) / ((float) buzzwords.size());
        }
    }

    public List<FactoryItem> list() {
        return factoryItems;
    }

    public SkillFactory() {
        factoryItems = new ArrayList<>();
    }

    public void register(String name, Class<? extends Skill> skill, boolean enableGroup) {
        String buzzwords = Localization.getInstance().getTrigger(name);
        String exampleTrigger = Localization.getInstance().getExample(name);

        ArrayList<String> items = new ArrayList<>(Arrays.asList(buzzwords.toLowerCase().split(" ")));
        factoryItems.add(new FactoryItem(skill, items, exampleTrigger, enableGroup));
    }

    public Skill makeSkill(String buzzwords, boolean isGroup) throws InstantiationException, IllegalAccessException,InvocationTargetException {
        float maxMatch = 0;
        FactoryItem bestMatch = null;
        for (FactoryItem item : factoryItems) {
            float score = item.match(buzzwords, isGroup);
            if (score > maxMatch) {
                maxMatch = score;
                bestMatch = item;
            }
        }
        if (bestMatch == null) return null;

        logger.info("Find matching skill for buzzwords " + buzzwords);
        Constructor[] constructors = bestMatch.skill.getDeclaredConstructors();
        for (Constructor ctor : constructors) {
            if (ctor.getParameterCount() != 0) {
                continue;
            }
            return (Skill) ctor.newInstance();
        }
        throw new InstantiationException("Did not find matching constructor");
    }
}
