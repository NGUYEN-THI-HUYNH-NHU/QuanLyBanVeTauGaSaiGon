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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import bus.HoaDon_BUS;
import bus.KhachHang_BUS;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import gui.application.AuthService;
import gui.tuyChinh.FlexibleTableResizer;

public class HoaDonController {
	private PanelQuanLyHoaDon view;
	private JPopupMenu traCuuSuggestionPopup;
	private JPopupMenu khachHangsuggestionPopup;

	private final HoaDon_BUS hoaDonBUS;
	private final KhachHang_BUS khachHangBUS;

	private final NhanVien nhanVien = AuthService.getInstance().getCurrentUser();
	// Biến tạm để lưu khách hàng đang được chọn từ gợi ý (để dùng khi bấm nút Lọc)
	private KhachHang selectedKhachHang = null;

	public HoaDonController(PanelQuanLyHoaDon view) {
		this.view = view;
		this.traCuuSuggestionPopup = new JPopupMenu();
		this.khachHangsuggestionPopup = new JPopupMenu();

		this.hoaDonBUS = new HoaDon_BUS();
		this.khachHangBUS = new KhachHang_BUS();
		loadAllHoaDon();

		FlexibleTableResizer.resize(this.view.getTable(), List.of(HoaDonTableModel.COL_HOA_DON_ID,
				HoaDonTableModel.COL_TEN_KHACH_HANG, HoaDonTableModel.COL_KHACH_HANG_ID));

		init();
	}

	private void init() {
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

		// 4. Auto-suggest cho txtTuKhoa va txtKhachHangSuggest
		setupTraCuuSuggestion();
		setupKhachHangSuggestion();
	}

	private void handleTraCuu() {
		// 1. Lấy dữ liệu
		String keyword = view.getTxtTuKhoa().getText().trim();
		String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

		// 2. Gọi BUS/DAO
		List<HoaDon> result = hoaDonBUS.layHoaDonTheoKeyWord(this.nhanVien, keyword, type);

		// 3. Update Table
		view.getTableModel().setRows(result);

		if (result.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			System.out.println("Tìm thấy " + result.size() + " kết quả cho: " + keyword);
		}
	}

