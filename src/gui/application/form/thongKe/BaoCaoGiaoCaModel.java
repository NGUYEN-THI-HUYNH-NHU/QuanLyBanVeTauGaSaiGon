package gui.application.form.thongKe;

/**
 * Model dùng để lưu trữ thông tin giao ca (tiền mặt thực tế và trạng thái xác nhận).
 */
public class BaoCaoGiaoCaModel {

    private double tienMatTaiKetValue = 0.0;
    private String ghiChu = "";
    private boolean isGiaoCaConfirmed = false;

    // Constructor mặc định
    public BaoCaoGiaoCaModel() {
    }

    // === SETTER để cập nhật từ NhapTienMat ===
    public void setGiaoCaData(double tienMatTaiKetValue, String ghiChu) {
        this.tienMatTaiKetValue = tienMatTaiKetValue;
        this.ghiChu = ghiChu;
        this.isGiaoCaConfirmed = true;
    }

    // === GETTER để PanelBaoCao đọc dữ liệu ===
    public double getTienMatTaiKetValue() {
        return tienMatTaiKetValue;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public boolean isGiaoCaConfirmed() {
        return isGiaoCaConfirmed;
    }

    /** Reset trạng thái sau khi đã xuất báo cáo cuối cùng */
    public void reset() {
        this.tienMatTaiKetValue = 0.0;
        this.ghiChu = "";
        this.isGiaoCaConfirmed = false;
    }
}