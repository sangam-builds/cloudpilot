package com.cloudpilot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "faqs")
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    public Faq() {}

    public Faq(Long id, String category, String question, String answer, ZonedDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public static FaqBuilder builder() { return new FaqBuilder(); }

    public static class FaqBuilder {
        private Long id;
        private String category;
        private String question;
        private String answer;
        private ZonedDateTime createdAt;

        public FaqBuilder id(Long id) { this.id = id; return this; }
        public FaqBuilder category(String category) { this.category = category; return this; }
        public FaqBuilder question(String question) { this.question = question; return this; }
        public FaqBuilder answer(String answer) { this.answer = answer; return this; }
        public FaqBuilder createdAt(ZonedDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Faq build() {
            return new Faq(id, category, question, answer, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
