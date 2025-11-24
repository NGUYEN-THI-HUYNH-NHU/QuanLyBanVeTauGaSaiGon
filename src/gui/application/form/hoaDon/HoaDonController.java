package gui.application.form.hoaDon;

/*
 * @(#) HoaDonController.java  1.0  [2:58:25 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bus.HoaDon_BUS;
import entity.HoaDon;
import gui.application.AuthService;
import gui.tuyChinh.FlexibleTableResizer;

public class HoaDonController {
	private PanelQuanLyHoaDon view;
	private final HoaDon_BUS hoaDonBUS;

	public HoaDonController(PanelQuanLyHoaDon view) {
		this.view = view;
		this.hoaDonBUS = new HoaDon_BUS();
		loadAllHoaDon();

		FlexibleTableResizer.resize(this.view.getTable(), List.of(HoaDonTableModel.COL_HOA_DON_ID,
				HoaDonTableModel.COL_TEN_KHACH_HANG, HoaDonTableModel.COL_KHACH_HANG_ID));

		initController();
	}

	private void initController() {
		// 1. Sự kiện nút Lọc
		view.getBtnLoc().addActionListener(e -> handleLoc());

		// 2. Sự kiện nút Reset
		view.getBtnReset().addActionListener(e -> handleReset());

		// 1. Gán Renderer cho cột Button
		view.getTable().getColumnModel().getColumn(HoaDonTableModel.COL_XEM)
				.setCellRenderer(new HoaDonTableButtonRenderer());
		view.getTable().getColumnModel().getColumn(HoaDonTableModel.COL_IN)
				.setCellRenderer(new HoaDonTableButtonRenderer());

		// 2. Xử lý sự kiện click trên Table (Thay vì dùng CellEditor)
		view.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int column = view.getTable().getColumnModel().getColumnIndexAtX(e.getX());
				int row = e.getY() / view.getTable().getRowHeight();

				if (row < 0 || row >= view.getTable().getRowCount() || column < 0
						|| column >= view.getTable().getColumnCount()) {
					return;
				}

				// Lấy đối tượng Hóa đơn tại dòng click
				HoaDon selectedHoaDon = view.getTableModel().getRow(row);

				if (column == HoaDonTableModel.COL_XEM) {
					handleXemChiTiet(selectedHoaDon);
				} else if (column == HoaDonTableModel.COL_IN) {
					handleInHoaDon(selectedHoaDon);
				}
			}
		});

		// 3. Sự kiện nút Tra Cứu
		view.getBtnTraCuu().addActionListener(e -> handleTraCuu());

		// 4. Auto-suggest cho txtKhachHangSuggest
		setupAutoSuggest();
	}

	// Auto-sugest hoTen/soDienThoai/soGiayTo/khachHangID
	private void setupAutoSuggest() {
		// TODO Auto-generated method stub

	}

	// Xử lý khi bấm nút Làm mới (Reset)
	private void handleReset() {
		// 1. Xóa trắng / Set về mặc định các trường nhập liệu
		view.getCboLoaiHoaDon().setSelectedIndex(0);
		view.getTxtKhachHangSuggest().setText("");
		view.getCboHinhThucTT().setSelectedIndex(0);

		// Reset ngày về hiện tại (hoặc null tùy logic của bạn)
		view.getDateChooserTuNgay().setDate(new Date());
		view.getDateChooserDenNgay().setDate(new Date());

		// 2. Load lại toàn bộ danh sách (như lúc mới mở)
		System.out.println("Đã reset bộ lọc. Load lại toàn bộ danh sách.");

		// TODO: Gọi hàm load tất cả
		loadAllHoaDon();
		handleLoc(); // Hoặc gọi lại hàm lọc để nó lọc theo tiêu chí mặc định (lấy hết)
	}

	/**
	 * 
	 */
	private void loadAllHoaDon() {
		this.view.getTableModel()
				.setRows(hoaDonBUS.layCacHoaDonTheoNhanVienID(AuthService.getInstance().getCurrentUser()));
	}

	// Xử lý khi bấm nút Lọc
	private void handleLoc() {
		// Lấy dữ liệu từ View
		String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
		String khachHang = view.getTxtKhachHangSuggest().getText().trim();
		Date tuNgay = view.getDateChooserTuNgay().getDate();
		Date denNgay = view.getDateChooserDenNgay().getDate();
		String hinhThucTT = (String) view.getCboHinhThucTT().getSelectedItem();

		// Kiểm tra logic ngày tháng (Validation)
		if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
			JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		System.out.println("Đang lọc với: " + loaiHD + " | " + khachHang + " | " + hinhThucTT);

		// TODO: Gọi hàm BUS để lọc dữ liệu
		// List<HoaDon> results = hoaDonBUS.filter(loaiHD, khachHang, tuNgay, denNgay,
		// hinhThucTT);
		// view.getTableModel().setRows(results);
	}

	private void handleTraCuu() {
		// Lấy dữ liệu từ View
		String keyword = view.getTxtTuKhoa().getText();
		String type = (String) view.getCboLoaiTimKiem().getSelectedItem();
		String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
		// ... lấy các tiêu chí khác

		// Gọi BUS
//		 List<HoaDon> result = hoaDonBUS.searchAndFilter(keyword, type, loaiHD, ...);
//		 view.getTableModel().setRows(result);

		System.out.println("Đang tra cứu: " + keyword);
	}

	private void handleXemChiTiet(HoaDon hd) {
		// Tạo JDialog Modal
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view),
				"Chi tiết hóa đơn: " + hd.getHoaDonID(), true);
		dialog.setSize(600, 400);
		dialog.setLocationRelativeTo(view);

		// Gọi BUS lấy chi tiết
		// List<HoaDonChiTiet> listCT = bus.getChiTietByHoaDonID(hd.getHoaDonID());

		// Tạo UI hiển thị chi tiết (Có thể tách ra class PanelChiTietHoaDon riêng)
		JPanel pnlDetail = new JPanel();
		pnlDetail.add(new JLabel("Đang xem chi tiết hóa đơn của khách: " + hd.getKhachHang().getHoTen()));
		// Thêm JTable hiển thị listCT vào đây...

		dialog.add(pnlDetail);
		dialog.setVisible(true);
	}

	private void handleInHoaDon(HoaDon hd) {
		int confirm = JOptionPane.showConfirmDialog(view, "Bạn có muốn in hóa đơn " + hd.getHoaDonID() + " không?",
				"Xác nhận in", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// Gọi hàm in (JasperReport hoặc PDF)
			System.out.println("Đang in hóa đơn: " + hd.getHoaDonID());
		}
	}
}