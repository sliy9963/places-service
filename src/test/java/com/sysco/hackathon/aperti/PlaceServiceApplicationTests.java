package com.sysco.hackathon.aperti;

import com.sysco.hackathon.aperti.dto.ScheduledDeliveryDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class PlaceServiceApplicationTests {

	@Test
	void contextLoads() {
		ApiUtils apiUtils = new ApiUtils();
		Map<String, List<ScheduledDeliveryDTO>> data = apiUtils.readScheduleFile("mockScheduledData.json");
		System.out.println(data);
	}

}
