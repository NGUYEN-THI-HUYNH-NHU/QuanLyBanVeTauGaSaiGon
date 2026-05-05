/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/5/2026
 */

package dao.impl;

import entity.HangToa;

public class HangToaDAO extends AbstractGenericDAO<HangToa, String> implements dao.IHangToaDAO {
    public HangToaDAO() {
        super(HangToa.class);
    }
}
