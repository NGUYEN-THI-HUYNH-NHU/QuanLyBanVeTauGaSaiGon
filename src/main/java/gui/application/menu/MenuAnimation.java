package gui.application.menu;
/*
 * @(#) MenuAnimation.java  1.0  [4:11:36 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */


import java.util.HashMap;

import com.formdev.flatlaf.util.Animator;

public class MenuAnimation {

	private static HashMap<ThanhPhanMenu, Animator> hash = new HashMap<>();

	public static void animate(ThanhPhanMenu menu, boolean show) {
		if (hash.containsKey(menu) && hash.get(menu).isRunning()) {
			hash.get(menu).stop();
		}
		menu.setMenuShow(show);
		Animator animator = new Animator(400, new Animator.TimingTarget() {
			@Override
			public void timingEvent(float f) {
				if (show) {
					menu.setAnimate(f);
				} else {
					menu.setAnimate(1f - f);
				}
				menu.revalidate();
			}

			@Override
			public void end() {
				hash.remove(menu);
			}
		});
		animator.setResolution(1);
		animator.setInterpolator((float f) -> (float) (1 - Math.pow(1 - f, 3)));
		animator.start();
		hash.put(menu, animator);
	}

}
