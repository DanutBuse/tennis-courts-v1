package com.tenniscourts.tenniscourts;

import com.tenniscourts.schedules.ScheduleMapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ScheduleMapper.class)
public interface TennisCourtMapper {

    @Mapping(target = "tennisCourtSchedules", source = "schedules")
    TennisCourtDTO map(TennisCourt source);

    @InheritInverseConfiguration
    TennisCourt map(TennisCourtDTO source);
}
