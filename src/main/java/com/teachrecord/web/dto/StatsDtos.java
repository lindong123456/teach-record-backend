package com.teachrecord.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StatsDtos {

    public record StatsRequest(
            Granularity granularity,
            @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING) LocalDate from,
            @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING) LocalDate to,
            Long studentId) {

        public enum Granularity {
            DAY,
            MONTH
        }
    }

    /** `date` as `yyyy-MM-dd` string to avoid Jackson temporal edge cases. */
    public record DayPoint(
            long studentId, String studentName, String date, BigDecimal totalHours, BigDecimal totalAmount) {}

    public record MonthPoint(
            long studentId, String studentName, int year, int month, BigDecimal totalHours, BigDecimal totalAmount) {
    }

    /** 所选时间范围内，按是否已结算汇总课时与金额（金额 = 课时 × 单价快照）。 */
    public record SettlementSummary(
            BigDecimal unsettledHours,
            BigDecimal unsettledAmount,
            BigDecimal settledHours,
            BigDecimal settledAmount) {}

    public record StatsResponse(
            List<DayPoint> byDay, List<MonthPoint> byMonth, SettlementSummary settlement) {}
}
