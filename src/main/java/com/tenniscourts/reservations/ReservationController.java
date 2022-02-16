package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Api(value = "GuestController", description = "REST APIs related to Reservation Entity")
@RequestMapping(path = "/reservations")
public class ReservationController extends BaseRestController {

  private final ReservationService reservationService;

    @PostMapping(consumes = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Book a Reservation.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Reservation has been booked."),
        @ApiResponse(code = 400, message = "Invalid reservation request provided")})
    public ResponseEntity<Void> bookReservation(
        @RequestBody CreateReservationRequestDTO createReservationRequestDTO
    ) {
        return ResponseEntity.created(
            locationByEntity(
                reservationService.bookReservation(createReservationRequestDTO)
                    .getId()
            )
        ).build();
    }

    @GetMapping(value = "/{reservationId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Find a Reservation by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Reservation has been retrieved."),
        @ApiResponse(code = 404, message = "Reservation not found."),
        @ApiResponse(code = 400, message = "Invalid reservationId")})
    public ResponseEntity<ReservationDTO> findReservation(
        @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(
            reservationService.findReservation(reservationId)
        );
    }

    @DeleteMapping(value = "/{reservationId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Cancel a Reservation by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Reservation has been cancelled."),
        @ApiResponse(code = 404, message = "Reservation not found."),
        @ApiResponse(code = 400, message = "Invalid reservationId")})
    public ResponseEntity<ReservationDTO> cancelReservation(
        @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(
            reservationService.cancelReservation(reservationId)
        );
    }

    @PatchMapping(value = "/{reservationId}/schedules/{scheduleId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Reschedule a Reservation by id and Schedule id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Reservation has been rescheduled."),
        @ApiResponse(code = 404, message = "Reservation not found or Schedule not found."),
        @ApiResponse(code = 400, message = "Invalid reservationId or sheduleId")})
    public ResponseEntity<ReservationDTO> rescheduleReservation(
        @PathVariable("reservationId") Long reservationId,
        @PathVariable("scheduleId") Long scheduleId
    ) {
        return ResponseEntity.ok(
            reservationService.rescheduleReservation(reservationId, scheduleId)
        );
    }

    @PatchMapping(produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Keep Reservation deposit if guest did not show.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Retrieved Updated reservations"),
        @ApiResponse(code = 400, message = "Invalid keepDepositNotShowUp")})
    public ResponseEntity<List<ReservationDTO>> keepDepositFromNotShowingUp(
        @RequestParam("keepDepositNotShowUp") Boolean keepDepositNotShowUp
    ) {
      if (keepDepositNotShowUp) {
        return ResponseEntity.ok(
            reservationService.updateNotShowUpReservationDeposit()
        );
      }
      return listPastReservations();
    }

    @GetMapping(produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "List past reservations.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Retrieved past reservations")})
    public ResponseEntity<List<ReservationDTO>> listPastReservations() {
      return ResponseEntity.ok(
          reservationService.getPastReservations()
      );
    }
}
