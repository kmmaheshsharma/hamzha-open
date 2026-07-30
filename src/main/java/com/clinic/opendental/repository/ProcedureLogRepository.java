package com.clinic.opendental.repository;

import com.clinic.opendental.model.ProcedureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureLogRepository extends JpaRepository<ProcedureLog, Long>, JpaSpecificationExecutor<ProcedureLog> {

    List<ProcedureLog> findByPatNum(Long patNum);

    List<ProcedureLog> findByAptNum(Long aptNum);

    List<ProcedureLog> findByPatNumAndAptNum(Long patNum, Long aptNum);

    List<ProcedureLog> findByProcStatus(String procStatus);

    List<ProcedureLog> findByClinicNum(Long clinicNum);

    List<ProcedureLog> findByCodeNum(Long codeNum);

    List<ProcedureLog> findByPlannedAptNum(Long plannedAptNum);
}