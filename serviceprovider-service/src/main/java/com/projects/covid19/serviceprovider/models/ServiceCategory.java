package com.projects.covid19.serviceprovider.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_category")
@Getter @Setter
public class ServiceCategory extends BaseModel implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8537593073251332335L;
	private String name;
	private String description;
	private String code;

}
