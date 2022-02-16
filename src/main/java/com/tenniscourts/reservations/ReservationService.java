package com.tenniscourts.reservations;

import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;
import static java.math.BigDecimal.valueOf;

import com.tenniscourts.exceptions.CancelInactiveReservation;
import com.tenniscourts.exceptions.CancelPastReservationException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservationService {

    private final BigDecimal RESERVATION_BLOCKED_FEE = new BigDecimal(10);

    private final ReservationRepository reservationRepository;

    private final ScheduleRepository scheduleRepository;

    private final GuestRepository guestRepository;

    private final ReservationMapper reservationMapper;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Guest guest = getGuestIfExists(createReservationRequestDTO.getGuestId());
        Schedule schedule = getScheduleIfExists(createReservationRequestDTO.getScheduleId());
        Reservation reservation = reservationMapper.map(createReservationRequestDTO);
        reservation.setValue(RESERVATION_BLOCKED_FEE);
        reservation.setSchedule(schedule);
        reservation.setGuest(guest);

        return reservationMapper.map(
            reservationRepository.saveAndFlush(reservation)
        );
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .map(reservationMapper::map)
            .orElseThrow(() -> {
                throw new EntityNotFoundException("Reservation not found.");
            });
    }

    public List<ReservationDTO> getPastReservations() {
        return reservationRepository.findBySchedule_EndDateTimeLessThanEqual(
            LocalDateTime.now()
        ).stream()
            .map(reservationMapper::map)
            .collect(Collectors.toList());
    }

    public List<ReservationDTO> updateNotShowUpReservationDeposit() {
        return reservationRepository.findByReservationStatusAndSchedule_EndDateTimeLessThanEqual(
            READY_TO_PLAY,
            LocalDateTime.now()
        ).stream()
            .map(Reservation::cancelNotShowUpReservation)
            .map(reservationRepository::saveAndFlush)
            .map(reservationMapper::map)
            .collect(Collectors.toList());
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    @Transactional
    public ReservationDTO rescheduleReservation(
        Long previousReservationId,
        Long scheduleId
    ) {

        return
            reservationRepository.findByIdAndSchedule_Id(previousReservationId, scheduleId)
                .map(reservationMapper::map)
                .orElse(updateReservation(previousReservationId, scheduleId));

    }

    private Guest getGuestIfExists(Long guestId) {
        return guestRepository.findById(guestId)
            .orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Guest not found.");
                }
            );
    }

    private Schedule getScheduleIfExists(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(
                () -> {
                    throw new EntityNotFoundException("Schedule not found.");
                }
            );
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private ReservationDTO updateReservation(Long previousReservationId, Long newScheduleId) {
        Reservation previousReservation = cancel(previousReservationId);
        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.saveAndFlush(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
            .guestId(previousReservation.getGuest().getId())
            .scheduleId(newScheduleId)
            .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.saveAndFlush(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new CancelInactiveReservation(reservation.getReservationStatus());
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new CancelPastReservationException(reservation.getSchedule().getStartDateTime());
        }
    }

    private BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        if (hours >= 12) {
            return valueOf(0.75).multiply(reservation.getValue());
        }

        if (hours >= 2) {
            return valueOf(0.5).multiply(reservation.getValue());
        }

        if (hours * 60 >= 1) {
            return valueOf(0.25).multiply(reservation.getValue());
        }

        return BigDecimal.ZERO;
    }

}
