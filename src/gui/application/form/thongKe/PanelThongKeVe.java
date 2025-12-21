package gui.application.form.thongKe;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;

import com.toedter.calendar.JDateChooser;

import dao.ThongKeVe_DAO;
import dao.ThongKeVe_DAO.ThongKeVeChiTietItem;

/**
 * Panel hiển thị thống kê vé theo thời gian. Phiên bản: Đồng bộ hoàn toàn với
 * PanelThongKeDoanhThu (Chart Flat, Autocomplete Ga, Khóa Ga).
 */
public class PanelThongKeVe extends JPanel {

	private final ThongKeVe_DAO thongKeVeDAO;
	private static final Logger LOGGER = Logger.getLogger(PanelThongKeVe.class.getName());

	private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
	private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");

	// ====== Lọc thời gian ======
	private final JComboBox<String> cbLoaiThoiGian;
	private final JDateChooser tuNgay;
	private final JDateChooser denNgay;
	private final JComboBox<String> cbTuThang;
	private final JComboBox<String> cbDenThang;
	private final JComboBox<Integer> cbTuNamThang;
	private final JComboBox<Integer> cbDenNamThang;
	private final JComboBox<Integer> cbTuNam;
	private final JComboBox<Integer> cbDenNam;
	private final JPanel filterSwitcher;
	private static final String CARD_TATCA = "CARD_TATCA";
	private static final String CARD_NGAY = "CARD_NGAY";
	private static final String CARD_THANG = "CARD_THANG";
	private static final String CARD_NAM = "CARD_NAM";

	// ====== Lọc tuyến, nhân viên, loại vé ======
	private final JComboBox<String> cbLoaiTuyen;
	private final JComboBox<String> cbGaDi;
	private final JComboBox<String> cbGaDen;
	private final JComboBox<String> cbNhanVien;
	private final JComboBox<String> cbLoaiVe;

	private final JButton btnTimKiem;
	private final JButton btnXoaBoLoc;

	private final Map<String, String> nhanVienMap = new HashMap<>();
	private final Map<String, String> hangToaMap = new HashMap<>();

	// Danh sách gốc để phục vụ tìm kiếm (Autocomplete)
	private List<String> danhSachGaGoc;

	// ====== Card thống kê ======
	private final JLabel lblTongSoVeBanValue;
	private final JLabel lblTongVeConHieuLucValue;
	private final JLabel lblTongVeDaDungValue;
	private final JLabel lblTongVeDaDoiValue;
	private final JLabel lblTongVeHoanValue;
	private final JLabel lblTongTienVeValue;

	// ====== Biểu đồ & Chi tiết ======
	private final JPanel chartPanelContainer;
	private final JTable tableChiTiet;
	private final DefaultTableModel chiTietTableModel;

	// ====== Thành phần Header Chi tiết ======
	private final JLabel lblChiTietTitle;
	private final JButton btnExportExcel;

	// 4 Label hiển thị thông tin lọc
	private JLabel lblInfoThoiGian;
	private JLabel lblInfoTuyen;
	private JLabel lblInfoNhanVien;
	private JLabel lblInfoLoaiVe;

	private static class ThongKeVeResult {
		int tongSoVeBan, tongVeConHieuLuc, tongVeDaDung, tongVeDaDoi, tongVeHoan;
		double tongTienVe;
		Map<String, ThongKeVeChiTietItem> thongKeVeChiTietTheoThoiGian;
	}

	public PanelThongKeVe() {
		this.thongKeVeDAO = new ThongKeVe_DAO();

		// --- Khởi tạo components lọc ---
		cbLoaiThoiGian = new JComboBox<>(new String[] { "Tất cả", "Theo ngày", "Theo tháng", "Theo năm" });
		tuNgay = new JDateChooser();
		denNgay = new JDateChooser();
		cbTuThang = new JComboBox<>();
		cbDenThang = new JComboBox<>();
		cbTuNamThang = new JComboBox<>();
		cbDenNamThang = new JComboBox<>();
		cbTuNam = new JComboBox<>();
		cbDenNam = new JComboBox<>();
		filterSwitcher = new JPanel(new CardLayout());

		cbLoaiTuyen = new JComboBox<>(new String[] { "Tất cả", "Theo Ga đi/đến" });
		cbGaDi = new JComboBox<>();
		cbGaDen = new JComboBox<>();
		cbNhanVien = new JComboBox<>();
		cbLoaiVe = new JComboBox<>();

		btnTimKiem = new JButton("Tìm kiếm");
		btnXoaBoLoc = new JButton("Xóa bộ lọc");
		btnXoaBoLoc.setFont(new Font("Arial", Font.BOLD, 13));
		btnXoaBoLoc.setBackground(new Color(108, 117, 125));
		btnXoaBoLoc.setForeground(Color.WHITE);
		btnXoaBoLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnXoaBoLoc.addActionListener(e -> xoaBoLoc());

		// --- Khởi tạo Cards Value ---
		lblTongSoVeBanValue = createValueLabel("...");
		lblTongVeConHieuLucValue = createValueLabel("...");
		lblTongVeDaDungValue = createValueLabel("...");
		lblTongVeDaDoiValue = createValueLabel("...");
		lblTongVeHoanValue = createValueLabel("...");
		lblTongTienVeValue = createValueLabel("...");

		// --- Layout Chính ---
		setLayout(new BorderLayout(0, 15));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						"THỐNG KÊ VÉ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
						new Font("Arial", Font.BOLD, 16), new Color(0, 110, 185)),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// ===== Khu vực NORTH: Bộ lọc và Cards =====
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setOpaque(false);

