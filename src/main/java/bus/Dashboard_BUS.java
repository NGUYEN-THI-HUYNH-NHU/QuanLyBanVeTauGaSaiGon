package bus;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: Dashboard_BUS
 * Author: hongdung
 * Created: 5/5/26 11:35 AM
 */

import dao.impl.DashboardDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BUS layer cho Dashboard.
 * Chịu trách nhiệm validate input và điều phối gọi DAO.
 */
public class Dashboard_BUS {

    private final DashboardDAO dao = new DashboardDAO();

    // =========================================================================
    // 1. KPI & TỔNG QUAN
    // =========================================================================

    public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return dao.getKpiTotalRevenue(startDate, endDate);
    }

    public int getKpiTicketsSold(LocalDate startDate, LocalDate endDate) {
        return dao.getKpiTicketsSold(startDate, endDate);
    }

    public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
        return dao.getTotalAvailableSeats(startDate, endDate);
    }

    public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
        return dao.getTotalRefundsAndExchanges(startDate, endDate);
    }

    // =========================================================================
    // 2. DOANH THU THEO PHƯƠNG THỨC THANH TOÁN
    // =========================================================================

    public Map<String, Double> getRevenueByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        return dao.getRevenueByPaymentMethod(startDate, endDate);
    }

    // =========================================================================
    // 3. DOANH THU THEO THỜI GIAN
    // =========================================================================

    public Map<LocalDate, Double> getRevenueOverTime(LocalDate s, LocalDate e) {
        return dao.getRevenueOverTime(s, e);
    }

    public Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate s, LocalDate e) {
        return dao.getRevenueOverTimeByMonth(s, e);
    }

    public Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate s, LocalDate e) {
        return dao.getRevenueOverTimeByYear(s, e);
    }

    // =========================================================================
    // 4. HÓA ĐƠN
    // =========================================================================

    public Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate s, LocalDate e) {
        return dao.getInvoicesPaidOverTime(s, e);
    }

    public Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate s, LocalDate e) {
        return dao.getInvoicesRefundedOverTime(s, e);
    }

    public Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate s, LocalDate e) {
        return dao.getInvoicesPaidByMonth(s, e);
    }

    public Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate s, LocalDate e) {
        return dao.getInvoicesRefundedByMonth(s, e);
    }

    public Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate s, LocalDate e) {
        return dao.getInvoicesPaidByYear(s, e);
    }

    public Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate s, LocalDate e) {
        return dao.getInvoicesRefundedByYear(s, e);
    }

    // =========================================================================
    // 5. VÉ THEO LOẠI GHẾ
    // =========================================================================

    public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
        return dao.getTicketsBySeatTypeOverTime(startDate, endDate);
    }

    // =========================================================================
    // 6. CẢNH BÁO CHUYẾN TÀU
    // =========================================================================

    public int[] getTripOccupancyAlerts(LocalDate startDate, LocalDate endDate) {
        return dao.getTripOccupancyAlerts(startDate, endDate);
    }

    public List<Object[]> getHighOccupancyList(LocalDate startDate, LocalDate endDate) {
        return dao.getHighOccupancyList(startDate, endDate);
    }

    public List<Object[]> getLowOccupancyList(LocalDate startDate, LocalDate endDate) {
        return dao.getLowOccupancyList(startDate, endDate);
    }
}