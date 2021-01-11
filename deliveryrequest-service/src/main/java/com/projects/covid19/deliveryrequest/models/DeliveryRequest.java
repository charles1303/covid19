package com.projects.covid19.deliveryrequest.models;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery_requests")
@Getter @Setter @NoArgsConstructor
public class DeliveryRequest extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6316642049827058842L;

	@NotNull
	@Column(name = "service_request_id")
	private Long serviceRequestId; 
	
	@NotBlank
	@Column(name = "item")
	private String item;
	
	@Min(1)
	@Column(name = "quantity")
	private int quantity;
	
	@NotBlank
	@Column(name = "unit")
	private String unit;
	
	@Column(name = "accepter_id")
	private Long accepterId;
	
	@Basic
	@Column(name = "accepted_date")
	private LocalDateTime acceptedDate;
	
	@Basic
	@Column(name = "delivery_date")
	private LocalDateTime deliveryDate;
	
	@NotBlank
	@Column(name = "name")
	private String name;
	
	@NotBlank
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@NotBlank
	@Column(name = "address")
	private String address;
	
	@NotNull
	@Column(name = "status")
	private StatusTypeEnum status;

}
