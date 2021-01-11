package com.projects.covid19.servicerequest.utils;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StatusTypeConverter implements AttributeConverter<StatusTypeEnum, String> {

	@Override
	public String convertToDatabaseColumn(StatusTypeEnum attribute) {
		if(attribute == null)
			return null;
		return attribute.getCode();
	}

	@Override
	public StatusTypeEnum convertToEntityAttribute(String dbData) {
		if(dbData == null)
			return null;
		
		return Stream.of(StatusTypeEnum.values())
				.filter(c -> c.getCode().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
