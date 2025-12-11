package controller;/*
 * @ (#) ComboBoxSuggestion.java   1.0     10/12/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 10/12/2025
 */

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ComboBoxSuggestion {
    public static void addSuggestion(JComboBox<String> comboBox, List<String> items){
        comboBox.setEditable(true);
        JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                            e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        return;
                    }
                    String text = textField.getText();
                    int caret  = textField.getCaretPosition();
                    comboBox.removeAllItems();

                    for(String item : items){
                        if(item.toLowerCase().contains(text.toLowerCase())){
                            comboBox.addItem(item);
                        }
                    }
                    textField.setText(text);
                    textField.setCaretPosition(caret);
                    if(comboBox.getItemCount() > 0){
                        comboBox.showPopup();
                    }else{
                        comboBox.hidePopup();
                    }
                });
            }
        });
    }
}
