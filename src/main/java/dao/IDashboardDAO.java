package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: IDashboardDAO
 * Author: hongdung
 */
public interface IDashboardDAO {
    double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate);

    int getKpiTicketsSold(LocalDate startDate, LocalDate endDate);

    int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate);

    int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate);

    Map<String, Double> getRevenueByPaymentMethod(LocalDate startDate, LocalDate endDate);

    Map<LocalDate, Double> getRevenueOverTime(LocalDate s, LocalDate e);

    Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate s, LocalDate e);

    Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate s, LocalDate e);

    Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate s, LocalDate e);

    Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate);

    int[] getTripOccupancyAlerts(LocalDate startDate, LocalDate endDate);

    List<Object[]> getHighOccupancyList(LocalDate startDate, LocalDate endDate);

    List<Object[]> getLowOccupancyList(LocalDate startDate, LocalDate endDate);
}