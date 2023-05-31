package com.sysco.hackathon.aperti.service;

import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ScheduledMockService {

    Random random = new Random();

    public List<WindowItemDTO> getMockSchedules() {
        List<WindowItemDTO> windowItemsList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int openHour = random.ints(0, 12).findFirst().orElse(0);
            String openHourStr = String.valueOf(openHour);
            if (openHour < 10) {
                openHourStr = "0" + openHourStr;
            }
            int closeHour = random.ints(12, 24).findFirst().orElse(0);
            String closeHourStr = String.valueOf(closeHour);
            WindowItemDTO windowItem = WindowItemDTO.builder().from(openHourStr + ":00").to(closeHourStr + ":00").build();
            windowItemsList.add(windowItem);
        }
        return windowItemsList;
    }
}
