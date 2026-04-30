package gui.application;
/*
 * @(#) EmailService.java  1.0  [12:43:22 PM] Dec 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 14, 2025
 * @version: 1.0
 */

import entity.DonDatCho;
import entity.Ve;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class EmailService {

    private static final String SENDER_EMAIL = "huynhnhu.gmeet@gmail.com";
    private static final String SENDER_PASSWORD = "ypjj fkwx dpgu odvt";

    public static void sendTicketEmail(String recipientEmail, List<Ve> listVe, DonDatCho donDatCho, double tongTien) {
        // 1. Cấu hình SMTP Server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 2. Tạo phiên làm việc
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            // 3. Tạo nội dung Email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "F4 GA SÀI GÒN"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("XÁC NHẬN ĐẶT VÉ THÀNH CÔNG - MÃ ĐẶT CHỖ: " + donDatCho.getDonDatChoID());

            // Tạo nội dung HTML đẹp mắt
            String htmlContent = generateHtmlContent(listVe, donDatCho.getDonDatChoID(), tongTien);

            // Thiết lập nội dung là HTML (UTF-8 để hiện tiếng Việt)
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            // 4. Gửi Email
            Transport.send(message);
            System.out.println(">> Đã gửi email vé thành công tới: " + recipientEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }

    // Hàm phụ trợ: Tạo giao diện vé tàu bằng HTML
    private static String generateHtmlContent(List<Ve> listVe, String donDatCho, double tongTien) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif;'>");
        sb.append("<div style='background-color: #f4f4f4; padding: 20px;'>");
        sb.append(
                "<div style='background-color: white; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto;'>");

        sb.append("<h2 style='color: #28a745; text-align: center;'>ĐẶT VÉ THÀNH CÔNG</h2>");
        sb.append(
                "<p>Cảm ơn quý khách đã sử dụng dịch vụ của F4 Ga Sài Gòn. Dưới đây là thông tin vé điện tử của quý khách.</p>");

        sb.append("<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>");
        sb.append("<tr style='background-color: #007bff; color: white;'>")
                .append("<th style='padding: 10px;'>Mã Vé</th>").append("<th style='padding: 10px;'>Tàu/Toa/Ghế</th>")
                .append("<th style='padding: 10px;'>Hành Khách</th>").append("</tr>");

        for (Ve ve : listVe) {
            sb.append("<tr>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center;'><b>").append(ve.isVeDoi())
                    .append("</b></td>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append("Tàu: ")
                    .append(ve.getGhe().getToa().getTau().getTauID()).append("<br>").append("Toa: ")
                    .append(ve.getGhe().getToa().getSoToa()).append("<br>").append("Ghế: ")
                    .append(ve.getGhe().getSoGhe()).append("</td>");
            sb.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(ve.getKhachHang().getHoTen())
                    .append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        sb.append("<h3 style='text-align: right; color: #d9534f;'>Tổng tiền: ").append(String.format("%,.0f", tongTien))
                .append(" VNĐ</h3>");

        sb.append("<hr>");
        sb.append(
                "<p style='font-size: 12px; color: #777;'>Đây là email tự động. Vui lòng mang theo CCCD/CMND khi lên tàu.</p>");

        sb.append("</div></div></body></html>");
        return sb.toString();
    }

    public static boolean sendForgotPasswordEmail(String toEmail, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SENDER_EMAIL, "F4 GA SÀI GÒN"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            msg.setSubject("Mã xác nhận cấp lại mật khẩu - Phần mềm quản lý hệ thống bán vé tàu Ga Sài Gòn");
            msg.setText("Xin chào,\n\nMã xác nhận để đặt lại mật khẩu của bạn là: " + code
                    + "\n\nVui lòng không chia sẻ mã này cho người khác.\n\nTrân trọng.");

            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}