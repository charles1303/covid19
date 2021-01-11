package com.projects.covid19.serviceprovider.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="service_providers")
@Getter @Setter
public class ServiceProvider extends BaseModel {
	
	@NotBlank
	@Column(name = "name", unique = true)
	private String name;
	
	@NotBlank
	@Column(name = "email", unique = true)
	private String email;
	
	@NotBlank
	@Column(name = "phone_number", unique = true)
	private String phoneNumber;
	
	@NotBlank
	@Column(name = "reg_Number", unique = true)
	private String regNumber;
	
	@NotBlank
	@Column(name = "address")
	private String address;
	
	@ManyToMany
	private List<ServiceCategory> categories = new ArrayList<>();
	
	@OneToMany
	private List<User> users = new ArrayList<>();
	
	@ManyToMany
	private List<ServiceProvider> attachedDeliveryAgents = new ArrayList<>();

}
