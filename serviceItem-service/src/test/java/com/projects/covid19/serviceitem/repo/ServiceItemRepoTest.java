package com.projects.covid19.serviceitem.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.serviceitem.models.ServiceCategory;
import com.projects.covid19.serviceitem.models.ServiceItem;
import com.projects.covid19.serviceitem.repositories.ServiceCategoryRepository;
import com.projects.covid19.serviceitem.repositories.ServiceItemRepository;

import lombok.Getter;
import lombok.Setter;
import redis.embedded.RedisServer;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
@DataRedisTest
public class ServiceItemRepoTest {
	
	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;
	
	@Autowired
	private ServiceItemRepository serviceItemRepository;
	
	@Autowired
	private RedisTemplate<?, ?> redisTemplate;

	@TestConfiguration
	static class SpringConfig {
		private RedisServer redisServer;

		public SpringConfig(RedisPropConfig redisPropertiesConfig) {
			this.redisServer = new RedisServer(redisPropertiesConfig.getRedisPort());
		}

		@PostConstruct
		public void postConstruct() {
			redisServer.start();
		}

		@PreDestroy
		public void preDestroy() {
			redisServer.stop();
		}
	}

	@TestConfiguration
	@Getter
	@Setter
	static class RedisPropConfig {
		private int redisPort;
		private String redisHost;

		public RedisPropConfig(@Value("${spring.redis.port}") int redisPort,
				@Value("${spring.redis.host}") String redisHost) {
			this.redisPort = redisPort;
			this.redisHost = redisHost;
		}
	}

	

	//@Test
	public void shouldSaveCategory_toRedis() {
		ServiceCategory category = new ServiceCategory();
		String name = "Cat1";
		
		category.setName(name);

		ServiceCategory saved = serviceCategoryRepository.save(category);
		System.out.println("Printing Id here ============"+saved.getId());
		System.out.println("Printing name here ============"+saved.getName());
				
		ServiceCategory found = serviceCategoryRepository.findByName(name).get();

		assertEquals(saved.getName(), category.getName());
		assertEquals(saved.getName(), found.getName());
	}
	
	//@Test
	public void shouldGetCategory_whenExampleMatcherIsGiven() {
		String name = "Cat2";
		String name2 = "Cat2";
		
		ServiceCategory category = new ServiceCategory();
		category.setName(name);
		
		ServiceCategory saved = serviceCategoryRepository.save(category);
		System.out.println("Printing Id 2 here ============"+saved.getId());
		System.out.println("Printing name 2 here ============"+saved.getName());
		
		ServiceCategory category2 = new ServiceCategory();
		category2.setName(name2);
		
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("name", match -> match.exact())
				.withIgnoreCase(false)
				.withIgnorePaths("description","code");
		
		Example<ServiceCategory> found = Example.of(category2, matcher);
		
		Iterable<ServiceCategory> categories =  serviceCategoryRepository.findAll(found);
		
		for(ServiceCategory cat : categories) {
			System.out.println("Printing name 3  here ============"+cat.getName());
		}
		
		
		
		
	}
	
	//@Test
	public void shouldGetItems_whenParentCategoryIdExampleMatcherIsGiven() {
		
		Iterable<ServiceItem> items = saveItems();
		for(ServiceItem item : items) {
			System.out.println("Printing name 4  here ============"+item.getName());
		}
		
			
	}
	
	@Test
	public void shouldScanRedis_AndReturnItemsWithNamePatternProvided() {
		Iterable<ServiceItem> items = saveItems();
		//List<String> itemNames = getValuesByKeysPatternScan("service_item:name:*",100);
		List<String> itemNames = getValuesByKeysPatternScan("Met");
		
		assertTrue(itemNames.contains("Met i1"));
		assertTrue(itemNames.contains("Met i2"));
		//Iterator<ServiceItem> iter = items.iterator();
		/*while (iter.hasNext()) {
			assertTrue(itemNames.contains(iter.next().getName()));
			
			
		}*/
	}

	private Iterable<ServiceItem> saveItems() {
		String name = "Cat2";
				
		ServiceCategory category = new ServiceCategory();
		category.setName(name);
		
		ServiceCategory saved = serviceCategoryRepository.save(category);
		
		ServiceItem itemMatch = new ServiceItem();
		itemMatch.setServiceCategoryId(saved.getId());
				
		ServiceItem item1 = new ServiceItem();
		item1.setName("Item1");
		item1.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(item1);
				
		ServiceItem item2 = new ServiceItem();
		item2.setName("Item2");
		item2.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(item2);
		
		ServiceItem item3 = new ServiceItem();
		item3.setName("Item3");
		item3.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(item3);
		
		ServiceItem meti1 = new ServiceItem();
		meti1.setName("Met i1");
		meti1.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(meti1);
		
		ServiceItem meti2 = new ServiceItem();
		meti2.setName("Met i2");
		meti2.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(meti2);
		
		ServiceItem meti3 = new ServiceItem();
		meti3.setName("Meti2");
		meti3.setServiceCategoryId(saved.getId());
		serviceItemRepository.save(meti3);
		
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("serviceCategoryId", match -> match.exact())
				.withIgnoreCase(false)
				.withIgnorePaths("description","code","name");
		
		Example<ServiceItem> itemMatcher = Example.of(itemMatch, matcher);
		
		Iterable<ServiceItem> items = serviceItemRepository.findAll(itemMatcher);
		return items;
	}
		
	
	public List<String> getValuesByKeysPatternScan(String pattern) {
		String redisSearch = "service_item:name:".concat(pattern).concat("*");
		long countValue = 2;
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
