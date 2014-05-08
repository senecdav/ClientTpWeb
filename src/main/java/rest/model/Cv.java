package rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Réprésente un CV.
 */
@XmlRootElement(name = "cv")
public class Cv {

    private int id;
    private String lastName;
    private String firstName;
    private String objectives;
    private List<Experience> experiences;
    private List<Education> educations;
    private String skills;
    private List<Lang> langs;
    private List<ITSkill> itSkills;

    public Cv() {}

    public Cv(
            int id,
            String lastName,
            String firstName,
            String objectives,
            List<Experience> experiences,
            List<Education> educations,
            String skills,
            List<Lang> langs,
            List<ITSkill> itSkills) {
        this.id = id;
        this.firstName = firstName;
        this.objectives = objectives;
        this.lastName = lastName;
        this.experiences = experiences;
        this.educations = educations;
        this.skills = skills;
        this.langs = langs;
        this.itSkills = itSkills;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement
    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    @XmlElementWrapper(name = "schools")
    @XmlElement(name = "school")
    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }

    @XmlElementWrapper(name = "experiences")
    @XmlElement(name = "experience")
    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }


    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    @XmlElementWrapper(name = "langs")
    @XmlElement(name = "lang")
    public List<Lang> getLangs() {
        return langs;
    }

    public void setLangs(List<Lang> langs) {
        this.langs = langs;
    }

    @XmlElementWrapper(name = "computerskills")
    @XmlElement(name = "computeskill")
    public List<ITSkill> getItSkills() {
        return itSkills;
    }

    public void setItSkills(List<ITSkill> itSkills) {
        this.itSkills = itSkills;
    }
}
