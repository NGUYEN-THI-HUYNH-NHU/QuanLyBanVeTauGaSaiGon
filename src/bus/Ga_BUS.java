package bus;/*
 * @ (#) Ga_BUS.java   1.0     30/09/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import dao.Ga_DAO;
import entity.Ga;

import java.util.ArrayList;
import java.util.List;

public class Ga_BUS {
    private final Ga_DAO ga_dao;

    public Ga_BUS(){
        ga_dao = new Ga_DAO();
    }

    public List<String> timTenGaChoGoiY(String input){
        if (input == null || input.trim().isEmpty()){
            return new ArrayList<>();
        }

        List<Ga> dsGa = ga_dao.getGaByTenGaList(input.trim());
        List<String> tenGaList = new ArrayList<>();

        for(Ga ga : dsGa){
            tenGaList.add(ga.getTenGa());
        }
        return tenGaList;
    }

    public Ga getGaByTenGa(String tenGa){
        return ga_dao.getGaByTenGa(tenGa);
    }
}
