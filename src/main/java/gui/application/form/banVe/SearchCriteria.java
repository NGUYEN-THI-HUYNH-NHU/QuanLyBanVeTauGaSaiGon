package gui.application.form.banVe;
/*
 * @(#) SearchCriteria.java  1.0  [10:37:25 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 30, 2025
 * @version: 1.0
 */

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO chứa thông tin tìm chuyến (từ PanelBuoc1 -> SearchListener/PanelBuoc2)
 */
public class SearchCriteria {
    private final String gaDiId;
    private final String tenGaDi;
    private final String gaDenId;
    private final String tenGaDen;
    private final LocalDate ngayDi;
    private final LocalDate ngayVe;
    private final boolean khuHoi;

    private SearchCriteria(Builder b) {
        this.gaDiId = b.gaDiId;
        this.tenGaDi = b.tenGaDi;
        this.gaDenId = b.gaDenId;
        this.tenGaDen = b.tenGaDen;
        this.ngayDi = b.ngayDi;
        this.ngayVe = b.ngayVe;
        this.khuHoi = b.khuHoi;
    }

    public String getGaDiId() {
        return gaDiId;
    }

    public String getGaDiName() {
        return tenGaDi;
    }

    public String getGaDenId() {
        return gaDenId;
    }

    public String getGaDenName() {
        return tenGaDen;
    }

    public LocalDate getNgayDi() {
        return ngayDi;
    }

    public LocalDate getNgayVe() {
        return ngayVe;
    }

    public boolean isKhuHoi() {
        return khuHoi;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" + "gaDiId='" + gaDiId + ", tenGaDi='" + tenGaDi + '\'' + ", gaDenId='" + gaDenId
                + ", tenGaDen='" + tenGaDen + '\'' + ", ngayDi=" + ngayDi + ", ngayVe=" + ngayVe + ", khuHoi=" + khuHoi
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchCriteria)) {
            return false;
        }
        SearchCriteria that = (SearchCriteria) o;
        return khuHoi == that.khuHoi && Objects.equals(gaDiId, that.gaDiId) && Objects.equals(tenGaDi, that.tenGaDi)
                && Objects.equals(gaDenId, that.gaDenId) && Objects.equals(tenGaDen, that.tenGaDen)
                && Objects.equals(ngayDi, that.ngayDi) && Objects.equals(ngayVe, that.ngayVe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gaDiId, tenGaDi, gaDenId, tenGaDen, ngayDi, ngayVe, khuHoi);
    }

    public boolean isValidForSearch() {
        if (tenGaDi == null || tenGaDi.trim().isEmpty()) {
            return false;
        }
        if (tenGaDen == null || tenGaDen.trim().isEmpty()) {
            return false;
        }
        if (ngayDi == null) {
            return false;
        }
        // nếu khứ hồi, ngày về phải khác null và >= ngày đi
        if (khuHoi) {
            if (ngayVe == null) {
                return false;
            }
            if (ngayVe.isBefore(ngayDi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Trả về criteria cho chiều ngược lại (dùng khi chuẩn bị tìm chiều về). - hoán
     * đổi gaDi <-> gaDen - đặt ngayDi = ngayVe (nếu có)
     */
    public SearchCriteria buildReturnCriteria() {
        if (!khuHoi) {
            return null;
        }
        return new Builder().gaDiId(this.gaDenId).tenGaDi(this.tenGaDen).gaDenId(this.gaDiId).tenGaDen(this.tenGaDi)
                .ngayDi(this.ngayVe).khuHoi(false) // khi tìm return, treat as one-way find for that leg
                .build();
    }

    public static class Builder {
        private String gaDiId;
        private String tenGaDi;
        private String gaDenId;
        private String tenGaDen;
        private LocalDate ngayDi;
        private LocalDate ngayVe;
        private boolean khuHoi = false;

        public Builder gaDiId(String id) {
            this.gaDiId = id;
            return this;
        }

        public Builder tenGaDi(String name) {
            this.tenGaDi = name;
            return this;
        }

        public Builder gaDenId(String id) {
            this.gaDenId = id;
            return this;
        }

        public Builder tenGaDen(String name) {
            this.tenGaDen = name;
            return this;
        }

        public Builder ngayDi(LocalDate d) {
            this.ngayDi = d;
            return this;
        }

        public Builder ngayVe(LocalDate d) {
            this.ngayVe = d;
            return this;
        }

        public Builder khuHoi(boolean b) {
            this.khuHoi = b;
            return this;
        }

        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
}