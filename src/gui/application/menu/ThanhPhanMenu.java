package gui.application.menu;
/*
 * @(#) ThanhPhanMenu.java  1.1  [12:11:42 PM] Sep 26, 2025
 *
 * Cập nhật: thêm listener để click submenu hoạt động bình thường.
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

public class ThanhPhanMenu extends JPanel {
    private static final long serialVersionUID = 1L;
    private final List<SuKienMenu> suKien;
    private Menu menu;
    private final String menus[];
    private int menuIndex;
    private final int chieuCaoThanhPhanMenu = 38;
    private final int chieuCaoThanhPhanMenuCon = 35;
    private final int leftGapMenuCon = 34;
    private final int firstGap = 5;
    private final int bottomGap = 5;
    private boolean menuShow;
    private float animate;
    private MenuCon menuCon;

    public ThanhPhanMenu(Menu menu, String menus[], int menuIndex, List<SuKienMenu> suKien, String role) {
        this.menu = menu;
        this.menus = menus;
        this.menuIndex = menuIndex;
        this.suKien = suKien;
        init(role);
    }

    public boolean isMenuShow() { return menuShow; }
    public void setMenuShow(boolean menuShow) { this.menuShow = menuShow; }
    public float getAnimate() { return animate; }
    public void setAnimate(float animate) { this.animate = animate; }
    public String[] getMenus() { return menus; }
    public int getMenuIndex() { return menuIndex; }

    private Icon getIcon(String role) {
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + menuIndex + ".svg");
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#000000"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
    }

    private void init(String role) {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE,
                "background:$Menu.background;foreground:$Menu.lineColor");

        for (int i = 0; i < menus.length; i++) {
            JButton menuItem = createButtonItem(menus[i]);
            menuItem.setHorizontalAlignment(
                    menuItem.getComponentOrientation().isLeftToRight() ? JButton.LEADING : JButton.TRAILING);

            if (i == 0) {
                menuItem.setIcon(getIcon(role));
                menuItem.addActionListener((ActionEvent e) -> {
                    if (menus.length > 1) {
                        if (menu.isMenuFull()) {
                            MenuAnimation.animate(ThanhPhanMenu.this, !menuShow);
                        } else {
                            menuCon.show(ThanhPhanMenu.this,
                                    (int) ThanhPhanMenu.this.getWidth() + UIScale.scale(5),
                                    UIScale.scale(chieuCaoThanhPhanMenu) / 2);
                        }
                    } else {
                        menu.runEvent(menuIndex, 0);
                    }
                });
            } else {
                final int subIndex = i;
                menuItem.addActionListener((ActionEvent e) -> menu.runEvent(menuIndex, subIndex));
            }
            add(menuItem);
        }

        menuCon = new MenuCon(getComponentOrientation(), menu, menuIndex, menus);

        // ✅ Thêm đoạn này để submenu click được
        menuCon.addMenuListener((int subIndex) -> {
            menu.runEvent(menuIndex, subIndex);
        });
    }

    protected void setIndexDuocChon(int index) {
        int size = getComponentCount();
        boolean selected = false;
        for (int i = 0; i < size; i++) {
            Component com = getComponent(i);
            if (com instanceof JButton) {
                ((JButton) com).setSelected(i == index);
                if (i == index) selected = true;
            }
        }
        ((JButton) getComponent(0)).setSelected(selected);
        menuCon.setSelectedIndex(index);
    }

    private JButton createButtonItem(String text) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE,
                "background:$Menu.background;foreground:$Menu.foreground;"
                        + "selectedBackground:$Menu.button.selectedBackground;"
                        + "selectedForeground:$Menu.button.selectedForeground;"
                        + "borderWidth:0;focusWidth:0;innerFocusWidth:0;"
                        + "arc:10;iconTextGap:10;margin:3,11,3,11");
        return button;
    }

    public void hideThanhPhanMenu() { animate = 0; menuShow = false; }

    public void setFull(boolean full) {
        if (full) {
            for (Component com : getComponents()) {
                if (com instanceof JButton) {
                    JButton button = (JButton) com;
                    button.setText(menus[getComponentZOrder(com)]);
                    button.setHorizontalAlignment(
                            getComponentOrientation().isLeftToRight() ? JButton.LEFT : JButton.RIGHT);
                }
            }
        } else {
            for (Component com : getComponents()) {
                if (com instanceof JButton) {
                    JButton button = (JButton) com;
                    button.setText("");
                    button.setHorizontalAlignment(JButton.CENTER);
                }
            }
            animate = 0f;
            menuShow = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (animate > 0) {
            int schieuCaoThanhPhanMenuCon = UIScale.scale(chieuCaoThanhPhanMenuCon);
            int sleftGapMenuCon = UIScale.scale(leftGapMenuCon);
            int schieuCaoThanhPhanMenu = UIScale.scale(chieuCaoThanhPhanMenu);
            int sfirstGap = UIScale.scale(firstGap);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D.Double p = new Path2D.Double();
            int last = getComponent(getComponentCount() - 1).getY() + (schieuCaoThanhPhanMenuCon / 2);
            boolean ltr = getComponentOrientation().isLeftToRight();
            int round = UIScale.scale(10);
            int x = ltr ? (sleftGapMenuCon - round) : (getWidth() - (sleftGapMenuCon - round));
            p.moveTo(x, schieuCaoThanhPhanMenu + sfirstGap);
            p.lineTo(x, last - round);
            for (int i = 1; i < getComponentCount(); i++) {
                int com = getComponent(i).getY() + (schieuCaoThanhPhanMenuCon / 2);
                p.append(createCurve(round, x, com, ltr), false);
            }
            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            g2.draw(p);
            g2.dispose();
        }
    }

    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.curveTo(x, y - round, x, y, x + (ltr ? round : -round), y);
        return p2;
    }

    private class MenuLayout implements LayoutManager {
        @Override public void addLayoutComponent(String name, Component comp) {}
        @Override public void removeLayoutComponent(Component comp) {}
        @Override public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets inset = parent.getInsets();
                int width = parent.getWidth();
                int height = inset.top + inset.bottom;
                int size = parent.getComponentCount();
                height += UIScale.scale(chieuCaoThanhPhanMenu);
                if (size > 1) {
                    int subMenuHeight = UIScale.scale(firstGap + bottomGap);
                    for (int i = 1; i < size; i++) subMenuHeight += UIScale.scale(chieuCaoThanhPhanMenuCon);
                    height += (subMenuHeight * animate);
                }
                return new Dimension(width, height);
            }
        }
        @Override public Dimension minimumLayoutSize(Container parent) { return new Dimension(0, 0); }
        @Override public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = parent.getInsets();
                int x = insets.left, y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        if (i == 0) {
                            com.setBounds(x, y, width, UIScale.scale(chieuCaoThanhPhanMenu));
                            y += UIScale.scale(chieuCaoThanhPhanMenu + firstGap);
                        } else {
                            int subX = ltr ? UIScale.scale(leftGapMenuCon) : 0;
                            com.setBounds(x + subX, y, width - subX, UIScale.scale(chieuCaoThanhPhanMenuCon));
                            y += UIScale.scale(chieuCaoThanhPhanMenuCon);
                        }
                    }
                }
            }
        }
    }
}
