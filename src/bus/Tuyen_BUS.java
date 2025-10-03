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
import entity.TuyenChiTiet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tuyen_BUS {
    private final dao.Tuyen_DAO tuyen_dao;
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

    public boolean themTuyenMoi(Tuyen tuyenMoi){
        if(tuyenMoi.getDanhSachTuyenChiTiet() == null || tuyenMoi.getDanhSachTuyenChiTiet().size() < 2){
            throw new IllegalArgumentException("Tuyến mới phải có ít nhất 2 ga.");
        }
        return tuyen_dao.themTuyenMoi(tuyenMoi);
    }



    public int capNhatTuyenByID(String tuyenID, Tuyen tuyenCapNhat){
        return tuyen_dao.capNhatTuyenByID(tuyenID, tuyenCapNhat);
    }

    public List<String> timIDTuyenChoGoiY(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Gọi xuống DAO
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(input.trim());
        List<String> idTuyenList = new ArrayList<>();

        for(Tuyen tuyen : dsTuyen){
            idTuyenList.add(tuyen.getTuyenID());
        }

        return idTuyenList;
    }

   public List<Tuyen> timTuyenTheoGa(String gaDi, String gaDen){
        if((gaDi == null || gaDi.trim().isEmpty()) && (gaDen == null || gaDen.trim().isEmpty())){
            return new ArrayList<>();
        }
        return tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim());
    }
}


