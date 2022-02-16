package com.tenniscourts.guests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@WebMvcTest(
    controllers = GuestController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureDataJpa
class GuestControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  GuestRepository guestRepository;

  @Test
  void shouldGetAllGuests() throws Exception {
    this.mockMvc
        .perform(get("/guests"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].name").value("Roger Federer"))
        .andExpect(jsonPath("$.[1].id").value(2))
        .andExpect(jsonPath("$.[1].name").value("Rafael Nadal"));
  }

  @Test
  void shouldGetGuestByName() throws Exception {
    this.mockMvc
        .perform(get("/guests").
            param("guestName", "Roger Federer"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].name").value("Roger Federer"));
  }

  @Test
  void shouldGetGuestById() throws Exception {
    this.mockMvc
        .perform(get("/guests/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Roger Federer"));
  }


  @Test
  void shouldDeleteGuestById() throws Exception {
    this.mockMvc
        .perform(delete("/guests/10"))
        .andDo(print())
        .andExpect(status().isNoContent());

    assertThat(guestRepository.existsById(10L)).isFalse();
  }

  @Test
  void shouldCreateGuest() throws Exception {
    this.mockMvc
        .perform(
            post("/guests")
                .content("{ \"name\":\"Roger Test\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(redirectedUrl("http://localhost/guests/11"));

    Optional<Guest> savedGuest = guestRepository.findByName("Roger Test");
    assertThat(savedGuest).isPresent();
    assertThat(savedGuest.get().getId()).isNotNull();
    assertThat(savedGuest.get().getName()).isEqualTo("Roger Test");
  }

}