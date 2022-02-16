package com.tenniscourts.schedules;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@WebMvcTest(
    controllers = ScheduleController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ScheduleControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ScheduleService scheduleService;

  @MockBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  static ScheduleDTO response = new ScheduleDTO();

  @BeforeAll
  static void populateScheduleDto() {
    response.setId(1L);
    response.setTennisCourtId(1L);
    response.setStartDateTime(LocalDateTime.parse("2020-12-20T00:00"));
    response.setEndDateTime(LocalDateTime.parse("2020-12-20T23:59"));
  }

  @Test
  void shouldAddScheduleToTennisCourt() throws Exception {
    when(scheduleService.addSchedule(
        any(CreateScheduleRequestDTO.class)
    )).thenReturn(response);

    this.mockMvc
        .perform(
            post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{"
                        + "\"tennisCourtId\": 1,"
                        + "\"startDateTime\":\"2020-12-20T00:00\","
                        + "\"endDateTime\":\"2020-12-20T23:59\""
                  + "}"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrl("http://localhost/schedules/1"));
  }

  @Test
  void shouldFindSchedule() throws Exception {
    when(scheduleService.findSchedule(1L)).thenReturn(response);

    this.mockMvc
        .perform(get("/schedules/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.startDateTime").value("2020-12-20T00:00"))
        .andExpect(jsonPath("$.endDateTime").value("2020-12-20T23:59"))
        .andExpect(jsonPath("$.tennisCourtId").value(1));
  }

  @Test
  void shouldFindSchedulesByDates() throws Exception {
    when(scheduleService.findSchedulesByDates(
        LocalDateTime.parse("2020-12-20T00:00"),
        LocalDateTime.parse("2020-12-20T23:59")
    )).thenReturn(singletonList(response));

    this.mockMvc
        .perform(
            get("/schedules")
            .param("startDate", "2020-12-20")
            .param("endDate", "2020-12-20")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].startDateTime").value("2020-12-20T00:00"))
        .andExpect(jsonPath("$.[0].endDateTime").value("2020-12-20T23:59"))
        .andExpect(jsonPath("$.[0].tennisCourtId").value(1));
  }

}