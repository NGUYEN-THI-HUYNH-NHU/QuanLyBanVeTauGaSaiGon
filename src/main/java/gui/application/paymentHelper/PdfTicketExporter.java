package gui.application.paymentHelper;
/*
 * @(#) PdfTicketExporter.java  1.0  [5:39:21 PM] Dec 8, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dto.VeDTO;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfTicketExporter {
    // CẤU HÌNH KÍCH THƯỚC AN TOÀN CHO K57
    // Khổ giấy vật lý là 58mm (~164pt).
    // Tuy nhiên vùng in khả dụng chỉ khoảng 48mm (~136pt).
    // Ta set chiều rộng PDF là 138pt để đảm bảo lọt lòng, không bị mất mép.
    private static final float PAGE_WIDTH = 138f;
    private static final float PAGE_HEIGHT = 600f;
    // Lề trong file PDF (Nhỏ thôi vì giấy đã hẹp rồi)
    private static final float MARGIN_X = 2f;
    private static final float MARGIN_Y = 5f;
    private final DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
    private PDType0Font fontRegular;
    private PDType0Font fontBold;

    public PdfTicketExporter() {
    }

    // 1. ENTRY POINTS
    public void exportTicketsToPdf(BookingSession session) {
        if (session == null || session.getAllSelectedTickets().isEmpty()) {
            return;
        }
        processDirectPrint(session.getAllSelectedTickets(), session.getDonDatCho().getId());
    }

    public void exportTicketsToPdf(VeDTO ve) {
        if (ve == null) {
            return;
        }
        processDirectPrint(List.of(new VeSession(ve)), ve.getDonDatChoID());
    }

    public void exportTicketsToPdf(ExchangeSession exchangeSession) {
        if (exchangeSession == null || exchangeSession.getListVeMoiDangChon().isEmpty()) {
            return;
        }
        processDirectPrint(exchangeSession.getListVeMoiDangChon(), exchangeSession.getDonDatChoMoi().getId());
    }

    // 2. XỬ LÝ IN ẤN (RASTER MODE)
    private void processDirectPrint(List<VeSession> tickets, String donDatChoID) {
        try {
            File tempFile = File.createTempFile("Job_Print_", ".pdf");
            tempFile.deleteOnExit();
            createPdfDocument(tempFile, tickets, donDatChoID);
            printPdfAsImage(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi in: " + e.getMessage());
        }
    }

    private void printPdfAsImage(File pdfFile) {
        try {
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            if (defaultService == null) {
                JOptionPane.showMessageDialog(null, "Chưa chọn máy in mặc định!");
                return;
            }

            PDDocument document = PDDocument.load(pdfFile);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(defaultService);

            PageFormat pageFormat = job.defaultPage();
            Paper paper = pageFormat.getPaper();

            // Set khổ giấy vật lý cho Job là 58mm (~164pt)
            // Lưu ý: Đây là khổ giấy CỦA MÁY IN, không phải của file PDF
            double paperW = 164;
            double paperH = 600;
            paper.setSize(paperW, paperH);
            paper.setImageableArea(0, 0, paperW, paperH);
            pageFormat.setPaper(paper);

            job.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
                    if (pageIndex >= document.getNumberOfPages()) {
                        return NO_SUCH_PAGE;
                    }

                    try {
                        Graphics2D g2d = (Graphics2D) graphics;
                        // Dịch chuyển về vùng in khả dụng
                        g2d.translate(pf.getImageableX(), pf.getImageableY());

                        PDFRenderer renderer = new PDFRenderer(document);
                        // Scale 2.0 để nét hơn
                        BufferedImage image = renderer.renderImage(pageIndex, 2.0f, ImageType.BINARY);

                        // TÍNH TOÁN LẠI TỌA ĐỘ VẼ ĐỂ CĂN GIỮA
                        // pf.getWidth() là chiều rộng khổ giấy máy in (164)
                        // image.getWidth() là chiều rộng ảnh (đã nhân 2.0)

                        double clientWidth = pf.getWidth();
                        double clientHeight = pf.getHeight();

                        // Tính tỷ lệ scale để ảnh PDF (138pt) vừa khít hoặc nhỏ hơn giấy (164pt)
                        // Ta muốn in ảnh rộng khoảng 138-140pt thực tế
                        double targetWidth = 138;

                        // Nếu máy in báo vùng in nhỏ hơn 138, thì co lại theo vùng in
                        if (clientWidth < targetWidth) {
                            targetWidth = clientWidth;
                        }

                        double scale = targetWidth / image.getWidth(); // Tỷ lệ thu nhỏ ảnh

                        int drawW = (int) (image.getWidth() * scale);
                        int drawH = (int) (image.getHeight() * scale);

                        // Căn giữa theo chiều ngang (Center Horizontal)
                        int x = (int) ((clientWidth - drawW) / 2);

                        g2d.drawImage(image, x, 0, drawW, drawH, null);

                        return PAGE_EXISTS;
                    } catch (IOException e) {
                        return NO_SUCH_PAGE;
                    }
                }
            }, pageFormat);

            job.print();
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. VẼ PDF (LAYOUT CHO PAGE_WIDTH = 138pt)
    private void createPdfDocument(File fileToSave, List<VeSession> tickets, String donDatChoID) {
        try (PDDocument document = new PDDocument()) {
            loadFonts(document);

            for (VeSession ticket : tickets) {
                // Tạo trang với kích thước nhỏ (138pt)
                PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
                document.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    drawTicketK57(document, cs, ticket, donDatChoID);
                }
            }
            document.save(fileToSave);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFonts(PDDocument document) throws IOException {
        InputStream fontStreamReg = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
        InputStream fontStreamBold = getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf");
        if (fontStreamReg == null || fontStreamBold == null) {
            throw new IOException("Font not found");
        }
        fontRegular = PDType0Font.load(document, fontStreamReg);
        fontBold = PDType0Font.load(document, fontStreamBold);
    }

    private void drawTicketK57(PDDocument document, PDPageContentStream cs, VeSession ticket, String donDatChoID)
            throws IOException {
        float centerX = PAGE_WIDTH / 2;
        float y = PAGE_HEIGHT - MARGIN_Y - 5;
        VeDTO ve = ticket.getVe();

        // --- BẮT ĐẦU VẼ ---
        cs.setNonStrokingColor(0, 0, 0);

        // Header
        drawCenteredText(cs, fontBold, 9, "F4 - ĐƯỜNG SẮT VIỆT NAM", centerX, y);
        y -= 10;
        drawCenteredText(cs, fontRegular, 8, "Ga Sài Gòn", centerX, y);
        y -= 18;

        // Title
        drawCenteredText(cs, fontBold, 12, "THẺ LÊN TÀU", centerX, y);
        y -= 10;
        drawCenteredText(cs, fontRegular, 7, "(BOARDING PASS)", centerX, y);
        y -= 15;

        // QR Code (Giảm size còn 75 cho vừa khổ 138)
        float qrSize = 75;
        float qrX = (PAGE_WIDTH - qrSize) / 2;
        try {
            BufferedImage qrImage = createQRCode(ve.getVeID(), 200, 200);
            PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);
            cs.drawImage(pdImage, qrX, y - qrSize, qrSize, qrSize);
        } catch (Exception e) {
        }
        y -= (qrSize + 8);

        // Mã vé
        drawCenteredText(cs, fontBold, 8, "Vé: " + ve.getVeID(), centerX, y);
        y -= 10;
        drawCenteredText(cs, fontRegular, 8, "ĐĐC: " + donDatChoID, centerX, y);
        y -= 20;

        // Ga Đi - Ga Đến
        drawLeftText(cs, fontRegular, 8, "Ga đi:", MARGIN_X, y);
        drawRightText(cs, fontBold, 9, ve.getTenGaDi(), PAGE_WIDTH - MARGIN_X, y);
        y -= 12;
        drawCenteredText(cs, fontRegular, 8, "v", centerX, y);
        y -= 12;
        drawLeftText(cs, fontRegular, 8, "Ga đến:", MARGIN_X, y);
        drawRightText(cs, fontBold, 9, ve.getTenGaDen(), PAGE_WIDTH - MARGIN_X, y);
        y -= 15;

        // Kẻ dòng
        cs.setLineDashPattern(new float[]{2}, 0);
        cs.moveTo(MARGIN_X, y);
        cs.lineTo(PAGE_WIDTH - MARGIN_X, y);
        cs.stroke();
        cs.setLineDashPattern(new float[]{}, 0);
        y -= 15;

        // Chi tiết
        y = drawRowSmart(cs, "Tàu:", ve.getTauID(), y);
        y = drawRowSmart(cs, "Ngày:", ve.getNgayGioDi().format(dtfDate), y);
        y = drawRowSmart(cs, "Giờ:", ve.getNgayGioDi().format(dtfTime), y);
        y -= 5;

        // Toa/Ghế
        cs.beginText();
        cs.setFont(fontBold, 10);
        cs.newLineAtOffset(MARGIN_X, y);
        cs.showText("TOA " + ve.getSoToa());
        cs.endText();
        drawRightText(cs, fontBold, 10, "GHẾ " + ve.getSoGhe(), PAGE_WIDTH - MARGIN_X, y);
        y -= 15;

        y = drawRowSmart(cs, "Hạng toa:", ve.getHangToaID(), y);

        // Tên khách
        String tenKhach = ve.getKhachHangDTO().getHoTen().toUpperCase();
        if (tenKhach.length() > 20) {
            tenKhach = tenKhach.substring(0, 18) + "..";
        }
        cs.beginText();
        cs.setFont(fontRegular, 8);
        cs.newLineAtOffset(MARGIN_X, y);
        cs.showText("Hành khách:");
        cs.endText();
        y -= 10;
        cs.beginText();
        cs.setFont(fontBold, 9);
        cs.newLineAtOffset(MARGIN_X + 10, y);
        cs.showText(tenKhach);
        cs.endText();
        y -= 12;

        y = drawRowSmart(cs, "Số giấy tờ:", ve.getKhachHangDTO().getSoGiayTo(), y);

        // Footer
        y -= 10;
        cs.setLineDashPattern(new float[]{3}, 0);
        cs.moveTo(0, y);
        cs.lineTo(PAGE_WIDTH, y);
        cs.stroke();
        y -= 10;
        drawCenteredText(cs, fontRegular, 7, "Vui lòng đến trước 30 phút!", centerX, y);
        y -= 8;
        drawCenteredText(cs, fontRegular, 7, "Xin cảm ơn!", centerX, y);

        // VÙNG CẮT GIẤY (MANUAL CUT AREA)
        // Khoảng cách này giúp đẩy nội dung vé ra khỏi đầu in để xé không bị rách chữ
        y -= 25;
        drawCenteredText(cs, fontRegular, 7, "- - - - - - Cắt tại đây - - - - - -", centerX, y);
    }

    // UTILS
    private float drawRowSmart(PDPageContentStream cs, String label, String value, float y) throws IOException {
        cs.beginText();
        cs.setFont(fontRegular, 8);
        cs.newLineAtOffset(MARGIN_X, y);
        cs.showText(label);
        cs.endText();
        drawRightText(cs, fontBold, 8, value, PAGE_WIDTH - MARGIN_X, y);
        return y - 12;
    }

    private void drawCenteredText(PDPageContentStream cs, PDType0Font font, float size, String text, float centerX,
                                  float y) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(centerX - (textWidth / 2), y);
        cs.showText(text);
        cs.endText();
    }

    private void drawLeftText(PDPageContentStream cs, PDType0Font font, float size, String text, float x, float y)
            throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawRightText(PDPageContentStream cs, PDType0Font font, float size, String text, float rightX, float y)
            throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(rightX - textWidth, y);
        cs.showText(text);
        cs.endText();
    }

    private BufferedImage createQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}