		JPanel filterPanel = buildFilterBar();
		filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPanel.add(filterPanel);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setForeground(new Color(180, 180, 180));
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		sep.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPanel.add(Box.createVerticalStrut(8));
		topPanel.add(sep);
		topPanel.add(Box.createVerticalStrut(8));

		JPanel infoWrapper = new JPanel(new GridLayout(2, 3, 15, 15));
		infoWrapper.setOpaque(false);
		infoWrapper.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		infoWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

		infoWrapper.add(createCard("Tổng số vé bán", lblTongSoVeBanValue, new Color(52, 152, 219)));
		infoWrapper.add(createCard("Vé còn hiệu lực", lblTongVeConHieuLucValue, new Color(46, 204, 113)));
		infoWrapper.add(createCard("Vé đã dùng", lblTongVeDaDungValue, new Color(155, 89, 182)));
		infoWrapper.add(createCard("Vé hoàn", lblTongVeHoanValue, new Color(231, 76, 60)));
		infoWrapper.add(createCard("Vé đổi", lblTongVeDaDoiValue, new Color(243, 156, 18)));
		infoWrapper.add(createCard("Tổng tiền vé", lblTongTienVeValue, new Color(39, 174, 96)));

		topPanel.add(infoWrapper);
		add(topPanel, BorderLayout.NORTH);

		// ===== Tab Panel =====
		JTabbedPane tab = new JTabbedPane();
		tab.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
		chartPanelContainer = taoPanelTongQuan();

		String[] columnNames = { "STT", "Thời Gian", "Vé bán", "Vé còn hiệu lực", "Vé đã dùng", "Vé đổi", "Vé hoàn",
				"Tổng tiền vé (VNĐ)" };

		chiTietTableModel = new DefaultTableModel(columnNames, 0)
        {
			@Override
			public boolean isCellEditable(int row, int column) {

				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return switch (columnIndex) {
				case 0, 2, 3, 4, 5, 6 -> Integer.class;
				case 7 -> Double.class;
				default -> String.class;
				};
			}
		};


        tableChiTiet = new JTable(chiTietTableModel);

		// --- Tạo Panel Chi Tiết & Header ---

