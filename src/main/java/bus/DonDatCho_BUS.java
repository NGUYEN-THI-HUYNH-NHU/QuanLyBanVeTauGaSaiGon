package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [2:11:25 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.impl.DonDatChoDAO;
import dto.DonDatChoDTO;
import mapper.DonDatChoMapper;

import java.util.Date;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

public class DonDatCho_BUS {
    private final DonDatChoDAO donDatChoDAO = new DonDatChoDAO();

    /**
     * @param donDatChoID
     * @param soGiayTo
     * @return
     */
    public DonDatChoDTO timDonDatChoTheoIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        return DonDatChoMapper.INSTANCE.toDTO(donDatChoDAO.findDonDatChoByIDVaSoGiayTo(donDatChoID, soGiayTo));
    }

    public List<DonDatChoDTO> layDanhSachDonDatCho() {
        return donDatChoDAO.getListDonDatCho().stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();
    }

    public List<DonDatChoDTO> layDonDatChoTheoKeyword(String keyword, String type) {
        return donDatChoDAO.searchDonDatChoByKeyword(keyword, type).stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();

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
        return donDatChoDAO.searchDonDatChoByFilter(tuNgay, denNgay).stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();
    }
}
