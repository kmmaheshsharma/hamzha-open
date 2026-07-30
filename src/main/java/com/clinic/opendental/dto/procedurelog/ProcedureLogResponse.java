package com.clinic.opendental.dto.procedurelog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureLogResponse {

    private Long ProcNum;
    private Long PatNum;
    private Long AptNum;
    private String ProcDate;
    private String ProcFee;
    private String Surf;
    private String ToothNum;
    private String ToothRange;
    private Long Priority;
    private String priority;
    private String ProcStatus;
    private Long ProvNum;
    private String provAbbr;
    private Long Dx;
    private String dxName;
    private Long PlannedAptNum;
    private String PlaceService;
    private String Prosthesis;
    private String DateOriginalProsth;
    private String ClaimNote;
    private String DateEntryC;
    private Long ClinicNum;
    private String DiagnosticCode;
    private String IsPrincDiag;
    private Long CodeNum;
    private String procCode;
    private String descript;
    private Integer UnitQty;
    private Integer BaseUnits;
    private String DateTP;
    private Long SiteNum;
    private String HideGraphics;
    private String CanadianTypeCodes;
    private String ProcTime;
    private String ProcTimeEnd;
    private String DateTStamp;
    private Long Prognosis;
    private String IsLocked;
    private String BillingNote;
    private String SnomedBodySite;
    private String DiagnosticCode2;
    private String DiagnosticCode3;
    private String DiagnosticCode4;
    private Double Discount;
    private String IsDateProsthEst;
    private Integer IcdVersion;
    private String SecDateEntry;
    private Double DiscountPlanAmt;
    private String serverDateTime;
}