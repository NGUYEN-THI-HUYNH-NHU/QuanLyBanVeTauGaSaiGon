package bus;/*
 * @ (#) PhanQuyen_BUS.java   1.0     03/10/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 03/10/2025
 */

import entity.type.VaiTroNhanVien;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;

public class PhanQuyen_BUS {
    public static void phanQuyenQuanLyTuyen(PanelQuanLyTuyen pnlQLTuyen, VaiTroNhanVien vaiTro){
        if(vaiTro == VaiTroNhanVien.NHAN_VIEN){
            pnlQLTuyen.getBtnThemTuyen().setEnabled(false);
            pnlQLTuyen.getBtnCapNhatTuyen().setEnabled(false);
        }else if(vaiTro == VaiTroNhanVien.QUAN_LY){
            pnlQLTuyen.getBtnThemTuyen().setEnabled(true);
            pnlQLTuyen.getBtnCapNhatTuyen().setEnabled(true);
            pnlQLTuyen.getBtnLamMoiTuyen().setEnabled(true);
            pnlQLTuyen.getTxtGaDi().setEditable(true);
            pnlQLTuyen.getTxtGaDen().setEditable(true);
            pnlQLTuyen.getTxtTimKiem().setEditable(true);
        }
    }
}
