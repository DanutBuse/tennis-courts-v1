package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.persistence.BaseEntity;
import com.tenniscourts.schedules.Schedule;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "schedules")
@ToString
@SuperBuilder
public class TennisCourt extends BaseEntity<Long> {

    @Column
    @NotNull
    private String name;

    @OneToMany(mappedBy = "tennisCourt")
    @Cascade(CascadeType.ALL)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();
}
