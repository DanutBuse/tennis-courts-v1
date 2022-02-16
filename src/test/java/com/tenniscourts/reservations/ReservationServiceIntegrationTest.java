package com.tenniscourts.reservations;

import static com.tenniscourts.util.Assertions.assertEqualsReservations;
import static com.tenniscourts.util.Assertions.assertEqualsReservationsDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapperImpl;
import com.tenniscourts.tenniscourts.TennisCourt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(value = {ReservationService.class, ReservationMapperImpl.class, ScheduleMapperImpl.class})
class ReservationServiceIntegrationTest {

  static final TennisCourt EXPECTED_TENNIS_COURT = TennisCourt.builder()
      .id(1L)
      .name("Roland Garros - Court Philippe-Chatrier")
      .build();

  static final Schedule EXPECTED_SCHEDULE = Schedule.builder()
      .id(1L)
      .tennisCourt(EXPECTED_TENNIS_COURT)
      .startDateTime(LocalDateTime.parse("2022-12-20T20:00"))
      .endDateTime(LocalDateTime.parse("2025-02-20T21:00"))
      .build();

  static final Schedule EXPECTED_NEW_SCHEDULE = Schedule.builder()
      .id(3L)
      .tennisCourt(EXPECTED_TENNIS_COURT)
      .startDateTime(LocalDateTime.parse("2024-12-20T20:00"))
      .endDateTime(LocalDateTime.parse("2025-05-20T21:00"))
      .build();

  static final Guest EXPECTED_GUEST = Guest.builder()
      .id(1L)
      .name("Roger Federer")
      .build();

  static final Schedule EXPECTED_NOT_SHOW_UP_SCHEDULE = Schedule.builder()
      .id(2L)
      .tennisCourt(EXPECTED_TENNIS_COURT)
      .startDateTime(LocalDateTime.parse("2020-12-20T20:00"))
      .endDateTime(LocalDateTime.parse("2020-05-20T21:00"))
      .build();

  static final Reservation EXPECTED_NOT_SHOW_UP_RESERVATION = Reservation.builder()
      .id(2L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_NOT_SHOW_UP_SCHEDULE)
      .value(BigDecimal.TEN)
      .reservationStatus(ReservationStatus.NOT_SHOW_UP)
      .refundValue(BigDecimal.ZERO)
      .build();

  static final Reservation EXPECTED_READY_TO_PLAY_PAST_RESERVATION = Reservation.builder()
      .id(2L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_NOT_SHOW_UP_SCHEDULE)
      .value(BigDecimal.TEN)
      .reservationStatus(ReservationStatus.READY_TO_PLAY)
      .refundValue(BigDecimal.ZERO)
      .build();

  static final Reservation EXPECTED_CANCELLED_RESERVATION = Reservation.builder()
      .id(1L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_SCHEDULE)
      .value(BigDecimal.ZERO)
      .reservationStatus(ReservationStatus.CANCELLED)
      .refundValue(BigDecimal.TEN)
      .build();

  static final Reservation EXPECTED_READY_TO_PLAY_RESERVATION = Reservation.builder()
      .id(1L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_SCHEDULE)
      .value(BigDecimal.TEN)
      .reservationStatus(ReservationStatus.READY_TO_PLAY)
      .refundValue(BigDecimal.ZERO)
      .build();

  static final Reservation EXPECTED_OLD_RESCHEDULED_RESERVATION = Reservation.builder()
      .id(1L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_SCHEDULE)
      .value(BigDecimal.ZERO)
      .reservationStatus(ReservationStatus.RESCHEDULED)
      .refundValue(BigDecimal.TEN)
      .build();

  static final Reservation EXPECTED_NEW_RESCHEDULED_RESERVATION = Reservation.builder()
      .id(4L)
      .guest(EXPECTED_GUEST)
      .schedule(EXPECTED_NEW_SCHEDULE)
      .value(BigDecimal.TEN)
      .reservationStatus(ReservationStatus.READY_TO_PLAY)
      .refundValue(BigDecimal.ZERO)
      .build();

  static ScheduleDTO expectedScheduleDto = new ScheduleDTO();
  static ScheduleDTO expectedNotShowUpScheduleDto = new ScheduleDTO();
  static ScheduleDTO expectedNewScheduleDto = new ScheduleDTO();

