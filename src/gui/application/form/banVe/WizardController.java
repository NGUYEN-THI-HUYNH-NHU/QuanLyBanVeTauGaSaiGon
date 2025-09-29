package gui.application.form.banVe;
/*
 * @(#) WizardController.java  1.0  [10:37:39 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.CardLayout;

import javax.swing.JPanel;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class WizardController {
    private CardLayout layout;
    private JPanel container;
    private int currentStep = 0;
    private final String[] steps = {"search", "seat", "info", "confirm", "payment"};

    public WizardController(CardLayout layout, JPanel container) {
        this.layout = layout;
        this.container = container;
    }

    public void nextStep() {
        if (currentStep < steps.length - 1) {
            currentStep++;
            layout.show(container, steps[currentStep]);
        }
    }

    public void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            layout.show(container, steps[currentStep]);
        }
    }

    public void reset() {
        currentStep = 0;
        layout.show(container, steps[currentStep]);
    }
}