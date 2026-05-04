/*
 * @(#) KhuyenMaiDAO.java  1.0  [4:03 PM] 5/1/2026
 *
 * Copyright (c) 2026 IUH. All rights reserved.
 */

/*
 * @description
 * @author: Yen
 * @date: 5/1/2026
 * @version: 1.0
 */

package dao.impl;

import dao.IKhuyenMaiDAO;
import dto.VeDTO;
import entity.*;
import gui.application.form.banVe.VeSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhuyenMaiDAO extends AbstractGenericDAO<KhuyenMai, String> implements IKhuyenMaiDAO {
    public KhuyenMaiDAO(){
        super(KhuyenMai.class);
    }

    @Override
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        try {
            return doInTransaction(em -> {
                em.persist(km);
                dkkm.setKhuyenMai(km);
                em.persist(dkkm);
                return true;
            });
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dk) {
        try {
            return doInTransaction(em -> {

                KhuyenMai managedKm = em.find(KhuyenMai.class, km.getKhuyenMaiID());
                if (managedKm == null) {
                    System.err.println("Không tìm thấy Khuyến mãi trong DB!");
                    return false;
                }


                managedKm.setMaKhuyenMai(km.getMaKhuyenMai());
                managedKm.setMoTa(km.getMoTa());
                managedKm.setTyLeGiamGia(km.getTyLeGiamGia());
                managedKm.setTienGiamGia(km.getTienGiamGia());
                managedKm.setNgayBatDau(km.getNgayBatDau());
                managedKm.setNgayKetThuc(km.getNgayKetThuc());
                managedKm.setSoLuong(km.getSoLuong());
                managedKm.setGioiHanMoiKhachHang(km.getGioiHanMoiKhachHang());
                managedKm.setTrangThai(km.isTrangThai());

                DieuKienKhuyenMai managedDk = em.createQuery(
                                "SELECT d FROM DieuKienKhuyenMai d WHERE d.khuyenMai.khuyenMaiID = :kmID",
                                DieuKienKhuyenMai.class)
                        .setParameter("kmID", km.getKhuyenMaiID())
                        .getResultList()
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (managedDk != null) {

                    managedDk.setTuyen(dk.getTuyen());
                    managedDk.setLoaiTau(dk.getLoaiTau());
                    managedDk.setHangToa(dk.getHangToa());
                    managedDk.setLoaiDoiTuong(dk.getLoaiDoiTuong());
                    managedDk.setNgayTrongTuan(dk.getNgayTrongTuan());
                    managedDk.setNgayLe(dk.isNgayLe());
                    managedDk.setMinGiaTriDonHang(dk.getMinGiaTriDonHang());

                    managedDk.setKhuyenMai(managedKm);

                    em.flush();
                }

                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate ngayBD, LocalDate ngayKT, LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuongEnums) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder();
            jpql.append("SELECT DISTINCT km FROM KhuyenMai km ")
                    .append("LEFT JOIN FETCH km.dieuKienKhuyenMai dk ")
                    .append("WHERE 1=1 ");

            if (tuKhoa != null && !tuKhoa.isBlank()) {
                jpql.append(" AND (km.maKhuyenMai LIKE :tuKhoa OR km.moTa LIKE :tuKhoa) ");
            }
            if (maTuyen != null && !maTuyen.isBlank()) {
                jpql.append(" AND dk.tuyen.tuyenID = :maTuyen ");
            }
            if (trangThai != null) {
                jpql.append(" AND km.trangThai = :trangThai ");
            }
            if (ngayBD != null) {
                jpql.append(" AND km.ngayBatDau >= :ngayBD ");
            }
            if (ngayKT != null) {
                jpql.append(" AND km.ngayKetThuc <= :ngayKT ");
            }
            if (loaiTau != null) {
                jpql.append(" AND dk.loaiTau.loaiTauID = :loaiTauID ");
            }
            if (hangToa != null) {
                jpql.append(" AND dk.hangToa.hangToaID = :hangToaID ");
            }
            if (loaiDoiTuongEnums != null) {
                jpql.append(" AND dk.loaiDoiTuong.loaiDoiTuongID = :loaiDoiTuongID ");
            }

            jpql.append(" ORDER BY km.ngayBatDau DESC ");

            var query = em.createQuery(jpql.toString(), KhuyenMai.class);

            // Gán tham số
            if (tuKhoa != null && !tuKhoa.isBlank()) query.setParameter("tuKhoa", "%" + tuKhoa.trim() + "%");
            if (maTuyen != null && !maTuyen.isBlank()) query.setParameter("maTuyen", maTuyen.trim());
            if (trangThai != null) query.setParameter("trangThai", trangThai);
            if (ngayBD != null) query.setParameter("ngayBD", ngayBD);
            if (ngayKT != null) query.setParameter("ngayKT", ngayKT);
            if (loaiTau != null) query.setParameter("loaiTauID", loaiTau.getLoaiTauID());
            if (hangToa != null) query.setParameter("hangToaID", hangToa.getHangToaID());
            if (loaiDoiTuongEnums != null) query.setParameter("loaiDoiTuongID", loaiDoiTuongEnums.getLoaiDoiTuongID());

            return query.getResultList();
        });
    }

    @Override
    public String layDieuKienKhuyenMai(String khuyenMaiID) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT d.dieuKienID FROM DieuKienKhuyenMai d WHERE d.khuyenMai.khuyenMaiID = :kmID", String.class)
                            .setParameter("kmID", khuyenMaiID)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<KhuyenMai> getAllKhuyenMai() {
        return doInTransaction(em ->
                em.createQuery("SELECT km FROM KhuyenMai km ORDER BY km.ngayBatDau DESC", KhuyenMai.class).getResultList()
        );
    }

    @Override
    public String taoMaKhuyenMaiTuDong() {
        return doInTransaction(em -> {
            Long count = em.createQuery("SELECT COUNT(km) FROM KhuyenMai km", Long.class).getSingleResult();
            return String.format("KM%03d", count + 1);
        });
    }

    @Override
    public String taoMaDieuKienTuDong() {
        return doInTransaction(em -> {
            String lastID = em.createQuery("SELECT MAX(dk.dieuKienID) FROM DieuKienKhuyenMai dk", String.class).getSingleResult();
            if (lastID != null) {
                int so = Integer.parseInt(lastID.substring(2)) + 1;
                return String.format("DK%03d", so);
            }
            return "DK001";
        });
    }

    @Override
    public DieuKienKhuyenMai layDieuKienKhuyenMaiTheoKhuyenMai(String khuyenMaiID) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT dk FROM DieuKienKhuyenMai dk WHERE dk.khuyenMai.khuyenMaiID = :id", DieuKienKhuyenMai.class)
                            .setParameter("id", khuyenMaiID)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    // ================= LẤY DANH SÁCH THỰC THỂ CHO COMBOBOX =================

    @Override
    public List<Tuyen> layDanhSachTuyen() {
        return doInTransaction(em -> em.createQuery("SELECT t FROM Tuyen t", Tuyen.class).getResultList());
    }

    @Override
    public List<LoaiTau> layDanhSachLoaiTau() {
        return doInTransaction(em ->
                em.createQuery("SELECT lt FROM LoaiTau lt", LoaiTau.class).getResultList()
        );
    }

    @Override
    public List<HangToa> layDanhSachHangToa() {
        return doInTransaction(em ->
                em.createQuery("SELECT ht FROM HangToa ht", HangToa.class).getResultList()
        );
    }

    @Override
    public List<LoaiDoiTuong> layDanhSachLoaiDoiTuong() {
        return doInTransaction(em ->
                em.createQuery("SELECT ldt FROM LoaiDoiTuong ldt", LoaiDoiTuong.class).getResultList()
        );
    }

    @Override
    public boolean tuDongCapNhatTrangThai() {
        return doInTransaction(em -> {
            int updated = em.createQuery("UPDATE KhuyenMai km SET km.trangThai = false WHERE km.ngayKetThuc < :today")
                    .setParameter("today", LocalDate.now())
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public List<KhuyenMai> getDanhSachKhuyenMaiPhuHop(VeSession veSession) {
        return doInTransaction(em -> {
            String tuyenID = veSession.getVe().getTuyenID();
            String loaiTauID = veSession.getVe().getLoaiTauID();
            String hangToaID = veSession.getVe().getHangToaID();
            String loaiDoiTuongID = veSession.getVe().getKhachHangDTO().getLoaiDoiTuongID();
            String khachHangID = veSession.getVe().getKhachHangDTO().getId();
            double giaVe = veSession.getVe().getGia();
            int ngayTrongTuan = veSession.getVe().getNgayGioDi().getDayOfWeek().getValue();

            String sql = "SELECT km.* FROM KhuyenMai km " +
                    "JOIN DieuKienKhuyenMai dk ON km.khuyenMaiID = dk.khuyenMaiID " +
                    "WHERE km.trangThai = 1 AND km.soLuong > 0 " +
                    "AND CAST(GETDATE() AS DATE) BETWEEN km.ngayBatDau AND km.ngayKetThuc " +
                    "AND (dk.tuyenID IS NULL OR dk.tuyenID = ?1) " +
                    "AND (dk.loaiTauID IS NULL OR dk.loaiTauID = ?2) " +
                    "AND (dk.hangToaID IS NULL OR dk.hangToaID = ?3) " +
                    "AND (dk.loaiDoiTuongID IS NULL OR dk.loaiDoiTuongID = ?4) " +
                    "AND (dk.minGiaTriDonHang IS NULL OR ?5 >= dk.minGiaTriDonHang) " +
                    "AND (dk.ngayTrongTuan IS NULL OR dk.ngayTrongTuan = ?6) " +
                    "AND (km.gioiHanMoiKhachHang = 0 OR " +
                    "  (SELECT COUNT(*) FROM SuDungKhuyenMai sd " +
                    "   JOIN HoaDonChiTiet hdct ON sd.hoaDonChiTietID = hdct.hoaDonChiTietID " +
                    "   JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID " +
                    "   WHERE sd.khuyenMaiID = km.khuyenMaiID " +
                    "   AND hd.khachHangID = ?7) < km.gioiHanMoiKhachHang)";

            var query = em.createNativeQuery(sql, KhuyenMai.class);

            query.setParameter(1, tuyenID);
            query.setParameter(2, loaiTauID);
            query.setParameter(3, hangToaID);
            query.setParameter(4, loaiDoiTuongID);
            query.setParameter(5, giaVe);
            query.setParameter(6, ngayTrongTuan);
            query.setParameter(7, khachHangID);

            return query.getResultList();
        });
    }

    @Override
    public boolean giamSoLuongKhuyenMai(String khuyenMaiID) {
        return doInTransaction(em -> {
            int row = em.createQuery("UPDATE KhuyenMai km SET km.soLuong = km.soLuong - 1 WHERE km.khuyenMaiID = :id AND km.soLuong > 0")
                    .setParameter("id", khuyenMaiID)
                    .executeUpdate();
            return row > 0;
        });
    }

    @Override
    public int demSoLanSuDungCuaKhachHang(String khuyenMaiID, String khachHangID) {
        return doInTransaction(em -> {
            String sql = "SELECT COUNT(sd) FROM SuDungKhuyenMai sd " +
                    "JOIN sd.hoaDonChiTiet hdct " +
                    "JOIN hdct.hoaDon hd " +
                    "WHERE sd.khuyenMai.khuyenMaiID = :kmID AND hd.khachHang.khachHangID = :khID AND sd.trangThai = 'DA_AP_DUNG'";
            Long count = em.createQuery(sql, Long.class)
                    .setParameter("kmID", khuyenMaiID)
                    .setParameter("khID", khachHangID)
                    .getSingleResult();
            return count.intValue();
        });
    }

    @Override
    public Map<String, Integer> getDanhSachKhuyenMaiCanHoan(List<VeDTO> listVe) {
        return doInTransaction(em -> {
            Map<String, Integer> resultMap = new HashMap<>();
            if (listVe == null || listVe.isEmpty()) return resultMap;

            List<String> listVeID = listVe.stream().map(VeDTO::getVeID).toList();

            String jpql = "SELECT sd.khuyenMai.khuyenMaiID, COUNT(sd) FROM SuDungKhuyenMai sd " +
                    "WHERE sd.trangThai = 'DA_AP_DUNG' AND sd.hoaDonChiTiet.ve.veID IN :veIDs " +
                    "GROUP BY sd.khuyenMai.khuyenMaiID";

            List<Object[]> results = em.createQuery(jpql)
                    .setParameter("veIDs", listVeID)
                    .getResultList();

            for (Object[] row : results) {
                resultMap.put((String) row[0], ((Long) row[1]).intValue());
            }
            return resultMap;
        });
    }

    @Override
    public boolean updateSoLuongKhuyenMai(String khuyenMaiID, int soLuongCanCong) {
        return doInTransaction(em -> {
            int updated = em.createQuery("UPDATE KhuyenMai km SET km.soLuong = km.soLuong + :qty WHERE km.khuyenMaiID = :id")
                    .setParameter("qty", soLuongCanCong)
                    .setParameter("id", khuyenMaiID)
                    .executeUpdate();
            return updated > 0;
        });
    }

    @Override
    public KhuyenMai timKiemKhuyenMaiByID(String khuyenMaiID) {
        return findById(khuyenMaiID);
    }
}