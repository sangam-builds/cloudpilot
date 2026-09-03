package com.cloudpilot.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationDto implements Serializable {

    private String category;
    private String priority;
    private String sentiment;
    private String department;
    private Double confidence;
    private List<String> extractedKeywords;
    private String rationale;
}