  static final ReservationDTO EXPECTED_NOT_SHOW_UP_RESERVATION_DTO = ReservationDTO.builder()
      .id(2L)
      .guestId(1L)
      .schedule(expectedNotShowUpScheduleDto)
      .reservationStatus("NOT_SHOW_UP")
      .value(BigDecimal.TEN)
      .refundValue(BigDecimal.ZERO)
      .scheduledId(2L)
      .build();

  static final ReservationDTO EXPECTED_READY_TO_PLAY_PAST_RESERVATION_DTO = ReservationDTO.builder()
      .id(2L)
      .guestId(1L)
      .schedule(expectedNotShowUpScheduleDto)
      .reservationStatus("READY_TO_PLAY")
      .value(BigDecimal.TEN)
      .refundValue(BigDecimal.ZERO)
      .scheduledId(2L)
      .build();

  static final ReservationDTO EXPECTED_CANCELLED_RESERVATION_DTO = ReservationDTO.builder()
      .id(1L)
      .guestId(1L)
      .schedule(expectedScheduleDto)
      .reservationStatus("CANCELLED")
      .value(BigDecimal.ZERO)
      .refundValue(BigDecimal.TEN)
      .scheduledId(1L)
      .build();

  static final ReservationDTO EXPECTED_READY_TO_PLAY_RESERVATION_DTO = ReservationDTO.builder()
      .id(1L)
      .guestId(1L)
      .schedule(expectedScheduleDto)
      .reservationStatus("READY_TO_PLAY")
      .value(BigDecimal.TEN)
      .refundValue(BigDecimal.ZERO)
      .scheduledId(1L)
      .build();

  static final ReservationDTO EXPECTED_NEW_RESCHEDULED_RESERVATION_DTO = ReservationDTO.builder()
      .id(4L)
      .guestId(1L)
      .schedule(expectedNewScheduleDto)
      .reservationStatus("READY_TO_PLAY")
      .value(BigDecimal.TEN)
      .refundValue(BigDecimal.ZERO)
      .scheduledId(3L)
      .previousReservation(
          ReservationDTO.builder()
              .id(1L)
              .guestId(1L)
              .schedule(expectedScheduleDto)
              .reservationStatus("RESCHEDULED")
              .value(BigDecimal.ZERO)
              .refundValue(BigDecimal.TEN)
              .scheduledId(1L)
              .build()
      )
      .build();

  @BeforeAll
  static void populateExpectedPojos() {
    expectedScheduleDto.setId(1L);
    expectedScheduleDto.setTennisCourtId(1L);
    expectedScheduleDto.setStartDateTime(LocalDateTime.parse("2022-12-20T20:00"));
    expectedScheduleDto.setEndDateTime(LocalDateTime.parse("2025-02-20T21:00"));

    expectedNotShowUpScheduleDto.setId(2L);
    expectedNotShowUpScheduleDto.setTennisCourtId(1L);
    expectedNotShowUpScheduleDto.setStartDateTime(LocalDateTime.parse("2020-12-20T20:00"));
    expectedNotShowUpScheduleDto.setEndDateTime(LocalDateTime.parse("2020-05-20T21:00"));

    expectedNewScheduleDto.setId(3L);
    expectedNewScheduleDto.setTennisCourtId(1L);
    expectedNewScheduleDto.setStartDateTime(LocalDateTime.parse("2024-12-20T20:00"));
    expectedNewScheduleDto.setEndDateTime(LocalDateTime.parse("2025-05-20T21:00"));
  }

  @Autowired
  ReservationService sut;

  @Autowired
  ReservationRepository reservationRepository;

  @Test
  void shouldBookReservation() {
    // Given
    Reservation expectedReservation =
        Reservation.builder()
            .id(3L)
            .guest(EXPECTED_GUEST)
            .schedule(EXPECTED_SCHEDULE)
            .value(BigDecimal.TEN)
            .reservationStatus(ReservationStatus.READY_TO_PLAY)
            .refundValue(BigDecimal.ZERO)
            .build();

    ReservationDTO expectedReservationDto =
        ReservationDTO.builder()
            .id(3L)
            .guestId(1L)
            .schedule(expectedScheduleDto)
            .reservationStatus("READY_TO_PLAY")
            .value(BigDecimal.TEN)
            .refundValue(BigDecimal.ZERO)
            .scheduledId(1L)
            .build();

    // When
    ReservationDTO actualReservationDto = sut.bookReservation(
        CreateReservationRequestDTO.builder()
            .guestId(1L)
            .scheduleId(1L)
            .build()
    );

    // Then
    Optional<Reservation> actualReservation = reservationRepository.findById(3L);
    assertThat(actualReservation).isPresent();
    assertEqualsReservations(actualReservation.get(), expectedReservation);
    assertEqualsReservationsDto(actualReservationDto, expectedReservationDto);
  }

