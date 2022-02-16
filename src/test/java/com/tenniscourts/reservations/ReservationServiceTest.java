package com.tenniscourts.reservations;

import static com.tenniscourts.util.Assertions.stripZeros;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenniscourts.exceptions.CancelInactiveReservation;
import com.tenniscourts.exceptions.CancelPastReservationException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @InjectMocks
  ReservationService sut;

  @Spy
  ReservationRepository reservationRepository;

  @Mock
  ScheduleRepository scheduleRepository;

  @Mock
  GuestRepository guestRepository;

  @Mock
  ReservationMapper reservationMapper;

  @Captor
  ArgumentCaptor<Reservation> reservationCaptor;

  @Test
  void shouldThrowExceptionWhenReservationNotFound() {
    assertThatThrownBy(() -> sut.findReservation(1L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Reservation not found.");
  }

  @Test
  void shouldThrowExceptionWhenNonExistentReservationIsCancelled() {
    when(reservationRepository.findById(1L)).thenReturn(empty());
    assertThatThrownBy(() -> sut.cancelReservation(1L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Reservation not found.");
  }

  @ParameterizedTest
  @ValueSource(strings = {"CANCELLED", "RESCHEDULED", "NOT_SHOW_UP"})
  void shouldThrowCancelInactiveExceptionWhenReservationIsNotReadyToPlay(ReservationStatus status) {
    when(reservationRepository.findById(1L)).thenReturn(
        of(
            Reservation.builder()
                .reservationStatus(status)
                .build()
        )
    );
    assertThatThrownBy(() -> sut.cancelReservation(1L))
        .isInstanceOf(CancelInactiveReservation.class)
        .hasMessage("Reservation should be in ready to play status. Actual status: " + status.name());
  }

  @Test
  void shouldThrowCancelInactiveExceptionWhenReservationIsInThePast() {
    LocalDateTime startDateTime = LocalDateTime.now().minus(10, SECONDS);
    when(reservationRepository.findById(1L)).thenReturn(
        of(
            Reservation.builder()
                .schedule(
                    Schedule.builder()
                        .startDateTime(startDateTime)
                        .build())
                .build()
        )
    );
    assertThatThrownBy(() -> sut.cancelReservation(1L))
        .isInstanceOf(CancelPastReservationException.class)
        .hasMessage("Can cancel/reschedule only future dates. Reservation start date: " + startDateTime);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
          "25,10",
          "13,7.5",
          "3,5",
          "2,2.5",
          "1,0"
      }
  )
  void shouldCancelReservation(Integer hourDifference, BigDecimal refund) {
    // Given
    LocalDateTime startDateTime = LocalDateTime.now().plus(hourDifference, HOURS);
    when(reservationRepository.findById(1L)).thenReturn(
        of(
            Reservation.builder()
                .value(BigDecimal.TEN)
                .schedule(
                    Schedule.builder()
                        .startDateTime(startDateTime)
                        .build())
                .build()
        )
    );
    when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(
        Reservation.builder().build()
    );

    // When
    sut.cancelReservation(1L);

    // Then
    verify(reservationRepository).saveAndFlush(reservationCaptor.capture());
    Reservation actualReservation = reservationCaptor.getValue();
    assertThat(stripZeros(actualReservation.getRefundValue()))
        .isEqualTo(stripZeros(refund));
  }

}