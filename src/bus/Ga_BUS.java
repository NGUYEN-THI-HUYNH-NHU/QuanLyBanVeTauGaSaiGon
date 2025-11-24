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
import dao.KhoangCachChuan_DAO;
import dao.TuyenChiTiet_DAO;
import entity.Ga;
import entity.TuyenChiTiet;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class Ga_BUS {
    private final Ga_DAO ga_dao;
    private TuyenChiTiet_DAO tuyenChiTietDao;
    private KhoangCachChuan_DAO khoangCachChuanDao;

    public Ga_BUS(){
        ga_dao = new Ga_DAO();
        tuyenChiTietDao = new TuyenChiTiet_DAO();
        khoangCachChuanDao = new KhoangCachChuan_DAO();
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

    /**
     * lấy danh sách tên tất cả ga để hiển thị lên combobox
     * @return List<String> danh sách tên ga
     */
    public List<String> getDanhSachTenGa(){
        List<Ga> dsGa = ga_dao.getAllGa();
        List<String> dsTenGa = new ArrayList<>();
        for(Ga ga : dsGa){
            dsTenGa.add(ga.getTenGa());
        }
        return dsTenGa;
    }

    /**
     * Loại bỏ dấu tiếng việt
     * @param input Chuỗi cần loại bỏ dấu
     * @return Chuỗi đã loại bỏ dấu
     */
    private String removeAccents(String input){
        if(input == null){
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        withoutAccents = withoutAccents.replace("[^a-zA-Z\\s]", "");
        return withoutAccents.toUpperCase(Locale.ROOT);
    }

    /**
     * Hàm tạo mã rút gọn 3 ký tự
     * @param tenGa Tên Ga đầy đủ
     * @return Mã rút gọn 3 ký tự
     */
    private String taoMaRutGon(String tenGa) {
        String normalizedName = removeAccents(tenGa);
        String[] words = normalizedName.split("\\s+");
        List<String> validWords = Arrays.stream(words).filter(word -> !word.isEmpty()).collect(Collectors.toList());

        int numWords = validWords.size();
        StringBuilder ma = new StringBuilder();

        if (numWords == 0) {
            return "";
        }

        if(numWords == 1){
            ma.append(validWords.get(0).charAt(0));
            ma.append("XX");
        }
        else if(numWords == 2){
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(0).charAt(1));
            ma.append(validWords.get(1).charAt(0));
        }
        else if(numWords == 3){
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(1).charAt(0));
            ma.append(validWords.get(2).charAt(0));
        }
        else {
            // Nhiều hơn 3 từ, lấy ký tự đầu tiên của từ đầu tiên, ký tự đầu tiên của từ thứ hai và ký tự đầu tiên của từ cuối cùng
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(1).charAt(0));
            ma.append(validWords.get(2).charAt(0));
        }
        return ma.toString();
    }

    public List<Object[]> getAllGaSortedByMainRoute(){
        final String MAIN_ROUTE = "SGO-HNO";
        List<TuyenChiTiet> mainRouteDetails = tuyenChiTietDao.layDanhSachTheoTuyenID(MAIN_ROUTE);
        Map<String , Integer> kcxpMap = mainRouteDetails.stream()
                .collect(Collectors.toMap(
                        tct -> tct.getGa().getGaID(), TuyenChiTiet::getKhoangCachTuGaXuatPhatKm, (existing, replacement) -> existing
                ));
        List<Ga> allGa = ga_dao.getAllGa();

        List<Map<String,Object>> gaData = new ArrayList<>();
        List<Object[]> dsGaBang = new ArrayList<>();
        List<Ga> gaSortedList = gaData.stream().map(data -> (Ga) data.get("Ga")).collect(Collectors.toList());
        for(int i=0; i< gaSortedList.size(); i++){
            Ga gaHienTai = gaSortedList.get(i);
            String khoangCach2Ga = "-";
            if(i == 0 && gaHienTai.getGaID().equals("SGO")){
                khoangCach2Ga = "0 Km";
            }
            if(i< gaSortedList.size() -1){
                Ga gaSau = gaSortedList.get(i+1);
                int kcSegment = khoangCachChuanDao.getKhoangCachDoan(gaHienTai.getGaID(),gaSau.getGaID());
                if(kcSegment > 0){
                    khoangCach2Ga = kcSegment + " Km";
                }
            }

            Object[] row = new Object[]{
                    gaHienTai.getTenGa(),
                    gaHienTai.getGaID(),
                    khoangCach2Ga,
                    gaHienTai.getTinhThanh()
            };
            dsGaBang.add(row);
            }
        return dsGaBang;
    }

}
