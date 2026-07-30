package com.clinic.opendental.client;

import com.clinic.opendental.dto.appointment.*;
import com.clinic.opendental.dto.document.*;
import com.clinic.opendental.dto.patient.CreatePatientRequest;
import com.clinic.opendental.dto.patient.PatientResponse;
import com.clinic.opendental.dto.patient.PatientSimpleResponse;
import com.clinic.opendental.dto.patient.UpdatePatientRequest;
import com.clinic.opendental.dto.procedurelog.*;
import com.clinic.opendental.dto.query.QueryRequest;
import com.clinic.opendental.dto.query.ShortQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenDentalClient {

    @Value("${opendental.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    // ========================================================================
    // Patient Endpoints
    // ========================================================================

    public PatientResponse getPatient(Long patNum) {
        return restTemplate.getForObject(
                baseUrl + "/patients/" + patNum,
                PatientResponse.class);
    }

    public List<PatientSimpleResponse> getSimplePatients(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/patients/Simple");
        params.forEach(builder::queryParam);

        ResponseEntity<List<PatientSimpleResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PatientSimpleResponse>>() {});
        return response.getBody();
    }

    public List<PatientResponse> getPatients(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/patients");
        params.forEach(builder::queryParam);

        ResponseEntity<List<PatientResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PatientResponse>>() {});
        return response.getBody();
    }

    public PatientResponse createPatient(CreatePatientRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/patients",
                request,
                PatientResponse.class);
    }

    public PatientResponse updatePatient(Long patNum, UpdatePatientRequest request) {
        HttpEntity<UpdatePatientRequest> entity = new HttpEntity<>(request);
        ResponseEntity<PatientResponse> response = restTemplate.exchange(
                baseUrl + "/patients/" + patNum,
                HttpMethod.PUT,
                entity,
                PatientResponse.class);
        return response.getBody();
    }

    // ========================================================================
    // Appointment Endpoints
    // ========================================================================

    // --- GET /appointments/{aptNum} ---
    public AppointmentResponse getAppointment(Long aptNum) {
        return restTemplate.getForObject(
                baseUrl + "/appointments/" + aptNum,
                AppointmentResponse.class);
    }

    // --- GET /appointments ---
    public List<AppointmentResponse> getAppointments(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/appointments");
        params.forEach(builder::queryParam);

        ResponseEntity<List<AppointmentResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AppointmentResponse>>() {});
        return response.getBody();
    }

    // --- GET /appointments/ASAP ---
    public List<AppointmentResponse> getASAPAppointments(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/appointments/ASAP");
        params.forEach(builder::queryParam);

        ResponseEntity<List<AppointmentResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AppointmentResponse>>() {});
        return response.getBody();
    }

    // --- GET /appointments/Slots ---
    public List<SlotResponse> getSlots(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/appointments/Slots");
        params.forEach(builder::queryParam);

        ResponseEntity<List<SlotResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SlotResponse>>() {});
        return response.getBody();
    }

    // --- GET /appointments/SlotsWebSched ---
    public List<SlotResponse> getSlotsWebSched(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/appointments/SlotsWebSched");
        params.forEach(builder::queryParam);

        ResponseEntity<List<SlotResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SlotResponse>>() {});
        return response.getBody();
    }

    // --- GET /appointments/WebSched ---
    public List<AppointmentResponse> getWebSchedAppointments(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/appointments/WebSched");
        params.forEach(builder::queryParam);

        ResponseEntity<List<AppointmentResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AppointmentResponse>>() {});
        return response.getBody();
    }

    // --- POST /appointments ---
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/appointments",
                request,
                AppointmentResponse.class);
    }

    // --- POST /appointments/Planned ---
    public AppointmentResponse createPlannedAppointment(PlannedAppointmentRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/appointments/Planned",
                request,
                AppointmentResponse.class);
    }

    // --- POST /appointments/SchedulePlanned ---
    public AppointmentResponse schedulePlannedAppointment(SchedulePlannedRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/appointments/SchedulePlanned",
                request,
                AppointmentResponse.class);
    }

    // --- POST /appointments/WebSched ---
    public AppointmentResponse createWebSchedAppointment(WebSchedRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/appointments/WebSched",
                request,
                AppointmentResponse.class);
    }

    // --- PUT /appointments/{aptNum} ---
    public AppointmentResponse updateAppointment(Long aptNum, UpdateAppointmentRequest request) {
        HttpEntity<UpdateAppointmentRequest> entity = new HttpEntity<>(request);
        ResponseEntity<AppointmentResponse> response = restTemplate.exchange(
                baseUrl + "/appointments/" + aptNum,
                HttpMethod.PUT,
                entity,
                AppointmentResponse.class);
        return response.getBody();
    }

    // --- PUT /appointments/{aptNum}/Break ---
    public void breakAppointment(Long aptNum, BreakAppointmentRequest request) {
        HttpEntity<BreakAppointmentRequest> entity = new HttpEntity<>(request);
        restTemplate.exchange(
                baseUrl + "/appointments/" + aptNum + "/Break",
                HttpMethod.PUT,
                entity,
                Void.class);
    }

    // --- PUT /appointments/{aptNum}/Note ---
    public void appendNote(Long aptNum, NoteRequest request) {
        HttpEntity<NoteRequest> entity = new HttpEntity<>(request);
        restTemplate.exchange(
                baseUrl + "/appointments/" + aptNum + "/Note",
                HttpMethod.PUT,
                entity,
                Void.class);
    }

    // --- PUT /appointments/{aptNum}/Confirm ---
    public void confirmAppointment(Long aptNum, ConfirmAppointmentRequest request) {
        HttpEntity<ConfirmAppointmentRequest> entity = new HttpEntity<>(request);
        restTemplate.exchange(
                baseUrl + "/appointments/" + aptNum + "/Confirm",
                HttpMethod.PUT,
                entity,
                Void.class);
    }

    // ========================================================================
    // ProcedureLog Endpoints
    // ========================================================================

    // --- GET /procedurelogs/{procNum} ---
    public ProcedureLogResponse getProcedureLog(Long procNum) {
        return restTemplate.getForObject(
                baseUrl + "/procedurelogs/" + procNum,
                ProcedureLogResponse.class);
    }

    // --- GET /procedurelogs ---
    public List<ProcedureLogResponse> getProcedureLogs(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/procedurelogs");
        params.forEach(builder::queryParam);

        ResponseEntity<List<ProcedureLogResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProcedureLogResponse>>() {});
        return response.getBody();
    }

    // --- GET /procedurelogs/InsuranceHistory ---
    public List<InsuranceHistoryResponse> getInsuranceHistory(Long patNum, Long insSubNum) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/procedurelogs/InsuranceHistory")
                .queryParam("PatNum", patNum)
                .queryParam("InsSubNum", insSubNum);

        ResponseEntity<List<InsuranceHistoryResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InsuranceHistoryResponse>>() {});
        return response.getBody();
    }

    // --- GET /procedurelogs/GroupNotes ---
    public List<GroupNoteResponse> getGroupNotes(Long patNum) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/procedurelogs/GroupNotes")
                .queryParam("PatNum", patNum);

        ResponseEntity<List<GroupNoteResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GroupNoteResponse>>() {});
        return response.getBody();
    }

    // --- POST /procedurelogs ---
    public ProcedureLogResponse createProcedureLog(CreateProcedureLogRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/procedurelogs",
                request,
                ProcedureLogResponse.class);
    }

    // --- POST /procedurelogs/GroupNote ---
    public GroupNoteResponse createGroupNote(GroupNoteRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/procedurelogs/GroupNote",
                request,
                GroupNoteResponse.class);
    }

    // --- POST /procedurelogs/InsuranceHistory ---
    public ProcedureLogResponse createInsuranceHistory(InsuranceHistoryRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/procedurelogs/InsuranceHistory",
                request,
                ProcedureLogResponse.class);
    }

    // --- PUT /procedurelogs/{procNum} ---
    public ProcedureLogResponse updateProcedureLog(Long procNum, UpdateProcedureLogRequest request) {
        HttpEntity<UpdateProcedureLogRequest> entity = new HttpEntity<>(request);
        ResponseEntity<ProcedureLogResponse> response = restTemplate.exchange(
                baseUrl + "/procedurelogs/" + procNum,
                HttpMethod.PUT,
                entity,
                ProcedureLogResponse.class);
        return response.getBody();
    }

    // --- PUT /procedurelogs/{procNum}/GroupNote ---
    public GroupNoteResponse updateGroupNote(Long procNum, UpdateGroupNoteRequest request) {
        HttpEntity<UpdateGroupNoteRequest> entity = new HttpEntity<>(request);
        ResponseEntity<GroupNoteResponse> response = restTemplate.exchange(
                baseUrl + "/procedurelogs/" + procNum + "/GroupNote",
                HttpMethod.PUT,
                entity,
                GroupNoteResponse.class);
        return response.getBody();
    }

    // --- DELETE /procedurelogs/{procNum} ---
    public void deleteProcedureLog(Long procNum) {
        restTemplate.delete(baseUrl + "/procedurelogs/" + procNum);
    }

    // --- DELETE /procedurelogs/{procNum}/GroupNote ---
    public void deleteGroupNote(Long procNum) {
        restTemplate.delete(baseUrl + "/procedurelogs/" + procNum + "/GroupNote");
    }

    // ========================================================================
    // Query Endpoints
    // ========================================================================

    // --- POST /queries ---
    public void runQuery(QueryRequest request) {
        restTemplate.postForObject(
                baseUrl + "/queries",
                request,
                Void.class);
    }

    // --- PUT /queries/ShortQuery ---
    public List<Map<String, Object>> runShortQuery(ShortQueryRequest request, Integer offset) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/queries/ShortQuery");

        if (offset != null) {
            builder.queryParam("Offset", offset);
        }

        HttpEntity<ShortQueryRequest> entity = new HttpEntity<>(request);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        return response.getBody();
    }

    // ========================================================================
    // Document Endpoints
    // ========================================================================

    // --- GET /documents/{docNum} ---
    public DocumentResponse getDocument(Long docNum) {
        return restTemplate.getForObject(
                baseUrl + "/documents/" + docNum,
                DocumentResponse.class);
    }

    // --- GET /documents ---
    public List<DocumentResponse> getDocuments(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/documents");
        params.forEach(builder::queryParam);

        ResponseEntity<List<DocumentResponse>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DocumentResponse>>() {});
        return response.getBody();
    }

    // --- POST /documents/Upload ---
    public DocumentResponse uploadDocument(UploadDocumentRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/documents/Upload",
                request,
                DocumentResponse.class);
    }

    // --- POST /documents/SetByUrl ---
    public DocumentResponse setByUrl(SetByUrlRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/documents/SetByUrl",
                request,
                DocumentResponse.class);
    }

    // --- POST /documents/UploadSftp ---
    public DocumentResponse uploadSftp(UploadSftpRequest request) {
        return restTemplate.postForObject(
                baseUrl + "/documents/UploadSftp",
                request,
                DocumentResponse.class);
    }

    // --- POST /documents/DownloadSftp ---
    public String downloadSftp(DownloadSftpRequest request) {
        HttpEntity<DownloadSftpRequest> entity = new HttpEntity<>(request);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/documents/DownloadSftp",
                HttpMethod.POST,
                entity,
                String.class);
        return response.getBody();
    }

    // --- POST /documents/Thumbnails ---
    public List<ThumbnailResult> getThumbnails(ThumbnailsRequest request) {
        HttpEntity<ThumbnailsRequest> entity = new HttpEntity<>(request);
        ResponseEntity<List<ThumbnailResult>> response = restTemplate.exchange(
                baseUrl + "/documents/Thumbnails",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<ThumbnailResult>>() {});
        return response.getBody();
    }

    // --- POST /documents/DownloadMount ---
    public List<ThumbnailResult> downloadMount(DownloadMountRequest request) {
        HttpEntity<DownloadMountRequest> entity = new HttpEntity<>(request);
        ResponseEntity<List<ThumbnailResult>> response = restTemplate.exchange(
                baseUrl + "/documents/DownloadMount",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<ThumbnailResult>>() {});
        return response.getBody();
    }

    // --- PUT /documents/{docNum} ---
    public DocumentResponse updateDocument(Long docNum, UpdateDocumentRequest request) {
        HttpEntity<UpdateDocumentRequest> entity = new HttpEntity<>(request);
        ResponseEntity<DocumentResponse> response = restTemplate.exchange(
                baseUrl + "/documents/" + docNum,
                HttpMethod.PUT,
                entity,
                DocumentResponse.class);
        return response.getBody();
    }

    // --- DELETE /documents/{docNum} ---
    public void deleteDocument(Long docNum) {
        restTemplate.delete(baseUrl + "/documents/" + docNum);
    }
}
