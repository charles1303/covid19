package com.projects.covid19.servicerequest.dto.requests;

import java.io.Serializable;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ServiceRequestDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1540314902325032421L;
	
	private Long serviceRequestId; 

	@NotBlank
	private String item;
	
	@Min(1)
	private int quantity;
	
	@NotBlank
	private String unit;
	
	@NotBlank
	private String requestedDate;
	
	private Long requesteeId;
	
	@NotNull
	private Long requesterId;
	
	@Column(name = "accepter_id")
	private Long accepterId;
	
	@NotBlank
	private String acceptedDate;
	
	@NotNull
	@Column(name = "status")
	private StatusTypeEnum status;


}