  @Test
  void shouldThrowExceptionIfGuestDoesNotExist() {
    assertThatThrownBy(() ->  sut.bookReservation(
        CreateReservationRequestDTO.builder()
            .guestId(1001L)
            .scheduleId(1L)
            .build()
    ))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Guest not found.");
  }

  @Test
  void shouldThrowExceptionIfScheduleDoesNotExist() {
    assertThatThrownBy(() ->  sut.bookReservation(
        CreateReservationRequestDTO.builder()
            .guestId(1L)
            .scheduleId(1001L)
            .build()
    ))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Schedule not found.");
  }

  @Test
  void shouldFindReservation() {
    // When
    ReservationDTO actualReservationDto = sut.findReservation(1L);

    // Expect
    Optional<Reservation> actualReservation = reservationRepository.findById(1L);
    assertThat(actualReservation).isPresent();
    assertEqualsReservations(actualReservation.get(), EXPECTED_READY_TO_PLAY_RESERVATION);
    assertEqualsReservationsDto(actualReservationDto, EXPECTED_READY_TO_PLAY_RESERVATION_DTO);
  }

  @Test
  void shouldFindPastReservation() {
    // When
    List<ReservationDTO> actualReservationsDto = sut.getPastReservations();

    // Expect
    Optional<Reservation> actualReservation = reservationRepository.findById(2L);
    assertThat(actualReservation).isPresent();
    assertEqualsReservations(actualReservation.get(), EXPECTED_READY_TO_PLAY_PAST_RESERVATION);

    assertThat(actualReservationsDto).hasSize(1);
    assertEqualsReservationsDto(actualReservationsDto.get(0), EXPECTED_READY_TO_PLAY_PAST_RESERVATION_DTO);
  }

  @Test
  void shouldUpdateNotShowUpReservation() {
    // When
    List<ReservationDTO> actualReservationsDto = sut.updateNotShowUpReservationDeposit();

    // Expect
    Optional<Reservation> actualReservation = reservationRepository.findById(2L);
    assertThat(actualReservation).isPresent();
    assertEqualsReservations(actualReservation.get(), EXPECTED_NOT_SHOW_UP_RESERVATION);

    assertThat(actualReservationsDto).hasSize(1);
    assertEqualsReservationsDto(actualReservationsDto.get(0), EXPECTED_NOT_SHOW_UP_RESERVATION_DTO);
  }

  @Test
  void shouldCancelReservation() {
    // When
    ReservationDTO actualReservationsDto = sut.cancelReservation(1L);

    // Expect
    Optional<Reservation> actualReservation = reservationRepository.findById(1L);
    assertThat(actualReservation).isPresent();
    assertEqualsReservations(actualReservation.get(), EXPECTED_CANCELLED_RESERVATION);
    assertEqualsReservationsDto(actualReservationsDto, EXPECTED_CANCELLED_RESERVATION_DTO);
  }

  @Test
  void shouldRescheduleReservation() {
    // When
    ReservationDTO actualReservationsDto = sut.rescheduleReservation(1L, 3L);

    // Expect
    Optional<Reservation> actualOldReservation = reservationRepository.findById(1L);
    assertThat(actualOldReservation).isPresent();
    assertEqualsReservations(actualOldReservation.get(), EXPECTED_OLD_RESCHEDULED_RESERVATION);

    Optional<Reservation> actualNewReservation = reservationRepository.findById(4L);
    assertThat(actualNewReservation).isPresent();
    assertEqualsReservations(actualNewReservation.get(), EXPECTED_NEW_RESCHEDULED_RESERVATION);

    assertEqualsReservationsDto(actualReservationsDto, EXPECTED_NEW_RESCHEDULED_RESERVATION_DTO);
  }

}