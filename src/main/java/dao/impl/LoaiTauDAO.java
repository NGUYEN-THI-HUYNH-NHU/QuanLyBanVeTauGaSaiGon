/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/5/2026
 */

package dao.impl;

import entity.LoaiTau;

public class LoaiTauDAO extends AbstractGenericDAO<LoaiTau, String> {
    public LoaiTauDAO() {
        super(LoaiTau.class);
    }
}
