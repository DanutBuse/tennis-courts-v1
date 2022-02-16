package com.tenniscourts.config.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tenniscourts.audit.CustomAuditEntityListener;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(CustomAuditEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEntity<ID> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = Access.READ_ONLY)
    private ID id;

    @Column
    private String ipNumberUpdate;

    @Column
    private Long userCreate;

    @Column
    private Long userUpdate;

    @Column
    @LastModifiedDate
    private LocalDateTime dateUpdate;

    @Column
    private String ipNumberCreate;

    @Column
    @CreatedDate
    private LocalDateTime dateCreate;

}
