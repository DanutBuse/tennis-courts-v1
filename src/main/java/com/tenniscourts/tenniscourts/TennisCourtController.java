package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(value = "TennisCourtController", description = "REST APIs related to Tennis Court Entity")
@RequestMapping(path = "/tennis-courts")
public class TennisCourtController extends BaseRestController {

    private final TennisCourtService tennisCourtService;

    @PostMapping(consumes = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Create Tennis Court.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created Tennis Court"),
        @ApiResponse(code = 400, message = "Invalid request payload")})
    public ResponseEntity<Void> addTennisCourt(
        @RequestBody TennisCourtDTO tennisCourtDTO
    ) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @GetMapping(value = "/{tennisCourtId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Retrieve Tennis Court by Tennis Court Id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Retrieved Tennis Court"),
        @ApiResponse(code = 404, message = "Tennis Court not found"),
        @ApiResponse(code = 400, message = "Invalid Tennis Court Id provided")})
    public ResponseEntity<TennisCourtDTO> findTennisCourtByIdIncludingSchedules(
        @PathVariable Long tennisCourtId,
        @RequestParam(value = "includeSchedules", defaultValue = "false") Boolean includeSchedules
    ) {
        if (includeSchedules) {
            return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));
        } else {
            return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
        }
    }
}
