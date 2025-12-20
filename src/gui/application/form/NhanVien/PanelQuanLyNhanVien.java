package gui.application.form.NhanVien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import controller.NhanVien_CTRL;
import entity.CaLam;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

public class PanelQuanLyNhanVien extends JPanel implements ActionListener, MouseListener, KeyListener {
	private final NhanVien nhanVienHienTai;
	private final NhanVien_CTRL nhanVien_ctrl;

	private JTextField txtMaNV, txtTenNV, txtEmail, txtSDT, txtDiaChi;
	private JComboBox<VaiTroNhanVien> cbVaiTro;
	private JComboBox<String> cbCaLam;
	private JRadioButton rbtnNam, rbtnNu;
	private JCheckBox chkDangHoatDong;
	private List<JComponent> allField;
	private JDateChooser txtNgaySinh, txtNgayThamGia;
	private JLabel lblMaNVDetail, lblVaiTroDetail, lblTenNVDetail, lblGioiTinhDetail, lblNgaySinhDetail, lblSDTDetail,
			lblEmailDetail, lblDiaChiDetail, lblNgayThamGiaDetail, lblTrangThaiDetail, lblCaLamDetail, lblAvatar;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnAdd, btnEdit, btnFind, btnClean;
	private boolean isEditing = false;
	private Font font = new Font("Roboto", Font.PLAIN, 14);

	// màu sắc giao diện
	private final Color COLOR_PRIMARY = new Color(30, 100, 150);
	private final Color COLOR_ACCENT = new Color(74, 163, 208);
	private final Color COLOR_BG_MAIN = new Color(248, 250, 251);
	private final Color COLOR_BG_PANEL = new Color(226, 232, 240);
	private final Color COLOR_TEXT_TITLE = new Color(30, 41, 59);
	private final Color COLOR_TEXT_LABEL = new Color(51, 65, 85);

	public PanelQuanLyNhanVien(NhanVien nhanVienHienTai) {
		this.nhanVienHienTai = nhanVienHienTai;
		this.nhanVien_ctrl = new NhanVien_CTRL(nhanVienHienTai);

		btnAdd = createButton("Thêm", "/gui/icon/png/save.png");
		btnEdit = createButton("Sửa", "/gui/icon/png/update.png");
		btnFind = createButton("Tìm kiếm", "/gui/icon/png/find.png");
		btnClean = createButton("Xóa trắng", "/gui/icon/png/clean.png");
		btnFind.setToolTipText("Tìm theo: Tên, Số điện thoại, Vai trò, Trạng thái");

		setLayout(new BorderLayout(10, 10));
		setBackground(COLOR_BG_MAIN);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Roboto", Font.BOLD, 26));
		lblTitle.setForeground(COLOR_TEXT_TITLE);
		add(lblTitle, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelInput(), panelChiTiet());
		splitPane.setDividerLocation(550);
		splitPane.setResizeWeight(0.5);
		splitPane.setContinuousLayout(true);
		add(splitPane, BorderLayout.CENTER);

		add(panelTable(), BorderLayout.SOUTH);

		table.addMouseListener(this);
		btnAdd.addActionListener(this);
		btnClean.addActionListener(this);
		btnEdit.addActionListener(this);
		btnFind.addActionListener(this);

		loadDataToTable();
		initPlaceholders();
	}

