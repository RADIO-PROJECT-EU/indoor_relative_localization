package models;

/**
 * @author Konstantinos Antonopoulos <konsantonop@gmail.com>
 * @version 0.1 
 * @category System Module ( Things Entity )
 * @since 2017-03-07
 **/

import java.util.Date;


public class Observation{

    private long id;

    private Thing thing;

	private Date createdAt;

   	private Date updatedAt;    

    private String vicinityThings;
   
	public Observation() {}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	public String getVicinityThings() {
		return vicinityThings;
	}

	public void setVicinityThings(String vicinityThings) {
		this.vicinityThings = vicinityThings;
	}


}