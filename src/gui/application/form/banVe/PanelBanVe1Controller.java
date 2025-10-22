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
import entity.Chuyen;
import gui.application.form.banVe.PanelBuoc1Controller.SearchListener;

public class PanelBanVe1Controller {
    
    private final PanelBanVe1 view;
    private final BookingSession bookingSession;

    // Các sub-controller
    private final PanelBuoc1Controller buoc1Controller;
    private final PanelBuoc2Controller buoc2Controller;
    // private final PanelBuoc3Controller buoc3Controller; // Sẽ thêm sau

    public PanelBanVe1Controller(PanelBanVe1 view, BookingSession session) {
        this.view = view;
        this.bookingSession = session;

        this.buoc1Controller = new PanelBuoc1Controller(view.getPanelBuoc1());
        
        // PanelBuoc2Controller cần 5 panel con [cite: 68]
        PanelBuoc2 p2 = view.getPanelBuoc2();
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
                view.setBuoc2Enabled(true);
                
                // 3. Vô hiệu hóa Buoc3 (phòng trường hợp tìm lại)
                view.setBuoc3Enabled(false);

                // 4. "Đẩy" dữ liệu vào Buoc2 để hiển thị
                buoc2Controller.displayChuyenList(criteria, results, 0); // 0 = chiều đi
            }

            @Override
            public void onSearchFailure() {
                // 1. Cập nhật BookingSession
                bookingSession.setOutboundResults(null);
                
                // 2. Vô hiệu hóa Buoc2 và Buoc3
                view.setBuoc2Enabled(false);
                view.setBuoc3Enabled(false);
            }
        });

        // (Tương tự, sau này bạn sẽ lắng nghe sự kiện từ Buoc2)
        // Ví dụ: khi Buoc2 chọn ghế xong (onSeatClicked)
        // this.buoc2Controller.addSeatSelectedListener( ticket -> {
        //     // Cập nhật session 
        //     bookingSession.addTicketForTrip(0, ticket); 
        //     // Kích hoạt Buoc3
        //     view.setBuoc3Enabled(true);
        // });
    }
}