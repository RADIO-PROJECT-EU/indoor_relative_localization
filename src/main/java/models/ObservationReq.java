package models;

import java.util.List;

public class ObservationReq {
	
	private int id;
	private String name;
	private List<Thing> things;
	private long timestamp;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Thing> getThings() {
		return things;
	}
	public void setThings(List<Thing> things) {
		this.things = things;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ObservationReq{" +
				"id=" + id +
				", name='" + name + '\'' +
				", things=" + things +
				", timestamp=" + timestamp +
				'}';
	}
}
