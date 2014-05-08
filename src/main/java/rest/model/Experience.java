package rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by si3ll on 4/24/14.
 */
@XmlType(propOrder={"name", "year", "description"})
public class Experience {

    private String name;
    private String description;
    private String year;

    public Experience() {}

    public Experience(String name, String description, String year) {
        this.name = name;
        this.description = description;
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
