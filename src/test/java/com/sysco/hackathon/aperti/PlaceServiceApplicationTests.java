package com.sysco.hackathon.aperti;

import com.sysco.hackathon.aperti.util.ApiUtils;
import org.junit.jupiter.api.Test;


class PlaceServiceApplicationTests {

	@Test
	void contextLoads() {
		ApiUtils apiUtils = new ApiUtils();
		System.out.println(apiUtils.generateExceptionLevel());
	}

}
