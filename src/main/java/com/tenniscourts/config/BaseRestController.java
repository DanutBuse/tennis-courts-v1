package com.tenniscourts.config;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;

public class BaseRestController {

  public static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

  protected URI locationByEntity(Long entityId) {
    return fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(entityId)
        .toUri();
  }
}
