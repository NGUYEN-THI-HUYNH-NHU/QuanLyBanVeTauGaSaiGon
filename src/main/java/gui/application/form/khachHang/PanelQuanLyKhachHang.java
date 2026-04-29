package gui.application.form.khachHang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import controller.KhachHang_CTRL;
import entity.KhachHang;
import entity.NhanVien;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiKhachHang;

public class PanelQuanLyKhachHang extends JPanel implements ActionListener, MouseListener, KeyListener {

	private final KhachHang_CTRL khachHang_ctrl;
	private final NhanVien nhanVienThucHien;

	private JTextField txtMaKH, txtTenKH, txtSDT, txtEmail, txtSoGiayTo, txtDiaChi;
	private JComboBox<LoaiDoiTuong> cbLDT;
	private JComboBox<LoaiKhachHang> cbLKH;
	private JLabel lblErrorTenKH, lblErrorSDT, lblErrorEmail, lblErrorDiaChi, lblErrorSGT;
	private List<JTextField> listText;
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton btnAdd, btnEdit, btnFind, btnClean;

	// panel hiển thị thông tin khi click
	private JLabel lblChiTietTen, lblChiTietSDT, lblChiTietEmail, lblChiTietDiaChi, lblChiTietLoaiDoiTuong,
			lblChiTietLoaiKhachHang, lblChiTietGiayTo;
	private boolean Editing;
	private Font titleFont;

	// màu sắc chủ đạo
	private final Color COLOR_PRIMARY = new Color(30, 100, 150);
	private final Color COLOR_BG_MAIN = new Color(248, 250, 251);
	private final Color COLOR_BG_PANEL = new Color(226, 232, 240);
	private final Color COLOR_TEXT_TITLE = new Color(30, 41, 59);
	private final Color COLOR_TEXT_LABEL = new Color(51, 65, 85);
	private JLabel lblAvatar;

	public PanelQuanLyKhachHang(NhanVien nhanVienThucHien) {
		this.khachHang_ctrl = new KhachHang_CTRL();
		this.nhanVienThucHien = nhanVienThucHien;

		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(COLOR_BG_MAIN);

		JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Roboto", Font.BOLD, 26));
		lblTitle.setForeground(COLOR_TEXT_TITLE);
		add(lblTitle, BorderLayout.NORTH);

		JPanel panelBody = new JPanel(new BorderLayout());
		panelBody.setBackground(COLOR_BG_MAIN);

		panelBody.add(createTopSplitPanel(), BorderLayout.NORTH);

		panelBody.add(createTablePanel(), BorderLayout.CENTER);
		add(panelBody, BorderLayout.CENTER);
		loadDataToTable();
		initPlaceholders();
	}

	public PanelQuanLyKhachHang(NhanVien nhanVienThucHien, String cccd) {
		this.khachHang_ctrl = new KhachHang_CTRL();
		this.nhanVienThucHien = nhanVienThucHien;

		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(COLOR_BG_MAIN);

		JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Roboto", Font.BOLD, 26));
		lblTitle.setForeground(COLOR_TEXT_TITLE);
		add(lblTitle, BorderLayout.NORTH);

		JPanel panelBody = new JPanel(new BorderLayout());
		panelBody.setBackground(COLOR_BG_MAIN);

		panelBody.add(createTopSplitPanel(), BorderLayout.NORTH);

		panelBody.add(createTablePanel(), BorderLayout.CENTER);
		add(panelBody, BorderLayout.CENTER);
		loadDataToTable();
		initPlaceholders();

		txtSoGiayTo.setText(cccd);
		txtSoGiayTo.setForeground(getForeground());
		// Dùng invokeLater để đảm bảo giao diện đã chuyển xong mới focus
		SwingUtilities.invokeLater(() -> {
			txtTenKH.requestFocusInWindow();
		});
		cbLKH.setSelectedItem(LoaiKhachHang.KHACH_HANG);
	}

