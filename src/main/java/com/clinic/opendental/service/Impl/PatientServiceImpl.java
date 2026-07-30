package com.clinic.opendental.service.Impl;

import com.clinic.opendental.client.OpenDentalClient;
import com.clinic.opendental.dto.patient.CreatePatientRequest;
import com.clinic.opendental.dto.patient.PatientResponse;
import com.clinic.opendental.dto.patient.PatientSimpleResponse;
import com.clinic.opendental.dto.patient.UpdatePatientRequest;
import com.clinic.opendental.exception.ApiException;
import com.clinic.opendental.model.Patient;
import com.clinic.opendental.repository.PatientRepository;
import com.clinic.opendental.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final OpenDentalClient client;
    private final PatientRepository patientRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatients(Map<String, String> params) {
        // Try to fetch from OpenDental API and sync to database
        try {
            List<PatientResponse> apiPatients = client.getPatients(params);
            // Sync to database asynchronously
            syncPatientsToDb(apiPatients);
            return apiPatients;
        } catch (Exception e) {
            log.warn("OpenDental API unavailable, falling back to database: {}", e.getMessage());
            // Fallback to database
            return patientRepository.findAll().stream()
                    .map(this::toPatientResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientSimpleResponse> getSimplePatients(Map<String, String> params) {
        try {
            List<PatientSimpleResponse> apiPatients = client.getSimplePatients(params);
            return apiPatients;
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for simple patients, falling back to database: {}", e.getMessage());
            return patientRepository.findAll().stream()
                    .map(this::toSimplePatientResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatient(Long patNum) {
        try {
            PatientResponse apiPatient = client.getPatient(patNum);
            // Sync to database
            savePatientToDb(apiPatient);
            return apiPatient;
        } catch (Exception e) {
            log.warn("OpenDental API unavailable for patient {}, falling back to database: {}", patNum, e.getMessage());
            Patient patient = patientRepository.findById(patNum)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Patient not found with PatNum: " + patNum));
            return toPatientResponse(patient);
        }
    }

    @Override
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        try {
            PatientResponse response = client.createPatient(request);
            savePatientToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to create patient via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create patient: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long patNum, UpdatePatientRequest request) {
        try {
            PatientResponse response = client.updatePatient(patNum, request);
            savePatientToDb(response);
            return response;
        } catch (Exception e) {
            log.error("Failed to update patient via OpenDental API: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update patient: " + e.getMessage());
        }
    }

    // ========== Database sync helpers ==========

    @Transactional
    protected void syncPatientsToDb(List<PatientResponse> apiPatients) {
        for (PatientResponse dto : apiPatients) {
            savePatientToDb(dto);
        }
    }

    @Transactional
    protected void savePatientToDb(PatientResponse dto) {
        try {
            Patient patient = toPatientEntity(dto);
            patientRepository.save(patient);
        } catch (Exception e) {
            log.error("Failed to sync patient {} to database: {}", dto.getPatNum(), e.getMessage());
        }
    }

    private Patient toPatientEntity(PatientResponse dto) {
        Patient.PatientBuilder builder = Patient.builder()
                .patNum(dto.getPatNum())
                .lName(dto.getLName())
                .fName(dto.getFName())
                .middleI(dto.getMiddleI())
                .preferred(dto.getPreferred())
                .patStatus(dto.getPatStatus())
                .gender(dto.getGender())
                .position(dto.getPosition())
                .ssn(dto.getSSN())
                .address(dto.getAddress())
                .address2(dto.getAddress2())
                .city(dto.getCity())
                .state(dto.getState())
                .zip(dto.getZip())
                .hmPhone(dto.getHmPhone())
                .wkPhone(dto.getWkPhone())
                .wirelessPhone(dto.getWirelessPhone())
                .guarantor(dto.getGuarantor())
                .email(dto.getEmail())
                .priProv(dto.getPriProv())
                .secProv(dto.getSecProv())
                .feeSched(dto.getFeeSched())
                .billingType(dto.getBillingType())
                .chartNumber(dto.getChartNumber())
                .medicaidId(dto.getMedicaidID())
                .employerNum(dto.getEmployerNum())
                .clinicNum(dto.getClinicNum())
                .clinicAbbr(dto.getClinicAbbr())
                .hasIns(dto.getHasIns())
                .premed("true".equalsIgnoreCase(dto.getPremed()))
                .ward(dto.getWard())
                .preferConfirmMethod(dto.getPreferConfirmMethod())
                .preferContactMethod(dto.getPreferContactMethod())
                .preferRecallMethod(dto.getPreferRecallMethod())
                .language(dto.getLanguage())
                .siteNum(dto.getSiteNum())
                .siteDesc(dto.getSiteDesc())
                .superFamily(dto.getSuperFamily())
                .txtMsgOk(dto.getTxtMsgOk())
                .secUserNumEntry(dto.getSecUserNumEntry())
                .estBalance(dto.getEstBalance() != null ? BigDecimal.valueOf(dto.getEstBalance()) : BigDecimal.ZERO)
                .bal030(dto.getBal_0_30() != null ? BigDecimal.valueOf(dto.getBal_0_30()) : BigDecimal.ZERO)
                .bal3160(dto.getBal_31_60() != null ? BigDecimal.valueOf(dto.getBal_31_60()) : BigDecimal.ZERO)
                .bal6190(dto.getBal_61_90() != null ? BigDecimal.valueOf(dto.getBal_61_90()) : BigDecimal.ZERO)
                .balOver90(dto.getBalOver90() != null ? BigDecimal.valueOf(dto.getBalOver90()) : BigDecimal.ZERO)
                .insEst(dto.getInsEst() != null ? BigDecimal.valueOf(dto.getInsEst()) : BigDecimal.ZERO)
                .balTotal(dto.getBalTotal() != null ? BigDecimal.valueOf(dto.getBalTotal()) : BigDecimal.ZERO);

        // Parse dates safely
        if (dto.getBirthdate() != null && !dto.getBirthdate().isEmpty() && !dto.getBirthdate().equals("0001-01-01")) {
            builder.birthdate(LocalDate.parse(dto.getBirthdate(), DATE_FORMAT));
        }
        if (dto.getDateFirstVisit() != null && !dto.getDateFirstVisit().isEmpty() && !dto.getDateFirstVisit().equals("0001-01-01")) {
            builder.dateFirstVisit(LocalDate.parse(dto.getDateFirstVisit(), DATE_FORMAT));
        }
        if (dto.getAdmitDate() != null && !dto.getAdmitDate().isEmpty() && !dto.getAdmitDate().equals("0001-01-01")) {
            builder.admitDate(LocalDate.parse(dto.getAdmitDate(), DATE_FORMAT));
        }
        if (dto.getSecDateEntry() != null && !dto.getSecDateEntry().isEmpty() && !dto.getSecDateEntry().equals("0001-01-01")) {
            builder.secDateEntry(LocalDate.parse(dto.getSecDateEntry(), DATE_FORMAT));
        }
        if (dto.getDateTimeLastAging() != null && !dto.getDateTimeLastAging().isEmpty() && !dto.getDateTimeLastAging().equals("0001-01-01 00:00:00")) {
            builder.dateTimeLastAging(LocalDateTime.parse(dto.getDateTimeLastAging(), DATETIME_FORMAT));
        }

        return builder.build();
    }

    private PatientResponse toPatientResponse(Patient entity) {
        PatientResponse.PatientResponseBuilder builder = PatientResponse.builder()
                .PatNum(entity.getPatNum())
                .LName(entity.getLName())
                .FName(entity.getFName())
                .MiddleI(entity.getMiddleI())
                .Preferred(entity.getPreferred())
                .PatStatus(entity.getPatStatus())
                .Gender(entity.getGender())
                .Position(entity.getPosition())
                .SSN(entity.getSsn())
                .Address(entity.getAddress())
                .Address2(entity.getAddress2())
                .City(entity.getCity())
                .State(entity.getState())
                .Zip(entity.getZip())
                .HmPhone(entity.getHmPhone())
                .WkPhone(entity.getWkPhone())
                .WirelessPhone(entity.getWirelessPhone())
                .Guarantor(entity.getGuarantor())
                .Email(entity.getEmail())
                .PriProv(entity.getPriProv())
                .SecProv(entity.getSecProv())
                .FeeSched(entity.getFeeSched())
                .BillingType(entity.getBillingType())
                .ChartNumber(entity.getChartNumber())
                .MedicaidID(entity.getMedicaidId())
                .EmployerNum(entity.getEmployerNum())
                .ClinicNum(entity.getClinicNum())
                .clinicAbbr(entity.getClinicAbbr())
                .HasIns(entity.getHasIns())
                .Premed(entity.getPremed() != null && entity.getPremed() ? "true" : "false")
                .Ward(entity.getWard())
                .PreferConfirmMethod(entity.getPreferConfirmMethod())
                .PreferContactMethod(entity.getPreferContactMethod())
                .PreferRecallMethod(entity.getPreferRecallMethod())
                .Language(entity.getLanguage())
                .SiteNum(entity.getSiteNum())
                .siteDesc(entity.getSiteDesc())
                .SuperFamily(entity.getSuperFamily())
                .TxtMsgOk(entity.getTxtMsgOk())
                .SecUserNumEntry(entity.getSecUserNumEntry())
                .EstBalance(entity.getEstBalance() != null ? entity.getEstBalance().doubleValue() : 0.0)
                .Bal_0_30(entity.getBal030() != null ? entity.getBal030().doubleValue() : 0.0)
                .Bal_31_60(entity.getBal3160() != null ? entity.getBal3160().doubleValue() : 0.0)
                .Bal_61_90(entity.getBal6190() != null ? entity.getBal6190().doubleValue() : 0.0)
                .BalOver90(entity.getBalOver90() != null ? entity.getBalOver90().doubleValue() : 0.0)
                .InsEst(entity.getInsEst() != null ? entity.getInsEst().doubleValue() : 0.0)
                .BalTotal(entity.getBalTotal() != null ? entity.getBalTotal().doubleValue() : 0.0);

        if (entity.getBirthdate() != null) {
            builder.Birthdate(entity.getBirthdate().format(DATE_FORMAT));
        }
        if (entity.getDateFirstVisit() != null) {
            builder.DateFirstVisit(entity.getDateFirstVisit().format(DATE_FORMAT));
        }
        if (entity.getAdmitDate() != null) {
            builder.AdmitDate(entity.getAdmitDate().format(DATE_FORMAT));
        }
        if (entity.getSecDateEntry() != null) {
            builder.SecDateEntry(entity.getSecDateEntry().format(DATE_FORMAT));
        }
        if (entity.getDateTimeLastAging() != null) {
            builder.dateTimeLastAging(entity.getDateTimeLastAging().format(DATETIME_FORMAT));
        }

        return builder.build();
    }

    private PatientSimpleResponse toSimplePatientResponse(Patient entity) {
        PatientSimpleResponse.PatientSimpleResponseBuilder builder = PatientSimpleResponse.builder()
                .PatNum(entity.getPatNum())
                .LName(entity.getLName())
                .FName(entity.getFName())
                .MiddleI(entity.getMiddleI())
                .Preferred(entity.getPreferred())
                .PatStatus(entity.getPatStatus())
                .Gender(entity.getGender())
                .Position(entity.getPosition())
                .SSN(entity.getSsn())
                .Address(entity.getAddress())
                .Address2(entity.getAddress2())
                .City(entity.getCity())
                .State(entity.getState())
                .Zip(entity.getZip())
                .HmPhone(entity.getHmPhone())
                .WkPhone(entity.getWkPhone())
                .WirelessPhone(entity.getWirelessPhone())
                .Guarantor(entity.getGuarantor())
                .Email(entity.getEmail())
                .PriProv(entity.getPriProv())
                .SecProv(entity.getSecProv())
                .FeeSched(entity.getFeeSched())
                .BillingType(entity.getBillingType())
                .ChartNumber(entity.getChartNumber())
                .MedicaidID(entity.getMedicaidId())
                .EmployerNum(entity.getEmployerNum())
                .ClinicNum(entity.getClinicNum())
                .clinicAbbr(entity.getClinicAbbr())
                .HasIns(entity.getHasIns())
                .Premed(entity.getPremed() != null && entity.getPremed() ? "true" : "false")
                .Ward(entity.getWard())
                .PreferConfirmMethod(entity.getPreferConfirmMethod())
                .PreferContactMethod(entity.getPreferContactMethod())
                .PreferRecallMethod(entity.getPreferRecallMethod())
                .Language(entity.getLanguage())
                .SiteNum(entity.getSiteNum())
                .siteDesc(entity.getSiteDesc())
                .SuperFamily(entity.getSuperFamily())
                .TxtMsgOk(entity.getTxtMsgOk())
                .SecUserNumEntry(entity.getSecUserNumEntry())
                .EstBalance(entity.getEstBalance() != null ? entity.getEstBalance().doubleValue() : 0.0)
                .Bal_0_30(entity.getBal030() != null ? entity.getBal030().doubleValue() : 0.0)
                .Bal_31_60(entity.getBal3160() != null ? entity.getBal3160().doubleValue() : 0.0)
                .Bal_61_90(entity.getBal6190() != null ? entity.getBal6190().doubleValue() : 0.0)
                .BalOver90(entity.getBalOver90() != null ? entity.getBalOver90().doubleValue() : 0.0)
                .InsEst(entity.getInsEst() != null ? entity.getInsEst().doubleValue() : 0.0)
                .BalTotal(entity.getBalTotal() != null ? entity.getBalTotal().doubleValue() : 0.0);

        if (entity.getBirthdate() != null) {
            builder.Birthdate(entity.getBirthdate().format(DATE_FORMAT));
        }
        if (entity.getDateFirstVisit() != null) {
            builder.DateFirstVisit(entity.getDateFirstVisit().format(DATE_FORMAT));
        }
        if (entity.getAdmitDate() != null) {
            builder.AdmitDate(entity.getAdmitDate().format(DATE_FORMAT));
        }
        if (entity.getSecDateEntry() != null) {
            builder.SecDateEntry(entity.getSecDateEntry().format(DATE_FORMAT));
        }
        if (entity.getDateTimeLastAging() != null) {
            builder.dateTimeLastAging(entity.getDateTimeLastAging().format(DATETIME_FORMAT));
        }

        return builder.build();
    }
}