package io.ahababo.bot.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SkillFactory {
    private final static Logger logger = Logger.getLogger(SkillFactory.class.getName());
    private Map<String, Class<? extends Skill>> skills;

    public SkillFactory() {
        skills = new HashMap<>();
    }

    public void register(String buzzwords, Class<? extends Skill> skill) {
        skills.put(buzzwords, skill);
    }

    public Skill makeSkill(String buzzwords) throws InstantiationException, IllegalAccessException,InvocationTargetException {
        Class<? extends Skill> match = skills.get(buzzwords);
        if (match == null) return null;

        logger.fine("Find matching skill for buzzwords " + buzzwords);
        Constructor[] constructors = match.getDeclaredConstructors();
        for (Constructor ctor : constructors) {
            if (ctor.getParameterCount() != 0) {
                continue;
            }
            return (Skill) ctor.newInstance();
        }
        throw new InstantiationException("Did not find matching constructor");
    }
}
