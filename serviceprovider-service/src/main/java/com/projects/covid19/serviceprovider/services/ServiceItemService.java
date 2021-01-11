package com.projects.covid19.serviceprovider.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.projects.covid19.serviceprovider.models.ServiceItem;
import com.projects.covid19.serviceprovider.repositories.ServiceItemRepository;

@Service
public class ServiceItemService {

	@Autowired
	private ServiceItemRepository serviceItemRepository;

	public ServiceItem saveServiceItem(ServiceItem item, Long serviceCategoryId) {
		item.setServiceCategoryId(serviceCategoryId);
		return serviceItemRepository.save(item);

	}

	public Optional<ServiceItem> getServiceItemById(Long itemId) {

		return serviceItemRepository.findById(itemId);

	}

	public Iterable<ServiceItem> getByCategoryId(Long serviceCategoryId) {

		ServiceItem item = new ServiceItem();
		item.setServiceCategoryId(serviceCategoryId);

		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("serviceCategoryId", match -> match.exact())
				.withIgnoreCase(false).withIgnorePaths("description", "code", "name");

		Example<ServiceItem> itemMatcher = Example.of(item, matcher);

		return serviceItemRepository.findAll(itemMatcher);

	}
}
