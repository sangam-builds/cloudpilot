package com.cloudpilot.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClassificationDto implements Serializable {

    private String category;
    private String priority;
    private String sentiment;
    private String department;
    private Double confidence;
    private List<String> extractedKeywords = new ArrayList<>();
    private String rationale;

    public ClassificationDto() {}

    public ClassificationDto(String category, String priority, String sentiment, String department, Double confidence, List<String> extractedKeywords, String rationale) {
        this.category = category;
        this.priority = priority;
        this.sentiment = sentiment;
        this.department = department;
        this.confidence = confidence;
        if (extractedKeywords != null) this.extractedKeywords = extractedKeywords;
        this.rationale = rationale;
    }

    public static ClassificationDtoBuilder builder() { return new ClassificationDtoBuilder(); }

    public static class ClassificationDtoBuilder {
        private String category;
        private String priority;
        private String sentiment;
        private String department;
        private Double confidence;
        private List<String> extractedKeywords = new ArrayList<>();
        private String rationale;

        public ClassificationDtoBuilder category(String category) { this.category = category; return this; }
        public ClassificationDtoBuilder priority(String priority) { this.priority = priority; return this; }
        public ClassificationDtoBuilder sentiment(String sentiment) { this.sentiment = sentiment; return this; }
        public ClassificationDtoBuilder department(String department) { this.department = department; return this; }
        public ClassificationDtoBuilder confidence(Double confidence) { this.confidence = confidence; return this; }
        public ClassificationDtoBuilder extractedKeywords(List<String> extractedKeywords) { this.extractedKeywords = extractedKeywords; return this; }
        public ClassificationDtoBuilder rationale(String rationale) { this.rationale = rationale; return this; }

        public ClassificationDto build() {
            return new ClassificationDto(category, priority, sentiment, department, confidence, extractedKeywords, rationale);
        }
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public List<String> getExtractedKeywords() { return extractedKeywords; }
    public void setExtractedKeywords(List<String> extractedKeywords) { this.extractedKeywords = extractedKeywords; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
}
