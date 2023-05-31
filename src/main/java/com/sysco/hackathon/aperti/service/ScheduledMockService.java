package com.sysco.hackathon.aperti.service;

import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.dto.schedule.OpeningHoursDTO;
import com.sysco.hackathon.aperti.dto.schedule.PeriodDTO;
import com.sysco.hackathon.aperti.dto.schedule.WindowStatusDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ScheduledMockService {

    Random random = new Random();

    public List<WindowItemDTO> getMockSchedules() {
        List<OpeningHoursDTO> scheduledOpenHoursList = new ArrayList<>();
        List<WindowItemDTO> windowItemsList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int noOfWindows = random.ints(1, 7).findFirst().orElse(0);
            List<PeriodDTO> periods = new ArrayList<>();
            for (int j = 0; j < noOfWindows; j++) {
                int openHour = random.ints(0, 12).findFirst().orElse(0);
                String openHourStr = String.valueOf(openHour);
                if(openHour < 10) {
                    openHourStr = "0" + openHourStr;
                }
                int closeHour = random.ints(12, 24).findFirst().orElse(0);
                String closeHourStr = String.valueOf(closeHour);
                WindowStatusDTO open = WindowStatusDTO.builder().time(openHourStr + ":00").day(String.valueOf(j)).build();
                WindowStatusDTO close = WindowStatusDTO.builder().time(closeHourStr + ":00").day(String.valueOf(j)).build();
                PeriodDTO period = PeriodDTO.builder().close(close).open(open).build();
                periods.add(period);

                WindowItemDTO windowItem = WindowItemDTO.builder().from(openHourStr + ":00").to(closeHourStr + ":00").build();
                windowItemsList.add(windowItem);

            }
            OpeningHoursDTO openingHours = OpeningHoursDTO.builder().periods(periods).build();
            scheduledOpenHoursList.add(openingHours);
        }
        return windowItemsList;
    }
}
