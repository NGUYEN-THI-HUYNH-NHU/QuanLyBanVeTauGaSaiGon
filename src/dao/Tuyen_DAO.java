package dao;

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

public class Tuyen_DAO {
    private ConnectDB connectDB;
    private Ga_DAO ga_dao;

    public Tuyen_DAO() {
        connectDB = ConnectDB.getInstance();
        ga_dao = new Ga_DAO();
    }

    public List<Tuyen> getAllTuyen() {
        Map<String, Tuyen> tuyenMap = new LinkedHashMap<>();

        String sql = "SELECT t.tuyenID, t.moTa, " +
                " tct.gaID, tct.thuTu, tct.khoangCachTuGaXuatPhatKm, " +
                " ga.tenGa " +
                "FROM Tuyen t " +
                "JOIN TuyenChiTiet tct ON t.tuyenID = tct.tuyenID " +
                "JOIN Ga ga ON tct.gaID = ga.gaID " +
                "ORDER BY t.tuyenID, tct.thuTu";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String tuyenID = rs.getString("tuyenID");

                // Nếu tuyến chưa có trong map thì tạo mới
                Tuyen tuyen = tuyenMap.get(tuyenID);
                if (tuyen == null) {
                    tuyen = new Tuyen(tuyenID, rs.getString("moTa"));
                    tuyen.setDanhSachTuyenChiTiet(new ArrayList<>());
                    tuyenMap.put(tuyenID, tuyen);
                }

                // Tạo ga
                Ga ga = new Ga(rs.getString("gaID"), rs.getString("tenGa"));

                // Tạo chi tiết tuyến
                TuyenChiTiet tuyenChiTiet = new TuyenChiTiet(
                        tuyen,
                        ga,
                        rs.getInt("thuTu"),
                        rs.getInt("khoangCachTuGaXuatPhatKm")
                );

                // Thêm vào danh sách chi tiết của tuyến
                tuyen.getDanhSachTuyenChiTiet().add(tuyenChiTiet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(tuyenMap.values());
    }


    public List<Tuyen> getTuyenByID(String tuyenIDTim) {
        Map<String, Tuyen> tuyenMap = new LinkedHashMap<>();

        String sql = "SELECT t.tuyenID, t.moTa, " +
                " tct.gaID, tct.thuTu, tct.khoangCachTuGaXuatPhatKm, " +
                " ga.tenGa " +
                "FROM Tuyen t " +
                "JOIN TuyenChiTiet tct ON t.tuyenID = tct.tuyenID " +
                "JOIN Ga ga ON tct.gaID = ga.gaID " +
                "WHERE t.tuyenID LIKE ? " +
                "ORDER BY t.tuyenID, tct.thuTu";

        try (Connection con = connectDB.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, "%" + tuyenIDTim + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String tuyenID = rs.getString("tuyenID");
                Tuyen tuyen = tuyenMap.get(tuyenID);
                if (tuyen == null) {
                    tuyen = new Tuyen(tuyenID, rs.getString("moTa"));
                    tuyen.setDanhSachTuyenChiTiet(new ArrayList<>());
                    tuyenMap.put(tuyenID, tuyen);
                }

                Ga ga = new Ga(rs.getString("gaID"), rs.getString("tenGa"));
                TuyenChiTiet chiTiet = new TuyenChiTiet(
                        tuyen,
                        ga,
                        rs.getInt("thuTu"),
                        rs.getInt("khoangCachTuGaXuatPhatKm")
                );

                tuyen.getDanhSachTuyenChiTiet().add(chiTiet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(tuyenMap.values());
    }


    public boolean themTuyenMoi(Tuyen tuyenMoi) {
        String sqlTuyen = "INSERT INTO Tuyen (tuyenID, moTa) VALUES (?, ?)";
        String sqlChiTiet = "INSERT INTO TuyenChiTiet (tuyenID, gaID, thuTu, khoangCachTuGaXuatPhatKm) VALUES (?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection()) {
            con.setAutoCommit(false); // bắt đầu transaction

            try (PreparedStatement pstmtTuyen = con.prepareStatement(sqlTuyen)) {
                // Thêm tuyến
                pstmtTuyen.setString(1, tuyenMoi.getTuyenID());
                pstmtTuyen.setString(2, tuyenMoi.getMoTa());
                pstmtTuyen.executeUpdate();
            }

            // Thêm chi tiết ga của tuyến
            try (PreparedStatement pstmtChiTiet = con.prepareStatement(sqlChiTiet)) {
                for (TuyenChiTiet chiTiet : tuyenMoi.getDanhSachTuyenChiTiet()) {
                    pstmtChiTiet.setString(1, tuyenMoi.getTuyenID());
                    pstmtChiTiet.setString(2, chiTiet.getGa().getGaID());
                    pstmtChiTiet.setInt(3, chiTiet.getThuTu());
                    pstmtChiTiet.setInt(4, chiTiet.getKhoangCachTuGaXuatPhatKm());
                    pstmtChiTiet.addBatch(); // gom nhiều lệnh để insert nhanh hơn
                }
                pstmtChiTiet.executeBatch();
            }

            con.commit(); // commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public int capNhatTuyenByID(String tuyenIDSua, Tuyen capNhatTuyen) {
        String sqlUpdateTuyen = "UPDATE Tuyen SET moTa = ? WHERE tuyenID = ?";
        String sqlDeleteChiTiet = "DELETE FROM TuyenChiTiet WHERE tuyenID = ?";
        String sqlInsertChiTiet = "INSERT INTO TuyenChiTiet (tuyenID, gaID, thuTu, khoangCachTuGaXuatPhatKm) VALUES (?, ?, ?, ?)";

        int rowsAffected = 0;

        Connection con = null;
        try {
            con = connectDB.getConnection();
            con.setAutoCommit(false); // bắt đầu transaction

            // 1. Update bảng TUYEN
            try (PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdateTuyen)) {
                pstmtUpdate.setString(1, capNhatTuyen.getMoTa());
                pstmtUpdate.setString(2, tuyenIDSua);
                rowsAffected = pstmtUpdate.executeUpdate();
            }

            // 2. Xóa chi tiết cũ
            try (PreparedStatement pstmtDelete = con.prepareStatement(sqlDeleteChiTiet)) {
                pstmtDelete.setString(1, tuyenIDSua);
                pstmtDelete.executeUpdate();
            }

            // 3. Insert chi tiết mới
            try (PreparedStatement pstmtInsert = con.prepareStatement(sqlInsertChiTiet)) {
                for (TuyenChiTiet chiTiet : capNhatTuyen.getDanhSachTuyenChiTiet()) {
                    pstmtInsert.setString(1, tuyenIDSua);
                    pstmtInsert.setString(2, chiTiet.getGa().getGaID());
                    pstmtInsert.setInt(3, chiTiet.getThuTu());
                    pstmtInsert.setInt(4, chiTiet.getKhoangCachTuGaXuatPhatKm());
                    pstmtInsert.addBatch();
                }
                pstmtInsert.executeBatch();
            }

            con.commit(); // commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
            rowsAffected = -1; // báo lỗi
        }
        return rowsAffected;
    }



    public List<Tuyen> getTuyenTheoGa(String gaDi, String gaDen) {
        Map<String, Tuyen> tuyenMap = new LinkedHashMap<>();

        String sql = "SELECT t.tuyenID, t.moTa, tct.gaID, tct.thuTu, tct.khoangCachTuGaXuatPhatKm, ga.tenGa " +
                "FROM Tuyen t " +
                "JOIN TuyenChiTiet tct ON t.tuyenID = tct.tuyenID " +
                "JOIN Ga ga ON tct.gaID = ga.gaID " +
                "WHERE t.tuyenID IN ( " +
                "    SELECT t1.tuyenID FROM TuyenChiTiet t1 " +
                "    JOIN Ga g1 ON t1.gaID = g1.gaID " +
                "    WHERE (? IS NULL OR LOWER(g1.tenGa) LIKE LOWER(?)) " +
                ") " +
                "AND t.tuyenID IN ( " +
                "    SELECT t2.tuyenID FROM TuyenChiTiet t2 " +
                "    JOIN Ga g2 ON t2.gaID = g2.gaID " +
                "    WHERE (? IS NULL OR LOWER(g2.tenGa) LIKE LOWER(?)) " +
                "    AND (? IS NULL OR t2.thuTu > ( " +
                "        SELECT MIN(t3.thuTu) FROM TuyenChiTiet t3 " +
                "        JOIN Ga g3 ON t3.gaID = g3.gaID " +
                "        WHERE LOWER(g3.tenGa) LIKE LOWER(?) " +
                "    )) " +
                ") " +
                "ORDER BY t.tuyenID, tct.thuTu";

        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Thiết lập parameter
            pstmt.setString(1, gaDi != null && !gaDi.isEmpty() ? gaDi : null);        // subquery 1
            pstmt.setString(2, gaDi != null && !gaDi.isEmpty() ? "%" + gaDi + "%" : null); // subquery 1 LIKE

            pstmt.setString(3, gaDen != null && !gaDen.isEmpty() ? gaDen : null);       // subquery 2
            pstmt.setString(4, gaDen != null && !gaDen.isEmpty() ? "%" + gaDen + "%" : null); // subquery 2 LIKE

            pstmt.setString(5, gaDi != null && !gaDi.isEmpty() ? gaDi : null);         // so sánh thứ tự
            pstmt.setString(6, gaDi != null && !gaDi.isEmpty() ? "%" + gaDi + "%" : null); // MIN(thutu) của ga đi

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tuyenID = rs.getString("tuyenID");
                    Tuyen tuyen = tuyenMap.get(tuyenID);
                    if (tuyen == null) {
                        tuyen = new Tuyen(tuyenID, rs.getString("moTa"));
                        tuyen.setDanhSachTuyenChiTiet(new ArrayList<>());
                        tuyenMap.put(tuyenID, tuyen);
                    }

                    Ga ga = new Ga(rs.getString("gaID"), rs.getString("tenGa"));
                    TuyenChiTiet chiTiet = new TuyenChiTiet(
                            tuyen,
                            ga,
                            rs.getInt("thuTu"),
                            rs.getInt("khoangCachTuGaXuatPhatKm")
                    );
                    tuyen.getDanhSachTuyenChiTiet().add(chiTiet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(tuyenMap.values());
    }











}
