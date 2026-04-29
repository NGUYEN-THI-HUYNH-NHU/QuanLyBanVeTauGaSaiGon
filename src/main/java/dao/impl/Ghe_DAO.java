package dao.impl;
/*
 * @(#) Ghe_DAO.java  1.0  [1:00:58 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import dao.IGhe_DAO;
import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class Ghe_DAO extends AbstractGenericDao<Ghe, String> implements IGhe_DAO {

    public Ghe_DAO() {
        super(Ghe.class);
    }

    /**
     * 1. Truy vấn bằng Native Query với ánh xạ trạng thái Transient
     */
    @Override
    public List<Ghe> getGheByGaDiGaDenChuyenToa(String gaDiID, String gaDenID, String chuyenID, String toaID) {
        return doInTransaction(em -> {
            String sql = "DECLARE @chuyenID VARCHAR(50) = :chuyenID;\n"
                    + "DECLARE @gaDiID   VARCHAR(50) = :gaDiID;\n"
                    + "DECLARE @gaDenID  VARCHAR(50) = :gaDenID;\n"
                    + "DECLARE @toaID    VARCHAR(50) = :toaID;\n"
                    + "DECLARE @holdMinutes INT = 10;\n\n"
                    + "-- Lấy thứ tự ga đi / ga đến (của phân đoạn *yêu cầu*)\n"
                    + "DECLARE @thuTuGaDi_YeuCau  INT, @thuTuGaDen_YeuCau INT;\n"
                    + "SELECT @thuTuGaDi_YeuCau = cg.thuTu FROM ChuyenGa cg WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDiID;\n"
                    + "SELECT @thuTuGaDen_YeuCau = cg.thuTu FROM ChuyenGa cg WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDenID;\n\n"
                    + "IF @thuTuGaDi_YeuCau IS NULL OR @thuTuGaDen_YeuCau IS NULL\n"
                    + "BEGIN\n"
                    + "    RAISERROR('Không tìm được gaDi hoặc gaDen cho chuyenID = %s', 16, 1, @chuyenID);\n"
                    + "    RETURN;\n"
                    + "END\n\n"
                    + ";WITH GheList AS (\n"
                    + "    SELECT g.gheID, g.toaID, g.soGhe FROM Ghe g WHERE g.toaID = @toaID\n"
                    + "),\n"
                    + "VeChongLap AS (\n"
                    + "    SELECT v.gheID, COUNT(1) AS cntVe FROM Ve v\n"
                    + "    JOIN ChuyenGa cgDi ON v.chuyenID = cgDi.chuyenID AND v.gaDiID = cgDi.gaID\n"
                    + "    JOIN ChuyenGa cgDen ON v.chuyenID = cgDen.chuyenID AND v.gaDenID = cgDen.gaID\n"
                    + "    WHERE v.chuyenID = @chuyenID AND v.trangThai IN ('DA_BAN','DA_DUNG')\n"
                    + "      AND cgDi.thuTu < @thuTuGaDen_YeuCau AND cgDen.thuTu > @thuTuGaDi_YeuCau\n"
                    + "    GROUP BY v.gheID\n"
                    + "),\n"
                    + "PGC_ChongLap AS (\n"
                    + "    SELECT pgcct.gheID,\n"
                    + "        SUM(CASE WHEN pgcct.trangThai = 'XAC_NHAN' THEN 1 ELSE 0 END) AS cntXacNhan,\n"
                    + "        SUM(CASE WHEN pgcct.trangThai = 'DANG_GIU' AND pg.trangThai = 'DANG_GIU' \n"
                    + "                 AND pg.thoiDiemTao > DATEADD(minute, -@holdMinutes, SYSUTCDATETIME()) \n"
                    + "            THEN 1 ELSE 0 END) AS cntDangGiuConHieuLuc\n"
                    + "    FROM PhieuGiuChoChiTiet pgcct\n"
                    + "    JOIN PhieuGiuCho pg ON pg.phieuGiuChoID = pgcct.phieuGiuChoID\n"
                    + "    JOIN ChuyenGa cgDi ON pgcct.chuyenID = cgDi.chuyenID AND pgcct.gaDiID = cgDi.gaID\n"
                    + "    JOIN ChuyenGa cgDen ON pgcct.chuyenID = cgDen.chuyenID AND pgcct.gaDenID = cgDen.gaID\n"
                    + "    WHERE pgcct.chuyenID = @chuyenID AND pgcct.trangThai IN ('DANG_GIU', 'XAC_NHAN')\n"
                    + "      AND cgDi.thuTu < @thuTuGaDen_YeuCau AND cgDen.thuTu > @thuTuGaDi_YeuCau\n"
                    + "    GROUP BY pgcct.gheID\n"
                    + ")\n"
                    + "SELECT g.gheID, g.soGhe, g.toaID,\n"
                    + "    CASE\n"
                    + "        WHEN ISNULL(vc.cntVe, 0) > 0 THEN 'DA_BAN'\n"
                    + "        WHEN ISNULL(pc.cntXacNhan, 0) > 0 THEN 'DA_BAN'\n"
                    + "        WHEN ISNULL(pc.cntDangGiuConHieuLuc, 0) > 0 THEN 'BI_CHIEM'\n"
                    + "        ELSE 'TRONG'\n"
                    + "    END AS trangThai\n"
                    + "FROM GheList g\n"
                    + "LEFT JOIN VeChongLap vc ON vc.gheID = g.gheID\n"
                    + "LEFT JOIN PGC_ChongLap pc ON pc.gheID = g.gheID\n"
                    + "ORDER BY g.soGhe;";

            Query query = em.createNativeQuery(sql);
            query.setParameter("chuyenID", chuyenID);
            query.setParameter("gaDiID", gaDiID);
            query.setParameter("gaDenID", gaDenID);
            query.setParameter("toaID", toaID);

            List<Object[]> results = query.getResultList();
            List<Ghe> gheList = new ArrayList<>();

            for (Object[] row : results) {
                Ghe g = new Ghe();
                g.setId((String) row[0]); // gheID
                g.setSoGhe((Integer) row[1]); // soGhe

                Toa toa = new Toa();
                toa.setId((String) row[2]); // toaID
                g.setToa(toa);

                // Đọc chuỗi tính toán từ DB và set vào thuộc tính @Transient
                String trangThaiStr = (String) row[3];
                g.setTrangThai(TrangThaiGhe.valueOf(trangThaiStr));

                gheList.add(g);
            }
            return gheList;
        });
    }

    /**
     * 2. Truy vấn chi tiết Ghế bằng JPQL (Rất ngắn gọn)
     * Lưu ý: Cột trangThai không có trong CSDL nên JPQL không thể SELECT nó.
     * Thuộc tính này sẽ được để trống (null) hoặc bạn tự gán mặc định.
     */
    @Override
    public Ghe getGheByChuyenIDGheID(String chuyenID, String gheID) {
        return doInTransaction(em -> {
            String jpql = "SELECT g FROM Ghe g " +
                    "JOIN g.toa toa " +
                    "JOIN toa.tau tau " +
                    "JOIN Chuyen c ON c.tau.id = tau.id " +
                    "WHERE c.id = :chuyenID AND g.id = :gheID";
            try {
                return em.createQuery(jpql, Ghe.class)
                        .setParameter("chuyenID", chuyenID)
                        .setParameter("gheID", gheID)
                        .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 3. Tính toán giá vé (Native Query)
     */
    @Override
    public int calcGia(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
        return doInTransaction(em -> {
            String sql = "DECLARE @chuyenID VARCHAR(50) = :chuyenID;\n"
                    + "DECLARE @gaDiID VARCHAR(50) = :gaDiID;\n"
                    + "DECLARE @gaDenID VARCHAR(50) = :gaDenID;\n"
                    + "DECLARE @loaiTauID VARCHAR(50) = :loaiTauID;\n"
                    + "DECLARE @hangToaID VARCHAR(50) = :hangToaID;\n\n"
                    + ";WITH ChuyenInfo AS (\n"
                    + "    SELECT c.tuyenID, c.ngayDi, ABS(ttDen.khoangCachTuGaXuatPhatKm - ttDi.khoangCachTuGaXuatPhatKm) AS km\n"
                    + "    FROM Chuyen c\n"
                    + "    JOIN TuyenChiTiet ttDi ON c.tuyenID = ttDi.tuyenID AND ttDi.gaID = @gaDiID\n"
                    + "    JOIN TuyenChiTiet ttDen ON c.tuyenID = ttDen.tuyenID AND ttDen.gaID = @gaDenID\n"
                    + "    WHERE c.chuyenID = @chuyenID\n"
                    + "),\n"
                    + "BestRule AS (\n"
                    + "    SELECT TOP 1 bg.donGiaTrenKm, bg.giaCoBan, ci.km\n"
                    + "    FROM BieuGiaVe bg\n"
                    + "    JOIN ChuyenInfo ci ON ci.km BETWEEN bg.minKm AND bg.maxKm\n"
                    + "    WHERE (ci.ngayDi >= bg.ngayBatDau AND (bg.ngayKetThuc IS NULL OR ci.ngayDi <= bg.ngayKetThuc))\n"
                    + "      AND (bg.tuyenApDungID IS NULL OR bg.tuyenApDungID = ci.tuyenID)\n"
                    + "      AND bg.loaiTauApDungID = @loaiTauID AND bg.hangToaApDungID = @hangToaID\n"
                    + "    ORDER BY bg.doUuTien DESC, (CASE WHEN bg.tuyenApDungID IS NOT NULL THEN 1 ELSE 2 END) ASC\n"
                    + "),\n"
                    + "BasePrice AS (\n"
                    + "    SELECT CASE WHEN r.donGiaTrenKm IS NOT NULL THEN r.donGiaTrenKm * r.km ELSE r.giaCoBan END AS giaTrcHeSo\n"
                    + "    FROM BestRule r\n"
                    + "),\n"
                    + "Multipliers AS (\n"
                    + "    SELECT \n"
                    + "        ISNULL((SELECT hsg FROM HeSoGiaLoaiTau hst WHERE hst.loaiTauID = @loaiTauID AND hst.isCoHieuLuc = 1), 1.0) AS hsgTau,\n"
                    + "        ISNULL((SELECT hsg FROM HeSoGiaHangToa hsh WHERE hsh.hangToaID = @hangToaID AND hsh.isCoHieuLuc = 1), 1.0) AS hsgToa\n"
                    + ")\n"
                    + "SELECT ISNULL(ROUND(bp.giaTrcHeSo * m.hsgTau * m.hsgToa, 2), 0.00) AS finalPrice\n"
                    + "FROM BasePrice bp, Multipliers m;";

            Query query = em.createNativeQuery(sql);
            query.setParameter("chuyenID", chuyenID);
            query.setParameter("gaDiID", gaDiID);
            query.setParameter("gaDenID", gaDenID);
            query.setParameter("loaiTauID", loaiTauID);
            query.setParameter("hangToaID", hangToaID);

            try {
                Number result = (Number) query.getSingleResult();
                if (result != null) {
                    return (int) (Math.round(result.doubleValue() / 1000.0) * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
    }
}