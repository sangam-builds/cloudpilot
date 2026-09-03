package com.cloudpilot.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer360Dto implements Serializable {

    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private ZonedDateTime customerSince;
    private Integer totalOrders;
    private BigDecimal totalSpend;
    private Integer openTicketsCount;
    private Integer resolvedTicketsCount;
    private String aiSummary;
    private List<ActivityItemDto> recentActivity = new ArrayList<>();

    public Customer360Dto() {}

    public Customer360Dto(Long customerId, String name, String email, String phone, ZonedDateTime customerSince,
                          Integer totalOrders, BigDecimal totalSpend, Integer openTicketsCount, Integer resolvedTicketsCount,
                          String aiSummary, List<ActivityItemDto> recentActivity) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.customerSince = customerSince;
        this.totalOrders = totalOrders;
        this.totalSpend = totalSpend;
        this.openTicketsCount = openTicketsCount;
        this.resolvedTicketsCount = resolvedTicketsCount;
        this.aiSummary = aiSummary;
        if (recentActivity != null) this.recentActivity = recentActivity;
    }

    public static Customer360DtoBuilder builder() { return new Customer360DtoBuilder(); }

    public static class Customer360DtoBuilder {
        private Long customerId;
        private String name;
        private String email;
        private String phone;
        private ZonedDateTime customerSince;
        private Integer totalOrders;
        private BigDecimal totalSpend;
        private Integer openTicketsCount;
        private Integer resolvedTicketsCount;
        private String aiSummary;
        private List<ActivityItemDto> recentActivity = new ArrayList<>();

        public Customer360DtoBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Customer360DtoBuilder name(String name) { this.name = name; return this; }
        public Customer360DtoBuilder email(String email) { this.email = email; return this; }
        public Customer360DtoBuilder phone(String phone) { this.phone = phone; return this; }
        public Customer360DtoBuilder customerSince(ZonedDateTime customerSince) { this.customerSince = customerSince; return this; }
        public Customer360DtoBuilder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public Customer360DtoBuilder totalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; return this; }
        public Customer360DtoBuilder openTicketsCount(Integer openTicketsCount) { this.openTicketsCount = openTicketsCount; return this; }
        public Customer360DtoBuilder resolvedTicketsCount(Integer resolvedTicketsCount) { this.resolvedTicketsCount = resolvedTicketsCount; return this; }
        public Customer360DtoBuilder aiSummary(String aiSummary) { this.aiSummary = aiSummary; return this; }
        public Customer360DtoBuilder recentActivity(List<ActivityItemDto> recentActivity) { this.recentActivity = recentActivity; return this; }

        public Customer360Dto build() {
            return new Customer360Dto(customerId, name, email, phone, customerSince, totalOrders, totalSpend, openTicketsCount, resolvedTicketsCount, aiSummary, recentActivity);
        }
    }

    public static class ActivityItemDto implements Serializable {
        private String type;
        private Long id;
        private String title;
        private String status;
        private BigDecimal amount;
        private ZonedDateTime timestamp;

        public ActivityItemDto() {}

        public ActivityItemDto(String type, Long id, String title, String status, BigDecimal amount, ZonedDateTime timestamp) {
            this.type = type;
            this.id = id;
            this.title = title;
            this.status = status;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public static ActivityItemDtoBuilder builder() { return new ActivityItemDtoBuilder(); }

        public static class ActivityItemDtoBuilder {
            private String type;
            private Long id;
            private String title;
            private String status;
            private BigDecimal amount;
            private ZonedDateTime timestamp;

            public ActivityItemDtoBuilder type(String type) { this.type = type; return this; }
            public ActivityItemDtoBuilder id(Long id) { this.id = id; return this; }
            public ActivityItemDtoBuilder title(String title) { this.title = title; return this; }
            public ActivityItemDtoBuilder status(String status) { this.status = status; return this; }
            public ActivityItemDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
            public ActivityItemDtoBuilder timestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; return this; }

            public ActivityItemDto build() {
                return new ActivityItemDto(type, id, title, status, amount, timestamp);
            }
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public ZonedDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public ZonedDateTime getCustomerSince() { return customerSince; }
    public void setCustomerSince(ZonedDateTime customerSince) { this.customerSince = customerSince; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getTotalSpend() { return totalSpend; }
    public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }
    public Integer getOpenTicketsCount() { return openTicketsCount; }
    public void setOpenTicketsCount(Integer openTicketsCount) { this.openTicketsCount = openTicketsCount; }
    public Integer getResolvedTicketsCount() { return resolvedTicketsCount; }
    public void setResolvedTicketsCount(Integer resolvedTicketsCount) { this.resolvedTicketsCount = resolvedTicketsCount; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public List<ActivityItemDto> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<ActivityItemDto> recentActivity) { this.recentActivity = recentActivity; }
}
