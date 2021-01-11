package com.projects.covid19.serviceitem.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import com.projects.covid19.serviceitem.models.ServiceItem;
import com.projects.covid19.serviceitem.repositories.ServiceItemRepository;

@Service
public class ServiceItemService {

	@Autowired
	private ServiceItemRepository serviceItemRepository;

	@Autowired
	private RedisTemplate<?, ?> redisTemplate;

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

	public List<String> getValuesByKeysPatternScan(String pattern) {
		String redisSearch = "service_item:name:".concat(pattern).concat("*");
		long countValue = 10;
		List<String> itemNames = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(redisSearch).count(countValue).build();
		boolean done = false;

		while (!done) {
			try (Cursor<byte[]> c = redisTemplate.getRequiredConnectionFactory().getConnection().scan(options)) {
				while (c.hasNext()) {
					itemNames.add(new String(c.next()).split(":")[2]);
				}
				done = true;
			} catch (NoSuchElementException | IOException nse) {
				options = ScanOptions.scanOptions().match(redisSearch).count(countValue * 2).build();
			}
		}
		return itemNames;
	}
}
