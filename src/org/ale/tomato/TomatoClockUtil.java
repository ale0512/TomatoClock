package org.ale.tomato;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

/**
 * @author
 * 
 */
public class TomatoClockUtil {
	final Timer timer = new Timer();
	/**
	 * 工作时间 25分钟,毫秒
	 */
	private static int workTime = 25 * 60 * 1000;
	/**
	 * 休息时间，毫秒
	 */
	private static int restTime = 5 * 60 * 1000;

	/**
	 * 开始计时 1、记录当期时间 2、检测当期时间是否到25分钟 3、提示并进入休息时间
	 */
	public void start(int minutes) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
				timer.cancel();
			}
		}, minutes * 60 * 1000);
	}

	/**
	 * 停止计时
	 * 
	 */
	public void end() {

	}

	/**
	 * 重新计时
	 */
	public void reset() {

	}

	/**
	 * 工作时段
	 */
	public void work() {

	}

	/**
	 * 休息时段
	 */
	public void rest() {

	}

	/**
	 * 监控,当期时间与与目标时间
	 */
	public int monitor(int target, int curr) {
		return 0;
	}

	/**
	 * 进入下一时段
	 */
	public void next() {

	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				break;
			}
			super.handleMessage(msg);
		}
	};

}