	private void setupTraCuuSuggestion() {
		JTextField txtSearch = view.getTxtTuKhoa();
		JComboBox<String> cboType = view.getCboLoaiTimKiem();

		// 1. Reset text khi đổi loại tìm kiếm (để tránh user tìm ID hóa đơn bằng mã KH)
		cboType.addActionListener(e -> {
			txtSearch.setText("");
			traCuuSuggestionPopup.setVisible(false);
		});

		// 2. Lắng nghe sự kiện gõ phím
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				showTraCuuSuggestions();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				showTraCuuSuggestions();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				showTraCuuSuggestions();
			}
		});

		// 3. Ẩn popup khi click ra ngoài
		txtSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtSearch.getText().trim().isEmpty()) {
					traCuuSuggestionPopup.setVisible(false);
				}
			}
		});
	}

	private void showTraCuuSuggestions() {
		String keyword = view.getTxtTuKhoa().getText().trim();
		String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

		traCuuSuggestionPopup.setVisible(false);
		traCuuSuggestionPopup.removeAll();

		if (keyword.length() < 1) {
			return;
		}

		List<String> suggestions = new ArrayList<>();

		// 4. Lấy danh sách gợi ý dựa trên loại đang chọn
		if ("Mã hóa đơn".equals(type)) {
			suggestions = hoaDonBUS.layTop10HoaDonID(keyword);
		} else if ("Mã khách hàng".equals(type)) {
			suggestions = hoaDonBUS.layTop10KhachHangID(keyword);
		} else if ("Mã giao dịch".equals(type)) {
			suggestions = hoaDonBUS.layTop10MaGD(keyword);
		}

		// 5. Hiển thị Popup
		if (!suggestions.isEmpty()) {
			for (String s : suggestions) {
				JMenuItem item = new JMenuItem(s);
				// Highlight icon khác nhau cho đẹp
				if ("Mã hóa đơn".equals(type)) {
					item.setIcon(new FlatSVGIcon("gui/icon/svg/order.svg", 0.6f));
				} else if ("Mã khách hàng".equals(type)) {
					item.setIcon(new FlatSVGIcon("gui/icon/svg/person.svg", 0.6f));
				} else if ("Mã giao dịch".equals(type)) {
					item.setIcon(new FlatSVGIcon("gui/icon/svg/payment.svg", 0.6f));
				}

				item.addActionListener(e -> {
					view.getTxtTuKhoa().setText(s);
					traCuuSuggestionPopup.setVisible(false);
				});
				traCuuSuggestionPopup.add(item);
			}
			// Hiển thị ngay dưới TextField
			traCuuSuggestionPopup.show(view.getTxtTuKhoa(), 0, view.getTxtTuKhoa().getHeight());
			view.getTxtTuKhoa().requestFocus(); // Giữ focus để gõ tiếp
		}
	}

	// Gợi ý kiểu google search trong phạm vi hoTen/soDienThoai/soGiayTo/khachHangID
	private void setupKhachHangSuggestion() {
		JTextField txtSuggest = view.getTxtKhachHangSuggest();

		// 1. Lắng nghe sự kiện thay đổi text
		txtSuggest.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				showKhachHangSuggestions();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				showKhachHangSuggestions();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				showKhachHangSuggestions();
			}
		});

		// 2. Ẩn popup khi click ra ngoài hoặc click vào textfield mà không gõ
		txtSuggest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtSuggest.getText().trim().isEmpty()) {
					khachHangsuggestionPopup.setVisible(false);
				}
			}
		});
	}

	private void showKhachHangSuggestions() {
		String keyword = view.getTxtKhachHangSuggest().getText().trim();
		khachHangsuggestionPopup.setVisible(false); // Ẩn cái cũ đi
		khachHangsuggestionPopup.removeAll(); // Xóa item cũ

		if (keyword.length() < 1) {
			selectedKhachHang = null;
			return;
		}

		// Lấy danh sách gợi ý
		List<KhachHang> listSuggest = khachHangBUS.layGoiYKhachHangChoHoaDon(keyword);

		if (!listSuggest.isEmpty()) {
			for (KhachHang kh : listSuggest) {
				// Tạo text hiển thị: "Tên - SĐT - CCCD - ID"
				String displayText = String.format(
						"<html><b>%s</b> - %s <br><i style='color:gray; font-size:9px'>%s - %s</i></html>",
						kh.getHoTen(), (kh.getSoDienThoai() == null ? "N/A" : kh.getSoDienThoai()), kh.getSoGiayTo(),
						kh.getKhachHangID());

				JMenuItem item = new JMenuItem(displayText);

				// Sự kiện khi chọn 1 dòng gợi ý
				item.addActionListener(e -> {
					// 1. Điền tên vào TextField
					view.getTxtKhachHangSuggest().setText(kh.getHoTen());
					// 2. Lưu đối tượng được chọn để xử lý lọc chính xác hơn
					selectedKhachHang = kh;
					// 3. Ẩn popup
					khachHangsuggestionPopup.setVisible(false);
				});
				khachHangsuggestionPopup.add(item);
			}

			// Hiển thị Popup ngay dưới TextField
			khachHangsuggestionPopup.show(view.getTxtKhachHangSuggest(), 0, view.getTxtKhachHangSuggest().getHeight());
			// Focus lại vào textfield để user gõ
			view.getTxtKhachHangSuggest().requestFocus();
		}
	}

	// Getter để lấy ID khách hàng đã chọn (Dùng cho hàm Lọc ở câu trước)
	public String getSelectedKhachHangID() {
		return (selectedKhachHang != null) ? selectedKhachHang.getKhachHangID() : null;
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
		loadAllHoaDon();
	}

	private void loadAllHoaDon() {
		this.view.getTableModel().setRows(hoaDonBUS.layCacHoaDonTheoNhanVienID(this.nhanVien));
	}

	// Xử lý khi bấm nút Lọc
	private void handleLoc() {
		// 1. Lấy dữ liệu từ View
		String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
		String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
		Date tuNgay = view.getDateChooserTuNgay().getDate();
		Date denNgay = view.getDateChooserDenNgay().getDate();
		String hinhThucTT = (String) view.getCboHinhThucTT().getSelectedItem();

		String searchKeyword = null; // Dùng tìm theo tên/sđt (LIKE)
		String searchID = null; // Dùng tìm chính xác theo ID (=)

		// 2. Logic thông minh
		if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen())) {
			// Nếu người dùng chọn từ gợi ý và không sửa tên -> Tìm chính xác theo ID
			searchID = selectedKhachHang.getKhachHangID();
		} else {
			// Nếu tự gõ hoặc đã sửa tên -> Tìm tương đối theo từ khóa
			searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;
			selectedKhachHang = null; // Reset biến nhớ để tránh nhầm lẫn lần sau
		}

		// 3. Validate Ngày tháng
		if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
			JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		System.out.println(String.format("Filter: Loai=%s | Keyword=%s | ID=%s | Ngay=%s-%s", loaiHD, searchKeyword,
				searchID, tuNgay, denNgay));

		// 4. Gọi BUS lọc theo tiêu chí
		List<HoaDon> results = hoaDonBUS.locHoaDonTheoCacTieuChi(this.nhanVien, loaiHD, searchKeyword, searchID, tuNgay,
				denNgay, hinhThucTT);

		// 5. Cập nhật UI và thông báo kết quả
		view.getTableModel().setRows(results);

		if (results.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn nào phù hợp!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			view.getTable().scrollRectToVisible(view.getTable().getCellRect(0, 0, true));
		}
	}

	private void handleXemChiTiet(HoaDon hd) {
		// Tạo JDialog Modal
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view),
				"Chi tiết hóa đơn: " + hd.getHoaDonID(), true);
		dialog.setSize(600, 700);
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
			System.out.println("Đang in hóa đơn: " + hd.getHoaDonID());
		}
	}
}