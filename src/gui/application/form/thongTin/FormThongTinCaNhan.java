package gui.application.form.thongTin;
/*
 * @(#) FormThongTinCaNhan.java  1.0  [1:01:02 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */
//
//import java.awt.BorderLayout;
//import java.awt.Font;
//import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import javax.swing.filechooser.FileNameExtensionFilter;
//
//import org.apache.commons.io.FilenameUtils;
//
//import dao.NhanVien_DAO;
//import entity.NhanVien;
//import net.miginfocom.swing.MigLayout;
//import raven.crazypanel.CrazyPanel;
//
//public class FormThongTinCaNhan {
//
//    private static final long serialVersionUID = 1L;
//    private CrazyPanel container;
//    private JLabel nhanVienIDLabel, fullNameLabel, genderLabel, dateOfBirthLabel, emailLabel,
//                     phoneNumberLabel, roleLabel, startingDateLabel, salaryLabel, imageSourceDisplay;
//    private JTextField nhanVienIDTextField, fullNameTextField, genderTextField, dateOfBirthTextField,
//                       emailTextField, phoneNumberTextField, roleTextField, startingDateTextField, salaryTextField;
//    private JLabel title;
//    private JButton changeAvatarButton;
//    private JFileChooser fileChooser;
//    private FileNameExtensionFilter filter;
//    private File selectedFile;
//    private String imagePath;
//    private NhanVien_DAO nhanVien_DAO;
//
//    public FormProfileInfo(NhanVien nhanVien) {
//        setLayout(new BorderLayout());
//        nhanVien_DAO = new NhanVien_DAO();
//        fileChooser = new JFileChooser();
//        initComponents(nhanVien);
//    }
//
//    private void initComponents(NhanVien nhanVien) {
//        container = new CrazyPanel();
//        title = new JLabel("Thông tin cá nhân");
//        imageSourceDisplay = new JLabel();
//
//        // TextFields with adjusted lengths
//        nhanVienIDTextField = new JTextField(20);
//        fullNameTextField = new JTextField(20);
//        genderTextField = new JTextField(10);
//        dateOfBirthTextField = new JTextField(15);
//        emailTextField = new JTextField(20);
//        phoneNumberTextField = new JTextField(15);
//        roleTextField = new JTextField(15);
//        startingDateTextField = new JTextField(15);
//        salaryTextField = new JTextField(15);
//
//        boolean isValid = nhanVien.getImageSource() != null && new File(nhanVien.getImageSource()).exists();
//
//        Image avatarImage = new ImageIcon(isValid ? nhanVien.getImageSource() : "images/default.png")
//                .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//        imageSourceDisplay.setIcon(new ImageIcon(avatarImage));
//
//        // Set texts
//        nhanVienIDTextField.setText(nhanVien.getNhanVienID());
//        fullNameTextField.setText(nhanVien.getFullName());
//        genderTextField.setText(nhanVien.isGender() ? "Nam" : "Nữ");
//        dateOfBirthTextField.setText(nhanVien.getDateOfBirth().toString());
//        emailTextField.setText(nhanVien.getEmail());
//        phoneNumberTextField.setText(nhanVien.getPhoneNumber());
//        roleTextField.setText(nhanVien.getRole());
//        startingDateTextField.setText(nhanVien.getStartingDate().toString());
//        salaryTextField.setText(Double.toString(nhanVien.getSalary()));
//
//        // Labels
//        nhanVienIDLabel = new JLabel("ID Nhân viên:");
//        fullNameLabel = new JLabel("Họ và Tên:");
//        genderLabel = new JLabel("Giới tính:");
//        dateOfBirthLabel = new JLabel("Ngày sinh:");
//        emailLabel = new JLabel("Email:");
//        phoneNumberLabel = new JLabel("SĐT:");
//        roleLabel = new JLabel("Chức vụ:");
//        startingDateLabel = new JLabel("Ngày bắt đầu:");
//        salaryLabel = new JLabel("Lương:");
//        changeAvatarButton = new JButton("Đổi Avatar");
//
//        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 22));
//
//        // MigLayout setup
//        container.setLayout(new MigLayout(
//                "wrap 2, fillx, insets 10 20 10 20, gap 10",
//                "[right]10[fill, grow]"
//        ));
//
//        container.add(title, "wrap, span, align left, gapbottom 10");
//        container.add(imageSourceDisplay, "wrap, span, align center");
//        container.add(changeAvatarButton, "wrap, span, align center, gapbottom 15");
//
//        // Add fields
//        container.add(nhanVienIDLabel); container.add(nhanVienIDTextField);
//        container.add(fullNameLabel); container.add(fullNameTextField);
//        container.add(genderLabel); container.add(genderTextField);
//        container.add(dateOfBirthLabel); container.add(dateOfBirthTextField);
//        container.add(emailLabel); container.add(emailTextField);
//        container.add(phoneNumberLabel); container.add(phoneNumberTextField);
//        container.add(roleLabel); container.add(roleTextField);
//        container.add(startingDateLabel); container.add(startingDateTextField);
//        container.add(salaryLabel); container.add(salaryTextField);
//
//        // Add scroll pane
//        JScrollPane scrollPane = new JScrollPane(container);
//        scrollPane.setBorder(null);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Set fields as non-editable
//        for (JTextField tf : Arrays.asList(
//                nhanVienIDTextField, fullNameTextField, genderTextField, dateOfBirthTextField,
//                emailTextField, phoneNumberTextField, roleTextField, startingDateTextField, salaryTextField)) {
//            tf.setEditable(false);
//        }
//
//        changeAvatarButton.addActionListener(e -> {
//            fileChooser.setDialogTitle("Choose Image File");
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            fileChooser.setAcceptAllFileFilterUsed(false);
//            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
//
//            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//                selectedFile = fileChooser.getSelectedFile();
//                imagePath = saveImage(selectedFile, nhanVien);
//                displayImage(imagePath);
//            }
//        });
//    }
//
//    private String saveImage(File imageFile, NhanVien nhanVien) {
//        try {
//            BufferedImage image = ImageIO.read(imageFile);
//            String extension = FilenameUtils.getExtension(imageFile.getName()).toLowerCase();
//            if (!Arrays.asList("jpg", "png", "gif", "bmp").contains(extension)) return null;
//
//            String imagePath = "images/" + imageFile.getName();
//            File destinationFile = new File(imagePath);
//            ImageIO.write(image, extension, destinationFile);
//            updateImagePathInDatabase(imagePath, nhanVien);
//            return imagePath;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void updateImagePathInDatabase(String imagePath, NhanVien nhanVien) {
//        if (nhanVien_DAO.updateAvatar(imagePath, nhanVien.getNhanVienID())) {
//            nhanVien.setImageSource(imagePath);
//        }
//    }
//
//    private void displayImage(String imagePath) {
//        if (imagePath != null) {
//            Image scaledImage = new ImageIcon(imagePath).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//            imageSourceDisplay.setIcon(new ImageIcon(scaledImage));
//        }
//    }
//}