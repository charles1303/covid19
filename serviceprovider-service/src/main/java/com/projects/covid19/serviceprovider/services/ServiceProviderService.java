package com.projects.covid19.serviceprovider.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.serviceprovider.models.ServiceCategory;
import com.projects.covid19.serviceprovider.models.ServiceProvider;
import com.projects.covid19.serviceprovider.repositories.ServiceCategoryRepository;
import com.projects.covid19.serviceprovider.repositories.ServiceProviderRepository;

import lombok.Setter;

@Service
@Setter
public class ServiceProviderService {

	@Value("${notification.service.url}")
	private String notificationServiceUrl;

	@Autowired
	private ServiceProviderRepository serviceProviderRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@Autowired
	private RestTemplate template;

	Optional<ServiceProvider> findByEmail(String email) {
		return serviceProviderRepository.findByEmail(email);
	}

	Optional<ServiceProvider> findByName(String name) {
		return serviceProviderRepository.findByName(name);
	}

	public ServiceProvider saveServiceProvider(ServiceProvider provider) {

		return serviceProviderRepository.save(provider);

	}

	public List<ServiceProvider> getServiceProviders() {

		return serviceProviderRepository.findAll();

	}

	public Optional<ServiceProvider> getServiceProvider(Long providerId) {

		return serviceProviderRepository.findById(providerId);

	}

	public ServiceProvider addCategoryToProvider(Long providerId, Long categoryId) {

		Optional<ServiceProvider> providerOpt = serviceProviderRepository.loadProviderWithCategories(providerId);

		if (providerOpt.isEmpty())
			return null;
		ServiceProvider provider = providerOpt.get();

		Optional<ServiceCategory> categoryOpt = serviceCategoryRepository.findById(categoryId);

		if (categoryOpt.isEmpty())
			return null;

		ServiceCategory category = categoryOpt.get();

		provider.getCategories().add(category);

		return serviceProviderRepository.save(provider);

	}

	public ServiceProvider addCategoriesToProvider(Long providerId, List<Long> categoryIds) {

		Optional<ServiceProvider> providerOpt = serviceProviderRepository.loadProviderWithCategories(providerId);

		if (providerOpt.isEmpty())
			return null;
		ServiceProvider provider = providerOpt.get();

		List<ServiceCategory> categories = serviceCategoryRepository.findByCategoryIds(categoryIds);

		if (categories.isEmpty())
			return null;

		provider.getCategories().addAll(categories);

		return serviceProviderRepository.save(provider);

	}

	public Optional<ServiceProvider> findByCategoryCodeAndProviderId(String categoryCode, Long providerId) {
		return serviceProviderRepository.findByCategoryCodeAndProviderId(categoryCode, providerId);
	}

	public List<ServiceProvider> findByCategoryCode(String categoryCode) {
		return serviceProviderRepository.findByCategoryCode(categoryCode);
	}

	public List<ServiceProvider> getRandomServiceProviders() {
		long qty = serviceProviderRepository.count();
		int idx = (int) (Math.random() * qty);
		Page<ServiceProvider> spPage = serviceProviderRepository.findAll(PageRequest.of(idx, 10));
		List<ServiceProvider> sp = new ArrayList<>();
		if (spPage.hasContent()) {
			sp = spPage.getContent();
		}
		return sp;

	}

	public List<ServiceProvider> getRandomServiceProvidersByCategoryCode(String categoryCode, int pageSize) {
		long qty = serviceProviderRepository.getCountByPageCategoryCode(categoryCode);
		int idx = (int) (Math.random() * (qty / pageSize + 1));
		Page<ServiceProvider> spPage = serviceProviderRepository.findByPageCategoryCode(categoryCode,
				PageRequest.of(idx, pageSize));
		List<ServiceProvider> sp = new ArrayList<>();
		if (spPage.hasContent()) {
			sp = spPage.getContent();
		}
		return sp;

	}

	public List<ServiceProvider> getRandomServiceProvidersByIds(List<Long> idList) {
		int pageSize = 10;
		long qty = serviceProviderRepository.getCountByPageIds(idList);
		int idx = (int) (Math.random() * (qty / pageSize + 1));
		Page<ServiceProvider> spPage = serviceProviderRepository.findByPageIds(idList, PageRequest.of(idx, pageSize));
		List<ServiceProvider> sp = new ArrayList<>();
		if (spPage.hasContent()) {
			sp = spPage.getContent();
		}
		return sp;

	}

	public void notifyServiceProvidersForPendingRequests(List<Long> idList) throws URISyntaxException {

		List<ServiceProvider> serviceProviders = getRandomServiceProvidersByIds(idList);

		List<String> emails = serviceProviders.stream().filter(sp -> !sp.getEmail().equalsIgnoreCase(""))
				.map(sp -> sp.getEmail()).collect(Collectors.toList());

		if (!emails.isEmpty()) {
			callNotificationEndPoint(emails);
		}

	}

	@Async
	public void callNotificationEndPoint(List<String> emails) throws URISyntaxException {
		String uri = notificationServiceUrl + "/emails";
		URI fooResourceUrl = new URI(uri);
		HttpEntity<List<String>> request = new HttpEntity<>(emails);
		template.postForObject(fooResourceUrl, request, Object.class);
		System.out.println("Called callNotificationEndPoint.....");
	}

}
