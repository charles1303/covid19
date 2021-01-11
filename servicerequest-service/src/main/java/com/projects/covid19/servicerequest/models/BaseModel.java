package com.projects.covid19.servicerequest.models;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5947694330131990840L;
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
    
	public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
    
    
}
