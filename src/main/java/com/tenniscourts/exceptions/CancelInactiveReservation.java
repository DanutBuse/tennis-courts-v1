package com.tenniscourts.exceptions;

import com.tenniscourts.reservations.ReservationStatus;

public class CancelInactiveReservation extends RuntimeException {

  public CancelInactiveReservation(ReservationStatus reservationStatus) {
    super("Reservation should be in ready to play status. Actual status: "
        + reservationStatus
    );
  }
}
