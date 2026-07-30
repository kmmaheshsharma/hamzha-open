package com.clinic.opendental.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private String serverDateTime;
    private Long AptNum;
    private Long PatNum;
    private String AptStatus;
    private String Pattern;
    private Long Confirmed;
    private String confirmed;
    private String TimeLocked;
    private Long Op;
    private String Note;
    private Long ProvNum;
    private String provAbbr;
    private Long ProvHyg;
    private String AptDateTime;
    private Long NextAptNum;
    private Long UnschedStatus;
    private String unschedStatus;
    private String IsNewPatient;
    private String ProcDescript;
    private Long Assistant;
    private Long ClinicNum;
    private String IsHygiene;
    private String DateTStamp;
    private String DateTimeArrived;
    private String DateTimeSeated;
    private String DateTimeDismissed;
    private Long InsPlan1;
    private Long InsPlan2;
    private String DateTimeAskedToArrive;
    private String colorOverride;
    private Long AppointmentTypeNum;
    private Long SecUserNumEntry;
    private String SecDateTEntry;
    private String Priority;
    private String PatternSecondary;
    private Long ItemOrderPlanned;
    private String IsMirrored;
    private String eServiceLogType;
}