package dao.impl;
/*
 * @(#) HoaDonChiTiet_DAO.java 1.0 [11:34:32 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 *
 * @author: NguyenThiHuynhNhu
 *
 * @date: Nov 1, 2025
 *
 * @version: 1.0
 */

import connectDB.ConnectDB;
import entity.HoaDonChiTiet;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.LoaiDichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoaDonChiTiet_DAO {
    private final ConnectDB connectDB = ConnectDB.getInstance();

    public HoaDonChiTiet_DAO() {
        connectDB.connect();
    }

    public boolean insertHoaDonChiTiet(Connection conn, HoaDonChiTiet hoaDonChiTiet) throws Exception {
        String sql = "INSERT INTO HoaDonChiTiet (hoaDonChiTietID, hoaDonID, veID, phieuDungPhongVIPID, tenDichVu, loaiDichVu, donViTinh, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoaDonChiTiet.getId());
            ps.setString(2, hoaDonChiTiet.getHoaDon().getId());
            if (hoaDonChiTiet.getVe() != null && (hoaDonChiTiet.getLoaiDichVu() == LoaiDichVu.VE_BAN
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVu.VE_HOAN
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVu.VE_DOI)) {
                ps.setString(3, hoaDonChiTiet.getVe().getId());
                ps.setNull(4, 0);
            } else if (hoaDonChiTiet.getPhieuDungPhongVIP() != null
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVu.PHONG_VIP) {
                ps.setNull(3, 0);
                ps.setString(4, hoaDonChiTiet.getPhieuDungPhongVIP().getId());
            } else {
                ps.setNull(3, 0);
                ps.setNull(4, 0);
            }
            ps.setString(5, hoaDonChiTiet.getTenDichVu());
            ps.setString(6, hoaDonChiTiet.getLoaiDichVu().toString());
            ps.setString(7, hoaDonChiTiet.getDonViTinh());
            ps.setInt(8, hoaDonChiTiet.getSoLuong());
            ps.setDouble(9, hoaDonChiTiet.getDonGia());
            ps.setDouble(10, hoaDonChiTiet.getThanhTien());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * @param hoaDonID
     * @return
     */
    public List<HoaDonChiTiet> getHoaDonChiTietByHoaDonID(String hoaDonID) {
        String sql = "SELECT  hoaDonChiTietID, veID, phieuDungPhongVIPID, tenDichVu, loaiDichVu, donViTinh, soLuong, donGia, thanhTien FROM HoaDonChiTiet WHERE hoaDonID = ?";
        Connection conn = connectDB.getConnection();
        List<HoaDonChiTiet> listHDCT = new ArrayList<HoaDonChiTiet>();
        ResultSet rs = null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoaDonID);
            rs = ps.executeQuery();

            while (rs.next()) {
                HoaDonChiTiet ct = new HoaDonChiTiet();
                ct.setId(rs.getString("hoaDonChiTietID"));
                if (rs.getString("veID") != null) {
                    ct.setVe(Ve.builder().id(rs.getString("veID")).build());
                }
                if (rs.getString("phieuDungPhongVIPID") != null) {
                    ct.setPhieuDungPhongVIP(PhieuDungPhongVIP.builder().id(rs.getString("phieuDungPhongVIPID")).build());
                }
                ct.setTenDichVu(rs.getString("tenDichVu"));
                ct.setLoaiDichVu(LoaiDichVu.valueOf(rs.getString("loaiDichVu")));
                ct.setDonViTinh(rs.getString("donViTinh"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                ct.setThanhTien(rs.getDouble("thanhTien"));

                listHDCT.add(ct);
            }
            return listHDCT;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
