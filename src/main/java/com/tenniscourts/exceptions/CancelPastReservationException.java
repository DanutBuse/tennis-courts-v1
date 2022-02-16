package com.tenniscourts.exceptions;

import java.time.LocalDateTime;

public class CancelPastReservationException extends RuntimeException {

  public CancelPastReservationException(LocalDateTime startDateTime) {
    super("Can cancel/reschedule only future dates. Reservation start date: " + startDateTime);
  }
}
