package com.projects.covid19.notification.models;

import java.io.Serializable;

import org.springframework.data.annotation.Id;


public class BaseModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5947694330131990840L;
	
	@Id
    private String id;
	
    
	public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
    
    
}
