package com.cloudpilot.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private List<ActivityItemDto> recentActivity;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivityItemDto implements Serializable {
        private String type; // ORDER or TICKET
        private Long id;
        private String title;
        private String status;
        private BigDecimal amount;
        private ZonedDateTime timestamp;
    }
}
