package rest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by si3ll on 4/24/14.
 */
public class Lang {
    private int level;
    private String langName;

    public Lang() {}

    public Lang(String name, int level) {
        this.langName = name;
        this.level = level;
    }

    @XmlAttribute
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @XmlElement
    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }
}
