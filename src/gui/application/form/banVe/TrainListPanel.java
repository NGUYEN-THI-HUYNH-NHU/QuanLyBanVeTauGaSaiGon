package gui.application.form.banVe;
/*
 * @(#) TrainListPanel.java  1.0  [5:02:14 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import javax.swing.*;
import javax.swing.border.*;

import entity.Tau;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class TrainListPanel extends JPanel {
 private final JPanel content; // panel chứa các card ngang
 private final JScrollPane scroll;
 private final List<TrainCard> cards = new ArrayList<>();
 private final List<TrainSelectionListener> listeners = new ArrayList<>();
 private TrainCard selectedCard = null;
 private final JLabel lblNoResult = new JLabel("Không tìm thấy chuyến phù hợp", SwingConstants.CENTER);

 public TrainListPanel() {
     setLayout(new BorderLayout());
     content = new JPanel();
     content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
     content.setBackground(Color.WHITE);

     scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
     scroll.getHorizontalScrollBar().setUnitIncrement(16);
     add(scroll, BorderLayout.CENTER);

     lblNoResult.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
     lblNoResult.setFont(lblNoResult.getFont().deriveFont(Font.ITALIC));
 }

 public void setTrains(List<Tau> trains) {
     content.removeAll();
     cards.clear();
     selectedCard = null;
     if (trains == null || trains.isEmpty()) {
         add(lblNoResult, BorderLayout.CENTER);
         revalidate();
         repaint();
         return;
     } else {
         remove(lblNoResult);
     }

     for (Tau t : trains) {
         TrainCard c = new TrainCard(t);
         c.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 selectCard(c);
                 fireSelected(t);
             }
         });
         cards.add(c);
         content.add(Box.createRigidArea(new Dimension(8, 0))); // gap
         content.add(c);
     }
     content.add(Box.createHorizontalGlue());
     revalidate();
     repaint();
 }

 public void clear() {
     content.removeAll();
     cards.clear();
     selectedCard = null;
     revalidate(); repaint();
 }

 public void addTrainSelectionListener(TrainSelectionListener l) {
     listeners.add(l);
 }

 private void fireSelected(Tau t) {
     for (TrainSelectionListener l : listeners) l.trainSelected(t);
 }

 private void selectCard(TrainCard c) {
     if (selectedCard != null) selectedCard.setSelected(false);
     selectedCard = c;
     if (c != null) c.setSelected(true);
 }

 public Optional<Tau> getSelectedTrain() {
     return Optional.ofNullable(selectedCard).map(TrainCard::getTrain);
 }

 // --- inner TrainCard class ---
 private static class TrainCard extends JPanel {
     private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");
     private final Tau train;
     private boolean selected = false;

     private final Color BASE = new Color(245,245,245);
     private final Color HOVER = new Color(230,240,255);
     private final Color SELECTED = new Color(200,230,255);

     public TrainCard(Tau train) {
         this.train = train;
         initUI();
     }

     public Tau getTrain() { return train; }

     private void initUI() {
         setPreferredSize(new Dimension(200, 120));
         setMaximumSize(new Dimension(220, 130));
         setBorder(new CompoundBorder(new LineBorder(Color.GRAY,1), new EmptyBorder(6,6,6,6)));
         setBackground(BASE);
         setLayout(new BorderLayout(6,6));

         // Image on left
         JLabel img = new JLabel();
         ImageIcon icon = loadIcon("/img", 56, 56);
         img.setIcon(icon);
         img.setPreferredSize(new Dimension(56,56));
         add(img, BorderLayout.WEST);

         // center panel with labels
         JPanel center = new JPanel(new GridBagLayout());
         center.setOpaque(false);
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
         JLabel lblName = new JLabel(train.getTenTau());
         lblName.setFont(lblName.getFont().deriveFont(Font.BOLD, 12f));
         center.add(lblName, gbc);

         gbc.gridy++;
         String times = "dd/mm/yyyy -> DD/MM/YYYY";
         JLabel lblTimes = new JLabel(times);
         lblTimes.setFont(lblTimes.getFont().deriveFont(11f));
         center.add(lblTimes, gbc);

         gbc.gridy++;
         JLabel lblCounts = new JLabel(String.format("Đã chọn: %d  •  Trống: %d", 0, 50));
         lblCounts.setFont(lblCounts.getFont().deriveFont(11f));
         lblCounts.setForeground(Color.DARK_GRAY);
         center.add(lblCounts, gbc);

         add(center, BorderLayout.CENTER);

         // small badge area (right/bottom)
         JPanel badge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
         badge.setOpaque(false);
         add(badge, BorderLayout.EAST);

         // tooltip
         setToolTipText(train.getTenTau()+ " — " + times);

         // hover & click visuals
         addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { if (!selected) setBackground(HOVER); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
             @Override public void mouseExited(MouseEvent e) { if (!selected) setBackground(BASE); setCursor(Cursor.getDefaultCursor()); }
             @Override public void mousePressed(MouseEvent e) { /* click handled externally */ }
         });
     }

     public void setSelected(boolean sel) {
         this.selected = sel;
         setBackground(sel ? SELECTED : BASE);
         repaint();
     }

     private ImageIcon loadIcon(String path, int w, int h) {
         try {
             java.net.URL url = getClass().getResource(path);
             if (url != null) {
                 Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                 return new ImageIcon(img);
             }
         } catch (Exception ex) { /* ignore */ }
         // fallback: simple colored rectangle icon
         BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g = bi.createGraphics();
         g.setColor(new Color(180,180,180));
         g.fillRoundRect(0,0,w,h,8,8);
         g.setColor(Color.WHITE);
         g.drawString("Loco", 6, h/2);
         g.dispose();
         return new ImageIcon(bi);
     }
 }
}
