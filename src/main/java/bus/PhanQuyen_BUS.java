package bus;/*
 * @ (#) PhanQuyen_BUS.java   1.0     03/10/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 03/10/2025
 */

import entity.type.VaiTroNhanVienEnums;
import gui.application.form.quanLyChuyen.PanelQuanLyChuyen;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;

public class PhanQuyen_BUS {
    public static void phanQuyenQuanLyTuyen(PanelQuanLyTuyen pnlQLTuyen, VaiTroNhanVienEnums vaiTro) {
        if (vaiTro == VaiTroNhanVienEnums.NHAN_VIEN) {
            pnlQLTuyen.getBtnThemTuyen().setEnabled(false);
            pnlQLTuyen.getBtnCapNhatTuyen().setEnabled(false);
        } else if (vaiTro == VaiTroNhanVienEnums.QUAN_LY) {
            pnlQLTuyen.getBtnThemTuyen().setEnabled(true);
            pnlQLTuyen.getBtnCapNhatTuyen().setEnabled(true);
            pnlQLTuyen.getBtnLamMoiTuyen().setEnabled(true);
            pnlQLTuyen.getTxtGaDi().setEditable(true);
            pnlQLTuyen.getTxtGaDen().setEditable(true);
            pnlQLTuyen.getTxtTimKiem().setEditable(true);
        }
    }

    public static void phanQuyenQuanLyChuyen(PanelQuanLyChuyen pnlQLChuyen, VaiTroNhanVienEnums vaiTro) {
        if (vaiTro == VaiTroNhanVienEnums.NHAN_VIEN) {
            pnlQLChuyen.getBtnThemChuyen().setEnabled(false);
            pnlQLChuyen.getBtnCapNhatChuen().setEnabled(false);
        } else if (vaiTro == VaiTroNhanVienEnums.QUAN_LY) {
            pnlQLChuyen.getBtnThemChuyen().setEnabled(true);
            pnlQLChuyen.getBtnCapNhatChuen().setEnabled(true);
            pnlQLChuyen.getBtnLamMoi().setEnabled(true);
        }
    }
}
