package gui.application.form.thongKe;

import java.util.HashMap;
import java.util.Map;

/**
 * Model dùng để lưu trữ thông tin giao ca (tiền mặt thực tế, chi tiết tờ tiền và trạng thái xác nhận).
 */
public class BaoCaoGiaoCaModel {

    private double tienMatTaiKetValue = 0.0;
    private String ghiChu = "";
    private boolean isGiaoCaConfirmed = false;

    // BIẾN MỚI: Lưu số lượng tờ tiền (Mệnh giá -> Số lượng)
    private Map<Integer, Integer> chiTietTienMat = new HashMap<>();

    // Constructor mặc định
    public BaoCaoGiaoCaModel() {
    }

    // === SETTER để cập nhật từ NhapTienMat (ĐÃ CẬP NHẬT) ===
    public void setGiaoCaData(double tienMatTaiKetValue, String ghiChu, Map<Integer, Integer> chiTietTienMat) {
        this.tienMatTaiKetValue = tienMatTaiKetValue;
        this.ghiChu = ghiChu;
        this.isGiaoCaConfirmed = true;
        this.chiTietTienMat = chiTietTienMat; // Lưu chi tiết
    }

    // === GETTER ===
    public double getTienMatTaiKetValue() {
        return tienMatTaiKetValue;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public boolean isGiaoCaConfirmed() {
        return isGiaoCaConfirmed;
    }

    // GETTER MỚI CHO MAP
    public Map<Integer, Integer> getChiTietTienMat() {
        return chiTietTienMat;
    }

    /** Reset trạng thái sau khi đã xuất báo cáo cuối cùng */
    public void reset() {
        this.tienMatTaiKetValue = 0.0;
        this.ghiChu = "";
        this.isGiaoCaConfirmed = false;
        if (this.chiTietTienMat != null) {
            this.chiTietTienMat.clear();
        }
    }
}