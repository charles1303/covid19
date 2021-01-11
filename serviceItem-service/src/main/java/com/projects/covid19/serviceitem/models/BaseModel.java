package com.projects.covid19.serviceitem.models;

import java.io.Serializable;


public class BaseModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5947694330131990840L;
	
    private Long id;
	
    
	public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
    
    
}
