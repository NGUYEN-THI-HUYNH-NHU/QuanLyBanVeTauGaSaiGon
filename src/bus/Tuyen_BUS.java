package bus;/*
 * @ (#) Tuyen_BUS.java   1.0     30/09/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import dao.Ga_DAO;
import dao.Tuyen_DAO;
import entity.Tuyen;

import java.util.ArrayList;
import java.util.List;

public class Tuyen_BUS {
    private final Tuyen_DAO tuyen_dao;
    private final Ga_DAO ga_dao;

    public Tuyen_BUS(){
        tuyen_dao = new Tuyen_DAO();
        ga_dao = new Ga_DAO();
    }

    public List<Tuyen> getAllTuyen(){
        return tuyen_dao.getAllTuyen();
    }

    public List<Tuyen> getTuyenByID(String tuyenID){
        return tuyen_dao.getTuyenByID(tuyenID);
    }

    public boolean themTuyenMoi(Tuyen tuyen){
        if(tuyen.getGaDi().getGaID().equals(tuyen.getGaDen().getGaID())){
            throw new IllegalArgumentException("Ga Đi và Ga Đến không được trùng nhau!");
        }
        return tuyen_dao.themTuyenMoi(tuyen);
    }

    public int capNhatTuyenByID(String id, Tuyen tuyenCapNhat){
        return tuyen_dao.capNhatTuyenByID(id, tuyenCapNhat);
    }

    public List<Tuyen> timTuyenTheoGa(String gaDiID, String gaDenID){
        return tuyen_dao.getTuyenTheoGa(gaDiID, gaDenID);
    }

    public List<String> timIDTuyenChoGoiY(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(input.trim());
        List<String> idTuyenList = new ArrayList<>();


            for (Tuyen tuyen : dsTuyen) {
                idTuyenList.add(tuyen.getTuyenID());
            }

        return idTuyenList;
    }
}
