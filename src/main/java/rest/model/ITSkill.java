package rest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by si3ll on 4/24/14.
 */
public class ITSkill {
    private int level;
    private String skillName;

    public ITSkill() {}

    public ITSkill(String name, int level) {
        this.skillName = name;
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
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
