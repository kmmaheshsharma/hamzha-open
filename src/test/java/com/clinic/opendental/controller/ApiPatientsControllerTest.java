package com.clinic.opendental.controller;

import com.clinic.opendental.OpenDentalApiApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = OpenDentalApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ApiPatientsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getSinglePatientReturnsSamplePatient() throws Exception {
        mockMvc.perform(get("/api/patients/48"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PatNum", is(48)))
                .andExpect(jsonPath("$.LName", is("Smith")))
                .andExpect(jsonPath("$.Language", is("spa")))
                .andExpect(jsonPath("$.BalTotal", is(388.0)));
    }

    @Test
    void getSimplePatientsIncludesServerDateTimeAndBalanceFields() throws Exception {
        mockMvc.perform(get("/api/patients/Simple").param("LName", "smi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serverDateTime").exists())
                .andExpect(jsonPath("$[0].EstBalance", is(0.0)))
                .andExpect(jsonPath("$[0].Language", is("spa")));
    }

    @Test
    void getMultiplePatientsFiltersBySearchCriteria() throws Exception {
        mockMvc.perform(get("/api/patients").param("LName", "smi").param("Birthdate", "1976-05-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].LName", is("Smith")))
                .andExpect(jsonPath("$[0].Birthdate", is("1976-05-24")));
    }

    @Test
    void createPatientReturnsCreatedResponseAndLocationHeader() throws Exception {
        Map<String, Object> body = Map.of(
                "LName", "Doe",
                "FName", "John",
                "Language", "eng"
        );

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/patients/1001"))
                .andExpect(jsonPath("$.PatNum", is(1001)))
                .andExpect(jsonPath("$.LName", is("Doe")))
                .andExpect(jsonPath("$.Language", is("eng")));
    }

    @Test
    void updatePatientReturnsUpdatedFields() throws Exception {
        Map<String, Object> body = Map.of(
                "Preferred", "Janie",
                "PreferContactMethod", "WirelessPh"
        );

        mockMvc.perform(put("/api/patients/47")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PatNum", is(47)))
                .andExpect(jsonPath("$.Preferred", is("Janie")))
                .andExpect(jsonPath("$.PreferContactMethod", is("WirelessPh")));
    }

    @Test
    void deletePatientIsRejectedByApi() throws Exception {
        mockMvc.perform(delete("/api/patients/47"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error", is("Patients cannot be deleted via the Open Dental API. Delete is only supported in Open Dental.")));
    }
}