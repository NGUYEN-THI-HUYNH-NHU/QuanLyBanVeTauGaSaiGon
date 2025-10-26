package gui.application.form.banVe;
/*
 * @(#) PanelBanVe1Controller.java  1.0  [10:42:48 AM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */
import java.util.List;

import bus.VeSession_BUS;
import entity.Chuyen;
import entity.Ve;
import gui.application.form.banVe.PanelBuoc1Controller.SearchListener;
import gui.application.form.banVe.PanelBuoc2Controller.SeatSelectedListener;

public class PanelBanVe1Controller {
    
    private final PanelBanVe1 p1;
    private final BookingSession bookingSession;
    // Các sub-controller
    private final PanelBuoc1Controller buoc1Controller;
    private final PanelBuoc2Controller buoc2Controller;
    // private final PanelBuoc3Controller buoc3Controller; // Sẽ thêm sau
    private Runnable onPanel1CompleteListener;
	private PanelBuoc2 p2;
    
    public void addPanel1CompleteListener(Runnable listener) {
        this.onPanel1CompleteListener = listener;
    }

    public PanelBanVe1Controller(PanelBanVe1 p1, BookingSession session) {
        this.p1 = p1;
        this.bookingSession = session;

        this.buoc1Controller = new PanelBuoc1Controller(p1.getPanelBuoc1());
        
        // PanelBuoc2Controller cần 5 panel con [cite: 68]
        p2 = p1.getPanelBuoc2();
        this.buoc2Controller = new PanelBuoc2Controller(
            p2.getPanelChieuLabel(), 
            p2.getPanelChuyenTau(), 
            p2.getPanelDoanTau(), 
            p2.getPanelSoDoCho(), 
            p2.getPanelGioVe()
        );

        // 2. Tiêm (inject) BookingSession vào các controller con cần nó
        this.buoc2Controller.setBookingSession(this.bookingSession);
        // this.buoc3Controller.setBookingSession(this.bookingSession);

        // 3. Kết nối logic (Mediator Pattern)
        initMediatorLogic();
    }

    private void initMediatorLogic() {
        
        // Lắng nghe sự kiện từ Buoc1
        this.buoc1Controller.setSearchListener(new SearchListener() {
            @Override
            public void onSearchSuccess(List<Chuyen> results, SearchCriteria criteria) {
                // 1. Cập nhật BookingSession (việc mà Buoc1 đã làm )
                bookingSession.setOutboundCriteria(criteria);
                bookingSession.setOutboundResults(results);

                // 2. Kích hoạt Panel Buoc2
                p1.setBuoc2Enabled(true);
                // 3. Vô hiệu hóa Buoc3 (phòng trường hợp tìm lại)
                p1.setBuoc3Enabled(false);
                // 4. "Đẩy" dữ liệu vào Buoc2 để hiển thị
                buoc2Controller.displayChuyenList(criteria, results, 0); // 0 = chiều đi
            }

            @Override
            public void onSearchFailure() {
                // 1. Cập nhật BookingSession
                bookingSession.setOutboundResults(null);                
                // 2. Vô hiệu hóa Buoc2 và Buoc3
                p1.setBuoc2Enabled(false);
                p1.setBuoc3Enabled(false);
            }
        });

     // Lắng nghe sự kiện chọn ghế từ Buoc2
        this.buoc2Controller.addSeatSelectedListener(new SeatSelectedListener() {
            @Override
            public void onSeatSelected(VeSession ticket) {
                // (Sau này bạn cần logic chuyển đổi Ve -> SelectedTicket)
                // bookingSession.addTicketForTrip(0, convertedTicket); 
                
                // Kích hoạt Buoc3
                p1.setBuoc3Enabled(true);
                
                // (Tạm thời) Khi chọn ghế xong thì coi như xong
                // Lý tưởng nhất: Bạn nên lắng nghe 1 sự kiện
                // "onInfoComplete" từ PanelBuoc3Controller
                if (onPanel1CompleteListener != null) {
                    // onPanel1CompleteListener.run(); // Bỏ comment dòng này để test
                }
            }
        });
        
        // (Sau này) Bạn sẽ lắng nghe Buoc3
        // this.buoc3Controller.addInfoCompleteListener(() -> {
        //     if (onPanel1CompleteListener != null) {
        //         onPanel1CompleteListener.run(); // Gọi ở đây là đúng nhất
        //     }
        // });
    }
}