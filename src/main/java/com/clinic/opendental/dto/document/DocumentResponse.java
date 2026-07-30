package com.clinic.opendental.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Long DocNum;
    private Long MountNum;
    private String filePath;
    private String Description;
    private String PatNum;
    private String Note;
    private String DateCreated;
    private String docCategory;
    private Long DocCategory;
    private String FileName;
    private String ImgType;
    private String ToothNumbers;
    private String DateTStamp;
    private Long ProvNum;
    private String PrintHeading;
    private String serverDateTime;
}