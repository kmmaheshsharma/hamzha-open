package com.clinic.opendental.service.Impl;

import com.clinic.opendental.client.OpenDentalClient;
import com.clinic.opendental.dto.appointment.*;
import com.clinic.opendental.exception.ApiException;
import com.clinic.opendental.model.Appointment;
import com.clinic.opendental.repository.AppointmentRepository;
import com.clinic.opendental.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final OpenDentalClient client;
    private final AppointmentRepository appointmentRepository;

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long aptNum) {
        try {
            AppointmentResponse apiResponse = client.getAppointment(aptNum);
            saveAppointmentToDb(apiResponse);
            return apiResponse;
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for appointment {}, falling back to database: {}", aptNum, e.getMessage());
            Appointment appointment = appointmentRepository.findById(aptNum)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Appointment not found with AptNum: " + aptNum));
            return toAppointmentResponse(appointment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointments(Map<String, String> params) {
        try {
            List<AppointmentResponse> apiResponses = client.getAppointments(params);
            syncAppointmentsToDb(apiResponses);
            return apiResponses;
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for appointments, falling back to database: {}", e.getMessage());
            return appointmentRepository.findAll().stream()
                    .map(this::toAppointmentResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getASAPAppointments(Map<String, String> params) {
        try {
            return client.getASAPAppointments(params);
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for ASAP appointments: {}", e.getMessage());
            return appointmentRepository.findByAptStatus("Scheduled").stream()
                    .filter(a -> "ASAP".equals(a.getPriority()))
                    .map(this::toAppointmentResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponse> getSlots(Map<String, String> params) {
        return client.getSlots(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsWebSched(Map<String, String> params) {
        return client.getSlotsWebSched(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getWebSchedAppointments(Map<String, String> params) {
        try {
            return client.getWebSchedAppointments(params);
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for WebSched appointments: {}", e.getMessage());
            return appointmentRepository.findAll().stream()
                    .map(this::toAppointmentResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        try {
            AppointmentResponse response = client.createAppointment(request);
            saveAppointmentToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to create appointment via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AppointmentResponse createPlannedAppointment(PlannedAppointmentRequest request) {
        try {
            AppointmentResponse response = client.createPlannedAppointment(request);
            saveAppointmentToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to create planned appointment: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create planned appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AppointmentResponse schedulePlannedAppointment(SchedulePlannedRequest request) {
        try {
            AppointmentResponse response = client.schedulePlannedAppointment(request);
            saveAppointmentToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to schedule planned appointment: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to schedule planned appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AppointmentResponse createWebSchedAppointment(WebSchedRequest request) {
        try {
            AppointmentResponse response = client.createWebSchedAppointment(request);
            saveAppointmentToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to create WebSched appointment: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create WebSched appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long aptNum, UpdateAppointmentRequest request) {
        try {
            AppointmentResponse response = client.updateAppointment(aptNum, request);
            saveAppointmentToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to update appointment via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void breakAppointment(Long aptNum, BreakAppointmentRequest request) {
        try {
            client.breakAppointment(aptNum, request);
        } catch (Exception e) {
            log.error("Failed to break appointment via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to break appointment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void appendNote(Long aptNum, NoteRequest request) {
        try {
            client.appendNote(aptNum, request);
        } catch (Exception e) {
            log.error("Failed to append note via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to append note: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void confirmAppointment(Long aptNum, ConfirmAppointmentRequest request) {
        try {
            client.confirmAppointment(aptNum, request);
        } catch (Exception e) {
            log.error("Failed to confirm appointment via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to confirm appointment: " + e.getMessage());
        }
    }

    // ========== Database sync helpers ==========

    @Transactional
    protected void syncAppointmentsToDb(List<AppointmentResponse> apiResponses) {
        for (AppointmentResponse dto : apiResponses) {
            saveAppointmentToDb(dto);
        }
    }

    @Transactional
    protected void saveAppointmentToDb(AppointmentResponse dto) {
        try {
            Appointment appointment = toAppointmentEntity(dto);
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            log.error("Failed to sync appointment {} to database: {}", dto.getAptNum(), e.getMessage());
        }
    }

    private Appointment toAppointmentEntity(AppointmentResponse dto) {
        Appointment.AppointmentBuilder builder = Appointment.builder()
                .aptNum(dto.getAptNum())
                .patNum(dto.getPatNum())
                .aptStatus(dto.getAptStatus())
                .pattern(dto.getPattern())
                .confirmed(dto.getConfirmed())
                .timeLocked(dto.getTimeLocked())
                .op(dto.getOp())
                .note(dto.getNote())
                .provNum(dto.getProvNum())
                .provAbbr(dto.getProvAbbr())
                .provHyg(dto.getProvHyg())
                .nextAptNum(dto.getNextAptNum())
                .unschedStatus(dto.getUnschedStatus())
                .isNewPatient(dto.getIsNewPatient())
                .procDescript(dto.getProcDescript())
                .assistant(dto.getAssistant())
                .clinicNum(dto.getClinicNum())
                .isHygiene(dto.getIsHygiene())
                .insPlan1(dto.getInsPlan1())
                .insPlan2(dto.getInsPlan2())
                .colorOverride(dto.getColorOverride())
                .appointmentTypeNum(dto.getAppointmentTypeNum())
                .secUserNumEntry(dto.getSecUserNumEntry())
                .priority(dto.getPriority())
                .patternSecondary(dto.getPatternSecondary())
                .itemOrderPlanned(dto.getItemOrderPlanned())
                .isMirrored(dto.getIsMirrored())
                .eServiceLogType(dto.getEServiceLogType());

        if (dto.getAptDateTime() != null && !dto.getAptDateTime().isEmpty() && !dto.getAptDateTime().equals("0001-01-01 00:00:00")) {
            builder.aptDateTime(LocalDateTime.parse(dto.getAptDateTime(), DATETIME_FORMAT));
        }
        if (dto.getDateTStamp() != null && !dto.getDateTStamp().isEmpty() && !dto.getDateTStamp().equals("0001-01-01 00:00:00")) {
            builder.dateTStamp(LocalDateTime.parse(dto.getDateTStamp(), DATETIME_FORMAT));
        }
        if (dto.getDateTimeArrived() != null && !dto.getDateTimeArrived().isEmpty() && !dto.getDateTimeArrived().equals("0001-01-01 00:00:00")) {
            builder.dateTimeArrived(LocalDateTime.parse(dto.getDateTimeArrived(), DATETIME_FORMAT));
        }
        if (dto.getDateTimeSeated() != null && !dto.getDateTimeSeated().isEmpty() && !dto.getDateTimeSeated().equals("0001-01-01 00:00:00")) {
            builder.dateTimeSeated(LocalDateTime.parse(dto.getDateTimeSeated(), DATETIME_FORMAT));
        }
        if (dto.getDateTimeDismissed() != null && !dto.getDateTimeDismissed().isEmpty() && !dto.getDateTimeDismissed().equals("0001-01-01 00:00:00")) {
            builder.dateTimeDismissed(LocalDateTime.parse(dto.getDateTimeDismissed(), DATETIME_FORMAT));
        }
        if (dto.getDateTimeAskedToArrive() != null && !dto.getDateTimeAskedToArrive().isEmpty() && !dto.getDateTimeAskedToArrive().equals("0001-01-01 00:00:00")) {
            builder.dateTimeAskedToArrive(LocalDateTime.parse(dto.getDateTimeAskedToArrive(), DATETIME_FORMAT));
        }
        if (dto.getSecDateTEntry() != null && !dto.getSecDateTEntry().isEmpty() && !dto.getSecDateTEntry().equals("0001-01-01 00:00:00")) {
            builder.secDateTEntry(LocalDateTime.parse(dto.getSecDateTEntry(), DATETIME_FORMAT));
        }

        return builder.build();
    }

    private AppointmentResponse toAppointmentResponse(Appointment entity) {
        AppointmentResponse.AppointmentResponseBuilder builder = AppointmentResponse.builder()
                .AptNum(entity.getAptNum())
                .PatNum(entity.getPatNum())
                .AptStatus(entity.getAptStatus())
                .Pattern(entity.getPattern())
                .Confirmed(entity.getConfirmed())
                .TimeLocked(entity.getTimeLocked())
                .Op(entity.getOp())
                .Note(entity.getNote())
                .ProvNum(entity.getProvNum())
                .provAbbr(entity.getProvAbbr())
                .ProvHyg(entity.getProvHyg())
                .NextAptNum(entity.getNextAptNum())
                .UnschedStatus(entity.getUnschedStatus())
                .IsNewPatient(entity.getIsNewPatient())
                .ProcDescript(entity.getProcDescript())
                .Assistant(entity.getAssistant())
                .ClinicNum(entity.getClinicNum())
                .IsHygiene(entity.getIsHygiene())
                .InsPlan1(entity.getInsPlan1())
                .InsPlan2(entity.getInsPlan2())
                .colorOverride(entity.getColorOverride())
                .AppointmentTypeNum(entity.getAppointmentTypeNum())
                .SecUserNumEntry(entity.getSecUserNumEntry())
                .Priority(entity.getPriority())
                .PatternSecondary(entity.getPatternSecondary())
                .ItemOrderPlanned(entity.getItemOrderPlanned())
                .IsMirrored(entity.getIsMirrored())
                .eServiceLogType(entity.getEServiceLogType());

        if (entity.getAptDateTime() != null) {
            builder.AptDateTime(entity.getAptDateTime().format(DATETIME_FORMAT));
        }
        if (entity.getDateTStamp() != null) {
            builder.DateTStamp(entity.getDateTStamp().format(DATETIME_FORMAT));
        }
        if (entity.getDateTimeArrived() != null) {
            builder.DateTimeArrived(entity.getDateTimeArrived().format(DATETIME_FORMAT));
        }
        if (entity.getDateTimeSeated() != null) {
            builder.DateTimeSeated(entity.getDateTimeSeated().format(DATETIME_FORMAT));
        }
        if (entity.getDateTimeDismissed() != null) {
            builder.DateTimeDismissed(entity.getDateTimeDismissed().format(DATETIME_FORMAT));
        }
        if (entity.getDateTimeAskedToArrive() != null) {
            builder.DateTimeAskedToArrive(entity.getDateTimeAskedToArrive().format(DATETIME_FORMAT));
        }
        if (entity.getSecDateTEntry() != null) {
            builder.SecDateTEntry(entity.getSecDateTEntry().format(DATETIME_FORMAT));
        }

        return builder.build();
    }
}