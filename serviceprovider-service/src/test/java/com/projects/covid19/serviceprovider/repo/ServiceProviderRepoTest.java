package com.projects.covid19.serviceprovider.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.serviceprovider.models.ServiceCategory;
import com.projects.covid19.serviceprovider.models.ServiceProvider;
import com.projects.covid19.serviceprovider.repositories.ServiceCategoryRepository;
import com.projects.covid19.serviceprovider.repositories.ServiceProviderRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ServiceProviderRepoTest {
	
	/*@Test
	public void testException() {
		Exception exc = assertThrows(
		IllegalArgumentException.class,
		() -> whenFindByCategoryCodeAndProviderId_thenReturnServiceProvider());
		assertTrue(exc.getMessage().contains("Exception thrown"));
	}*/
	
	
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;
	
	@SpyBean
	private Random random;
	
	//@Test
	public void whenFindByCategoryCodeAndProviderId_thenReturnServiceProvider() {
		
		ServiceProvider sp = new ServiceProvider();
		
		sp.setAddress("Address1");
		sp.setEmail("sp@cat.com");
		sp.setName("SP1");
		sp.setPhoneNumber("08012345678");
		sp.setRegNumber("00123422");
		ServiceProvider spSaved = serviceProviderRepository.save(sp);
		
		ServiceCategory cat1 = new ServiceCategory();
		cat1.setName("cat1");
		cat1.setCode("cat1");
		
		ServiceCategory cat1Saved =serviceCategoryRepository.save(cat1);
		
		spSaved.getCategories().add(cat1Saved);
		
		ServiceCategory cat2 = new ServiceCategory();
		cat2.setName("cat2");
		cat2.setCode("cat2");
		
		ServiceCategory cat2Saved =serviceCategoryRepository.save(cat2);
		
		spSaved.getCategories().add(cat2Saved);
		
		serviceProviderRepository.save(spSaved);
				
		Optional<ServiceProvider> prov1 = serviceProviderRepository.findByCategoryCodeAndProviderId("cat1", spSaved.getId());
		Optional<ServiceProvider> prov2 = serviceProviderRepository.findByCategoryCodeAndProviderId("cat2", spSaved.getId());
		
		assertThat(!prov1.isEmpty());
		assertEquals(prov1.get().getName(), sp.getName());
		assertThat(!prov2.isEmpty());
		assertEquals(prov2.get().getName(), sp.getName());
		
	}
	
	//@Test()
	public void whenFindByCategoryCode_thenReturnServiceProviderList() {
		
		
		
		ServiceProvider sp = new ServiceProvider();
		
		sp.setAddress("Address1");
		sp.setEmail("sp@cat.com");
		sp.setName("SP1");
		sp.setPhoneNumber("08012345678");
		sp.setRegNumber("00123422");
		ServiceProvider spSaved = serviceProviderRepository.save(sp);
		
		ServiceProvider sp2 = new ServiceProvider();
		
		sp2.setAddress("Address2");
		sp2.setEmail("sp2@cat.com");
		sp2.setName("SP2");
		sp2.setPhoneNumber("08012345679");
		sp2.setRegNumber("00123423");
		ServiceProvider spSaved2 = serviceProviderRepository.save(sp2);
		
		ServiceProvider sp3 = new ServiceProvider();
		
		sp3.setAddress("Address3");
		sp3.setEmail("sp3@cat.com");
		sp3.setName("SP3");
		sp3.setPhoneNumber("08012345680");
		sp3.setRegNumber("00123424");
		ServiceProvider spSaved3 = serviceProviderRepository.save(sp3);
		
		ServiceCategory cat1 = new ServiceCategory();
		cat1.setName("cat1");
		cat1.setCode("cat1");
		
		ServiceCategory cat1Saved =serviceCategoryRepository.save(cat1);
		
		spSaved.getCategories().add(cat1Saved);
		spSaved2.getCategories().add(cat1Saved);
		spSaved3.getCategories().add(cat1Saved);
		
		ServiceCategory cat2 = new ServiceCategory();
		cat2.setName("cat2");
		cat2.setCode("cat2");
		
		ServiceCategory cat2Saved =serviceCategoryRepository.save(cat2);
		
		spSaved.getCategories().add(cat2Saved);
		spSaved2.getCategories().add(cat2Saved);
		spSaved3.getCategories().add(cat2Saved);
		
		serviceProviderRepository.save(spSaved);
		serviceProviderRepository.save(spSaved2);
		serviceProviderRepository.save(spSaved3);
		
		List<ServiceProvider> provs1 = serviceProviderRepository.findByCategoryCode("cat1");
		List<ServiceProvider> provs2 = serviceProviderRepository.findByCategoryCode("cat2");
		
		assertThat(!provs1.isEmpty());
		assertEquals(provs1.size(), 3);
		assertThat(provs1.contains(spSaved));
		assertThat(provs1.contains(spSaved2));
		assertThat(provs1.contains(spSaved3));
		
		
		assertThat(!provs2.isEmpty());
		assertEquals(provs2.size(), 3);	
		assertThat(provs2.contains(spSaved));
		assertThat(provs2.contains(spSaved2));
		assertThat(provs2.contains(spSaved3));
		
		
		
	}
	
	@Test()
	public void whenFindByCategoryCode_thenReturnRandomServiceProviderList() {
		saveRandomServiceProviders();
		for (int i = 0; i < 10; i++) {
			getRandomServiceProviders();
		}
	}
	
	private void saveRandomServiceProviders() {
		List<ServiceCategory> cats = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ServiceCategory cat1 = new ServiceCategory();
			cat1.setName("cat"+i);
			cat1.setCode("cat"+i);
			
			cats.add(serviceCategoryRepository.save(cat1));
		}
		
		
		
		for (int i = 0; i < 20; i++) {
			ServiceProvider sp = new ServiceProvider();
			
			sp.setAddress("Address"+i);
			sp.setEmail("sp"+i+"@cat.com");
			sp.setName("SP"+i);
			sp.setPhoneNumber("0801234567"+i);
			sp.setRegNumber("0012342"+i);
			ServiceProvider spSaved = serviceProviderRepository.save(sp);
			
			spSaved.getCategories().addAll(cats);
			serviceProviderRepository.save(spSaved);
		}
		
	}
	
	private List<ServiceProvider> getRandomServiceProviders() {
		long qty = serviceProviderRepository.getCountByPageCategoryCode("cat1");
		System.out.println("qty============================="+qty);
		//int idx = (int)(Math.random() * (qty/6 + 1));
		int idx = random.nextInt((int)(qty/6 + 1));
		System.out.println("idx============================="+idx);
		Page<ServiceProvider> spPage = serviceProviderRepository.findByPageCategoryCode("cat1",PageRequest.of(idx, 6));
		List<ServiceProvider> sp = new ArrayList<>();
		if(spPage.hasContent()) {
			sp = spPage.getContent();
		}
		
		sp.stream()
		.map(p -> p.getEmail())
		.forEach(System.out::println);
		
		System.out.println("=============================");
		
		return sp;
		
	}
	
	@Test
	public void testShuffle() {
		List<Integer> nums = IntStream.range(1, 10)
				.boxed()
				.collect(Collectors.toList());
		
		
		for (int i = 0; i < 5; i++) {
			printNums(nums);
		}
		
	}

	private void printNums(List<Integer> nums) {
		Collections.shuffle(nums);
		
		nums.stream()
		.forEach(System.out::println);
		
		System.out.println("=============================");
	}

}
