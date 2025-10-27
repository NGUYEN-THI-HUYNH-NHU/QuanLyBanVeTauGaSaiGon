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

import javax.swing.JOptionPane;

import entity.Chuyen;
import gui.application.form.banVe.PanelBuoc1Controller.SearchListener;
import gui.application.form.banVe.PanelBuoc2Controller.SeatSelectedListener;

public class BanVe1Controller {
    
    private final PanelBanVe1 view;
	private PanelBuoc2 p2;
	private PanelBuoc3 p3;

    private final BookingSession bookingSession;
    // Các sub-controller
    private final PanelBuoc1Controller buoc1Controller;
    private final PanelBuoc2Controller buoc2Controller;
//    private final PanelBuoc3Controller buoc3Controller;
    // private final PanelBuoc3Controller buoc3Controller; // Sẽ thêm sau
    private Runnable onPanel1CompleteListener;
    
    public void addPanel1CompleteListener(Runnable listener) {
        this.onPanel1CompleteListener = listener;
    }

    public BanVe1Controller(PanelBanVe1 view, BookingSession session) {
        this.view = view;
        this.bookingSession = session;

        this.buoc1Controller = new PanelBuoc1Controller(view.getPanelBuoc1());
        
        // PanelBuoc2Controller cần 5 panel con [cite: 68]
        this.p2 = view.getPanelBuoc2();
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

        this.p3 = view.getPanelBuoc3();
        
        // 3. Kết nối logic (Mediator Pattern)
        initMediatorLogic();
    }

    private void initMediatorLogic() {
        
        // Lắng nghe sự kiện từ Buoc1
        this.buoc1Controller.addSearchListener(new SearchListener() {
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

     // Lắng nghe sự kiện chọn ghế từ Buoc2
        this.buoc2Controller.addSeatSelectedListener(new SeatSelectedListener() {
            @Override
            public void onSeatSelected(VeSession ticket) {
                // (Sau này bạn cần logic chuyển đổi Ve -> SelectedTicket)
                // bookingSession.addTicketForTrip(0, convertedTicket); 
                
                // Kích hoạt Buoc3
                view.setBuoc3Enabled(true);
            }

			@Override
			public void onMuaVeClicked() {
				view.setBuoc2Enabled(false);
		        // hiển thị bước 3
		        view.setBuoc3Enabled(true);        // make step 3 visible in UI
		        p3.initFromBookingSession(bookingSession, buoc2Controller.getCurrentTripIndex());
		        // attach confirm handler once or each time
		        p3.getConfirmButton().addActionListener(ev -> {
		            if (!p3.validateRows()) {
		                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đầy đủ cho từng hành khách.");
		                return;
		            }
		            List<PassengerRow> rows = p3.getPassengerRows();
		            // add VeSession to bookingSession and/or attach passenger info
		            for (PassengerRow r : rows) {
		                VeSession v = r.getVeSession();
		                // optionally attach passenger info to v if model supports
		                bookingSession.addTicketForTrip(buoc2Controller.getCurrentTripIndex(), v);
		            }
		            // move wizard / continue flow
		            if (onPanel1CompleteListener != null) onPanel1CompleteListener.run();
		        });

		        p3.getCancelButton().addActionListener(ev -> {
		            // if cancel -> hide buoc3 or clear selection
		            view.setBuoc3Enabled(false);
		        });
		    }
        });
        
//        // Lắng nghe sự kiện chọn ghế từ Buoc3
//         this.buoc3Controller.addInfoCompleteListener(() -> {
//             if (onPanel1CompleteListener != null) {
//                 onPanel1CompleteListener.run(); // Gọi ở đây là đúng nhất
//             }
//         });
    }
}