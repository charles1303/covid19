package com.projects.covid19.servicerequest.generative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.spring.JqwikSpringSupport;

@JqwikSpringSupport
@ContextConfiguration
public class ServiceRequestGenerativeTest {
	
	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
		return reverse(reverse(original)).equals(original);
	}

	private <T> List<T> reverse(List<T> original) {
		List<T> clone = new ArrayList<>(original);
		Collections.reverse(clone);
		return clone;
	}

}
