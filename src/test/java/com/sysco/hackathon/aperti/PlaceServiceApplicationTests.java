package com.sysco.hackathon.aperti;

import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.service.ScheduledMockService;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlaceServiceApplicationTests {

	@Test
	void contextLoads() {
		ScheduledMockService scheduledMockService = new ScheduledMockService();
		List<WindowItemDTO> mockSchedules = scheduledMockService.getMockSchedules();
		mockSchedules.forEach(System.out::println);
		System.out.println(mockSchedules.size());
	}

}
