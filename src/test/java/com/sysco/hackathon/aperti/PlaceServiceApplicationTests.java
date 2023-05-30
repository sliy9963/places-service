package com.sysco.hackathon.aperti;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.hackathon.aperti.dto.OpCoDetailsDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;


class PlaceServiceApplicationTests {

	@Test
	void contextLoads() throws URISyntaxException, IOException {
		ApiUtils apiUtils = new ApiUtils();
		Resource resource = new ClassPathResource("opcoDetails.json");
		Map<String, OpCoDetailsDTO> map = new ObjectMapper()
				.readValue(resource.getInputStream(), new TypeReference<>() {});
		System.out.println(map);
	}

}
