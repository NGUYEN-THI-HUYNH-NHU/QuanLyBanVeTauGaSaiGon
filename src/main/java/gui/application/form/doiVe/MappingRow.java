package gui.application.form.doiVe;
/*
 * @(#) MappingRow.java  1.0  [6:45:43 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import gui.application.form.banVe.VeSession;

public class MappingRow {
    private VeDoiRow veDoiRow; // Vé cũ (Đã có thông tin lệ phí, hành khách)
    private VeSession veSessionMoi; // Vé mới (Có thể null nếu chưa chọn)

    public MappingRow(VeDoiRow veDoiRow, VeSession veSessionMoi) {
        this.veDoiRow = veDoiRow;
        this.veSessionMoi = veSessionMoi;
    }

    public VeDoiRow getVeDoiRow() {
        return veDoiRow;
    }

    public VeSession getVeSessionMoi() {
        return veSessionMoi;
    }

    public void setVeSessionMoi(VeSession veSessionMoi) {
        this.veSessionMoi = veSessionMoi;
    }

    // Tính tiền chênh lệch: (Giá vé mới + Phí đổi + Giá phiếu dùng phòng chờ VIP
    // (nếu có)) - Giá vé cũ
    public double getChenhLech() {
        double giaMoi = (veSessionMoi != null) ? veSessionMoi.getVe().getGia() : 0;
        double giaPhieu = (veSessionMoi != null) ? veSessionMoi.getPhiPhieuDungPhongChoVIP() : 0;
        double giaCu = veDoiRow.getVe().getGia();
        double phi = veDoiRow.getLePhiDoiVe();
        return (giaMoi + phi + giaPhieu) - giaCu;
    }
}