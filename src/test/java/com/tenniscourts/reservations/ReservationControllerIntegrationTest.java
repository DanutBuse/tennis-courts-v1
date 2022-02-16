package com.tenniscourts.reservations;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ReservationController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ReservationControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ReservationService reservationService;

  @MockBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  void shouldCreateReservation() throws Exception {
    when(reservationService.bookReservation(
        CreateReservationRequestDTO.builder()
          .guestId(1L)
          .scheduleId(1L)
          .build())
    ).thenReturn(
        ReservationDTO.builder()
            .id(1L)
            .build()
    );

    mockMvc
        .perform(
            post("/reservations")
              .contentType(APPLICATION_JSON)
              .content("{\"guestId\":1, \"scheduleId\":1}")
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrl("http://localhost/reservations/1"));
  }

  @Test
  void shouldGetReservation() throws Exception {
    when(reservationService.findReservation(1L))
      .thenReturn(
        ReservationDTO.builder()
            .id(1L)
            .guestId(2L)
            .scheduledId(3L)
            .reservationStatus("READY_TO_PLAY")
            .build()
    );

    mockMvc
        .perform(
            get("/reservations/1")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.guestId").value(2))
        .andExpect(jsonPath("$.scheduledId").value(3))
        .andExpect(jsonPath("$.reservationStatus").value("READY_TO_PLAY"));
  }

  @Test
  void shouldCancelReservation() throws Exception {
    when(reservationService.cancelReservation(1L))
        .thenReturn(
            ReservationDTO.builder()
                .id(1L)
                .guestId(2L)
                .scheduledId(3L)
                .reservationStatus("CANCELLED")
                .build()
        );

    mockMvc
        .perform(
            delete("/reservations/1")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.guestId").value(2))
        .andExpect(jsonPath("$.scheduledId").value(3))
        .andExpect(jsonPath("$.reservationStatus").value("CANCELLED"));
  }

  @Test
  void shouldRescheduleReservation() throws Exception {
    when(reservationService.rescheduleReservation(1L, 2L))
        .thenReturn(
            ReservationDTO.builder()
                .id(1L)
                .guestId(2L)
                .scheduledId(2L)
                .reservationStatus("RESCHEDULED")
                .build()
        );

    mockMvc
        .perform(
            patch("/reservations/1/schedules/2")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.guestId").value(2))
        .andExpect(jsonPath("$.scheduledId").value(2))
        .andExpect(jsonPath("$.reservationStatus").value("RESCHEDULED"));
  }

  @Test
  void shouldKeepDepositFromNotShowingUp() throws Exception {
    when(reservationService.updateNotShowUpReservationDeposit())
        .thenReturn(
            singletonList(ReservationDTO.builder()
                .id(1L)
                .guestId(2L)
                .scheduledId(2L)
                .reservationStatus("NOT_SHOW_UP")
                .build()
            ));

    mockMvc
        .perform(
            patch("/reservations")
            .param("keepDepositNotShowUp", "true")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].guestId").value(2))
        .andExpect(jsonPath("$.[0].scheduledId").value(2))
        .andExpect(jsonPath("$.[0].reservationStatus").value("NOT_SHOW_UP"));
  }

  @Test
  void shouldKeepDepositFromNotShowingUpFalseFlag() throws Exception {
    when(reservationService.getPastReservations())
        .thenReturn(
            singletonList(ReservationDTO.builder()
                .id(1L)
                .guestId(2L)
                .scheduledId(2L)
                .reservationStatus("READY_TO_PLAY")
                .build()
            ));

    mockMvc
        .perform(
            patch("/reservations")
                .param("keepDepositNotShowUp", "false")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].guestId").value(2))
        .andExpect(jsonPath("$.[0].scheduledId").value(2))
        .andExpect(jsonPath("$.[0].reservationStatus").value("READY_TO_PLAY"));
  }

  @Test
  void shouldKeepDepositFromNotShowingUpInvalidFlag() throws Exception {
    mockMvc
        .perform(
            patch("/reservations")
                .param("keepDepositNotShowUp", "INVALID")
        )
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(reservationService);
  }

  @Test
  void shouldGetPastReservations() throws Exception {
    when(reservationService.getPastReservations())
        .thenReturn(
            singletonList(ReservationDTO.builder()
                .id(1L)
                .guestId(2L)
                .scheduledId(2L)
                .reservationStatus("CANCELLED")
                .build()
            ));

    mockMvc
        .perform(
            get("/reservations")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].guestId").value(2))
        .andExpect(jsonPath("$.[0].scheduledId").value(2))
        .andExpect(jsonPath("$.[0].reservationStatus").value("CANCELLED"));
  }

}