	// tạo panel chia đôi ở phía trên
	private JSplitPane createTopSplitPanel() {
		JPanel leftForm = panelInput();
		JPanel rightInfo = createPanleInfor();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftForm, rightInfo);
		splitPane.setDividerLocation(700);
		splitPane.setResizeWeight(0.6);
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	// panel nhập thông tin khách hàng
	private JPanel panelInput() {
		JPanel panelTop = new JPanel(new BorderLayout(10, 10));
		panelTop.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin khách hàng",
						TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 13), COLOR_PRIMARY));
		panelTop.setBackground(COLOR_BG_PANEL);

		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(COLOR_BG_PANEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 8, 3, 8);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		Font font = new Font("Roboto", Font.PLAIN, 13);

		int y = 0; // Biến đếm hàng
		formAddField(form, gbc, y++, "Mã khách hàng:", txtMaKH = new JTextField(), null, font);
		txtMaKH.setEnabled(false);
		formAddField(form, gbc, y++, "Tên khách hàng:", txtTenKH = new JTextField(), lblErrorTenKH = errorLabel(),
				font);
		formAddField(form, gbc, y++, "Số điện thoại:", txtSDT = new JTextField(), lblErrorSDT = errorLabel(), font);
		formAddField(form, gbc, y++, "Email:", txtEmail = new JTextField(), lblErrorEmail = errorLabel(), font);
		formAddField(form, gbc, y++, "Số giấy tờ:", txtSoGiayTo = new JTextField(), lblErrorSGT = errorLabel(), font);
		formAddField(form, gbc, y++, "Loại đối tượng:", cbLDT = new JComboBox<>(LoaiDoiTuong.values()), null, font);
		formAddField(form, gbc, y++, "Loại khách hàng:", cbLKH = new JComboBox<>(LoaiKhachHang.values()), null, font);
		formAddField(form, gbc, y++, "Địa chỉ:", txtDiaChi = new JTextField(), lblErrorDiaChi = errorLabel(), font);

		listText = Arrays.asList(txtMaKH, txtTenKH, txtSDT, txtEmail, txtSoGiayTo, txtDiaChi);
		// Các nút thao tác
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		btnPanel.setBackground(COLOR_BG_PANEL);

		btnAdd = createButton("Thêm", "icon/svg/add-kh.svg");
		btnEdit = createButton("Sửa", "icon/svg/edit-kh.svg");
		btnFind = createButton("Tìm kiếm", "icon/svg/search-kh.svg");
		btnClean = createButton("Xóa trắng", "icon/svg/refresh-kh.svg");

		btnPanel.add(btnAdd);
		btnPanel.add(btnEdit);
		btnPanel.add(btnFind);
		btnPanel.add(btnClean);

		btnAdd.addActionListener(this);
		btnEdit.addActionListener(this);
		btnFind.addActionListener(this);
		btnClean.addActionListener(this);

		// gan su kien cho cac textfield
		for (JTextField txtField : listText) {
			txtField.addKeyListener(this);
		}
		cbLDT.addActionListener(this);
		cbLKH.addActionListener(this);

		JPanel footer = new JPanel(new GridBagLayout());
		footer.setBackground(COLOR_BG_PANEL);

		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;

		g.gridy = 0;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 10, 0, 10);
		footer.add(btnPanel, g);

		JLabel lblTieuChi = new JLabel("<html><b><i>Tìm kiếm theo:</i></b> SĐT hoặc Số giấy tờ</html>");
		lblTieuChi.setFont(new Font("Roboto", Font.ITALIC, 12));
		lblTieuChi.setForeground(COLOR_TEXT_LABEL);

		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 12, 6, 10);
		footer.add(lblTieuChi, g);

		panelTop.add(form, BorderLayout.CENTER);
		panelTop.add(footer, BorderLayout.SOUTH);
		return panelTop;

	}

	// Chọn 1 dòng trong table
	private JPanel createPanleInfor() {
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin chi tiết",
						TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 15), COLOR_PRIMARY));
		infoPanel.setBackground(COLOR_BG_PANEL);

		lblAvatar = new JLabel();
		lblAvatar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icon/png/adult.png")).getImage()
				.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(lblAvatar, BorderLayout.NORTH);

		JPanel details = new JPanel(new GridLayout(8, 2, 10, 5));
		details.setBackground(new Color(245, 245, 245));

		titleFont = new Font("Roboto", Font.PLAIN, 13);

		lblChiTietTen = lableInfor();
		lblChiTietSDT = lableInfor();
		lblChiTietEmail = lableInfor();
		lblChiTietDiaChi = lableInfor();
		lblChiTietLoaiDoiTuong = lableInfor();
		lblChiTietLoaiKhachHang = lableInfor();
		lblChiTietGiayTo = lableInfor();

		addDetailRow(details, "Tên khách hàng:", lblChiTietTen, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Số điện thoại:", lblChiTietSDT, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Email:", lblChiTietEmail, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Địa chỉ:", lblChiTietDiaChi, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Loại đối tượng:", lblChiTietLoaiDoiTuong, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Loại khách hàng:", lblChiTietLoaiKhachHang, titleFont, COLOR_TEXT_LABEL);
		addDetailRow(details, "Giấy tờ:", lblChiTietGiayTo, titleFont, COLOR_TEXT_LABEL);

		setupComboKeyboard(cbLDT);
		setupComboKeyboard(cbLKH);

		infoPanel.add(details, BorderLayout.CENTER);
		return infoPanel;
	}

	// them hang chi tiets
	private void addDetailRow(JPanel panel, String title, JLabel value, Font font, Color color) {
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(font);
		lblTitle.setForeground(color);
		panel.add(lblTitle);
		panel.add(value);
		;
	}

	// gắn giá trị ban đầu cho cái panel này nè
	private JLabel lableInfor() {
		JLabel lbl = new JLabel("");
		lbl.setFont(new Font("Roboto", Font.PLAIN, 13));
		lbl.setForeground(COLOR_TEXT_LABEL);
		return lbl;
	}

	// reset lable thông tin
	public void resetLableInfor() {
		lblChiTietTen.setText("");
		lblChiTietSDT.setText("");
		lblChiTietEmail.setText("");
		lblChiTietDiaChi.setText("");
		lblChiTietGiayTo.setText("");
		lblChiTietLoaiDoiTuong.setText("");
		lblChiTietLoaiKhachHang.setText("");

		initPlaceholders();
	}

	// khuôn của form
	private void formAddField(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent field,
			JLabel errorLabel, Font font) {
		gbc.gridy = y;
		gbc.gridx = 0;
		JLabel label = new JLabel(labelText);
		label.setFont(font);
		label.setForeground(COLOR_TEXT_LABEL);
		panel.add(label, gbc);
		gbc.gridx = 1;
		panel.add(field, gbc);
		if (errorLabel != null) {
			gbc.gridx = 2;
			panel.add(errorLabel, gbc);
		}
	}

	// mẫu label lỗi
	private JLabel errorLabel() {
		JLabel lbl = new JLabel("");
		lbl.setForeground(Color.RED);
		lbl.setFont(new Font("Roboto", Font.ITALIC, 12));
		return lbl;
	}

	// tạo button với ícon tương ứng
	private JButton createButton(String text, String iconPath) {
		JButton button = new JButton(text);
		button.setFont(new Font("Roboto", Font.BOLD, 13));
		button.setBackground(new Color(173, 216, 230));
		button.setIcon(new FlatSVGIcon(iconPath, 16, 16));
		button.setPreferredSize(new Dimension(100, 30));

		return button;
	}

	// Panle danh sách khách hàng
	private JScrollPane createTablePanel() {
		String[] columnNames = { "STT", "Mã KH", "Tên KH", "SĐT", "Email", "Giấy tờ", "Địa chỉ", "Loại đối tượng",
				"Loại KH" };

		// Tạo bảng không cho phép sửa trực tiếp trên bảng
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);

		table.setFont(new Font("Roboto", Font.PLAIN, 13));
		table.setRowHeight(25);
		table.addMouseListener(this);

		JTableHeader header = table.getTableHeader();
		header.setBackground(COLOR_PRIMARY);
		header.setForeground(Color.WHITE);
		header.setFont(new Font("Roboto", Font.BOLD, 13));

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (isSelected) {
					c.setBackground(new Color(173, 216, 230));
					c.setForeground(Color.BLACK);
				} else {
					c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
					c.setForeground(Color.BLACK);
				}
				return c;
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(30, 100, 150), 1),
				"Danh sách khách hàng", TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 15)));
		scroll.setPreferredSize(new Dimension(1000, 2000));

		// chỉnh kích thước cột
		table.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
		table.getColumnModel().getColumn(1).setPreferredWidth(80); // Mã KH
		table.getColumnModel().getColumn(2).setPreferredWidth(160); // Tên KH
		table.getColumnModel().getColumn(3).setPreferredWidth(100); // SĐT
		table.getColumnModel().getColumn(4).setPreferredWidth(170); // Email
		table.getColumnModel().getColumn(5).setPreferredWidth(130); // Giấy tờ
		table.getColumnModel().getColumn(6).setPreferredWidth(250); // Địa chỉ
		table.getColumnModel().getColumn(7).setPreferredWidth(130); // Loại đối tượng
		table.getColumnModel().getColumn(8).setPreferredWidth(230); // Loại KH

		return scroll;
	}

	// Load dữ liệu lên bảng
	public void loadDataToTable() {
		List<KhachHang> dsKH = khachHang_ctrl.getAllKhachHang();
		int stt = 1;
		tableModel.setRowCount(0);

		for (KhachHang kh : dsKH) {
			tableModel.addRow(new Object[] { stt++, Objects.toString(kh.getKhachHangID(), ""),
					Objects.toString(kh.getHoTen(), ""), Objects.toString(kh.getSoDienThoai(), ""),
					Objects.toString(kh.getEmail(), ""), Objects.toString(kh.getSoGiayTo(), ""),
					Objects.toString(kh.getDiaChi(), ""),
					kh.getLoaiDoiTuong() == null ? "" : kh.getLoaiDoiTuong().getDescription(),
					kh.getLoaiKhachHang() == null ? "" : kh.getLoaiKhachHang().getDescription() });
		}
	}

	// Đặt placeholder cho JTextField
	private void applyPlaceholder(JTextField field, String placeholder) {
		field.setForeground(Color.GRAY);
		field.setText(placeholder);

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {

				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {

				if (field.getText().trim().isEmpty()) {
					field.setForeground(Color.GRAY);
					field.setText(placeholder);
				}
			}
		});
	}

	// Chỉ lấy giá trị thực tế từ JTextField, bỏ qua placeholder
	private String getRealText(JTextField field, String placeholder) {
		String text = field.getText().trim();
		if (text.equals(placeholder) && field.getForeground().equals(Color.GRAY)) {
			return "";
		}
		return text;
	}

	// khởi tạo placeholder cho các JTextField
	private void initPlaceholders() {
		applyPlaceholder(txtTenKH, "VD: Nguyễn Văn An");
		applyPlaceholder(txtSDT, "VD: 0912345678");
		applyPlaceholder(txtEmail, "VD: email@domain.com");
		applyPlaceholder(txtSoGiayTo, "VD: 079123456789");
		applyPlaceholder(txtDiaChi, "VD: 123 Lê Lợi, Q1, TP.HCM");
	}

	// kiểm tra xem JTextField có đang hiển thị placeholder không
	private boolean isPlaceholder(JTextField field, String placeholder) {
		return field.getForeground().equals(Color.GRAY) && field.getText().equals(placeholder);
	}

	// click 1 dòng trên table
	@Override
	public void mouseClicked(MouseEvent e) {
		resetLableInfor();
		clearInputFields();
		btnEdit.setText("Sửa");
		Editing = false;

		if (e.getSource() != table) {
			return;
		}

		int viewRow = table.getSelectedRow();
		if (viewRow < 0) {
			return;
		}

		int modelRow = table.convertRowIndexToModel(viewRow);

		String ten = safe(modelRow, 2);
		String sdt = safe(modelRow, 3);
		String email = safe(modelRow, 4);
		String giayTo = safe(modelRow, 5);
		String diaChi = safe(modelRow, 6);
		String loaiDoiTuong = safe(modelRow, 7);
		String loaiKhachHang = safe(modelRow, 8);

		lblChiTietTen.setText(ten);
		lblChiTietSDT.setText(sdt);
		lblChiTietEmail.setText(email);
		lblChiTietDiaChi.setText(diaChi);
		lblChiTietGiayTo.setText(giayTo);
		lblChiTietLoaiDoiTuong.setText(loaiDoiTuong);
		lblChiTietLoaiKhachHang.setText(loaiKhachHang);

		// avatar: tùy theo đối tượng
		if (loaiDoiTuong.equalsIgnoreCase("NGUOI_CAO_TUOI") || loaiDoiTuong.contains("Cao tuổi")) {
			lblAvatar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icon/png/older.png")).getImage()
					.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		} else if (loaiDoiTuong.equalsIgnoreCase("TRE_EM") || loaiDoiTuong.contains("Trẻ em")) {
			lblAvatar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icon/png/child.png")).getImage()
					.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		} else {
			lblAvatar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icon/png/adult.png")).getImage()
					.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		}
	}

	private String safe(int row, int col) {
		Object v = tableModel.getValueAt(row, col);
		return v == null ? "" : v.toString();
	}

	// reset lableError
	public void resetErrorLabels() {
		lblErrorTenKH.setText("");
		lblErrorSDT.setText("");
		lblErrorEmail.setText("");
		lblErrorDiaChi.setText("");
		lblErrorSGT.setText("");
	}

	// Valid form
	public boolean isValidForm() {
		resetErrorLabels();
		boolean isValid = true;

		String tenKH = getRealText(txtTenKH, "VD: Nguyễn Văn An");
		String sdt = getRealText(txtSDT, "VD: 0912345678");
		String email = getRealText(txtEmail, "VD: email@domain.com");
		String soGiayTo = getRealText(txtSoGiayTo, "VD: 079123456789");
		String diaChi = getRealText(txtDiaChi, "VD: 123 Lê Lợi, Q1, TP.HCM"); // optional

		// 0) BẮT BUỘC: phải có ít nhất 1 trong 2 (SĐT hoặc SGT)
		if (sdt.isEmpty() && soGiayTo.isEmpty()) {
			lblErrorSDT.setText("Phải nhập SĐT hoặc số giấy tờ");
			lblErrorSGT.setText("Phải nhập SĐT hoặc số giấy tờ");
			return false;
		}

		// Tên
		if (tenKH.isEmpty() || !khachHang_ctrl.isValidTen(tenKH)) {
			lblErrorTenKH.setText("Tên khách hàng không hợp lệ! VD: Nguyễn Văn A");
			txtTenKH.requestFocus();
			isValid = false;
		}

		// SĐT: chỉ validate nếu có nhập
		if (!sdt.isEmpty()) {
			if (!khachHang_ctrl.isValidPhoneNumber(sdt)) {
				lblErrorSDT.setText("Số điện thoại không hợp lệ! VD: 0912345678");
				txtSDT.requestFocus();
				isValid = false;
			} else if (khachHang_ctrl.kiemTraTrungSDT(sdt)) {
				lblErrorSDT.setText("Số điện thoại đã tồn tại!");
				txtSDT.requestFocus();
				isValid = false;
			}
		}

		// SGT: chỉ validate nếu có nhập
		if (!soGiayTo.isEmpty()) {
			if (khachHang_ctrl.kiemTraTrungSoGiayTo(soGiayTo)) {
				lblErrorSGT.setText("Số giấy tờ đã tồn tại!");
				txtSoGiayTo.requestFocus();
				isValid = false;
			}
		}

		// Email
		if (!email.isEmpty() && !khachHang_ctrl.isValidEmail(email)) {
			lblErrorEmail.setText("Email không hợp lệ! VD: email@domain.com");
			txtEmail.requestFocus();
			isValid = false;
		}

		// 5) Địa chỉ
		if (!diaChi.isEmpty() && !khachHang_ctrl.isValidDiaChi(diaChi)) {
			lblErrorDiaChi.setText("Địa chỉ không hợp lệ! VD: 123 Lê Lợi, Q1, TP.HCM");
			txtDiaChi.requestFocus();
			isValid = false;
		}

		return isValid;
	}

	// thêm khách hàng
	public boolean themKhachHang(KhachHang kh) {
		if (!isValidForm()) {
			return false;
		}
		if (khachHang_ctrl.themKhachHang(kh)) {
			loadDataToTable();
			JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			clearInputFields();
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!!!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	// tìm kiếm khách hàng bằng sdt
	public KhachHang timKiemKhachHangTheoSDT(String sdt) {
		KhachHang kh = khachHang_ctrl.timKiemKhachHang(sdt);
		tableModel.setRowCount(0);
		if (kh != null) {
			tableModel.addRow(
					new Object[] { 1, Objects.toString(kh.getKhachHangID(), ""), Objects.toString(kh.getHoTen(), ""),
							Objects.toString(kh.getSoDienThoai(), ""), Objects.toString(kh.getEmail(), ""),
							Objects.toString(kh.getSoGiayTo(), ""), Objects.toString(kh.getDiaChi(), ""),
							kh.getLoaiDoiTuong() == null ? "" : kh.getLoaiDoiTuong().getDescription(),
							kh.getLoaiKhachHang() == null ? "" : kh.getLoaiKhachHang().getDescription() });
			return kh;
		} else {
			JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt, "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			loadDataToTable();
			return null;
		}
	}

	// tìm kiếm khách hàng bằng số giấy tờ
	public KhachHang timKiemKhachHangTheoSGT(String sgt) {
		KhachHang kh = khachHang_ctrl.timKiemKhachHangTheoSoGiayTo(sgt);
		tableModel.setRowCount(0);
		if (kh != null) {
			tableModel.addRow(
					new Object[] { 1, Objects.toString(kh.getKhachHangID(), ""), Objects.toString(kh.getHoTen(), ""),
							Objects.toString(kh.getSoDienThoai(), ""), Objects.toString(kh.getEmail(), ""),
							Objects.toString(kh.getSoGiayTo(), ""), Objects.toString(kh.getDiaChi(), ""),
							kh.getLoaiDoiTuong() == null ? "" : kh.getLoaiDoiTuong().getDescription(),
							kh.getLoaiKhachHang() == null ? "" : kh.getLoaiKhachHang().getDescription() });
			return kh;
		} else {
			JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số giấy tờ: " + sgt, "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			loadDataToTable();
			return null;
		}
	}

	// clean txtField
	public void clearInputFields() {
		txtMaKH.setText("");
		txtTenKH.setText("");
		txtSDT.setText("");
		txtEmail.setText("");
		txtSoGiayTo.setText("");
		txtDiaChi.setText("");
		cbLDT.setSelectedIndex(0);
		cbLKH.setSelectedIndex(0);
		initPlaceholders();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd) {

			if (!isValidForm()) {
				return;
			}

			String maKH = khachHang_ctrl.taoMaKhachHang();
			String tenKH = getRealText(txtTenKH, "VD: Nguyễn Văn An");
			String sdt = getRealText(txtSDT, "VD: 0912345678");
			String email = getRealText(txtEmail, "VD: email@domain.com");
			String soGiayTo = getRealText(txtSoGiayTo, "VD: 079123456789");
			String diaChi = getRealText(txtDiaChi, "VD: 123 Lê Lợi, Q1, TP.HCM");
			String emailFinal = email.isEmpty() ? null : email;
			String diaChiFinal = diaChi.isEmpty() ? null : diaChi;
			String sdtFinal = sdt.isEmpty() ? null : sdt;
			String sgtFinal = soGiayTo.isEmpty() ? null : soGiayTo;

			LoaiDoiTuong ldt = (LoaiDoiTuong) cbLDT.getSelectedItem();
			LoaiKhachHang lkh = (LoaiKhachHang) cbLKH.getSelectedItem();

			KhachHang kh = new KhachHang(maKH, tenKH, sdtFinal, emailFinal, sgtFinal, diaChiFinal, ldt, lkh);
			themKhachHang(kh);
			return;

		} else if (e.getSource() == btnFind) {

			String sdtFind = getRealText(txtSDT, "VD: 0912345678");
			String sgtFind = getRealText(txtSoGiayTo, "VD: 079123456789");

			if (!sgtFind.isEmpty()) {
				timKiemKhachHangTheoSGT(sgtFind);
				resetLableInfor();
			} else if (!sdtFind.isEmpty()) {
				timKiemKhachHangTheoSDT(sdtFind);
				resetLableInfor();
			} else {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại hoặc số giấy tờ để tìm kiếm!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
			}

		} else if (e.getSource() == btnClean) {
			clearInputFields();
			resetLableInfor();
			loadDataToTable();
			resetErrorLabels();
			btnEdit.setText("Sửa");
			Editing = false;

		} else if (e.getSource() == btnEdit) {
			if (!Editing) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow < 0) {
					JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Cảnh báo",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				resetLableInfor();

				txtMaKH.setText(safeTable(selectedRow, 1));
				txtTenKH.setText(safeTable(selectedRow, 2));
				txtSDT.setText(safeTable(selectedRow, 3));
				txtEmail.setText(safeTable(selectedRow, 4));
				txtSoGiayTo.setText(safeTable(selectedRow, 5));
				txtDiaChi.setText(safeTable(selectedRow, 6));
				String ldtDesc = safeTable(selectedRow, 7);
				cbLDT.setSelectedItem(LoaiDoiTuong.fromDescription(ldtDesc));
				String lkhDesc = safeTable(selectedRow, 8);
				cbLKH.setSelectedItem(LoaiKhachHang.fromDescription(lkhDesc));

				Editing = true;
				btnEdit.setText("Lưu");
			} else {
				if (!khachHang_ctrl.isValidEmail(txtEmail.getText().trim())) {
					lblErrorEmail.setText("Email không hợp lệ! VD: email@domain.com");
					txtEmail.requestFocus();
					return;
				} else if (!khachHang_ctrl.isValidTen(txtTenKH.getText().trim())) {
					lblErrorTenKH.setText("Tên khách hàng không hợp lệ! VD: Nguyễn Văn A");
					txtTenKH.requestFocus();
					return;
				} else if (!khachHang_ctrl.isValidPhoneNumber(txtSDT.getText().trim())) {
					lblErrorSDT.setText("Số điện thoại không hợp lệ! VD: 0912345678");
					txtSDT.requestFocus();
					return;
				}

				String maKH1 = txtMaKH.getText().trim();
				String tenKH1 = txtTenKH.getText().trim();
				String sdt1 = txtSDT.getText().trim();
				String email1 = txtEmail.getText().trim();
				String soGiayTo1 = txtSoGiayTo.getText().trim();
				String diaChi1 = txtDiaChi.getText().trim();

				LoaiDoiTuong loaiDT1 = (LoaiDoiTuong) cbLDT.getSelectedItem();
				LoaiKhachHang loaiKH1 = (LoaiKhachHang) cbLKH.getSelectedItem();

				KhachHang kh1 = new KhachHang(maKH1, tenKH1, sdt1, email1, soGiayTo1, diaChi1, loaiDT1, loaiKH1);

				int confirm = JOptionPane.showConfirmDialog(this,
						"Bạn có chắc muốn cập nhật thông tin khách hàng này không?", "Xác nhận cập nhật",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (confirm != JOptionPane.YES_OPTION) {
					return;
				}

				if (khachHang_ctrl.capNhatKhachHang(kh1)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thông báo",
							JOptionPane.INFORMATION_MESSAGE);
					Editing = false;
					btnEdit.setText("Sửa");
					clearInputFields();
					resetLableInfor();
					resetErrorLabels();
				} else {
					JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		}
	}

	// lấy giá trị an toàn từ bảng
	private String safeTable(int viewRow, int col) {
		int modelRow = table.convertRowIndexToModel(viewRow);
		Object v = tableModel.getValueAt(modelRow, col);
		return v == null ? "" : v.toString();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_ENTER) {
			return;
		}

		Object src = e.getSource();

		if (src == txtSoGiayTo) {
			cbLDT.requestFocus();
			return;
		}
		if (src == cbLDT) {
			cbLKH.requestFocus();
			return;
		}
		if (src == cbLKH) {
			txtDiaChi.requestFocus();
			return;
		}
		if (src == txtDiaChi) {
			btnAdd.requestFocus();
			return;
		}

		if (src instanceof JTextField current) {
			int index = listText.indexOf(current);
			if (index != -1) {
				if (index + 1 < listText.size()) {
					JTextField next = listText.get(index + 1);
					next.requestFocus();
				} else {
					btnAdd.requestFocus();
				}
			}
		}
	}

	// bắt phím cho combox
	private void setupComboKeyboard(JComboBox<?> combo) {

		JComponent target = combo;
		if (combo.isEditable() && combo.getEditor().getEditorComponent() instanceof JComponent editor) {
			target = editor;
		}

		InputMap im = target.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = target.getActionMap();

		// 1) Nhấn DOWN khi chưa mở popup -> mở popup (và vẫn cho di chuyển item)

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "openOrMoveDown");
		am.put("openOrMoveDown", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!combo.isPopupVisible()) {
					combo.showPopup();
				} else {

					Action def = combo.getActionMap().get("selectNext");
					if (def != null) {
						def.actionPerformed(e);
					}
				}
			}
		});

		// 2) Enter: nếu popup đang mở -> đóng popup (chọn item hiện tại)

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterSelectOrNext");
		am.put("enterSelectOrNext", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (combo.isPopupVisible()) {
					combo.hidePopup();
				} else {
					combo.transferFocus();
				}
			}
		});
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
