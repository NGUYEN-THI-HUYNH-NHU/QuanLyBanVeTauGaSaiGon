package dao.impl;
/*
 * @(#) Chuyen_DAO.java  1.0  [12:59:19 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import connectDB.ConnectDB;
import dao.IChuyenDAO;
import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chuyen_DAO extends AbstractGenericDAO<Chuyen, String> implements IChuyenDAO {

    public Chuyen_DAO() {
        super(Chuyen.class);
    }

    @Override
    public List<Chuyen> getChuyenByGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi) {
        return doInTransaction(em -> {
            String querySQL = "DECLARE @gaDiID VARCHAR(50) = :gaDiID;\n"
                    + "DECLARE @gaDenID VARCHAR(50) = :gaDenID;\n"
                    + "DECLARE @ngayDi DATE = :ngayDi;\n\n"
                    + "SELECT\n"
                    + "    c.chuyenID,\n"
                    + "    c.tuyenID,\n"
                    + "    c.tauID,\n"
                    + "    t.loaiTauID,\r\n"
                    + "    cgDi.ngayDi   AS ngayDi,\n"
                    + "    cgDi.gioDi    AS gioDi,\n"
                    + "    cgDen.ngayDen  AS ngayDen,\n"
                    + "    cgDen.gioDen  AS gioDen\n"
                    + "FROM Chuyen c\n"
                    + "INNER JOIN Tau t ON c.tauID = t.tauID\n"
                    + "INNER JOIN ChuyenGa cgDi\n"
                    + "    ON cgDi.chuyenID = c.chuyenID\n"
                    + "    AND cgDi.gaID = @gaDiID\n"
                    + "INNER JOIN ChuyenGa cgDen\n"
                    + "    ON cgDen.chuyenID = c.chuyenID\n"
                    + "    AND cgDen.gaID = @gaDenID\n"
                    + "WHERE\n"
                    + "    cgDi.ngayDi = @ngayDi\n"
                    + "    AND cgDi.thuTu < cgDen.thuTu\n"
                    + "ORDER BY cgDi.gioDi, c.chuyenID;\n";

            Query query = em.createNativeQuery(querySQL);
            query.setParameter("gaDiID", gaDiID);
            query.setParameter("gaDenID", gaDenID);
            query.setParameter("ngayDi", ngayDi);

            List<Object[]> results = query.getResultList();
            List<Chuyen> chuyenList = new ArrayList<>();

            for (Object[] row : results) {
                String chuyenID = (String) row[0];
                String tuyenID = (String) row[1];
                String tauID = (String) row[2];
                String loaiTauID = (String) row[3];

                LocalDate ngayDi_ThucTe = ((java.sql.Date) row[4]).toLocalDate();
                LocalTime gioDi_ThucTe = ((java.sql.Time) row[5]).toLocalTime();
                LocalDate ngayDen_ThucTe = ((java.sql.Date) row[6]).toLocalDate();
                LocalTime gioDen_ThucTe = ((java.sql.Time) row[7]).toLocalTime();

                Tuyen tuyen = new Tuyen(tuyenID);
                LoaiTau loaiTau = new LoaiTau(loaiTauID);
                Tau tau = new Tau(tauID, loaiTau);

                Chuyen c = new Chuyen(chuyenID, tau, ngayDi_ThucTe, gioDi_ThucTe, ngayDen_ThucTe, gioDen_ThucTe);
                c.setTuyen(tuyen);

                chuyenList.add(c);
            }

            return chuyenList;
        });
    }

    @Override
    public List<Chuyen> getAllChuyen() {
        return doInTransaction(em -> {
            String sql = "SELECT c.*, t.tenTau, t.loaiTauID, "
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                    +

                    "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                    + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                    +

                    "FROM Chuyen c " + "JOIN Tau t ON c.tauID = t.tauID " + "ORDER BY c.ngayDi DESC, c.gioDi DESC";

            return getListChuyenFromResultSet(em, sql);
        });
    }

    @Override
    public Chuyen layChuyenTheoMa(String maChuyen) {
        return doInTransaction(em -> {
            String sql = "SELECT c.*, t.tenTau, t.loaiTauID ,"
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                    + "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                    + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                    + "FROM Chuyen c "
                    + "JOIN Tau t ON c.tauID = t.tauID "
                    + "WHERE c.chuyenID = ?1";

            List<Chuyen> list = getListChuyenFromResultSet(em, sql, maChuyen);
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public List<Chuyen> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi) {
        return doInTransaction(em -> {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT c.*, t.tenTau, t.loaiTauID, ");
            sql.append("(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, ");
            sql.append("(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, ");
            sql.append("(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, ");
            sql.append("(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc ");
            sql.append("FROM Chuyen c ");
            sql.append("JOIN Tau t ON c.tauID = t.tauID ");

            if (!gaDi.isEmpty()) {
                sql.append(" JOIN ChuyenGa cgStart ON c.chuyenID = cgStart.chuyenID ");
                sql.append(" JOIN Ga gStart ON cgStart.gaID = gStart.gaID ");
            }
            if (!gaDen.isEmpty()) {
                sql.append(" JOIN ChuyenGa cgEnd ON c.chuyenID = cgEnd.chuyenID ");
                sql.append(" JOIN Ga gEnd ON cgEnd.gaID = gEnd.gaID ");
            }

            sql.append("WHERE 1=1 ");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            if (!maChuyen.isEmpty()) {
                sql.append(" AND c.chuyenID LIKE ?");
                params.add("%" + maChuyen + "%");
            }
            if (!tenTau.isEmpty()) {
                sql.append(" AND t.tenTau LIKE ?");
                params.add("%" + tenTau + "%");
            }
            if (ngayDi != null) {
                sql.append(" AND c.ngayDi = ?");
                params.add(Date.valueOf(ngayDi));
            }
            if (!gaDi.isEmpty()) {
                sql.append(" AND gStart.tenGa LIKE ?");
                params.add("%" + gaDi + "%");
            }
            if (!gaDen.isEmpty()) {
                sql.append(" AND gEnd.tenGa LIKE ?");
                params.add("%" + gaDen + "%");
            }
            if (!gaDi.isEmpty() && !gaDen.isEmpty()) {
                sql.append(" AND cgStart.thuTu < cgEnd.thuTu ");
            }
            sql.append(" ORDER BY c.chuyenID");

            return getListChuyenFromResultSet(em, sql.toString(), params.toArray());
        });
    }

    @Override
    public List<String> getAllMaChuyenID() {
        return doInTransaction(em -> em.createNativeQuery("SELECT chuyenID FROM Chuyen").getResultList());
    }

    @Override
    public List<String> getAllTenGa() {
        return doInTransaction(em -> em.createNativeQuery("SELECT tenGa FROM Ga").getResultList());
    }

    @Override
    public List<String> getAllTenTau() {
        return doInTransaction(em -> em.createNativeQuery("SELECT tenTau FROM Tau").getResultList());
    }

    @Override
    public List<String> getAllTuyenID() {
        return doInTransaction(em -> em.createNativeQuery("SELECT tuyenID FROM Tuyen").getResultList());
    }

    @Override
    public List<String> getAllTauID() {
        return doInTransaction(em -> em.createNativeQuery("SELECT tauID FROM Tau").getResultList());
    }

    @Override
    public boolean themChuyenMoi(Chuyen chuyen, List<ChuyenGa> lichTrinh) {
        try{
            doInTransaction(em -> {
                String sqlChuyen = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5)";
                em.createNativeQuery(sqlChuyen)
                        .setParameter(1, chuyen.getChuyenID())
                        .setParameter(2, chuyen.getTuyen().getTuyenID())
                        .setParameter(3, chuyen.getTau().getTauID())
                        .setParameter(4, Date.valueOf(chuyen.getNgayDi()))
                        .setParameter(5, Time.valueOf(chuyen.getGioDi()))
                        .executeUpdate();

                String sqlChuyenGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)";
                for (ChuyenGa cg : lichTrinh) {
                    em.createNativeQuery(sqlChuyenGa)
                            .setParameter(1, chuyen.getChuyenID())
                            .setParameter(2, cg.getGa().getGaID())
                            .setParameter(3, cg.getThuTu())
                            .setParameter(4, cg.getNgayDen() != null ? Date.valueOf(cg.getNgayDen()) : null)
                            .setParameter(5, cg.getGioDen() != null ? Time.valueOf(cg.getGioDen()) : null)
                            .setParameter(6, cg.getNgayDi() != null ? Date.valueOf(cg.getNgayDi()) : null)
                            .setParameter(7, cg.getGioDi() != null ? Time.valueOf(cg.getGioDi()) : null)
                            .executeUpdate();
                }
                return true;
            });
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, String> getMapTenGaToID() {
        return doInTransaction(em -> {
            Map<String, String> map = new HashMap<>();
            List<Object[]> results = em.createNativeQuery("SELECT gaID, tenGa FROM Ga").getResultList();
            for(Object[] row : results) {
                map.put((String) row[1], (String) row[0]);
            }
            return map;
        });
    }

    @Override
    public boolean capNhatChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinhMoi) {
        try{
            doInTransaction(em -> {
                String sqlUpdateChuyen = "UPDATE Chuyen SET tuyenID=?1, tauID=?2, ngayDi=?3, gioDi=?4 WHERE chuyenID=?5";
                int rows = em.createNativeQuery(sqlUpdateChuyen)
                        .setParameter(1, chuyen.getTuyen().getTuyenID())
                        .setParameter(2, chuyen.getTau().getTauID())
                        .setParameter(3, Date.valueOf(chuyen.getNgayDi()))
                        .setParameter(4, Time.valueOf(chuyen.getGioDi()))
                        .setParameter(5, chuyen.getChuyenID())
                        .executeUpdate();

                if(rows == 0) {
                    throw new RuntimeException("Không tìm thấy chuyến để cập nhật");
                }

                em.createNativeQuery("DELETE FROM ChuyenGa WHERE chuyenID=?1")
                        .setParameter(1, chuyen.getChuyenID())
                        .executeUpdate();

                String sqlInsertGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)";
                for (ChuyenGa cg : lichTrinhMoi) {
                    em.createNativeQuery(sqlInsertGa)
                            .setParameter(1, chuyen.getChuyenID())
                            .setParameter(2, cg.getGa().getGaID())
                            .setParameter(3, cg.getThuTu())
                            .setParameter(4, cg.getNgayDen() != null ? Date.valueOf(cg.getNgayDen()) : null)
                            .setParameter(5, cg.getGioDen() != null ? Time.valueOf(cg.getGioDen()) : null)
                            .setParameter(6, cg.getNgayDi() != null ? Date.valueOf(cg.getNgayDi()) : null)
                            .setParameter(7, cg.getGioDi() != null ? Time.valueOf(cg.getGioDi()) : null)
                            .executeUpdate();
                }
                return true;
            });
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Ga> getDsGaTheoTuyen(String tuyenID) {
        return doInTransaction(em -> {
            String sql = "SELECT g.gaID, g.tenGa FROM TuyenChiTiet ct " + "JOIN Ga g ON ct.gaID = g.gaID "
                    + "WHERE ct.tuyenID = ? " + "ORDER BY ct.thuTu ASC";
            List<Object[]> results = em.createNativeQuery(sql)
                    .setParameter(1, tuyenID)
                    .getResultList();
            List<Ga> list = new ArrayList<>();
            for (Object[] row : results) {
                list.add(new Ga((String) row[0], (String) row[1]));
            }
            return list;
        });
    }

    @Override
    public boolean existsById(String chuyenID) {
        return doInTransaction(em -> {
            String sql = "SELECT 1 FROM Chuyen WHERE chuyenID = ?1";
            List<?> results = em.createNativeQuery(sql).setParameter(1, chuyenID).getResultList();
            return !results.isEmpty();
        });
    }

    @Override
    public int getTocDoTau(String tauID) {
        return doInTransaction(em -> {
            String sql = "SELECT loaiTauID FROM Tau WHERE tauID = ?1";
            List<String> results = em.createNativeQuery(sql).setParameter(1, tauID).getResultList();
            if (!results.isEmpty()) {
                String loaiTau = results.get(0);
                if ("TAU_NHANH".equalsIgnoreCase(loaiTau)) return 60;
                else if ("TAU_DU_LICH".equalsIgnoreCase(loaiTau)) return 40;
            }
            return 40;
        });
    }

    @Override
    public List<String[]> getTauHoatDong() {
        return doInTransaction(em -> {
            String sql = "SELECT tauID, loaiTauID FROM Tau WHERE trangThai = N'HOAT_DONG'";
            List<Object[]> results = em.createNativeQuery(sql).getResultList();
            List<String[]> list = new ArrayList<>();
            for (Object[] row : results) {
                list.add(new String[]{(String) row[0], (String) row[1]});
            }
            return list;
        });
    }

    @Override
    public List<Ga> getDsGaVaTrangThaiLonTheoTuyen(String tuyenID) {
        return doInTransaction(em -> {
            String sql = "SELECT g.gaID, g.tenGa, g.isGaLon FROM TuyenChiTiet ct JOIN Ga g ON ct.gaID = g.gaID WHERE ct.tuyenID = ?1 ORDER BY ct.thuTu ASC";
            List<Object[]> results = em.createNativeQuery(sql).setParameter(1, tuyenID).getResultList();
            List<Ga> list = new ArrayList<>();
            for (Object[] row : results) {
                Ga g = new Ga((String) row[0], (String) row[1]);
                g.setGaLon((Boolean) row[2]);
                list.add(g);
            }
            return list;
        });
    }

    @Override
    public boolean themChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh) {
        try {
            doInTransaction(em -> {
                String sqlChuyen = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5)";
                String sqlChuyenGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)";

                for (int i = 0; i < dsChuyen.size(); i++) {
                    Chuyen c = dsChuyen.get(i);
                    em.createNativeQuery(sqlChuyen)
                            .setParameter(1, c.getChuyenID())
                            .setParameter(2, c.getTuyen().getTuyenID())
                            .setParameter(3, c.getTau().getTauID())
                            .setParameter(4, java.sql.Date.valueOf(c.getNgayDi()))
                            .setParameter(5, java.sql.Time.valueOf(c.getGioDi()))
                            .executeUpdate();

                    for (ChuyenGa cg : dsLichTrinh.get(i)) {
                        em.createNativeQuery(sqlChuyenGa)
                                .setParameter(1, c.getChuyenID())
                                .setParameter(2, cg.getGa().getGaID())
                                .setParameter(3, cg.getThuTu())
                                .setParameter(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null)
                                .setParameter(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null)
                                .setParameter(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null)
                                .setParameter(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null)
                                .executeUpdate();
                    }
                }
                return true;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean capNhatChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh) {
        try {
            doInTransaction(em -> {
                for (Chuyen c : dsChuyen) {
                    em.createNativeQuery("DELETE FROM ChuyenGa WHERE chuyenID = ?1")
                            .setParameter(1, c.getChuyenID()).executeUpdate();
                    em.createNativeQuery("DELETE FROM Chuyen WHERE chuyenID = ?1")
                            .setParameter(1, c.getChuyenID()).executeUpdate();
                }

                String sqlInsC = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5)";
                String sqlInsCG = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)";

                for (int i = 0; i < dsChuyen.size(); i++) {
                    Chuyen c = dsChuyen.get(i);
                    em.createNativeQuery(sqlInsC)
                            .setParameter(1, c.getChuyenID())
                            .setParameter(2, c.getTuyen().getTuyenID())
                            .setParameter(3, c.getTau().getTauID())
                            .setParameter(4, java.sql.Date.valueOf(c.getNgayDi()))
                            .setParameter(5, java.sql.Time.valueOf(c.getGioDi()))
                            .executeUpdate();

                    for (ChuyenGa cg : dsLichTrinh.get(i)) {
                        em.createNativeQuery(sqlInsCG)
                                .setParameter(1, c.getChuyenID())
                                .setParameter(2, cg.getGa().getGaID())
                                .setParameter(3, cg.getThuTu())
                                .setParameter(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null)
                                .setParameter(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null)
                                .setParameter(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null)
                                .setParameter(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null)
                                .executeUpdate();
                    }
                }
                return true;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Chuyen> getChuyenTheoNgay(LocalDate ngay) {
        return doInTransaction(em -> {
            String sql = "SELECT c.*, t.tenTau, t.loaiTauID, "
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                    + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                    + "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                    + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                    + "FROM Chuyen c JOIN Tau t ON c.tauID = t.tauID WHERE c.ngayDi = ?1 ORDER BY c.gioDi ASC";

            return getListChuyenFromResultSet(em, sql, java.sql.Date.valueOf(ngay));
        });
    }

    /**
     * Lấy thống kê số chỗ đã đặt và số chỗ trống của một chuyến tàu trên một chặng
     * cụ thể. Logic: Kiểm tra sự trùng lặp chặng dựa trên thứ tự ga (thuTu) trong
     * bảng ChuyenGa. * @param chuyenID Mã chuyến tàu
     *
     * @param gaDiID
     * @param gaDenID
     * @return int[] mảng 2 phần tử: [0] = Số chỗ đã đặt, [1] = Số chỗ trống
     */
    @Override
    public int[] getThongKeCho(String chuyenID, String gaDiID, String gaDenID) {
        return doInTransaction(em -> {
            String sql = "DECLARE @chuyenID VARCHAR(50) = :chuyenID;\n"
                    + "DECLARE @gaDiID VARCHAR(50) = :gaDiID;\n"
                    + "DECLARE @gaDenID VARCHAR(50) = :gaDenID;\n\n"
                    + "DECLARE @orderDi INT = (SELECT thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDiID);\n"
                    + "DECLARE @orderDen INT = (SELECT thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDenID);\n\n"
                    + "SELECT "
                    + "    (SELECT COUNT(g.gheID) FROM Chuyen c JOIN Tau t ON c.tauID = t.tauID "
                    + "     JOIN Toa toa ON t.tauID = toa.tauID JOIN Ghe g ON toa.toaID = g.toaID "
                    + "     WHERE c.chuyenID = @chuyenID) AS TongSoGhe,\n"
                    + "    (SELECT COUNT(DISTINCT v.gheID) FROM Ve v "
                    + "     JOIN ChuyenGa cg_ve_di ON v.gaDiID = cg_ve_di.gaID AND cg_ve_di.chuyenID = v.chuyenID "
                    + "     JOIN ChuyenGa cg_ve_den ON v.gaDenID = cg_ve_den.gaID AND cg_ve_den.chuyenID = v.chuyenID "
                    + "     WHERE v.chuyenID = @chuyenID AND cg_ve_di.thuTu < @orderDen AND cg_ve_den.thuTu > @orderDi "
                    + "    ) AS SoGheDaDat";

            Query query = em.createNativeQuery(sql);
            query.setParameter("chuyenID", chuyenID);
            query.setParameter("gaDiID", gaDiID);
            query.setParameter("gaDenID", gaDenID);

            List<Object[]> results = query.getResultList();
            int[] result = new int[]{0, 0};

            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                int tongSoGhe = ((Number) row[0]).intValue();
                int soGheDaDat = ((Number) row[1]).intValue();
                int soGheTrong = Math.max(0, tongSoGhe - soGheDaDat);

                result[0] = 0;
                result[1] = soGheTrong;
            }
            return result;
        });
    }
}