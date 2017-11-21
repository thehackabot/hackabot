package hackatum.hackabot.bot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Localization {
    private final Properties localProperties;
    private Localization(String file) {
        localProperties = new Properties();
        try {
            localProperties.load(new FileInputStream(file));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open localization file");
        }
    }

    public String get(String s) {
        return localProperties.getProperty(s);
    }

    public String get(String skill, String param) {
        return localProperties.getProperty("Skill." + skill + "." + param);
    }

    public String getTrigger(String skill) {
        return get(skill, "Trigger");
    }

    public String getExample(String skill) {
        return get(skill, "Example");
    }

    private static Localization _instance;
    public static synchronized Localization getInstance() {
        if (_instance == null) {
            _instance = new Localization(System.getenv("LOCAL_FILE"));
        }
        return _instance;
    }
}
