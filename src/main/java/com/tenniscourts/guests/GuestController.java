package com.tenniscourts.guests;

import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.isEmpty;

import com.tenniscourts.config.BaseRestController;
import com.tenniscourts.exceptions.EntityNotFoundException;

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
@Api(value = "GuestController", description = "REST APIs related to Guest Entity")
@RequestMapping(path = "/guests")
public class GuestController extends BaseRestController {

  private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

  private final GuestRepository guestRepository;

    @PostMapping(consumes = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Create a Guest.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Guest has been created"),
        @ApiResponse(code = 400, message = "Invalid guest")})
    public ResponseEntity<Void> createGuest(
        @RequestBody Guest guest
    ) {
        return ResponseEntity.created(
            locationByEntity(
                guestRepository.saveAndFlush(guest).getId()
            )
        ).build();
    }

    @GetMapping(value = "/{guestId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Retrieve a Guest by guestId.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Guest has been retrieved"),
        @ApiResponse(code = 404, message = "Guest not found"),
        @ApiResponse(code = 400, message = "Invalid guestId provided")})
    public ResponseEntity<Guest> findGuestById(@PathVariable Long guestId) {
        return ResponseEntity.ok(
            guestRepository.findById(guestId).orElseThrow(() -> {
              throw new EntityNotFoundException("Guest not found.");
            })
        );
    }

    @DeleteMapping(value = "/{guestId}", produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Delete a Guest.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Guest has been deleted."),
        @ApiResponse(code = 404, message = "Guest not found."),
        @ApiResponse(code = 400, message = "Invalid guestId")})
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        if (guestRepository.existsById(guestId)) {
          guestRepository.deleteById(guestId);
          return ResponseEntity.noContent().build();
        }
        throw new EntityNotFoundException("Guest not found.");
    }

    @GetMapping(produces = APPLICATION_JSON_CONTENT_TYPE)
    @ApiOperation(value = "Find a Guest by name or get all guests if not provided.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Guests have been retrieved."),
        @ApiResponse(code = 404, message = "No Guests found."),
        @ApiResponse(code = 400, message = "Invalid guestName")})
    public ResponseEntity<List<Guest>> findGuestByName(
        @RequestParam(value = "guestName", required = false) String guestName
    ) {
      if (isEmpty(guestName)) {
        return ResponseEntity.ok(
            guestRepository.findAll()
        );
      }

      return ResponseEntity.ok(
          singletonList(guestRepository.findByName(guestName).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
          }))
      );
    }
}
