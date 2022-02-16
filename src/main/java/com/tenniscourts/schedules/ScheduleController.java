package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "GuestController", description = "REST APIs related to Schedule Entity")
@RequestMapping(path = "/schedules")
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @PostMapping(consumes = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Create Schedule.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created Schedule"),
        @ApiResponse(code = 400, message = "Invalid request payload")})
    public ResponseEntity<Void> addScheduleTennisCourt(
        @RequestBody CreateScheduleRequestDTO createScheduleRequestDTO
    ) {
        return ResponseEntity.created(
            locationByEntity(
                scheduleService.addSchedule(
                    createScheduleRequestDTO
                ).getId())
        ).build();
    }

    @GetMapping(produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Retrieve Schedules by startDate and endDate.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Retrieved Schedules"),
        @ApiResponse(code = 400, message = "Invalid dates provided")})
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
            scheduleService.findSchedulesByDates(
                LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59)))
        );
    }

    @GetMapping(value = "/{scheduleId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Retrieve Schedule by scheduleId.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Retrieved Schedule"),
        @ApiResponse(code = 404, message = "Schedule not found"),
        @ApiResponse(code = 400, message = "Invalid scheduleId provided")})
    public ResponseEntity<ScheduleDTO> findByScheduleId(
        @PathVariable Long scheduleId
    ) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
