package io.ahababo.bot.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class SkillFactory {
    private final static Logger logger = LoggerFactory.getLogger(SkillFactory.class);
    private ArrayList<FactoryItem> factoryItems;

    private class FactoryItem {
        public final Class<? extends Skill> skill;
        public final ArrayList<String> buzzwords;

        public FactoryItem(Class<? extends Skill> skill, ArrayList<String> buzzwords) {
            this.skill = skill;
            this.buzzwords = buzzwords;
        }

        public float match(String sentence) {
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

    public SkillFactory() {
        factoryItems = new ArrayList<>();
    }

    public void register(String buzzwords, Class<? extends Skill> skill) {
        ArrayList<String> items = new ArrayList<>(Arrays.asList(buzzwords.toLowerCase().split(" ")));
        factoryItems.add(new FactoryItem(skill, items));
    }

    public Skill makeSkill(String buzzwords) throws InstantiationException, IllegalAccessException,InvocationTargetException {
        float maxMatch = 0;
        FactoryItem bestMatch = null;
        for (FactoryItem item : factoryItems) {
            float score = item.match(buzzwords);
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
