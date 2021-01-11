package com.projects.covid19.servicerequest.models;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_requests")
@Getter @Setter @NoArgsConstructor
public class ServiceRequest extends BaseModel {
	
	@NotBlank
	@Column(name = "item")
	private String item;
	
	@Min(1)
	@Column(name = "quantity")
	private int quantity;
	
	@NotBlank
	@Column(name = "unit")
	private String unit;
	
	@NotNull
	@Basic
	@Column(name = "requested_date")
	private LocalDateTime requestedDate;
	
	@Column(name = "requestee_id")
	private Long requesteeId;
	
	@NotNull
	@Column(name = "requester_id")
	private Long requesterId;
	
	@NotBlank
	@Column(name = "requester_username")
	private String requesterUsername;
	
	@Column(name = "accepter_id")
	private Long accepterId;
	
	@NotNull
	@Basic
	@Column(name = "accepted_date")
	private LocalDateTime acceptedDate;
	
	@NotNull
	@Column(name = "status")
	private StatusTypeEnum status;

}
