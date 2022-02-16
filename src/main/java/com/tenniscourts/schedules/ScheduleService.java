package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO addSchedule(
        CreateScheduleRequestDTO createScheduleRequestDTO
    ) {
        return scheduleMapper.map(
            scheduleRepository.save(
                scheduleMapper.map(createScheduleRequestDTO)
        ));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(
            scheduleRepository.findByStartDateTimeAfterAndEndDateTimeBefore(startDate, endDate)
        );
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleMapper.map(
            scheduleRepository
                .findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found."))
        );
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
