package gui.application.form.banVe;
/*
 * @(#) WizardController.java  1.0  [10:37:39 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entity.Chuyen;

public class WizardController {
 private final CardLayout layout;
 private final JPanel container;
 private int currentStep = 1;
 private final String[] defaultStepNames = {"search", "seat", "info", "confirm", "payment", "complete"};
 private final Map<Integer, String> stepNameMap = new HashMap<>();
 private final Map<Integer, JPanel> panels = new HashMap<>();
 private BookingSession bookingSession;

 public WizardController(CardLayout layout, JPanel container) {
     if (layout == null || container == null) {
         throw new IllegalArgumentException("layout and container must not be null");
     }
     this.layout = layout;
     this.container = container;

     for (int i = 0; i < defaultStepNames.length; i++) {
         stepNameMap.put(i + 1, defaultStepNames[i]);
     }
 }

 public void registerPanel(int stepIndex, String cardName, JPanel panel) {
     if (stepIndex < 1 || stepIndex > defaultStepNames.length) {
         throw new IllegalArgumentException("stepIndex out of range: " + stepIndex);
     }
     if (cardName == null || panel == null) {
         throw new IllegalArgumentException("cardName and panel must not be null");
     }
     stepNameMap.put(stepIndex, cardName);
     panels.put(stepIndex, panel);

     if (panel.getParent() != container) {
         container.add(panel, cardName);
     }
 }

 public JPanel getPanel(int stepIndex) {
     return panels.get(stepIndex);
 }

 public void nextStep() {
     int max = defaultStepNames.length;
     if (currentStep < max) {
         goToStep(currentStep + 1, 0);
     }
 }

 public void previousStep() {
     if (currentStep > 1) {
         goToStep(currentStep - 1, 0);
     }
 }

 public void goToStep(int stepIndex, int tripIndex) {
     // validate
     int max = defaultStepNames.length;
     if (stepIndex < 1 || stepIndex > max) {
         System.err.println("WizardController.goToStep: invalid stepIndex=" + stepIndex);
         return;
     }

     final String cardName = stepNameMap.getOrDefault(stepIndex, defaultStepNames[stepIndex - 1]);

     // Show the card on EDT
     SwingUtilities.invokeLater(() -> {
         layout.show(container, cardName);
         currentStep = stepIndex;
     });

//     if (stepIndex == 2) {
//         JPanel p = getPanel(2);
//         if (p == null) {
//             // panel not registered — try to find by cardName (best-effort)
//             System.err.println("WizardController.goToStep: PanelBuoc2 not registered for step 2 (cardName=" + cardName + ")");
//             return;
//         }
//
//         if (p instanceof PanelBuoc2) {
//             PanelBuoc2 panel2 = (PanelBuoc2) p;
//             BookingSession s = getBookingSession();
//             SearchCriteria criteria = (tripIndex == 0) ? s.getOutboundCriteria() : s.getReturnCriteria();
//             @SuppressWarnings("unchecked")
//             List<Chuyen> results = (List<Chuyen>) ( (tripIndex == 0) ? s.getOutboundResults() : s.getReturnResults() );
//             // call enter on UI panel on EDT (PanelBuoc2 should handle being called from EDT)
//             SwingUtilities.invokeLater(() -> {
//                 try {
//                	 System.out.println("Wizard.goToStep -> step=2 tripIndex=" + tripIndex + " session=" + s);
//                     panel2.enter(criteria, results, tripIndex, s);
//                 } catch (Throwable t) {
//                     System.err.println("WizardController: error while calling PanelBuoc2.enter: " + t.getMessage());
//                     t.printStackTrace();
//                 }
//             });
//         } else {
//             System.err.println("WizardController.goToStep: registered panel for step 2 is not an instance of PanelBuoc2. Actual class: " + p.getClass().getName());
//         }
//     }
 }

 public void goToStep(int stepIndex) {
     goToStep(stepIndex, 0);
 }

 public void reset() {
     goToStep(1, 0);
 }

 public BookingSession getBookingSession() {
     if (bookingSession == null) bookingSession = new BookingSession();
     return bookingSession;
 }

 public void setBookingSession(BookingSession s) {
     this.bookingSession = s;
 }

 public int getCurrentStep() {
     return currentStep;
 }
}