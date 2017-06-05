package models;

import java.util.Date;

/**
 * Created by chris on 25/5/2017.
 */

public class Location {

    private long aalhouse;
    private Date createdAt;
    private String object;
    private String location;

    public long getAalhouse() {
        return aalhouse;
    }

    public void setAalhouse(long aalhouse) {
        this.aalhouse = aalhouse;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Location{" +
                ", aalhouse=" + aalhouse +
                ", createdAt=" + createdAt +
                ", object='" + object + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
