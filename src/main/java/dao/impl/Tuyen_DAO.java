package dao.impl;

import connectDB.ConnectDB;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tuyen_DAO extends AbstractGenericDAO<Tuyen, String> {
    private ConnectDB connectDB = ConnectDB.getInstance();
    private Ga_DAO ga_dao = new Ga_DAO();

    public Tuyen_DAO() {
        super(Tuyen.class);
    }

    public List<Tuyen> getAllTuyen() {
        List<Tuyen> danhSachTuyen = new ArrayList<>();
        String sql = "SELECT tuyenID, moTa, trangThai FROM Tuyen";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pstm = con.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                Tuyen tuyen = new Tuyen(rs.getString("tuyenID"), rs.getString("moTa"), rs.getBoolean("trangThai"));
                danhSachTuyen.add(tuyen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachTuyen;
    }

    public List<Tuyen> getTuyenByID(String tuyenIDTim) {
        List<Tuyen> danhSachTuyen = new ArrayList<>();
        String sql = "SELECT * FROM Tuyen WHERE LOWER(tuyenID) LIKE ?";

        try (Connection con = connectDB.getConnection(); PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, "%" + tuyenIDTim.toLowerCase() + "%");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Tuyen tuyen = new Tuyen(rs.getString("tuyenID"), rs.getString("moTa"), rs.getBoolean("trangThai"));
                danhSachTuyen.add(tuyen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachTuyen;
    }

    public List<Tuyen> getTuyenTheoGa(String gaDi, String gaDen) {
        Map<String, Tuyen> tuyepMap = new LinkedHashMap<>();
        boolean hasBothGa = (gaDi != null && !gaDi.trim().isEmpty()) && (gaDen != null && !gaDen.trim().isEmpty());
        String sql;
        int expectedParamCount; // Biến theo dõi số lượng tham số

        if (hasBothGa) {
            // SQL_FULL_SEARCH: Tìm tuyến có GaDi trước GaDen (3 tham số)
            sql = "SELECT DISTINCT t.tuyenID, t.moTa, t.trangThai " +
                    "FROM Tuyen t " +
                    "WHERE t.tuyenID IN ( "
                    + "    SELECT t1.tuyenID FROM TuyenChiTiet t1 JOIN Ga g1 ON t1.gaID = g1.gaID WHERE LOWER(g1.tenGa) LIKE LOWER(?) "
                    + "    AND t1.tuyenID IN ( "
                    + "        SELECT t2.tuyenID FROM TuyenChiTiet t2 JOIN Ga g2 ON t2.gaID = g2.gaID WHERE LOWER(g2.tenGa) LIKE LOWER(?) "
                    + "        AND t2.thuTu > ( "
                    + "            SELECT MIN(t3.thuTu) FROM TuyenChiTiet t3 JOIN Ga g3 ON t3.gaID = g3.gaID "
                    + "            WHERE LOWER(g3.tenGa) LIKE LOWER(?) AND t3.tuyenID = t2.tuyenID "
                    + "        ) " + "    ) " + ") ORDER BY t.tuyenID";
            expectedParamCount = 3;
        } else {
            sql = "SELECT DISTINCT t.tuyenID, t.moTa, t.trangThai "
                    + "FROM Tuyen t JOIN TuyenChiTiet t1 ON t.tuyenID = t1.tuyenID JOIN Ga g1 ON t1.gaID = g1.gaID "
                    + "WHERE 1=1 ";
            if (gaDi != null && !gaDi.trim().isEmpty()) {
                sql += " AND LOWER(g1.tenGa) LIKE LOWER(?) ";
            } else if (gaDen != null && !gaDen.trim().isEmpty()) {
                sql += " AND LOWER(g1.tenGa) LIKE LOWER(?) ";
            }
            sql += " ORDER BY t.tuyenID";
            expectedParamCount = 1;
        }

        try (Connection con = connectDB.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            if (hasBothGa) {
                pstmt.setString(1, "%" + gaDi.trim() + "%");
                pstmt.setString(2, "%" + gaDen.trim() + "%");
                pstmt.setString(3, "%" + gaDi.trim() + "%");
            } else if (gaDi != null && !gaDi.trim().isEmpty()) {
                pstmt.setString(1, "%" + gaDi.trim() + "%");
            } else if (gaDen != null && !gaDen.trim().isEmpty()) {
                pstmt.setString(1, "%" + gaDen.trim() + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tuyenID = rs.getString("tuyenID");
                    if (!tuyepMap.containsKey(tuyenID)) {
                        Tuyen tuyen = new Tuyen(tuyenID, rs.getString("moTa"), rs.getBoolean("trangThai"));
                        tuyepMap.put(tuyenID, tuyen);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(tuyepMap.values());
    }

    public boolean themTuyenMoi(Tuyen tuyenMoi) {
        String sql = "INSERT INTO Tuyen(tuyenID, moTa, trangThai) VALUES(?, ?, ?)";
        if (tuyenMoi == null || tuyenMoi.getTuyenID() == null || tuyenMoi.getTuyenID().isEmpty()) {
            return false;
        }
        try (Connection con = connectDB.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tuyenMoi.getTuyenID());
            pstmt.setString(2, tuyenMoi.getMoTa());
            pstmt.setBoolean(3, tuyenMoi.isTrangThai());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaTuyen(String tuyenID) {
        String sql = "DELETE FROM Tuyen WHERE tuyenID = ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tuyenID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatTuyen(Tuyen tuyenCapNhat) {
        String sql = "UPDATE Tuyen SET moTa = ?, trangThai = ? WHERE tuyenID = ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tuyenCapNhat.getMoTa());
            pstmt.setBoolean(2, tuyenCapNhat.isTrangThai());
            pstmt.setString(3, tuyenCapNhat.getTuyenID());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tuyến theo mã tuyến chính xác (không dùng like) Dùng để kiểm tra xem mã
     * đã tồn tại hay chưa.
     *
     * @param tuyenIDTim Mã tuyến cần tìm.
     * @return Tuyen object nếu tìm thấy, null nếu không.
     */
    public Tuyen getTuyenByExactID(String tuyenIDTim) {
        String sql = "SELECT * FROM Tuyen WHERE tuyenID = ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tuyenIDTim);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new Tuyen(rs.getString("tuyenID"), rs.getString("moTa"), rs.getBoolean("trangThai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tuyen> getTop10Tuyen(String keyword) {
        List<Tuyen> list = new ArrayList<>();
        String sql = "SELECT TOP 10 tuyenID, moTa, trangThai FROM Tuyen WHERE tuyenID LIKE ? OR moTa LIKE ?";
        Connection conn = connectDB.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Tuyen(rs.getString("tuyenID"), rs.getString("moTa"), rs.getBoolean("trangThai")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Tuyen layTuyenTheoMa(String maTuyen) {
        Tuyen tuyen = null;
        Connection con = ConnectDB.getInstance().getConnection();
        String sql = "SELECT * FROM Tuyen WHERE tuyenID = ?";

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maTuyen);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String id = rs.getString("tuyenID");
                String moTa = rs.getString("moTa");
                boolean trangThai = rs.getBoolean("trangThai");
                tuyen = new Tuyen(id, moTa, trangThai);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tuyen;
    }

    /**
     * Lấy danh sách chi tiết các ga (TuyenChiTiet) thuộc về một Tuyến
     * Sắp xếp theo thứ tự (thuTu)
     */
    public List<TuyenChiTiet> layDanhSachTuyenChiTiet(String maTuyen) {
        List<TuyenChiTiet> list = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();

        String sql = "SELECT tct.*, g.tenGa " +
                "FROM TuyenChiTiet tct " +
                "JOIN Ga g ON tct.gaID = g.gaID " +
                "WHERE tct.tuyenID = ? " +
                "ORDER BY tct.thuTu ASC";

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maTuyen);
            ResultSet rs = pst.executeQuery();

            Tuyen tuyen = getTuyenByExactID(maTuyen);

            if (tuyen == null) tuyen = new Tuyen(maTuyen);

            while (rs.next()) {
                String gaID = rs.getString("gaID");
                String tenGa = rs.getString("tenGa");
                int thuTu = rs.getInt("thuTu");
                int khoangCach = rs.getInt("khoangCachTuGaXP");

                Ga ga = new Ga(gaID, tenGa);

                TuyenChiTiet ct = new TuyenChiTiet(tuyen, ga, thuTu, khoangCach);
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
