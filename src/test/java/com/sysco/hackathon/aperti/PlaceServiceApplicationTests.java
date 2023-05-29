package com.sysco.hackathon.aperti;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class PlaceServiceApplicationTests {

	@Test
	void contextLoads() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("Accept", APPLICATION_JSON_VALUE);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		System.out.println(entity);
	}

}
