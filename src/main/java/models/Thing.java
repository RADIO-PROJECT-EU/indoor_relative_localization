package models;

/**
 * @author Konstantinos Antonopoulos <konsantonop@gmail.com>
 * @version 0.1 
 * @category System Module ( Things Entity )
 * @since 2017-03-07
 **/

import java.util.Date;
import java.util.List;

public class Thing{

    private long id;
    
    private String name;

    private long sensorId;

	public Thing() {}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public Thing(String name){
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Thing{" +
				"id=" + id +
				", name='" + name + '\'' +
				", sensorId=" + sensorId +
				'}';
	}
}