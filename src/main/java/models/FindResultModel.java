package models;

import java.io.Serializable;
import java.util.Date;

public class FindResultModel implements Serializable{

    private String name;
    private String place;
    private Date date;

    public FindResultModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    public FindResultModel(String name, String place, Date date) {
        this.name = name;
        this.place = place;
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    @Override
    public String toString() {
        return "FindResultModel [" + (name != null ? "name=" + name + ", " : "")
                + (place != null ? "place=" + place + ", " : "") + (date != null ? "date=" + date : "") + "]";
    }
}
