/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/5/2026
 */

package dao.impl;

import dao.ILoaiTauDAO;
import entity.LoaiTau;

import java.util.List;

public class LoaiTauDAO extends AbstractGenericDAO<LoaiTau, String> implements ILoaiTauDAO {
    public LoaiTauDAO() {
        super(LoaiTau.class);
    }

    @Override
    public List<String> getAllLoaiTauMoTa() {
        return List.of();
    }
}
