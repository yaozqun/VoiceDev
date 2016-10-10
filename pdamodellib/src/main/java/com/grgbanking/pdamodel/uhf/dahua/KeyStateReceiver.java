package com.grgbanking.pdamodel.uhf.dahua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyStateReceiver extends BroadcastReceiver {
	private boolean defaultdown = false;
	private boolean defaultup = false;
	private ScanKeyListener keyL;

	public KeyStateReceiver(ScanKeyListener keyL) {
		this.keyL = keyL;
	}

	public interface ScanKeyListener {
		public void onKeyUp();

		public void onKeyDown();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int keycode = intent.getIntExtra("keycode", 0);
		boolean keydown = intent.getBooleanExtra("keydown", defaultdown);
		boolean keyup = intent.getBooleanExtra("keyup", defaultup);
		if (keycode == 133) {
			if (keydown) {
				// 左键被按下

			} else if (keyup) {
				// 左键松开

			}
		}
		if (keycode == 134) {
			if (keydown) {
				// 右键被按下
				if (keyL != null) {
					keyL.onKeyDown();
				}
			} else if (keyup) {
				// 右键松开
				if (keyL != null) {
					keyL.onKeyUp();
				}
			}
		}

	}

}
