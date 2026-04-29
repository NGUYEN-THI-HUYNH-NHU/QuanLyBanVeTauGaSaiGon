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
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chuyen_DAO {
    private ConnectDB connectDB;

    public Chuyen_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public List<Chuyen> getChuyenByGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi) {
        Connection connection = connectDB.getConnection();
        String querySQL = "DECLARE @gaDiID VARCHAR(50) = ?;\r\n" + "DECLARE @gaDenID VARCHAR(50) = ?;\r\n"
                + "DECLARE @ngayDi DATE = ?;\r\n" + "\r\n" + "SELECT\r\n" + "    c.chuyenID,\r\n" + "    c.tuyenID,\r\n"
                + "    c.tauID,\r\n" + "    t.loaiTauID,\r\n" + "    cgDi.ngayDi   AS ngayDi,\r\n"
                + "    cgDi.gioDi    AS gioDi,\r\n" + "    cgDen.ngayDen  AS ngayDen,\r\n"
                + "    cgDen.gioDen  AS gioDen\r\n" + "FROM Chuyen c\r\n" + "INNER JOIN Tau t ON c.tauID = t.tauID\r\n"
                + "INNER JOIN ChuyenGa cgDi\r\n" + "    ON cgDi.chuyenID = c.chuyenID\r\n"
                + "    AND cgDi.gaID = @gaDiID\r\n" + "INNER JOIN ChuyenGa cgDen\r\n"
                + "    ON cgDen.chuyenID = c.chuyenID\r\n" + "    AND cgDen.gaID = @gaDenID\r\n" + "WHERE\r\n"
                + "    cgDi.ngayDi = @ngayDi\r\n" + "    AND cgDi.thuTu < cgDen.thuTu\r\n"
                + "ORDER BY cgDi.gioDi, c.chuyenID;\r\n";

        List<Chuyen> chuyenList = new ArrayList<Chuyen>();

        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, gaDiID);
            pstmt.setString(2, gaDenID);
            pstmt.setDate(3, java.sql.Date.valueOf(ngayDi));

            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    String chuyenID = resultSet.getString("chuyenID");
                    Tuyen tuyen = new Tuyen(resultSet.getString("tuyenID"));
                    String tauID = resultSet.getString("tauID");
                    LoaiTau loaiTau = LoaiTau.builder().id(resultSet.getString("loaiTauID")).build();
                    Tau tau = new Tau(tauID, loaiTau);

                    LocalDate ngayDi_ThucTe = resultSet.getDate("ngayDi").toLocalDate();
                    LocalTime gioDi_ThucTe = resultSet.getTime("gioDi").toLocalTime();
                    LocalDate ngayDen_ThucTe = resultSet.getDate("ngayDen").toLocalDate();
                    LocalTime gioDen_ThucTe = resultSet.getTime("gioDen").toLocalTime();

                    Chuyen c = Chuyen.builder().id(chuyenID).tau(tau).ngayDi(ngayDi_ThucTe).gioDi(gioDi_ThucTe).ngayDen(ngayDen_ThucTe).gioDen(gioDen_ThucTe).build();
                    c.setTuyen(tuyen);

                    chuyenList.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chuyenList;
    }

    public List<Chuyen> getAllChuyen() {
        Connection con = connectDB.getConnection();
        String sql = "SELECT c.*, t.tenTau, t.loaiTauID, "
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                +

                "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                +

                "FROM Chuyen c " + "JOIN Tau t ON c.tauID = t.tauID " + "ORDER BY c.ngayDi DESC, c.gioDi DESC";

        return getListChuyenFromResultSet(con, sql);
    }

    public Chuyen layChuyenTheoMa(String maChuyen) {
        Chuyen chuyen = null;
        Connection con = connectDB.getConnection();

        String sql = "SELECT c.*, t.tenTau, t.loaiTauID ,"
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                + "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                +

                "FROM Chuyen c " + "JOIN Tau t ON c.tauID = t.tauID " + "WHERE c.chuyenID = ?";

        List<Chuyen> list = getListChuyenFromResultSet(con, sql, maChuyen);
        if (!list.isEmpty()) {
            chuyen = list.get(0);
        }
        return chuyen;
    }

    public List<Chuyen> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi) {
        Connection con = connectDB.getConnection();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT c.*, t.tenTau, t.loaiTauID, ");
        sql.append(
                "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, ");
        sql.append(
                "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, ");
        sql.append(
                "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, ");
        sql.append(
                "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc ");
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
            params.add(java.sql.Date.valueOf(ngayDi));
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

        return getListChuyenFromResultSet(con, sql.toString(), params.toArray());
    }

    private List<Chuyen> getListChuyenFromResultSet(Connection con, String sql, Object... params) {
        List<Chuyen> list = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String) {
                    pst.setString(i + 1, (String) params[i]);
                } else if (params[i] instanceof java.sql.Date) {
                    pst.setDate(i + 1, (java.sql.Date) params[i]);
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String maChuyen = rs.getString("chuyenID");
                    String tenTau = rs.getString("tenTau");
                    String loaiTauStr = rs.getString("loaiTauID");
                    String tenGaDi = rs.getString("tenGaDi");
                    String tenGaDen = rs.getString("tenGaDen");
                    String tenChuyen = (tenGaDi != null ? tenGaDi : "N/A") + " - "
                            + (tenGaDen != null ? tenGaDen : "N/A");
                    LoaiTau loaiTau = null;
                    if (loaiTauStr != null) {
                        try {
                            loaiTau = LoaiTau.builder().id(loaiTauStr).build();
                        } catch (IllegalArgumentException e) {

                        }
                    }

                    Date sqlDateDi = rs.getDate("ngayDi");
                    Time sqlTimeDi = rs.getTime("gioDi");
                    LocalDate dateDi = (sqlDateDi != null) ? sqlDateDi.toLocalDate() : null;
                    LocalTime timeDi = (sqlTimeDi != null) ? sqlTimeDi.toLocalTime() : null;

                    Date sqlDateDen = rs.getDate("NgayDenThuc");
                    Time sqlTimeDen = rs.getTime("GioDenThuc");
                    LocalDate dateDen = (sqlDateDen != null) ? sqlDateDen.toLocalDate() : null;
                    LocalTime timeDen = (sqlTimeDen != null) ? sqlTimeDen.toLocalTime() : null;

                    Chuyen c = new Chuyen(maChuyen);
                    c.setTenChuyenHienThi(tenChuyen);
                    c.setTenGaDiHienThi(tenGaDi);
                    c.setTenGaDenHienThi(tenGaDen);
                    Tau tau = new Tau(rs.getString("tauID"), tenTau);
                    tau.setLoaiTau(loaiTau);
                    c.setTau(tau);
                    if (rs.getString("tuyenID") != null) {
                        c.setTuyen(new Tuyen(rs.getString("tuyenID")));
                    }

                    c.setNgayDi(dateDi);
                    c.setGioDi(timeDi);
                    c.setNgayDen(dateDen);
                    c.setGioDen(timeDen);

                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllMaChuyenID() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT chuyenID FROM Chuyen";
        try (Statement stmt = connectDB.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("chuyenID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllTenGa() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT tenGa FROM Ga";
        try (Statement stmt = connectDB.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("tenGa"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllTenTau() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT tenTau FROM Tau";
        try (Statement stmt = connectDB.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("tenTau"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllTuyenID() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT tuyenID FROM Tuyen";
        try (Statement stmt = connectDB.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("tuyenID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllTauID() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT tauID FROM Tau";
        try (Statement stmt = connectDB.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("tauID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean themChuyenMoi(Chuyen chuyen, List<ChuyenGa> lichTrinh) {
        Connection con = null;
        PreparedStatement pstChuyen = null;
        PreparedStatement pstChuyenGa = null;
        boolean success = false;

        try {
            con = ConnectDB.getInstance().getConnection();
            con.setAutoCommit(false);

            String sqlChuyen = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?, ?, ?, ?, ?)";
            pstChuyen = con.prepareStatement(sqlChuyen);
            pstChuyen.setString(1, chuyen.getId());
            pstChuyen.setString(2, chuyen.getTuyen().getId());
            pstChuyen.setString(3, chuyen.getTau().getId());
            pstChuyen.setDate(4, java.sql.Date.valueOf(chuyen.getNgayDi()));
            pstChuyen.setTime(5, java.sql.Time.valueOf(chuyen.getGioDi()));

            pstChuyen.executeUpdate();

            String sqlChuyenGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstChuyenGa = con.prepareStatement(sqlChuyenGa);

            for (ChuyenGa cg : lichTrinh) {
                pstChuyenGa.setString(1, chuyen.getId());
                pstChuyenGa.setString(2, cg.getGa().getGaID());
                pstChuyenGa.setInt(3, cg.getThuTu());

                pstChuyenGa.setTime(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null);
                pstChuyenGa.setTime(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null);
                pstChuyenGa.setDate(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null);
                pstChuyenGa.setDate(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null);

                pstChuyenGa.addBatch();
            }
            pstChuyenGa.executeBatch();
            con.commit();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstChuyen != null) {
                    pstChuyen.close();
                }
                if (pstChuyenGa != null) {
                    pstChuyenGa.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    public Map<String, String> getMapTenGaToID() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT gaID, tenGa FROM Ga";
        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("tenGa"), rs.getString("gaID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public boolean capNhatChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinhMoi) {
        Connection con = null;
        PreparedStatement pstUpdateChuyen = null;
        PreparedStatement pstDeleteChuyenGa = null;
        PreparedStatement pstInsertChuyenGa = null;
        boolean success = false;

        try {
            con = connectDB.getConnection();
            con.setAutoCommit(false);
            String sqlUpdateChuyen = "UPDATE Chuyen SET tuyenID=?, tauID=?, ngayDi=?, gioDi=? WHERE chuyenID=?";
            pstUpdateChuyen = con.prepareStatement(sqlUpdateChuyen);
            pstUpdateChuyen.setString(1, chuyen.getTuyen().getId());
            pstUpdateChuyen.setString(2, chuyen.getTau().getId());
            pstUpdateChuyen.setDate(3, java.sql.Date.valueOf(chuyen.getNgayDi()));
            pstUpdateChuyen.setTime(4, java.sql.Time.valueOf(chuyen.getGioDi()));
            pstUpdateChuyen.setString(5, chuyen.getId());

            int rowsAff = pstUpdateChuyen.executeUpdate();
            if (rowsAff == 0) {
                throw new SQLException("Không tìm thấy chuyến để cập nhật");
            }

            String sqlDeleteGa = "DELETE FROM ChuyenGa WHERE chuyenID=?";
            pstDeleteChuyenGa = con.prepareStatement(sqlDeleteGa);
            pstDeleteChuyenGa.setString(1, chuyen.getId());
            pstDeleteChuyenGa.executeUpdate();

            String sqlInsertGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstInsertChuyenGa = con.prepareStatement(sqlInsertGa);

            for (ChuyenGa cg : lichTrinhMoi) {
                pstInsertChuyenGa.setString(1, chuyen.getId());
                pstInsertChuyenGa.setString(2, cg.getGa().getGaID());
                pstInsertChuyenGa.setInt(3, cg.getThuTu());

                pstInsertChuyenGa.setDate(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null);
                pstInsertChuyenGa.setTime(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null);
                pstInsertChuyenGa.setDate(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null);
                pstInsertChuyenGa.setTime(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null);

                pstInsertChuyenGa.addBatch();
            }
            pstInsertChuyenGa.executeBatch();

            con.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstUpdateChuyen != null) {
                    pstUpdateChuyen.close();
                }
                if (pstDeleteChuyenGa != null) {
                    pstDeleteChuyenGa.close();
                }
                if (pstInsertChuyenGa != null) {
                    pstInsertChuyenGa.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    public List<Ga> getDsGaTheoTuyen(String tuyenID) {
        List<Ga> list = new ArrayList<>();
        String sql = "SELECT g.gaID, g.tenGa FROM TuyenChiTiet ct " + "JOIN Ga g ON ct.gaID = g.gaID "
                + "WHERE ct.tuyenID = ? " + "ORDER BY ct.thuTu ASC";
        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tuyenID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Ga(rs.getString("gaID"), rs.getString("tenGa")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsById(String chuyenID) {
        String sql = "SELECT 1 FROM Chuyen WHERE chuyenID = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, chuyenID);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTocDoTau(String tauID) {
        String sql = "SELECT loaiTauID FROM Tau WHERE tauID = ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tauID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String loaiTau = rs.getString("loaiTauID");
                    if ("TAU_NHANH".equalsIgnoreCase(loaiTau)) {
                        return 60;
                    } else if ("TAU_DU_LICH".equalsIgnoreCase(loaiTau)) {
                        return 40;
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 40;
    }

    public List<String[]> getTauHoatDong() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT tauID, loaiTauID FROM Tau WHERE trangThai = N'HOAT_DONG'";
        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("tauID"), rs.getString("loaiTauID")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Ga> getDsGaVaTrangThaiLonTheoTuyen(String tuyenID) {
        List<Ga> list = new ArrayList<>();
        String sql = "SELECT g.gaID, g.tenGa, g.isGaLon FROM TuyenChiTiet ct " + "JOIN Ga g ON ct.gaID = g.gaID "
                + "WHERE ct.tuyenID = ? " + "ORDER BY ct.thuTu ASC";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tuyenID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ga g = new Ga(rs.getString("gaID"), rs.getString("tenGa"));
                    g.setGaLon(rs.getBoolean("isGaLon"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean themChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh) {
        Connection con = null;
        try {
            con = ConnectDB.getInstance().getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            String sqlChuyen = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?, ?, ?, ?, ?)";
            String sqlChuyenGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstC = con.prepareStatement(sqlChuyen);
                 PreparedStatement pstCG = con.prepareStatement(sqlChuyenGa)) {

                for (int i = 0; i < dsChuyen.size(); i++) {
                    Chuyen c = dsChuyen.get(i);
                    pstC.setString(1, c.getId());
                    pstC.setString(2, c.getTuyen().getId());
                    pstC.setString(3, c.getTau().getId());
                    pstC.setDate(4, java.sql.Date.valueOf(c.getNgayDi()));
                    pstC.setTime(5, java.sql.Time.valueOf(c.getGioDi()));
                    pstC.addBatch();

                    for (ChuyenGa cg : dsLichTrinh.get(i)) {
                        pstCG.setString(1, c.getId());
                        pstCG.setString(2, cg.getGa().getGaID());
                        pstCG.setInt(3, cg.getThuTu());
                        pstCG.setDate(4, cg.getNgayDen() != null ? Date.valueOf(cg.getNgayDen()) : null);
                        pstCG.setTime(5, cg.getGioDen() != null ? Time.valueOf(cg.getGioDen()) : null);
                        pstCG.setDate(6, cg.getNgayDi() != null ? Date.valueOf(cg.getNgayDi()) : null);
                        pstCG.setTime(7, cg.getGioDi() != null ? Time.valueOf(cg.getGioDi()) : null);
                        pstCG.addBatch();
                    }
                }
                pstC.executeBatch();
                pstCG.executeBatch();
                con.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (Exception ex) {
            }
        }
    }

    public boolean capNhatChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh) {
        Connection con = null;
        try {
            con = connectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa các chuyến cũ để chuẩn bị nạp bản mới (Dựa trên ChuyenID đã có hậu tố
            // _CK)
            String sqlDeleteCG = "DELETE FROM ChuyenGa WHERE chuyenID = ?";
            String sqlDeleteC = "DELETE FROM Chuyen WHERE chuyenID = ?";

            try (PreparedStatement pstDelCG = con.prepareStatement(sqlDeleteCG);
                 PreparedStatement pstDelC = con.prepareStatement(sqlDeleteC)) {

                for (Chuyen c : dsChuyen) {
                    pstDelCG.setString(1, c.getId());
                    pstDelC.setString(1, c.getId());
                    pstDelCG.addBatch();
                    pstDelC.addBatch();
                }
                pstDelCG.executeBatch();
                pstDelC.executeBatch();
            }

            // 2. Tận dụng hàm themChuyenBatch đã có để chèn dữ liệu mới
            // Lưu ý: Phải truyền connection đang xử lý Transaction vào nếu muốn tối ưu,
            // hoặc gọi trực tiếp logic insert ở đây.

            String sqlInsertC = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?, ?, ?, ?, ?)";
            String sqlInsertCG = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstInsC = con.prepareStatement(sqlInsertC);
                 PreparedStatement pstInsCG = con.prepareStatement(sqlInsertCG)) {

                for (int i = 0; i < dsChuyen.size(); i++) {
                    Chuyen c = dsChuyen.get(i);
                    pstInsC.setString(1, c.getId());
                    pstInsC.setString(2, c.getTuyen().getId());
                    pstInsC.setString(3, c.getTau().getId());
                    pstInsC.setDate(4, java.sql.Date.valueOf(c.getNgayDi()));
                    pstInsC.setTime(5, java.sql.Time.valueOf(c.getGioDi()));
                    pstInsC.addBatch();

                    for (ChuyenGa cg : dsLichTrinh.get(i)) {
                        pstInsCG.setString(1, c.getId());
                        pstInsCG.setString(2, cg.getGa().getGaID());
                        pstInsCG.setInt(3, cg.getThuTu());
                        pstInsCG.setDate(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null);
                        pstInsCG.setTime(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null);
                        pstInsCG.setDate(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null);
                        pstInsCG.setTime(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null);
                        pstInsCG.addBatch();
                    }
                }
                pstInsC.executeBatch();
                pstInsCG.executeBatch();
            }

            con.commit(); // Hoàn tất thành công
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Chuyen> getChuyenTheoNgay(LocalDate ngay) {
        List<Chuyen> list = new ArrayList<>();
        Connection con = connectDB.getInstance().getConnection();
        String sql = "SELECT c.*, t.tenTau, t.loaiTauID, "
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu ASC) AS tenGaDi, "
                + "(SELECT TOP 1 g.tenGa FROM ChuyenGa cg JOIN Ga g ON cg.gaID = g.gaID WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS tenGaDen, "
                + "(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, "
                + "(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc "
                + "FROM Chuyen c " + "JOIN Tau t ON c.tauID = t.tauID " + "WHERE c.ngayDi = ? "
                + "ORDER BY c.gioDi ASC";

        return getListChuyenFromResultSet(con, sql, java.sql.Date.valueOf(ngay));
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
    public int[] getThongKeCho(String chuyenID, String gaDiID, String gaDenID) {
        Connection connection = connectDB.getConnection();
        int[] result = new int[]{0, 0};

        // 1. Lấy thứ tự (thuTu) của Ga Đi và Ga Đến khách chọn trong bảng ChuyenGa.
        // 2. Đếm tổng số ghế của tàu (TongSoGhe).
        // 3. Đếm số vé đã bán (SoGheDaDat) thõa mãn điều kiện trùng lặp chặng:
        // - Vé bắt đầu TRƯỚC KHI khách xuống (@orderDen)
        // - VÀ Vé kết thúc SAU KHI khách lên (@orderDi)
        String sql = "DECLARE @chuyenID VARCHAR(50) = ?;\r\n" + "DECLARE @gaDiID VARCHAR(50) = ?;\r\n"
                + "DECLARE @gaDenID VARCHAR(50) = ?;\r\n" + "\r\n" + "-- 1. Lấy thứ tự ga của hành trình khách chọn\r\n"
                + "DECLARE @orderDi INT = (SELECT thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDiID);\r\n"
                + "DECLARE @orderDen INT = (SELECT thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDenID);\r\n"
                + "\r\n" + "SELECT \r\n" + "    -- 2. Tính tổng số ghế của đoàn tàu\r\n"
                + "    (SELECT COUNT(g.gheID) \r\n" + "     FROM Chuyen c \r\n"
                + "     JOIN Tau t ON c.tauID = t.tauID \r\n" + "     JOIN Toa toa ON t.tauID = toa.tauID \r\n"
                + "     JOIN Ghe g ON toa.toaID = g.toaID \r\n" + "     WHERE c.chuyenID = @chuyenID) AS TongSoGhe,\r\n"
                + "\r\n" + "    -- 3. Tính số ghế bị chiếm (Vé hoặc Giữ chỗ trùng chặng)\r\n"
                + "    (SELECT COUNT(DISTINCT v.gheID) \r\n" + "     FROM Ve v\r\n"
                + "     JOIN ChuyenGa cg_ve_di ON v.gaDiID = cg_ve_di.gaID AND cg_ve_di.chuyenID = v.chuyenID\r\n"
                + "     JOIN ChuyenGa cg_ve_den ON v.gaDenID = cg_ve_den.gaID AND cg_ve_den.chuyenID = v.chuyenID\r\n"
                + "     WHERE v.chuyenID = @chuyenID \r\n" + "       -- Logic trùng lặp chặng: \r\n"
                + "       -- Vé bắt đầu < Khách đến (cg_ve_di.thuTu < @orderDen)\r\n"
                + "       -- VÀ Vé kết thúc > Khách đi (cg_ve_den.thuTu > @orderDi)\r\n"
                + "       AND cg_ve_di.thuTu < @orderDen \r\n" + "       AND cg_ve_den.thuTu > @orderDi\r\n"
                + "    ) AS SoGheDaDat";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chuyenID);
            pstmt.setString(2, gaDiID);
            pstmt.setString(3, gaDenID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int tongSoGhe = rs.getInt("TongSoGhe");
                    int soGheDaDat = rs.getInt("SoGheDaDat");
                    int soGheTrong = tongSoGhe - soGheDaDat;

                    if (soGheTrong < 0) {
                        soGheTrong = 0;
                    }

                    result[0] = 0;
                    result[1] = soGheTrong;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}