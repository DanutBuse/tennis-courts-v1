package com.tenniscourts.reservations;

import com.tenniscourts.config.persistence.BaseEntity;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.schedules.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Reservation extends BaseEntity<Long> {

    @OneToOne
    private Guest guest;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    private Schedule schedule;

    @NotNull
    private BigDecimal value;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.READY_TO_PLAY;

    @Builder.Default
    private BigDecimal refundValue = BigDecimal.ZERO;

    public Reservation cancelNotShowUpReservation() {
        if (
            reservationStatus.equals(ReservationStatus.READY_TO_PLAY)
            && schedule.getEndDateTime().isBefore(LocalDateTime.now())
        ) {
            reservationStatus = ReservationStatus.NOT_SHOW_UP;
            refundValue = BigDecimal.ZERO;
        }

        return this;
    }
}
