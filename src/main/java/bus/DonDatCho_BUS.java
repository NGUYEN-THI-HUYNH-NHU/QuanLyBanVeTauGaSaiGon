package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [2:11:25 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.impl.DonDatCho_DAO;
import entity.DonDatCho;
import gui.application.form.donDatCho.DonDatChoDTO;

import java.util.Date;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

public class DonDatCho_BUS {
    private final DonDatCho_DAO donDatChoDAO = new DonDatCho_DAO();

    /**
     * @param donDatChoID
     * @param soGiayTo
     * @return
     */
    public DonDatCho timDonDatChoTheoIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        return donDatChoDAO.findDonDatChoByIDVaSoGiayTo(donDatChoID, soGiayTo);
    }

    public List<DonDatChoDTO> layDanhSachDonDatCho() {
        return donDatChoDAO.getListDonDatCho();
    }

    public List<DonDatChoDTO> layDonDatChoTheoKeyword(String keyword, String type) {
        return donDatChoDAO.searchDonDatChoByKeyword(keyword, type);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10DonDatChoID(String keyword) {
        return donDatChoDAO.getTop10DonDatChoID(keyword);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10SoGiayTo(String keyword) {
        return donDatChoDAO.getTop10SoGiayTo(keyword);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10SoDienThoai(String keyword) {
        return donDatChoDAO.getTop10SoDienThoai(keyword);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10TenKhachHang(String keyword) {
        return donDatChoDAO.getTop10TenKhachHang(keyword);
    }

    /**
     * @param tuNgay
     * @param denNgay
     * @return
     */
    public List<DonDatChoDTO> locHoaDonTheoCacTieuChi(Date tuNgay, Date denNgay) {
        return donDatChoDAO.searchDonDatChoByFilter(tuNgay, denNgay);
    }
}
