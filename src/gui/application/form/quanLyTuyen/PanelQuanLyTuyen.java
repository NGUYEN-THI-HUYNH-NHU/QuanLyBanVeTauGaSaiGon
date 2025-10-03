package gui.application.form.quanLyTuyen;/*
 * @ (#) PanelQuanLyTuyen.java   1.0     29/09/2025
package gui.application.form.quanLyTuyen;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 29/09/2025
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import dao.Tuyen_DAO;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class PanelQuanLyTuyen extends JPanel {

    private final Tuyen_DAO tuyen_dao;

    private final NhanVien nhanVienThucHien;

    private JTextField txtGaDi;
    private JTextField txtGaDen;
    private JTextField txtTimKiem;

    private JButton btnTimKiem;
    private JButton btnThemTuyen;
    private JButton btnCapNhatTuyen;
    private JButton btnLamMoiTuyen;

    public PanelQuanLyTuyen(NhanVien nhanVien){
        setLayout(new BorderLayout());

        this.tuyen_dao = new Tuyen_DAO();
        this.nhanVienThucHien = nhanVien;

        initComponents();
    }

    private Icon getIcon(String iconFileName){
        FlatSVGIcon icon = new FlatSVGIcon("/gui/icon/svg/" + iconFileName);

        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.BLACK);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.WHITE);

        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#969696"), lightColor, darkColor);

        icon.setColorFilter(f);

        return icon;
    }

    public void initComponents(){
        JPanel panelNorth = new JPanel(new BorderLayout());

        // --- 1. HEADER PANEL ---
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new MigLayout("wrap 1, fillx, insets 10 10 5 10"));

        JLabel title = new JLabel("QUẢN LÝ VÀ TRA CỨU TUYẾN ĐƯỜNG SẮT", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        panelHeader.add(title, "growx");

        //Tìm kiếm
        JPanel panelSearch = new JPanel(new MigLayout("insets 5 10 10 10, gap 10"));
        txtGaDen = new JTextField(15);
        txtGaDi = new JTextField(15);
        btnLamMoiTuyen = new JButton("Làm mới tuyến");
        txtTimKiem = new JTextField(10);
        btnTimKiem = new JButton("Tìm kiếm");
        btnThemTuyen = new JButton("Thêm tuyến");
        btnCapNhatTuyen = new JButton("Cập nhật tuyến");

        btnTimKiem.setIcon(getIcon("search.svg"));
        btnTimKiem.setIconTextGap(8);

        btnLamMoiTuyen.setIcon(getIcon("refesh.svg"));
        btnLamMoiTuyen.setIconTextGap(8);

        btnThemTuyen.setIcon(getIcon("add.svg"));
        btnThemTuyen.setIconTextGap(8);

        btnCapNhatTuyen.setIcon(getIcon("edit.svg"));
        btnCapNhatTuyen.setIconTextGap(8);

        panelSearch.add(new JLabel("Ga Đi:"));
        panelSearch.add(txtGaDi, "w 150");

        panelSearch.add(new JLabel("Ga Đến:"));
        panelSearch.add(txtGaDen, "w 150");

        panelSearch.add(new JLabel("Mã Tuyến:"));
        panelSearch.add(txtTimKiem);

        panelSearch.add(btnTimKiem);
        panelSearch.add(btnThemTuyen);
        panelSearch.add(btnCapNhatTuyen);
        panelSearch.add(btnLamMoiTuyen);

        panelHeader.add(panelSearch, "growx");
        panelNorth.add(panelHeader, BorderLayout.NORTH);


        add(panelNorth, BorderLayout.NORTH);

    }



}
