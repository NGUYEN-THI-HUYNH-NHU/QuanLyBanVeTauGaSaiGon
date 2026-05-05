/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/5/2026
 */

package bus;

import dao.ILoaiTauDAO;
import dao.impl.LoaiTauDAO;

import java.util.List;

public class LoaiTau_BUS {
    private final ILoaiTauDAO loaiTauDAO = new LoaiTauDAO();

    public List<String> getAllLoaiTauMoTa() {
        return loaiTauDAO.getAllLoaiTauMoTa();
    }
}
