package com.projects.covid19.serviceitem.models;

import com.projects.covid19.serviceitem.utils.RoleType;


public class Role extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3109460123765881212L;
	
    private RoleType type;

	public RoleType getType() {
		return type;
	}

	public void setType(RoleType type) {
		this.type = type;
	}


}
