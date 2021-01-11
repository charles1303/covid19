package com.projects.covid19.servicerequest.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import com.projects.covid19.servicerequest.utils.RoleType;

@Entity
@Table(name = "roles")
public class Role extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3109460123765881212L;
	@Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private RoleType type;

	public RoleType getType() {
		return type;
	}

	public void setType(RoleType type) {
		this.type = type;
	}


}
