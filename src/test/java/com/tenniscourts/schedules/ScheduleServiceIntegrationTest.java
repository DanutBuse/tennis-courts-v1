package com.tenniscourts.schedules;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(value = {ScheduleMapperImpl.class, ScheduleService.class})
@AutoConfigureDataJpa
class ScheduleServiceIntegrationTest {

  @Autowired
  ScheduleService sut;

  @Autowired
  ScheduleRepository scheduleRepository;

  @Test
  void shouldAddSchedule() {
    // Given
    CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
    createScheduleRequestDTO.setTennisCourtId(1L);
    createScheduleRequestDTO.setStartDateTime(LocalDateTime.parse("2020-12-20T00:00"));
    createScheduleRequestDTO.setEndDateTime(LocalDateTime.parse("2020-12-20T23:59"));

    ScheduleDTO expectedScheduleDto = new ScheduleDTO();
    expectedScheduleDto.setId(4L);
    expectedScheduleDto.setTennisCourtId(1L);
    expectedScheduleDto.setStartDateTime(LocalDateTime.parse("2020-12-20T00:00"));
    expectedScheduleDto.setEndDateTime(LocalDateTime.parse("2020-12-20T23:59"));

    Schedule expectedSchedule = Schedule.builder()
        .tennisCourt(TennisCourt.builder().id(1L).build())
        .startDateTime(LocalDateTime.parse("2020-12-20T00:00"))
        .endDateTime(LocalDateTime.parse("2020-12-20T23:59"))
        .id(4L)
        .build();

    // When
    ScheduleDTO actualScheduleDto = sut.addSchedule(createScheduleRequestDTO);

    // Then
    Optional<Schedule> actualSchedule = scheduleRepository.findById(4L);
    assertThat(actualSchedule).isPresent();
    assertThat(actualSchedule.get()).usingRecursiveComparison()
      .ignoringFields("dateCreate", "dateUpdate", "ipNumberCreate",
          "ipNumberUpdate", "userCreate", "userUpdate", "reservations")
      .isEqualTo(expectedSchedule);
    assertThat(actualScheduleDto)
        .usingRecursiveComparison().isEqualTo(expectedScheduleDto);
  }

  @Test
  void shouldFindScheduleById() {
    // Given
    ScheduleDTO expectedScheduleDto = new ScheduleDTO();
    expectedScheduleDto.setId(1L);
    expectedScheduleDto.setTennisCourtId(1L);
    expectedScheduleDto.setStartDateTime(LocalDateTime.parse("2022-12-20T20:00"));
    expectedScheduleDto.setEndDateTime(LocalDateTime.parse("2025-02-20T21:00"));

    // When
    ScheduleDTO actualScheduleDto = sut.findSchedule(1L);

    // Then
    assertThat(actualScheduleDto)
        .usingRecursiveComparison().isEqualTo(expectedScheduleDto);
  }

  @Test
  void shouldFindScheduleByDate() {
    // Given
    LocalDateTime startDate = LocalDateTime.parse("2022-12-20T20:00");
    LocalDateTime endDate = LocalDateTime.parse("2025-02-20T21:00");
    ScheduleDTO expectedScheduleDto = new ScheduleDTO();
    expectedScheduleDto.setId(1L);
    expectedScheduleDto.setTennisCourtId(1L);
    expectedScheduleDto.setStartDateTime(startDate);
    expectedScheduleDto.setEndDateTime(endDate);

    // When
    List<ScheduleDTO> actualScheduleDtos = sut.findSchedulesByDates(
        startDate.minus(1, SECONDS),
        endDate.plus(1, SECONDS)
    );

    // Then
    assertThat(actualScheduleDtos).hasSize(1);
    assertThat(actualScheduleDtos.get(0))
        .usingRecursiveComparison().isEqualTo(expectedScheduleDto);
  }

  @Test
  void shouldThrowExceptionWhenScheduleNotFound() {
    assertThatThrownBy(() ->  sut.findSchedule(
        1000L
    ))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Schedule not found.");
  }

  @Test
  void shouldFindSchedulesByTennisCourtId() {
    // Given
    ScheduleDTO firstExpectedScheduleDto = new ScheduleDTO();
    firstExpectedScheduleDto.setId(2L);
    firstExpectedScheduleDto.setTennisCourtId(1L);
    firstExpectedScheduleDto.setStartDateTime(LocalDateTime.parse("2020-12-20T20:00"));
    firstExpectedScheduleDto.setEndDateTime(LocalDateTime.parse("2020-05-20T21:00"));

    ScheduleDTO secondExpectedScheduleDto = new ScheduleDTO();
    secondExpectedScheduleDto.setId(1L);
    secondExpectedScheduleDto.setTennisCourtId(1L);
    secondExpectedScheduleDto.setStartDateTime(LocalDateTime.parse("2022-12-20T20:00"));
    secondExpectedScheduleDto.setEndDateTime(LocalDateTime.parse("2025-02-20T21:00"));

    ScheduleDTO thirdExpectedScheduleDto = new ScheduleDTO();
    thirdExpectedScheduleDto.setId(3L);
    thirdExpectedScheduleDto.setTennisCourtId(1L);
    thirdExpectedScheduleDto.setStartDateTime(LocalDateTime.parse("2024-12-20T20:00"));
    thirdExpectedScheduleDto.setEndDateTime(LocalDateTime.parse("2025-05-20T21:00"));

    // When
    List<ScheduleDTO> actualScheduleDtos = sut.findSchedulesByTennisCourtId(1L);

    // Then
    assertThat(actualScheduleDtos).containsExactlyInAnyOrder(
        firstExpectedScheduleDto, secondExpectedScheduleDto, thirdExpectedScheduleDto
    );
  }
}