	private JPanel panelInput() {
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin nhân viên",
						TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 15), COLOR_PRIMARY));
		p.setBackground(COLOR_BG_PANEL);

		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(new Color(245, 245, 245));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		Font font = new Font("Roboto", Font.PLAIN, 14);

		txtMaNV = new JTextField(20);
		txtMaNV.setEnabled(false);
		txtTenNV = new JTextField(20);
		cbVaiTro = new JComboBox<>(VaiTroNhanVien.values());
		txtNgaySinh = new JDateChooser();
		txtNgaySinh.setDateFormatString("dd/MM/yyyy");
		txtNgaySinh.setCalendar(java.util.Calendar.getInstance());
		txtSDT = new JTextField();
		txtEmail = new JTextField();
		txtDiaChi = new JTextField();
		txtNgayThamGia = new JDateChooser();
		txtNgayThamGia.setDateFormatString("dd/MM/yyyy");
		txtNgayThamGia.setCalendar(java.util.Calendar.getInstance());

		cbCaLam = new JComboBox<>(new String[] { "Sáng", "Chiều" });
		rbtnNu = new JRadioButton("Nữ");
		rbtnNam = new JRadioButton("Nam");
		ButtonGroup group = new ButtonGroup();
		group.add(rbtnNam);
		group.add(rbtnNu);
		chkDangHoatDong = new JCheckBox("Đang hoạt động", true);

		// Sau khi tạo xong tất cả component input
		JComponent ngaySinhEditor = txtNgaySinh.getDateEditor().getUiComponent();
		JComponent ngayTGEditor = txtNgayThamGia.getDateEditor().getUiComponent();

		// Danh sách thứ tự tab bằng Enter
		allField = List.of(txtTenNV, cbVaiTro, ngaySinhEditor, txtSDT, txtEmail, txtDiaChi, ngayTGEditor, cbCaLam,
				rbtnNam, rbtnNu, chkDangHoatDong);

		// Gắn KeyListener trực tiếp
		for (JComponent comp : allField) {
			comp.addKeyListener(this);
		}

		// Thiết lập gõ phím cho JComboBoxs
		setupComboKeyboard(cbVaiTro);
		setupComboKeyboard(cbCaLam);

		addField(form, gbc, "Mã nhân viên:", txtMaNV, font);
		addField(form, gbc, "Tên nhân viên:", txtTenNV, font);
		addField(form, gbc, "Vai trò:", cbVaiTro, font);
		addField(form, gbc, "Giới tính:", genderPanel(), font);
		addField(form, gbc, "Ngày sinh:", txtNgaySinh, font);
		addField(form, gbc, "Số điện thoại:", txtSDT, font);
		addField(form, gbc, "Email:", txtEmail, font);
		addField(form, gbc, "Địa chỉ:", txtDiaChi, font);
		addField(form, gbc, "Ngày tham gia:", txtNgayThamGia, font);
		addField(form, gbc, "Trạng thái:", chkDangHoatDong, font);
		addField(form, gbc, "Ca làm:", cbCaLam, font);

		// Nút chức năng
		JPanel footer = new JPanel(new GridBagLayout());
		footer.setBackground(new Color(245, 245, 245));

		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		btnPanel.setBackground(new Color(245, 245, 245));

		btnPanel.add(btnAdd);
		btnPanel.add(btnEdit);
		btnPanel.add(btnFind);
		btnPanel.add(btnClean);

		g.gridy = 0;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 10, 0, 10);
		footer.add(btnPanel, g);

		JLabel lblTieuChi = new JLabel("<html><b><i>Tìm kiếm theo:</i></b> Tên, SĐT, Vai trò, Trạng thái</html>");
		lblTieuChi.setFont(new Font("Roboto", Font.ITALIC, 12));
		lblTieuChi.setForeground(COLOR_TEXT_LABEL);

		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 12, 6, 10);
		footer.add(lblTieuChi, g);

		p.add(form, BorderLayout.CENTER);
		p.add(footer, BorderLayout.SOUTH);
		return p;

	}

	private void addField(JPanel p, GridBagConstraints gbc, String label, JComponent comp, Font font) {
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		JLabel lbl = new JLabel(label);
		lbl.setFont(font);
		p.add(lbl, gbc);

		gbc.gridx = 1;
		gbc.gridwidth = 2;
		comp.setFont(font);
		p.add(comp, gbc);
		gbc.gridy++;
	}

	private JPanel genderPanel() {
		JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		g.setBackground(new Color(245, 245, 245));
		g.add(rbtnNam);
		g.add(rbtnNu);
		return g;
	}

	private JButton createButton(String text, String iconPath) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("Roboto", Font.BOLD, 13));
		btn.setBackground(COLOR_ACCENT);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		try {
			btn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(iconPath)).getImage().getScaledInstance(18,
					18, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			System.err.println("Không tìm thấy icon: " + iconPath);
		}

		return btn;
	}

	// panel chi tiết
	private JPanel panelChiTiet() {
		JPanel info = new JPanel(new BorderLayout());
		info.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin chi tiết",
						TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 15), COLOR_PRIMARY));
		info.setBackground(COLOR_BG_PANEL);

		// Avatar
		lblAvatar = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/gui/icon/png/avatar.png"))
				.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
		info.add(lblAvatar, BorderLayout.NORTH);

		// Chi tiết thông tin
		JPanel details = new JPanel(new GridLayout(11, 2, 10, 5));
		details.setBackground(new Color(245, 245, 245));

		Color color = COLOR_TEXT_LABEL;

		lblMaNVDetail = createValueLabel();
		lblVaiTroDetail = createValueLabel();
		lblTenNVDetail = createValueLabel();
		lblGioiTinhDetail = createValueLabel();
		lblNgaySinhDetail = createValueLabel();
		lblSDTDetail = createValueLabel();
		lblEmailDetail = createValueLabel();
		lblDiaChiDetail = createValueLabel();
		lblNgayThamGiaDetail = createValueLabel();
		lblTrangThaiDetail = createValueLabel();
		lblCaLamDetail = createValueLabel();

		addDetailRow(details, "Mã nhân viên:", lblMaNVDetail, font, color);
		addDetailRow(details, "Vai trò:", lblVaiTroDetail, font, color);
		addDetailRow(details, "Tên nhân viên:", lblTenNVDetail, font, color);
		addDetailRow(details, "Giới tính:", lblGioiTinhDetail, font, color);
		addDetailRow(details, "Ngày sinh:", lblNgaySinhDetail, font, color);
		addDetailRow(details, "Số điện thoại:", lblSDTDetail, font, color);
		addDetailRow(details, "Email:", lblEmailDetail, font, color);
		addDetailRow(details, "Địa chỉ:", lblDiaChiDetail, font, color);
		addDetailRow(details, "Ngày tham gia:", lblNgayThamGiaDetail, font, color);
		addDetailRow(details, "Trạng thái:", lblTrangThaiDetail, font, color);
		addDetailRow(details, "Ca làm:", lblCaLamDetail, font, color);

		info.add(details, BorderLayout.CENTER);
		return info;
	}

	// Đặt placeholder cho 1 JTextField
	private void applyPlaceholder(JTextField field, String placeholder) {
		field.setForeground(Color.GRAY);
		field.setText(placeholder);

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(Color.BLACK);
					field.setFont(font);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().trim().isEmpty()) {
					field.setForeground(Color.GRAY);
					field.setText(placeholder);
					field.setFont(font);
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

	// Gắn placeholder cho các field
	private void initPlaceholders() {
		applyPlaceholder(txtTenNV, "VD: Nguyễn Văn A");
		applyPlaceholder(txtSDT, "VD: 0912345678");
		applyPlaceholder(txtEmail, "VD: email123@gmail.com");
		applyPlaceholder(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");
	}

	// mẫu lable giá trị
	private JLabel createValueLabel() {
		JLabel lbl = new JLabel("");
		lbl.setFont(new Font("Roboto", Font.PLAIN, 14));
		lbl.setForeground(COLOR_TEXT_LABEL);
		return lbl;
	}

	// thêm hàng chi tiết
	private void addDetailRow(JPanel panel, String title, JLabel value, Font font, Color color) {
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(font);
		lblTitle.setForeground(color);
		panel.add(lblTitle);
		panel.add(value);
	}

	private JScrollPane panelTable() {
		String[] cols = { "Mã NV", "Vai trò", "Tên nhân viên", "Giới tính", "Ngày sinh", "Số điện thoại", "Email",
				"Địa chỉ", "Ngày tham gia", "Trạng thái", "Ca làm" };

		model = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(25);
		table.setFont(new Font("Roboto", Font.PLAIN, 13));
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Roboto", Font.BOLD, 14));
		header.setBackground(new Color(30, 41, 58));
		header.setForeground(Color.WHITE);

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (!isSelected) {
					if (row % 2 == 0) {
						c.setBackground(new Color(240, 248, 255));
					} else {
						c.setBackground(Color.WHITE);
					}
				} else {
					c.setBackground(new Color(184, 207, 229));
				}
				setHorizontalAlignment(SwingConstants.CENTER);
				return c;
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setPreferredSize(new Dimension(1000, 200));
		return scroll;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == btnAdd) {
			themNhanVien();
			return;
		}

		if (src == btnClean) {
			cleanInputFields();
			btnEdit.setText("Sửa");
			isEditing = false;
			loadDataToTable();
			return;
		}

		if (src == btnFind) {
			timKiemNhanVien();
		}

		if (src == btnEdit) {
			if (!isEditing) {
				int row = table.getSelectedRow();
				if (row < 0) {
					JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.", "Thông báo",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// Đổ dữ liệu chi tiết sang form
				txtMaNV.setText(lblMaNVDetail.getText());
				cbVaiTro.setSelectedItem(VaiTroNhanVien.valueOf(lblVaiTroDetail.getText()));
				txtTenNV.setText(lblTenNVDetail.getText());
				txtSDT.setText(lblSDTDetail.getText());
				txtEmail.setText(lblEmailDetail.getText());
				txtDiaChi.setText(lblDiaChiDetail.getText());
				cbCaLam.setSelectedItem(lblCaLamDetail.getText());
				chkDangHoatDong.setSelected(lblTrangThaiDetail.getText().equals("Đang hoạt động"));

				if (lblGioiTinhDetail.getText().equalsIgnoreCase("Nữ")) {
					rbtnNu.setSelected(true);
				} else {
					rbtnNam.setSelected(true);
				}

				try {
					String nsStr = lblNgaySinhDetail.getText();
					if (!nsStr.isEmpty()) {
						LocalDate ns = LocalDate.parse(nsStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						txtNgaySinh.setDate(java.sql.Date.valueOf(ns));
					} else {
						txtNgaySinh.setDate(null);
					}
				} catch (Exception ex) {
					txtNgaySinh.setDate(null);
				}

				try {
					String ntgStr = lblNgayThamGiaDetail.getText();
					if (!ntgStr.isEmpty()) {
						LocalDate ntg = LocalDate.parse(ntgStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						txtNgayThamGia.setDate(java.sql.Date.valueOf(ntg));
					} else {
						txtNgayThamGia.setDate(null);
					}
				} catch (Exception ex) {
					txtNgayThamGia.setDate(null);
				}

				btnEdit.setText("Lưu");
				isEditing = true;
			} else {
				if (!validForm()) {
					return;
				}

				suaNhanVien();
				txtNgaySinh.setEnabled(true);
				rbtnNam.setEnabled(true);
				rbtnNu.setEnabled(true);

				btnEdit.setText("Sửa");
				isEditing = false;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == table) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				lblMaNVDetail.setText("" + model.getValueAt(row, 0));
				lblVaiTroDetail.setText("" + model.getValueAt(row, 1));
				lblTenNVDetail.setText("" + model.getValueAt(row, 2));
				lblGioiTinhDetail.setText("" + model.getValueAt(row, 3));
				lblNgaySinhDetail.setText("" + model.getValueAt(row, 4));
				lblSDTDetail.setText("" + model.getValueAt(row, 5));
				lblEmailDetail.setText("" + model.getValueAt(row, 6));
				lblDiaChiDetail.setText("" + model.getValueAt(row, 7));
				lblNgayThamGiaDetail.setText("" + model.getValueAt(row, 8));
				lblTrangThaiDetail.setText("" + model.getValueAt(row, 9));
				lblCaLamDetail.setText("" + model.getValueAt(row, 10));
			}
		}
	}

	// load data len bang
	public void loadDataToTable() {
		model.setRowCount(0);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (NhanVien nv : nhanVien_ctrl.layDanhSachNhanVien()) {
			model.addRow(new Object[] { nv.getNhanVienID(), nv.getVaiTroNhanVien().toString(), nv.getHoTen(),
					nv.isNu() ? "Nữ" : "Nam", nv.getNgaySinh().format(dtf), nv.getSoDienThoai(), nv.getEmail(),
					nv.getDiaChi(), nv.getNgayThamGia().format(dtf),
					nv.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động",
					nv.getCaLam() != null ? nv.getCaLam().getCaLamID() : "" });
		}
	}

	// xóa trắng
	public void cleanInputFields() {
		txtMaNV.setText("");
		txtTenNV.setText("");
		cbVaiTro.setSelectedIndex(0);
		txtNgaySinh.setDateFormatString("dd/MM/yyyy");
		txtNgaySinh.setCalendar(java.util.Calendar.getInstance());
		txtSDT.setText("");
		txtEmail.setText("");
		txtDiaChi.setText("");
		txtNgayThamGia.setDateFormatString("dd/MM/yyyy");
		txtNgayThamGia.setCalendar(java.util.Calendar.getInstance());
		cbCaLam.setSelectedIndex(0);
		rbtnNam.setSelected(true);
		chkDangHoatDong.setSelected(true);

		// panel chi tiet
		lblMaNVDetail.setText("");
		lblVaiTroDetail.setText("");
		lblTenNVDetail.setText("");
		lblGioiTinhDetail.setText("");
		lblNgaySinhDetail.setText("");
		lblSDTDetail.setText("");
		lblEmailDetail.setText("");
		lblDiaChiDetail.setText("");
		lblNgayThamGiaDetail.setText("");
		lblTrangThaiDetail.setText("");
		lblCaLamDetail.setText("");
		initPlaceholders();
	}

	// regex
	private boolean validForm() {
		// Lấy dữ liệu
		String ten = getRealText(txtTenNV, "VD: Nguyễn Văn A");
		String sdt = getRealText(txtSDT, "VD: 0912345678");
		String email = getRealText(txtEmail, "VD: email123@gamil.com");
		String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");
		String caLam = (String) cbCaLam.getSelectedItem();

		// 1) Tên
		if (!nhanVien_ctrl.validHoTen(ten)) {
			JOptionPane.showMessageDialog(this, "Tên không hợp lệ VD: Nguyễn Văn A", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtTenNV.requestFocus();
			return false;
		} else if (ten.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên không được để trống", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
			txtTenNV.requestFocus();
			return false;
		}

		// 2) Số điện thoại
		if (!nhanVien_ctrl.validSDT(sdt)) {
			JOptionPane.showMessageDialog(this,
					"Số điện thoại không hợp lệ (đầu 0 theo dải nhà mạng VN.VD: 0912345678).", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtSDT.requestFocus();
			return false;
		} else if (sdt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtSDT.requestFocus();
			return false;
		}

		// 3) Email
		if (!email.isEmpty() && !nhanVien_ctrl.validEmail(email)) {
			JOptionPane.showMessageDialog(this, "Email không hợp lệ! VD: haNguyen123@gmail.com", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtEmail.requestFocus();
			return false;
		} else if (email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Email không được để trống", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtEmail.requestFocus();
			return false;
		}
		// 4) Địa chỉ
		if (!nhanVien_ctrl.validDiaChi(diaChi)) {
			JOptionPane.showMessageDialog(this,
					"Địa chỉ không hợp lệ (tối thiểu 5 ký tự, cho phép chữ/số/khoảng trắng , . -). VD: 45/2 Nguyễn Huệ, Quận 1",
					"Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
			txtDiaChi.requestFocus();
			return false;
		} else if (diaChi.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtDiaChi.requestFocus();
			return false;
		}

		// 6) Ngày sinh (phải chọn và < hôm nay)
		if (txtNgaySinh.getDate() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
			txtNgaySinh.requestFocus();
			return false;
		} else {
			// chuyển đổi Date sang LocalDate
			LocalDate ns = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
			if (!nhanVien_ctrl.ngaySinh(ns)) {
				JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (phải trước ngày hiện tại).", "Lỗi dữ liệu",
						JOptionPane.WARNING_MESSAGE);
				txtNgaySinh.requestFocus();
				return false;
			}
		}
		// 7) Ngày tham gia (nếu nhập phải đúng định dạng dd/MM/yyyy và không sau hôm
		// nay)
		if (txtNgayThamGia.getDate() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tham gia.", "Lỗi dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			txtNgayThamGia.requestFocus();
			return false;
		} else {
			LocalDate ntg = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
			if (!nhanVien_ctrl.ngayThamGia(ntg)) {
				JOptionPane.showMessageDialog(this, "Ngày tham gia không hợp lệ (không được sau hôm nay).",
						"Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
				txtNgayThamGia.requestFocus();
				return false;
			}
		}

		return true;
	}

	// Them nhan vien
	public void themNhanVien() {
		if (!validForm()) {
			return;
		}

		String maNV = nhanVien_ctrl.taoMaNhanVien();
		VaiTroNhanVien vaiTro = (VaiTroNhanVien) cbVaiTro.getSelectedItem();
		String hoTen = getRealText(txtTenNV, "VD: Nguyễn Văn A");
		boolean isNu = rbtnNu.isSelected();
		LocalDate ngaySinh = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
		String soDienThoai = getRealText(txtSDT, "VD: 0912345678");
		String email = getRealText(txtEmail, "VD: email123@gmail.com");
		String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");

		LocalDate ngayThamGia = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault())
				.toLocalDate();

		boolean isHoatDong = chkDangHoatDong.isSelected();
		CaLam caLam = new CaLam((String) cbCaLam.getSelectedItem() == "Sáng" ? "CA01" : "CA02");

		NhanVien nv = new NhanVien(maNV, vaiTro, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia,
				isHoatDong, caLam);

		boolean success = nhanVien_ctrl.themNhanVien(nv);
		if (success) {
			JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
			loadDataToTable();
			cleanInputFields();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại! Vui lòng kiểm tra lại.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// sua nhan vien
	public void suaNhanVien() {
		try {
			String maNV = txtMaNV.getText().trim();
			VaiTroNhanVien vaiTro = (VaiTroNhanVien) cbVaiTro.getSelectedItem();
			String hoTen = getRealText(txtTenNV, "VD: Nguyễn Văn A");
			boolean isNu = rbtnNu.isSelected();

			LocalDate ngaySinh = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault())
					.toLocalDate();

			String soDienThoai = getRealText(txtSDT, "VD: 0912345678");
			String email = getRealText(txtEmail, "VD: email123@gmail.com");
			String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");

			LocalDate ngayThamGia = null;
			if (txtNgayThamGia.getDate() != null) {
				ngayThamGia = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault())
						.toLocalDate();
			}

			boolean isHoatDong = chkDangHoatDong.isSelected();
			CaLam caLam = new CaLam((String) cbCaLam.getSelectedItem() == "Sáng" ? "CA01" : "CA02");

			NhanVien nv = new NhanVien(maNV, vaiTro, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia,
					isHoatDong, caLam);
			int confirm = JOptionPane.showConfirmDialog(this,
					"Bạn có chắc chắn muốn cập nhật thông tin nhân viên này không?", "Xác nhận cập nhật",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (confirm != JOptionPane.YES_OPTION) {
				return;
			}

			boolean success = nhanVien_ctrl.suaNhanVien(nv);
			if (success) {
				JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				loadDataToTable();
				cleanInputFields();
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	// tim kiem nhan vien
	public void timKiemNhanVien() {
		String ten = getRealText(txtTenNV, "VD: Nguyễn Văn A");
		String sdt = getRealText(txtSDT, "VD: 0912345678");
		VaiTroNhanVien vaiTro = (VaiTroNhanVien) cbVaiTro.getSelectedItem();
		Boolean isHoatDong = chkDangHoatDong.isSelected();

		model.setRowCount(0);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (NhanVien nv : nhanVien_ctrl.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong)) {
			model.addRow(new Object[] { nv.getNhanVienID(), nv.getVaiTroNhanVien().toString(), nv.getHoTen(),
					nv.isNu() ? "Nữ" : "Nam", nv.getNgaySinh().format(dtf), nv.getSoDienThoai(), nv.getEmail(),
					nv.getDiaChi(), nv.getNgayThamGia().format(dtf),
					nv.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động",
					nv.getCaLam() != null ? nv.getCaLam() : "" });
		}

		if (model.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp với tiêu chí đã chọn.", "Kết quả",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			Component current = (Component) e.getSource();

			if (current instanceof JCheckBox checkbox) {
				checkbox.doClick();
			}

			int index = allField.indexOf(current);
			if (index != -1) {
				if (index < allField.size() - 1) {
					allField.get(index + 1).requestFocus();
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
