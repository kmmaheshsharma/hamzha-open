package com.clinic.opendental.repository;

import com.clinic.opendental.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    List<Appointment> findByPatNum(Long patNum);

    List<Appointment> findByAptStatus(String aptStatus);

    List<Appointment> findByClinicNum(Long clinicNum);

    List<Appointment> findByAptDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDateTStampAfter(LocalDateTime dateTStamp);
}