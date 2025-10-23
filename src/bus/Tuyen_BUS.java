package bus;/*
 * @ (#) Tuyen_BUS.java   1.0     30/09/2025


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import dao.Ga_DAO;
import dao.TuyenChiTiet_DAO;
import dao.Tuyen_DAO;
import entity.Tuyen;
import entity.TuyenChiTiet;

import java.util.*;
import java.util.stream.Collectors;

public class Tuyen_BUS {
    private final Tuyen_DAO tuyen_dao;
    private final Ga_DAO ga_dao;
    private final TuyenChiTiet_DAO tuyenChiTietDao;

    public Tuyen_BUS(){
        tuyen_dao = new Tuyen_DAO();
        ga_dao = new Ga_DAO();
        tuyenChiTietDao = new TuyenChiTiet_DAO();
    }

    public List<Tuyen> getAllTuyen(){
        return tuyen_dao.getAllTuyen();
    }

    public List<Tuyen> getTuyenByID(String tuyenID){
        return tuyen_dao.getTuyenByID(tuyenID);
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

    public List<Object[]> getDuLieuBang(){
        return convertTuyenListToTableData(tuyen_dao.getAllTuyen());
    }

    private List<Object[]> convertTuyenListToTableData(List<Tuyen> dsTuyen) {
        List<Object[]> dsDuLieuBang = new ArrayList<>();

        for (Tuyen tuyen : dsTuyen) {
            List<TuyenChiTiet> dsTuyenChiTiet = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyen.getTuyenID());

            if(dsTuyenChiTiet != null && dsTuyenChiTiet.size() >= 2){
                TuyenChiTiet gaDiTCT = dsTuyenChiTiet.get(0);
                TuyenChiTiet gaDenTCT = dsTuyenChiTiet.get(dsTuyenChiTiet.size() - 1);
                int khoangCach = gaDenTCT.getKhoangCachTuGaXuatPhatKm();

                String gaTrungGian;
                if(dsTuyenChiTiet.size() > 2){
                    gaTrungGian = dsTuyenChiTiet.subList(1, dsTuyenChiTiet.size() - 1).stream()
                            .map(tct -> tct.getGa().getTenGa())
                            .collect(Collectors.joining(" -> "));
                } else {
                    gaTrungGian = "-";
                }

                Object[] rowData = new Object[]{
                        tuyen.getTuyenID(),
                        gaDiTCT.getGa().getTenGa(),
                        gaDenTCT.getGa().getTenGa(),
                        gaTrungGian,
                        khoangCach
                };
                dsDuLieuBang.add(rowData);
            }
        }
        return dsDuLieuBang;
    }

    /**
     * Lấy dữ liệu bảng cho GUI dựa trên Mã Tuyến gần đúng.
     * @param tuyenID Mã tuyến cần tìm kiếm.
     * @return List<Object[]> dữ liệu bảng.
     */
    public List<Object[]> getDuLieuBangTheoTuyenID(String tuyenID) {
        if (tuyenID == null || tuyenID.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Lấy List<Tuyen> từ DAO
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(tuyenID.trim());

        // 2. Chuyển đổi sang List<Object[]> (Sử dụng logic tương tự hàm getDuLieuBang)
        return convertTuyenListToTableData(dsTuyen);
    }

    /**
     * Lấy dữ liệu bảng cho GUI dựa trên Ga Đi và Ga Đến.
     * @param gaDi Tên ga xuất phát.
     * @param gaDen Tên ga đích.
     * @return List<Object[]> dữ liệu bảng.
     */
    public List<Object[]> getDuLieuBangTheoGa(String gaDi, String gaDen) {
        // 1. Lọc Tuyến thỏa mãn điều kiện Ga Đi/Ga Đến từ DAO
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim());

        // 2. Chuyển đổi sang List<Object[]>
        return convertTuyenListToTableData(dsTuyen);
    }
}


