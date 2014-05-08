package rest.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by si3ll on 4/24/14.
 */
public class Education {
    private String name;
    private String year;

    public Education() {}

    public Education(String name, String year) {
        this.name = name;
        this.year = year;
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