		// 1. Tiêu đề lớn
		lblChiTietTitle = new JLabel("Báo cáo thống kê chi tiết", JLabel.CENTER);
		lblChiTietTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 18));
		lblChiTietTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

		// 2. Panel Thông tin lọc (Header phụ)
		JPanel pnlFilterInfo = new JPanel(new GridLayout(2, 2, 20, 5));
		pnlFilterInfo.setOpaque(false);
		pnlFilterInfo.setBorder(new EmptyBorder(0, 50, 10, 50));

		Font fontInfo = new Font(getFont().getFontName(), Font.PLAIN, 14);
		lblInfoThoiGian = new JLabel("Thời gian: Tất cả");
		lblInfoThoiGian.setFont(fontInfo);
		lblInfoTuyen = new JLabel("Tuyến: Tất cả");
		lblInfoTuyen.setFont(fontInfo);
		lblInfoNhanVien = new JLabel("Nhân viên: Tất cả");
		lblInfoNhanVien.setFont(fontInfo);
		lblInfoLoaiVe = new JLabel("Loại vé: Tất cả");
		lblInfoLoaiVe.setFont(fontInfo);

		pnlFilterInfo.add(lblInfoThoiGian);
		pnlFilterInfo.add(lblInfoTuyen);
		pnlFilterInfo.add(lblInfoNhanVien);
		pnlFilterInfo.add(lblInfoLoaiVe);

		// Container chứa Title + Filter Info
		JPanel pnlTopContainer = new JPanel(new BorderLayout());
		pnlTopContainer.setOpaque(false);
		pnlTopContainer.add(lblChiTietTitle, BorderLayout.NORTH);
		pnlTopContainer.add(pnlFilterInfo, BorderLayout.CENTER);

		// 3. Nút Xuất Excel
		btnExportExcel = new JButton("Xuất Excel");
		btnExportExcel.setBackground(new Color(33, 115, 70));
		btnExportExcel.setForeground(Color.WHITE);
		btnExportExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnExportExcel.setEnabled(false);
		btnExportExcel.addActionListener(this::exportTableToExcel);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setOpaque(false);
		bottomPanel.add(btnExportExcel);

		// 4. Ghép vào Panel chính
		JPanel panelChiTietContainer = taoPanelChiTiet(pnlTopContainer, tableChiTiet, bottomPanel);

		tab.addTab("Tổng quan", chartPanelContainer);
		tab.addTab("Chi tiết", panelChiTietContainer);
		add(tab, BorderLayout.CENTER);

		// --- Load dữ liệu ---
		loadComboBoxesData();
		SwingUtilities.invokeLater(this::xuLyThongKe);
	}

	// ================== HÀM XỬ LÝ AUTOCOMPLETE ==================
	// Hàm này giúp biến JComboBox thành ô nhập liệu thông minh
	private void setupAutoComplete(final JComboBox<String> comboBox, final List<String> items) {
		final JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();

		// Khi gõ phím
		textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				SwingUtilities.invokeLater(() -> {
					String text = textfield.getText();
					// Không lọc khi dùng các phím điều hướng
					if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40) {
						return;
					}
					filterInfo(comboBox, text, items);
				});
			}
		});

		// Khi click chuột vào thì tự động bôi đen để dễ gõ
		textfield.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (comboBox.isEnabled()) {
					comboBox.setPopupVisible(true);
				}
			}
		});

		comboBox.setEditable(true);
	}

	private void filterInfo(JComboBox<String> comboBox, String enteredText, List<String> items) {
		if (!comboBox.isPopupVisible()) {
			comboBox.showPopup();
		}

		List<String> filterArray = items.stream().filter(p -> p.toLowerCase().contains(enteredText.toLowerCase()))
				.collect(Collectors.toList());

		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
		model.removeAllElements();

		for (String s : filterArray) {
			model.addElement(s);
		}

		JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();
		textfield.setText(enteredText);
	}

	// ================== HÀM XỬ LÝ LOGIC ==================
	private void xuLyThongKe() {
		String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
		if (loaiThoiGian == null) {
			loaiThoiGian = "Tất cả";
		}

		String loaiTuyen = (String) cbLoaiTuyen.getSelectedItem();
		String tenGaDi = (String) cbGaDi.getEditor().getItem(); // Lấy từ Editor
		String tenGaDen = (String) cbGaDen.getEditor().getItem();

		// Kiểm tra hợp lệ Ga đi / Ga đến
		if (!"Tất cả".equals(loaiTuyen)) {
			if (tenGaDi == null || tenGaDi.trim().isEmpty() || !danhSachGaGoc.contains(tenGaDi)) {
				JOptionPane.showMessageDialog(this, "Ga đi không hợp lệ hoặc không có trong danh sách!",
						"Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (tenGaDen == null || tenGaDen.trim().isEmpty() || !danhSachGaGoc.contains(tenGaDen)) {
				JOptionPane.showMessageDialog(this, "Ga đến không hợp lệ hoặc không có trong danh sách!",
						"Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		String selectedNhanVien = (String) cbNhanVien.getSelectedItem();
		String nhanVienID = (selectedNhanVien == null || "Tất cả".equals(selectedNhanVien)) ? null
				: nhanVienMap.get(selectedNhanVien);

		String selectedLoaiVe = (String) cbLoaiVe.getSelectedItem();
		String hangToaID = (selectedLoaiVe == null || "Tất cả".equals(selectedLoaiVe)) ? null
				: hangToaMap.get(selectedLoaiVe);

		LocalDate fromLocalDate, toLocalDate;
		String titleLoai, titleChart;
		String infoThoiGian;

		try {
			switch (loaiThoiGian) {
			case "Theo ngày" -> {
				Date utilFrom = tuNgay.getDate();
				Date utilTo = denNgay.getDate();
				if (!kiemTraKhoangNgayHopLe(utilFrom, utilTo)) {
					return;
				}
				fromLocalDate = utilFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				toLocalDate = utilTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				String strDate = String.format("%s - %s", fmtD(fromLocalDate), fmtD(toLocalDate));
				titleLoai = "ngày (" + strDate + ")";
				titleChart = "ngày";
				infoThoiGian = strDate;
			}
			case "Theo tháng" -> {
				int fm = cbTuThang.getSelectedIndex() + 1;
				int fy = nvl((Integer) cbTuNamThang.getSelectedItem(), LocalDate.now().getYear());
				int tm = cbDenThang.getSelectedIndex() + 1;
				int ty = nvl((Integer) cbDenNamThang.getSelectedItem(), LocalDate.now().getYear());
				fromLocalDate = LocalDate.of(fy, fm, 1);
				toLocalDate = LocalDate.of(ty, tm, LocalDate.of(ty, tm, 1).lengthOfMonth());
				if (toLocalDate.isBefore(fromLocalDate)) {
					JOptionPane.showMessageDialog(this, "Thời điểm kết thúc phải ≥ thời điểm bắt đầu.", "Lỗi",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				String strMonth = String.format("%d/%d - %d/%d", fm, fy, tm, ty);
				titleLoai = "tháng (" + strMonth + ")";
				titleChart = "tháng";
				infoThoiGian = strMonth;
			}
			case "Theo năm" -> {
				int sy = nvl((Integer) cbTuNam.getSelectedItem(), LocalDate.now().getYear());
				int ey = nvl((Integer) cbDenNam.getSelectedItem(), LocalDate.now().getYear());
				fromLocalDate = LocalDate.of(sy, 1, 1);
				toLocalDate = LocalDate.of(ey, 12, 31);
				if (toLocalDate.isBefore(fromLocalDate)) {
					JOptionPane.showMessageDialog(this, "Năm kết thúc phải ≥ năm bắt đầu.", "Lỗi",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				String strYear = String.format("%d - %d", sy, ey);
				titleLoai = "năm (" + strYear + ")";
				titleChart = "năm";
				infoThoiGian = strYear;
			}
			default -> {
				fromLocalDate = LocalDate.of(2000, 1, 1);
				toLocalDate = LocalDate.now().plusDays(1);
				titleLoai = "tất cả";
				titleChart = "Tất cả (theo năm)";
				loaiThoiGian = "Theo năm";
				infoThoiGian = "Tất cả";
			}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi thời gian: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// --- CẬP NHẬT HEADER LỌC ---
		lblInfoThoiGian.setText("Thời gian: " + infoThoiGian);

		String infoTuyen = "Tất cả";
		if (!"Tất cả".equals(loaiTuyen)) {
			infoTuyen = tenGaDi + " -> " + tenGaDen;
		}
		lblInfoTuyen.setText("Tuyến: " + infoTuyen);

		String infoNV = (selectedNhanVien == null) ? "Tất cả" : selectedNhanVien;
		lblInfoNhanVien.setText("Nhân viên: " + infoNV);

		String infoLoaiVe = (selectedLoaiVe == null) ? "Tất cả" : selectedLoaiVe;
		lblInfoLoaiVe.setText("Loại vé: " + infoLoaiVe);

		// --- CẬP NHẬT TRẠNG THÁI UI ---
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lblTongSoVeBanValue.setText("...");
		lblTongVeConHieuLucValue.setText("...");
		lblTongVeDaDungValue.setText("...");
		lblTongVeDaDoiValue.setText("...");
		lblTongVeHoanValue.setText("...");
		lblTongTienVeValue.setText("...");
		capNhatChartRong("🔄 Đang tải dữ liệu...");
		chiTietTableModel.setRowCount(0);

		final LocalDate finalFrom = fromLocalDate;
		final LocalDate finalTo = toLocalDate;
		final String finalDaoLoai = loaiThoiGian;
		final String finalTitleLoai = titleLoai;
		final String finalChartTitle = titleChart;
		final String finalLoaiTuyen = loaiTuyen;
		final String finalTenGaDi = tenGaDi;
		final String finalTenGaDen = tenGaDen;
		final String finalNhanVienID = nhanVienID;
		final String finalHangToaID = hangToaID;

		SwingWorker<ThongKeVeResult, Void> worker = new SwingWorker<>() {
			@Override
			protected ThongKeVeResult doInBackground() throws Exception {
				ThongKeVeResult result = new ThongKeVeResult();
				result.thongKeVeChiTietTheoThoiGian = thongKeVeDAO.getThongKeVeChiTietTheoThoiGian(finalDaoLoai,
						finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID,
						finalHangToaID, null);

				result.tongSoVeBan = thongKeVeDAO.getTongSoVeBanTrongKhoang(finalFrom, finalTo, finalLoaiTuyen,
						finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				result.tongVeConHieuLuc = thongKeVeDAO.getTongVeConHieuLucTrongKhoang(finalFrom, finalTo,
						finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				result.tongVeDaDung = thongKeVeDAO.getTongVeDaDungTrongKhoang(finalFrom, finalTo, finalLoaiTuyen,
						finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				result.tongVeDaDoi = thongKeVeDAO.getTongVeDaDoiTrongKhoang(finalFrom, finalTo, finalLoaiTuyen,
						finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				result.tongVeHoan = thongKeVeDAO.getTongVeHoanTrongKhoang(finalFrom, finalTo, finalLoaiTuyen,
						finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				result.tongTienVe = thongKeVeDAO.getTongTienVeTrongKhoang(finalFrom, finalTo, finalLoaiTuyen,
						finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);
				return result;
			}

			@Override
			protected void done() {
				setCursor(Cursor.getDefaultCursor());
				try {
					ThongKeVeResult result = get();
					lblTongSoVeBanValue.setText(integerFormatter.format(result.tongSoVeBan));
					lblTongVeConHieuLucValue.setText(integerFormatter.format(result.tongVeConHieuLuc));
					lblTongVeDaDungValue.setText(integerFormatter.format(result.tongVeDaDung));
					lblTongVeDaDoiValue.setText(integerFormatter.format(result.tongVeDaDoi));
					lblTongVeHoanValue.setText(integerFormatter.format(result.tongVeHoan));
					lblTongTienVeValue.setText(currencyFormatter.format(result.tongTienVe));

					capNhatChartVaTable(result.thongKeVeChiTietTheoThoiGian, finalChartTitle, finalTitleLoai);
				} catch (Exception ex) {
					handleLoadingError(ex, "Lỗi cập nhật UI");
				}
			}
		};
		worker.execute();
	}

	private void capNhatChartVaTable(Map<String, ThongKeVeChiTietItem> data, String chartTitle, String reportTitle) {
		lblChiTietTitle.setText("Báo cáo thống kê vé chi tiết theo " + reportTitle.toLowerCase());

		if (data == null || data.isEmpty()) {
			capNhatChartRong("📉 Không có dữ liệu vé trong khoảng đã chọn");
			capNhatBangRong();
			btnExportExcel.setEnabled(false);
			return;
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final String seriesVeBan = "Vé bán";
		final String seriesVeHoanDoi = "Vé hoàn/đổi";
		data.forEach((thoiGian, item) -> {
			dataset.addValue(item.tongSoVeBan, seriesVeBan, thoiGian);
			dataset.addValue(item.tongVeHoan + item.tongVeDaDoi, seriesVeHoanDoi, thoiGian);
		});

		JFreeChart barChart = ChartFactory.createBarChart("Số lượng vé theo " + chartTitle.toLowerCase(), "Thời gian",
				"Số lượng vé", dataset, PlotOrientation.VERTICAL, true, true, false);

		// --- TÙY CHỈNH STYLE (FLAT DESIGN) ---

		// 1. Plot
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setOutlineVisible(false); // Bỏ viền
		plot.setInsets(new RectangleInsets(10, 5, 5, 10));

		// 2. Trục Y
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance(new Locale("vi", "VN")));
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// 3. Renderer (Thanh biểu đồ)
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(46, 204, 113)); // Xanh (Vé bán)
		renderer.setSeriesPaint(1, new Color(231, 76, 60)); // Đỏ (Vé hoàn/đổi)
		renderer.setBarPainter(new StandardBarPainter()); // Phẳng
		renderer.setDrawBarOutline(false);
		renderer.setItemMargin(0.2);
		renderer.setShadowVisible(false);
		renderer.setMaximumBarWidth(0.08);

		// 4. Trục X
		CategoryAxis domainAxis = plot.getDomainAxis();
		if (!chartTitle.contains("năm") && dataset.getColumnCount() > 8) {
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		} else {
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		}
		domainAxis.setLowerMargin(0.02);
		domainAxis.setUpperMargin(0.02);

		// 5. Legend & Title
		if (barChart.getLegend() != null) {
			barChart.getLegend().setFrame(BlockBorder.NONE);
		}
		barChart.setBackgroundPaint(Color.WHITE);
		barChart.getTitle().setFont(new Font(getFont().getFontName(), Font.BOLD, 16));

		ChartPanel chartDisplayPanel = new ChartPanel(barChart);
		chartDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		chartDisplayPanel.setBackground(Color.WHITE);

		chartPanelContainer.removeAll();
		chartPanelContainer.add(chartDisplayPanel, BorderLayout.CENTER);
		chartPanelContainer.revalidate();
		chartPanelContainer.repaint();

		// --- Fill Table ---
		chiTietTableModel.setRowCount(0);
		int stt = 1;
		for (Map.Entry<String, ThongKeVeChiTietItem> entry : data.entrySet()) {
			String thoiGian = entry.getKey();
			ThongKeVeChiTietItem item = entry.getValue();
			chiTietTableModel.addRow(new Object[] { stt++, thoiGian, item.tongSoVeBan, item.tongVeConHieuLuc,
					item.tongVeDaDung, item.tongVeDaDoi, item.tongVeHoan, item.tongTienVe });
		}
		btnExportExcel.setEnabled(true);
	}

	// ================== HÀM XUẤT EXCEL (UPDATED) ==================
	private void exportTableToExcel(ActionEvent e) {
		if (chiTietTableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Lưu báo cáo Excel");
		String defaultFileName = "BaoCaoThongKeVe_" + LocalDate.now() + ".xlsx";
		fileChooser.setSelectedFile(new File(defaultFileName));
		fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

		if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File fileToSave = fileChooser.getSelectedFile();
		if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
			fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
		}
		final File finalFileToSave = fileToSave;

		try (XSSFWorkbook workbook = new XSSFWorkbook();
				FileOutputStream outputStream = new FileOutputStream(finalFileToSave)) {
			Sheet sheet = workbook.createSheet("ChiTietThongKeVe");

			// --- STYLES ---
			CellStyle titleStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 16);
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);

			CellStyle infoStyle = workbook.createCellStyle();
			infoStyle.setAlignment(HorizontalAlignment.LEFT);
			org.apache.poi.ss.usermodel.Font infoFont = workbook.createFont();
			infoFont.setItalic(true);
			infoStyle.setFont(infoFont);

			CellStyle headerCellStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);

			CellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);

			CellStyle currencyCellStyle = workbook.createCellStyle();
			currencyCellStyle.cloneStyleFrom(dataStyle);
			currencyCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0 ₫"));

			CellStyle centerStyle = workbook.createCellStyle();
			centerStyle.cloneStyleFrom(dataStyle);
			centerStyle.setAlignment(HorizontalAlignment.CENTER);

			// --- 1. TIÊU ĐỀ ---
			Row titleRow = sheet.createRow(0);
			titleRow.createCell(0).setCellValue(lblChiTietTitle.getText().toUpperCase());
			titleRow.getCell(0).setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, chiTietTableModel.getColumnCount() - 1));

			// --- 2. HEADER THÔNG TIN LỌC (4 dòng) ---
			int lastCol = Math.max(0, chiTietTableModel.getColumnCount() - 1);

			Row row1 = sheet.createRow(1);
			Cell cellTime = row1.createCell(0);
			cellTime.setCellValue(lblInfoThoiGian.getText());
			cellTime.setCellStyle(infoStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));

			Row row2 = sheet.createRow(2);
			Cell cellRoute = row2.createCell(0);
			cellRoute.setCellValue(lblInfoTuyen.getText());
			cellRoute.setCellStyle(infoStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCol));

			Row row3 = sheet.createRow(3);
			Cell cellNV = row3.createCell(0);
			cellNV.setCellValue(lblInfoNhanVien.getText());
			cellNV.setCellStyle(infoStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, lastCol));

			Row row4 = sheet.createRow(4);
			Cell cellType = row4.createCell(0);
			cellType.setCellValue(lblInfoLoaiVe.getText());
			cellType.setCellStyle(infoStyle);
			sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, lastCol));

			// --- 3. BẢNG DỮ LIỆU ---
			int startRow = 6;

			Row headerRow = sheet.createRow(startRow);
			for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(chiTietTableModel.getColumnName(col));
				cell.setCellStyle(headerCellStyle);
			}

			for (int row = 0; row < chiTietTableModel.getRowCount(); row++) {
				Row dataRow = sheet.createRow(startRow + 1 + row);
				for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
					Cell cell = dataRow.createCell(col);
					Object value = chiTietTableModel.getValueAt(row, col);

					if (value instanceof Integer) {
						cell.setCellValue((Integer) value);
						cell.setCellStyle(dataStyle);
					} else if (value instanceof Double) {
						cell.setCellValue((Double) value);
						cell.setCellStyle(currencyCellStyle);
					} else if (value instanceof String) {
						cell.setCellValue((String) value);
						if (col == 1) {
							cell.setCellStyle(centerStyle);
						} else {
							cell.setCellStyle(dataStyle);
						}
					} else {
						if (value != null) {
							cell.setCellValue(value.toString());
						}
						cell.setCellStyle(dataStyle);
					}
				}
			}
			for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
				sheet.autoSizeColumn(col);
			}

			workbook.write(outputStream);
			JOptionPane.showMessageDialog(this, "Xuất Excel thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(finalFileToSave);
			}
		} catch (Exception ex) {
			handleLoadingError(ex, "Lỗi xuất file Excel");
		}
	}

	// ================== UTILS UI ==================
	private JPanel buildFilterBar() {
		JPanel bar = new JPanel(new GridBagLayout());
		bar.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 10, 6, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		Dimension comboSize = new Dimension(250, 32);

		// Hàng 0
		gbc.gridx = 0;
		gbc.gridy = 0;
		bar.add(new JLabel("Loại thời gian:"), gbc);
		gbc.gridx = 1;
		cbLoaiThoiGian.setPreferredSize(comboSize);
		bar.add(cbLoaiThoiGian, gbc);
		gbc.gridx = 2;
		filterSwitcher.setOpaque(false);
		filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
		filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
		filterSwitcher.add(buildThangFilter(), CARD_THANG);
		filterSwitcher.add(buildNamFilter(), CARD_NAM);
		filterSwitcher.setPreferredSize(new Dimension(350, 32));
		bar.add(filterSwitcher, gbc);

		// Hàng 1
		gbc.gridx = 0;
		gbc.gridy = 1;
		bar.add(new JLabel("Lọc tuyến:"), gbc);
		gbc.gridx = 1;
		cbLoaiTuyen.setPreferredSize(comboSize);
		bar.add(cbLoaiTuyen, gbc);

		JPanel panelGa = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		panelGa.setOpaque(false);
		panelGa.add(new JLabel("Ga đi:"));
		cbGaDi.setPreferredSize(comboSize);
		panelGa.add(cbGaDi);
		panelGa.add(new JLabel("Ga đến:"));
		cbGaDen.setPreferredSize(comboSize);
		panelGa.add(cbGaDen);
		JPanel panelLoaiVe = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		panelLoaiVe.setOpaque(false);
		panelLoaiVe.add(new JLabel("Loại vé:"));
		cbLoaiVe.setPreferredSize(comboSize);
		panelLoaiVe.add(cbLoaiVe);

		JPanel panelCot2 = new JPanel(new GridLayout(2, 1, 0, 4));
		panelCot2.setOpaque(false);
		panelCot2.add(panelGa);
		panelCot2.add(panelLoaiVe);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		bar.add(panelCot2, gbc);
		gbc.gridheight = 1;

		// Hàng 2
		gbc.gridx = 0;
		gbc.gridy = 2;
		bar.add(new JLabel("Nhân viên:"), gbc);
		gbc.gridx = 1;
		cbNhanVien.setPreferredSize(comboSize);
		bar.add(cbNhanVien, gbc);

		// Hàng 3
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panelButtons.setOpaque(false);
		btnTimKiem.setText("Tìm kiếm");
		btnTimKiem.setFont(new Font("Arial", Font.BOLD, 13));
		btnTimKiem.setBackground(new Color(33, 150, 83));
		btnTimKiem.setForeground(Color.WHITE);
		btnTimKiem.setPreferredSize(new Dimension(120, 35));
		btnXoaBoLoc.setPreferredSize(new Dimension(120, 35));
		panelButtons.add(btnTimKiem);
		panelButtons.add(btnXoaBoLoc);
		bar.add(panelButtons, gbc);

		// Events
		cbLoaiThoiGian.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CardLayout cl = (CardLayout) filterSwitcher.getLayout();
				switch ((String) e.getItem()) {
				case "Theo ngày" -> cl.show(filterSwitcher, CARD_NGAY);
				case "Theo tháng" -> cl.show(filterSwitcher, CARD_THANG);
				case "Theo năm" -> cl.show(filterSwitcher, CARD_NAM);
				default -> cl.show(filterSwitcher, CARD_TATCA);
				}
			}
		});

		// --- SỰ KIỆN KHÓA/MỞ & RESET GA ---
		cbLoaiTuyen.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String val = (String) e.getItem();
				boolean enable = val != null && val.equals("Theo Ga đi/đến");
				cbGaDi.setEnabled(enable);
				cbGaDen.setEnabled(enable);
				if (!enable) {
					((JTextField) cbGaDi.getEditor().getEditorComponent()).setText("");
					((JTextField) cbGaDen.getEditor().getEditorComponent()).setText("");
				}
			}
		});
		cbGaDi.setEnabled(false);
		cbGaDen.setEnabled(false);

		btnTimKiem.addActionListener(e -> xuLyThongKe());
		((CardLayout) filterSwitcher.getLayout()).show(filterSwitcher, CARD_TATCA);

		return bar;
	}

	private JPanel taoPanelTongQuan() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		JLabel placeholder = new JLabel("📊 Chọn bộ lọc và nhấn Tìm kiếm", SwingConstants.CENTER);
		placeholder.setFont(new Font(getFont().getFontName(), Font.ITALIC, 16));
		placeholder.setForeground(Color.GRAY);
		panel.add(placeholder, BorderLayout.CENTER);
		return panel;
	}

	private JPanel taoPanelChiTiet(JComponent title, JTable table, JPanel bottomPanel) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		panel.add(title, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		return panel;
	}

	private void loadComboBoxesData() {
		try {
			danhSachGaGoc = thongKeVeDAO.getDanhSachTenGa();
			cbGaDi.removeAllItems();
			cbGaDen.removeAllItems();
			if (danhSachGaGoc == null || danhSachGaGoc.isEmpty()) {
				cbGaDi.addItem("Lỗi");
				cbGaDen.addItem("Lỗi");
			} else {
				for (String ga : danhSachGaGoc) {
					cbGaDi.addItem(ga);
					cbGaDen.addItem(ga);
				}
				setupAutoComplete(cbGaDi, danhSachGaGoc);
				setupAutoComplete(cbGaDen, danhSachGaGoc);
			}
			if (danhSachGaGoc != null) {
				if (danhSachGaGoc.contains("Sài Gòn")) {
					cbGaDi.setSelectedItem("Sài Gòn");
				}
				if (danhSachGaGoc.contains("Hà Nội")) {
					cbGaDen.setSelectedItem("Hà Nội");
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Lỗi load ga", e);
		}

		try {
			Map<String, String> dsNhanVien = thongKeVeDAO.getDanhSachNhanVien();
			nhanVienMap.clear();
			cbNhanVien.removeAllItems();
			cbNhanVien.addItem("Tất cả");
			if (dsNhanVien != null) {
				for (Map.Entry<String, String> e : dsNhanVien.entrySet()) {
					String d = e.getValue() + " (" + e.getKey() + ")";
					nhanVienMap.put(d, e.getKey());
					cbNhanVien.addItem(d);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Lỗi load NV", e);
		}

		try {
			Map<String, String> dsHangToa = thongKeVeDAO.getDanhSachLoaiVe();
			hangToaMap.clear();
			cbLoaiVe.removeAllItems();
			cbLoaiVe.addItem("Tất cả");
			if (dsHangToa != null) {
				for (Map.Entry<String, String> e : dsHangToa.entrySet()) {
					hangToaMap.put(e.getValue(), e.getKey());
					cbLoaiVe.addItem(e.getValue());
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Lỗi load Loại vé", e);
		}
	}

	// --- UTILS: ValueLabel, Card, Loading, Date Check ---
	private JLabel createValueLabel(String initialText) {
		JLabel label = new JLabel(initialText, SwingConstants.CENTER);
		label.setForeground(Color.WHITE);
		label.setFont(new Font(getFont().getFontName(), Font.BOLD, 16));
		return label;
	}

	private JPanel createCard(String title, JLabel valueLabel, Color color) {
		JPanel card = new JPanel(new BorderLayout(0, 5));
		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
		card.setBackground(color);
		card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		card.add(titleLabel, BorderLayout.NORTH);
		card.add(valueLabel, BorderLayout.CENTER);
		return card;
	}

	private void handleLoadingError(Exception ex, String context) {
		setCursor(Cursor.getDefaultCursor());
		LOGGER.log(Level.SEVERE, context, ex);
		JOptionPane.showMessageDialog(this, context + ":\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	}

	private boolean kiemTraKhoangNgayHopLe(Date from, Date to) {
		if (from == null || to == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ ngày.");
			return false;
		}
		if (!isSameDay(from, to) && to.before(from)) {
			JOptionPane.showMessageDialog(this, "Ngày kết thúc phải ≥ ngày bắt đầu.");
			return false;
		}
		return true;
	}

	private void capNhatChartRong(String msg) {
		SwingUtilities.invokeLater(() -> {
			chartPanelContainer.removeAll();
			chartPanelContainer.add(new JLabel(msg, SwingConstants.CENTER));
			chartPanelContainer.revalidate();
			chartPanelContainer.repaint();
		});
	}

	private void capNhatBangRong() {
		SwingUtilities.invokeLater(() -> chiTietTableModel.setRowCount(0));
	}

	private boolean isSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
				&& c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	private String fmtD(LocalDate d) {
		return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	private int nvl(Integer v, int def) {
		return v == null ? def : v;
	}

	private JPanel buildTatCaFilter() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p.setOpaque(false);
		p.add(new JLabel("Hiển thị dữ liệu theo năm"));
		p.setPreferredSize(new Dimension(300, 30));
		return p;
	}

	private JPanel buildNgayFilter() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		p.setOpaque(false);
		tuNgay.setDateFormatString("dd/MM/yyyy");
		denNgay.setDateFormatString("dd/MM/yyyy");
		tuNgay.setPreferredSize(new Dimension(160, 28));
		denNgay.setPreferredSize(new Dimension(160, 28));
		tuNgay.setDate(new Date());
		denNgay.setDate(new Date());
		addDateConstraint(tuNgay, denNgay);
		p.add(new JLabel("Từ:"));
		p.add(tuNgay);
		p.add(new JLabel("Đến:"));
		p.add(denNgay);
		return p;
	}

	private JPanel buildThangFilter() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		p.setOpaque(false);
		if (cbTuThang.getItemCount() == 0) {
			for (int i = 1; i <= 12; i++) {
				cbTuThang.addItem("Tháng " + i);
				cbDenThang.addItem("Tháng " + i);
			}
			int cy = Calendar.getInstance().get(Calendar.YEAR);
			for (int y = 2020; y <= cy + 5; y++) {
				cbTuNamThang.addItem(y);
				cbDenNamThang.addItem(y);
			}
			cbTuThang.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
			cbDenThang.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
			cbTuNamThang.setSelectedItem(cy);
			cbDenNamThang.setSelectedItem(cy);
		}
		Dimension m = new Dimension(120, 28);
		Dimension y = new Dimension(120, 28);
		cbTuThang.setPreferredSize(m);
		cbDenThang.setPreferredSize(m);
		cbTuNamThang.setPreferredSize(y);
		cbDenNamThang.setPreferredSize(y);
		p.add(new JLabel("Từ:"));
		p.add(cbTuThang);
		p.add(cbTuNamThang);
		p.add(new JLabel("Đến:"));
		p.add(cbDenThang);
		p.add(cbDenNamThang);
		return p;
	}

	private JPanel buildNamFilter() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		p.setOpaque(false);
		if (cbTuNam.getItemCount() == 0) {
			int cy = Calendar.getInstance().get(Calendar.YEAR);
			for (int y = 2020; y <= cy + 5; y++) {
				cbTuNam.addItem(y);
				cbDenNam.addItem(y);
			}
			cbTuNam.setSelectedItem(cy);
			cbDenNam.setSelectedItem(cy);
		}
		Dimension d = new Dimension(120, 28);
		cbTuNam.setPreferredSize(d);
		cbDenNam.setPreferredSize(d);
		p.add(new JLabel("Từ năm:"));
		p.add(cbTuNam);
		p.add(new JLabel("Đến năm:"));
		p.add(cbDenNam);
		return p;
	}

	private void addDateConstraint(JDateChooser f, JDateChooser t) {
		PropertyChangeListener l = evt -> {
			if (!"date".equals(evt.getPropertyName())) {
				return;
			}
			Date d1 = f.getDate(), d2 = t.getDate();
			if (d1 != null && d2 != null && !isSameDay(d1, d2) && d2.before(d1)) {
				t.setDate(d1);
			}
		};
		f.addPropertyChangeListener("date", l);
		t.addPropertyChangeListener("date", l);
	}

	private void xoaBoLoc() {
		cbLoaiThoiGian.setSelectedIndex(0);
		tuNgay.setDate(new Date());
		denNgay.setDate(new Date());
		Calendar now = Calendar.getInstance();
		cbTuThang.setSelectedIndex(now.get(Calendar.MONTH));
		cbDenThang.setSelectedIndex(now.get(Calendar.MONTH));
		cbTuNam.setSelectedItem(now.get(Calendar.YEAR));
		cbDenNam.setSelectedItem(now.get(Calendar.YEAR));

		cbLoaiTuyen.setSelectedIndex(0);
		cbGaDi.setEnabled(false);
		cbGaDen.setEnabled(false);
		// Reset text nhập trong Editor
		((JTextField) cbGaDi.getEditor().getEditorComponent()).setText("");
		((JTextField) cbGaDen.getEditor().getEditorComponent()).setText("");

		if (cbNhanVien.getItemCount() > 0) {
			cbNhanVien.setSelectedIndex(0);
		}
		if (cbLoaiVe.getItemCount() > 0) {
			cbLoaiVe.setSelectedIndex(0);
		}
		if (cbGaDi.getItemCount() > 0) {
			cbGaDi.setSelectedIndex(0);
		}
		if (cbGaDen.getItemCount() > 1) {
			cbGaDen.setSelectedIndex(1);
		}
		xuLyThongKe();
	}
}