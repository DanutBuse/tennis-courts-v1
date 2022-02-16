package com.tenniscourts.tenniscourts;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tenniscourts.schedules.ScheduleDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = TennisCourtController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class TennisCourtControllerIntegrationTest {

  static final TennisCourtDTO TEST_TENNIS_COURT_DTO = TennisCourtDTO.builder()
      .id(2L)
      .name("Test Tennis Court")
      .build();

  @Autowired
  MockMvc mockMvc;

  @MockBean
  TennisCourtService tennisCourtService;

  @MockBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  void shouldAddTennisCourt() throws Exception {
    when(tennisCourtService.addTennisCourt(
        TEST_TENNIS_COURT_DTO
    )).thenReturn(TEST_TENNIS_COURT_DTO);

    this.mockMvc
        .perform(
            post("/tennis-courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{"
                        + "\"id\": 2,"
                        + "\"name\":\"Test Tennis Court\""
                  + "}"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrl("http://localhost/tennis-courts/2"));
  }

  @Test
  void shouldFindTennisCourtByIdExcludingSchedules() throws Exception {
    when(tennisCourtService.findTennisCourtById(
        2L
    )).thenReturn(TEST_TENNIS_COURT_DTO);

    this.mockMvc
        .perform(get("/tennis-courts/2"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.name").value("Test Tennis Court"));
  }

  @Test
  void shouldFindTennisCourtByIdIncludingSchedules() throws Exception {
    TennisCourtDTO tennisCourtDTO = TennisCourtDTO.builder()
        .id(2L)
        .name("Test Tennis Court")
        .tennisCourtSchedules(
            singletonList(
                new ScheduleDTO()
            )
        )
        .build();

    when(tennisCourtService.findTennisCourtWithSchedulesById(
        2L
    )).thenReturn(tennisCourtDTO);

    this.mockMvc
        .perform(get("/tennis-courts/2").param("includeSchedules", "true"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.name").value("Test Tennis Court"))
        .andExpect(jsonPath("$.tennisCourtSchedules[0]").exists());
  }
}