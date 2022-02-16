package com.tenniscourts.tenniscourts;

import static com.tenniscourts.tenniscourts.TennisCourtControllerIntegrationTest.TEST_TENNIS_COURT_DTO;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapperImpl;
import com.tenniscourts.schedules.ScheduleService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@Import(value = {
    ScheduleService.class,
    ScheduleMapperImpl.class,
    TennisCourtService.class,
    TennisCourtMapperImpl.class
})
class TennisCourtServiceIntegrationTest {

  @Autowired
  TennisCourtService sut;

  @Autowired
  TennisCourtRepository tennisCourtRepository;

  @Test
  void shouldAddTennisCourt() {
    // When
    TennisCourtDTO actualTennisCourtDto = sut.addTennisCourt(TEST_TENNIS_COURT_DTO);

    // Expect
    Optional<TennisCourt> actualTennisCourt = tennisCourtRepository.findById(2L);
    assertThat(actualTennisCourt).isPresent();
    assertThat(actualTennisCourt.get()).usingRecursiveComparison()
        .ignoringFields(
            "dateCreate", "dateUpdate", "ipNumberCreate",
            "ipNumberUpdate", "userCreate", "userUpdate")
        .isEqualTo(TennisCourt.builder()
            .id(2L)
            .name("Test Tennis Court")
            .build());
    assertThat(actualTennisCourtDto).isEqualTo(TEST_TENNIS_COURT_DTO);
  }

  @Test
  void shouldFindTennisCourtWithSchedulesById() {
    // Given
    TennisCourtDTO expectedTennisCourtDTO = new TennisCourtDTO();
    expectedTennisCourtDTO.setName("Roland Garros - Court Philippe-Chatrier");
    expectedTennisCourtDTO.setId(1L);

    ScheduleDTO firstScheduleDTO = new ScheduleDTO();
    firstScheduleDTO.setId(1L);
    firstScheduleDTO.setTennisCourtId(1L);
    firstScheduleDTO.setStartDateTime(LocalDateTime.parse("2022-12-20T20:00:00.0"));
    firstScheduleDTO.setEndDateTime(LocalDateTime.parse("2025-02-20T21:00:00.0"));

    ScheduleDTO secondScheduleDTO = new ScheduleDTO();
    secondScheduleDTO.setId(2L);
    secondScheduleDTO.setTennisCourtId(1L);
    secondScheduleDTO.setStartDateTime(LocalDateTime.parse("2020-12-20T20:00:00.0"));
    secondScheduleDTO.setEndDateTime(LocalDateTime.parse("2020-05-20T21:00:00.0"));

    ScheduleDTO thirdScheduleDTO = new ScheduleDTO();
    thirdScheduleDTO.setId(3L);
    thirdScheduleDTO.setTennisCourtId(1L);
    thirdScheduleDTO.setStartDateTime(LocalDateTime.parse("2024-12-20T20:00:00.0"));
    thirdScheduleDTO.setEndDateTime(LocalDateTime.parse("2025-05-20T21:00:00.0"));

    expectedTennisCourtDTO.setTennisCourtSchedules(
        asList(firstScheduleDTO, secondScheduleDTO, thirdScheduleDTO)
    );

    // When
    TennisCourtDTO actualTennisCourtDto = sut.findTennisCourtWithSchedulesById(1L);

    // Then
    Optional<TennisCourt> actualTennisCourt = tennisCourtRepository.findById(1L);
    assertThat(actualTennisCourt).isPresent();
    assertThat(actualTennisCourt.get()).usingRecursiveComparison()
        .ignoringFields(
            "dateCreate", "dateUpdate", "ipNumberCreate",
            "ipNumberUpdate", "userCreate", "userUpdate", "schedules")
        .isEqualTo(TennisCourt.builder()
            .id(1L)
            .name("Roland Garros - Court Philippe-Chatrier")
            .build());
    assertThat(actualTennisCourtDto).usingRecursiveComparison()
        .ignoringFields("tennisCourtSchedules")
        .isEqualTo(expectedTennisCourtDTO);

    assertThat(actualTennisCourtDto.getTennisCourtSchedules())
        .containsAll(expectedTennisCourtDTO.getTennisCourtSchedules());
  }

  @Test
  void shouldThrowExceptionWhenTennisCourtNotFound() {
    assertThatThrownBy(() ->  sut.findTennisCourtById(
        1000L
    ))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Tennis Court not found.");
  }
}