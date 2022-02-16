package com.tenniscourts.reservations;

import com.tenniscourts.schedules.ScheduleMapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ScheduleMapper.class)
public interface ReservationMapper {

    @Mapping(target = "schedule.id", source = "scheduledId")
    @Mapping(target = "guest.id", source = "guestId")
    Reservation map(ReservationDTO source);

    @InheritInverseConfiguration
    ReservationDTO map(Reservation source);

    @Mapping(target = "guest.id", source = "guestId")
    @Mapping(target = "schedule.id", source = "scheduleId")
    Reservation map(CreateReservationRequestDTO source);
}
