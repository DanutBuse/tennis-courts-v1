package com.tenniscourts.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.reservations.ReservationDTO;

import java.math.BigDecimal;

public final class Assertions {

  private Assertions() {}

  public static void assertEqualsReservations(
      Reservation actual,
      Reservation expected
  ) {
    assertThat(actual).usingRecursiveComparison()
        .ignoringFields(
            "dateCreate", "dateUpdate", "ipNumberCreate",
            "ipNumberUpdate", "userCreate", "userUpdate",
            "refundValue", "value", "schedule")
        .isEqualTo(expected);

    assertThat(actual.getSchedule()).usingRecursiveComparison()
        .ignoringFields("reservations")
        .isEqualTo(expected.getSchedule());
    assertThat(stripZeros(actual.getValue()))
        .isEqualTo(stripZeros(expected.getValue()));
    assertThat(stripZeros(actual.getRefundValue()))
        .isEqualTo(stripZeros(expected.getRefundValue()));
  }

  public static void assertEqualsReservationsDto(
      ReservationDTO actual,
      ReservationDTO expected
  ) {
    assertThat(actual).usingRecursiveComparison()
        .ignoringFields("refundValue", "value", "previousReservation")
        .isEqualTo(expected);

    assertThat(stripZeros(actual.getValue()))
        .isEqualTo(stripZeros(expected.getValue()));
    assertThat(stripZeros(actual.getRefundValue()))
        .isEqualTo(stripZeros(expected.getRefundValue()));

    if (actual.getPreviousReservation() != null || expected.getPreviousReservation() != null) {
      assertEqualsReservationsDto(
          actual.getPreviousReservation(),
          expected.getPreviousReservation()
      );
    }
  }

  public static String stripZeros(BigDecimal value) {
    return value.stripTrailingZeros().toPlainString();
  }
}
