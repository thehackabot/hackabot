package io.ahababo.bot.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class SkillFactory {
    private final static Logger logger = LoggerFactory.getLogger(SkillFactory.class);
    private ConcurrentHashMap<String, Class<? extends Skill>> skills;

    public SkillFactory() {
        skills = new ConcurrentHashMap<>();
    }

    public void register(String buzzwords, Class<? extends Skill> skill) {
        skills.put(buzzwords, skill);
    }

    public Skill makeSkill(String buzzwords) throws InstantiationException, IllegalAccessException,InvocationTargetException {
        // TODO: Use matching algorithm for buzzwords
        Class<? extends Skill> match = skills.get(buzzwords);
        if (match == null) return null;

        logger.info("Find matching skill for buzzwords " + buzzwords);
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
