package com.projects.covid19.serviceprovider.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.serviceprovider.models.ServiceCategory;
import com.projects.covid19.serviceprovider.models.ServiceProvider;
import com.projects.covid19.serviceprovider.repositories.ServiceCategoryRepository;
import com.projects.covid19.serviceprovider.repositories.ServiceProviderRepository;

@ExtendWith(SpringExtension.class)
public class ServiceProviderServiceTest {
	
	@SpyBean
	private ServiceProviderService serviceProviderService;
	
	@MockBean
	private ServiceProviderRepository serviceProviderRepository;
	
	@MockBean
	private ServiceCategoryRepository serviceCategoryRepository;
	
	@MockBean
    private RestTemplate restTemplate;
	
	@Test
	public void addCategoryToProvider_ShouldReturnAServiceProviderWithAddedCategory_WhenCalled() {
		Long providerId = 1L;
		Long categoryId = 1L;
		
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setId(providerId);
		
		ServiceCategory serviceCategory = new ServiceCategory();
		serviceCategory.setId(categoryId);
		
		
		when(serviceProviderRepository.loadProviderWithCategories(providerId)).thenReturn(Optional.of(serviceProvider));
		
		when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(serviceCategory));
		when(serviceProviderRepository.save(serviceProvider)).thenReturn(serviceProvider);
		
		ServiceProvider saved = serviceProviderService.addCategoryToProvider(providerId, categoryId);
		
		assertTrue(saved.getCategories().contains(serviceCategory));
		
	}
	
	@Test
	public void addCategoriesToProvider_ShouldReturnAServiceProviderWithAddedCategories_WhenCalled() {
		Long providerId = 1L;
		Long categoryId = 1L;
		Long categoryId2 = 2L;
		
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setId(providerId);
		
		ServiceCategory serviceCategory = new ServiceCategory();
		serviceCategory.setId(categoryId);
		
		ServiceCategory serviceCategory2 = new ServiceCategory();
		serviceCategory2.setId(categoryId2);
		
		
		when(serviceProviderRepository.loadProviderWithCategories(providerId)).thenReturn(Optional.of(serviceProvider));
		
		when(serviceCategoryRepository.findByCategoryIds(List.of(categoryId,categoryId2))).thenReturn(List.of(serviceCategory,serviceCategory2));
		when(serviceProviderRepository.save(serviceProvider)).thenReturn(serviceProvider);
		
		ServiceProvider saved = serviceProviderService.addCategoriesToProvider(providerId, List.of(categoryId,categoryId2));
		
		assertTrue(saved.getCategories().contains(serviceCategory));
		assertTrue(saved.getCategories().contains(serviceCategory2));
		
	}
	
	@Test
	public void shouldCallNotificationEndPointWhenServiceProvidersAreFoundWithIds() throws URISyntaxException {
		
		ServiceProvider sp1 = new ServiceProvider();
		sp1.setId(1L);
		sp1.setEmail("sp@emai.com");
		
		ServiceProvider sp2 = new ServiceProvider();
		sp2.setId(1L);
		sp2.setEmail("sp2@emai.com");
		
		when(serviceProviderRepository.getCountByPageIds(any(List.class))).thenReturn(2L);
		
		when(serviceProviderRepository.findByPageIds(any(List.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(sp1,sp2)));
		
		doNothing().when(serviceProviderService).callNotificationEndPoint(any(List.class));
		
		serviceProviderService.notifyServiceProvidersForPendingRequests(List.of(1L,2L));
		
		verify(serviceProviderService, atLeast(1)).callNotificationEndPoint(any(List.class));
		
	}
	
	@Test
	public void shouldNotCallNotificationEndPointWhenServiceProvidersAreNotFoundWithIds() throws URISyntaxException {
		
		
		when(serviceProviderRepository.getCountByPageIds(any(List.class))).thenReturn(0L);
		
		when(serviceProviderRepository.findByPageIds(any(List.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));
		
		doNothing().when(serviceProviderService).callNotificationEndPoint(any(List.class));
		
		serviceProviderService.notifyServiceProvidersForPendingRequests(List.of(1L,2L));
		
		verify(serviceProviderService, never()).callNotificationEndPoint(any(List.class));
		
	}

}
