package org.ale.tomato;

import java.util.Timer;
import java.util.TimerTask;

import org.ale.tomato.log.Logger;
import org.ale.tomato.util.TimeFormatHelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.TextView;

public class TomatoClock extends Activity implements OnClickListener {
	private Timer timer = null;

	
	// 警告时间
	private int warningTime = 0;
	// 当前个人身份秒
	private int currentIndividualStatusSeconds = 0;
	// 剩余会议时间
	private int remainingMeetingSeconds = 0;

	/**
	 * 默认工作时间25 分钟
	 */
	protected static final Integer WORK_DEFAULT = 25;

	/**
	 * 默认放松时间5分钟
	 */
	protected static final Integer RELAX_DEFAULT = 5;

	/**
	 * 工作模式
	 */
	protected static final Integer WORK_MODEL = 0;

	/**
	 * 休息模式
	 */
	protected static final Integer RELAX_MODEL = 1;

	/**
	 * 工作时间
	 */
	private Integer work = 0;

	/**
	 * 放松时间
	 */
	private Integer relax = 0;

	/**
	 * 当前工作模式，默认为工作模式
	 */
	private Integer currentModel = 0;
	/**
	 * 个人秒开始
	 */

	protected static final String REMAINING_INDIVIDUAL_SECONDS = "remainingIndividualSeconds";
	/**
	 * 总参与者
	 */
	protected static final String TOTAL_PARTICIPANTS = "totalParticipants";
	/**
	 * 剩余会议秒
	 */
	protected static final String REMAINING_MEETING_SECONDS = "remainingMeetingSeconds";
	/**
	 * 个人秒开始
	 */
	protected static final String STARTING_INDIVIDUAL_SECONDS = "startingIndividualSeconds";
	/**
	 * 参与者完成
	 */
	protected static final String COMPLETED_PARTICIPANTS = "completedParticipants";

	/**
	 * 当前个人身份秒
	 */
	protected static final String CURRENT_INDIVIDUAL_STATUS_SECONDS = "currentIndividualStatusSeconds";
	/**
	 * 会议开始时间
	 */
	protected static final String MEETING_START_TIME = "meetingStartTime";
	/**
	 * 个人身份结束时间
	 */
	protected static final String INDIVIDUAL_STATUS_END_TIME = "individualStatusEndTime";
	/**
	 * 最快的
	 */
	protected static final String QUICKEST_STATUS = "quickestStatus";
	/**
	 * 最长的
	 */
	protected static final String LONGEST_STATUS = "longestStatus";
	private Handler updateDisplayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateDisplay();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// initializeTimerValues();
		initializeTimer();
		initializeButtonListeners();
		updateDisplay();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			return true;
		case 2:
			return true;
		default:
			return false;
		}
	}

	/**
	 * 更新显示
	 */
	protected synchronized void updateDisplay() {
		Logger.d("更新显示");
		// chr.stop();

		TextView totalTimeRemaining = (TextView) findViewById(R.id.individual_time_remaining);
		// Logger.d("剩余时间：" + remainingMeetingSeconds);
		String cs = TimeFormatHelper.formatTime(remainingMeetingSeconds);
		Logger.d("剩余时间" + cs);
		totalTimeRemaining.setText(cs);
		totalTimeRemaining.setTextColor(TimeFormatHelper.determineColor(remainingMeetingSeconds, warningTime));
	}

	/**
	 * 开始计时
	 */
	private synchronized void startTimer() {
		Logger.d("开始计时器");

		timer = new Timer();
		TimerTask updateTimerValuesTask = new TimerTask() {
			@Override
			public void run() {
				updateTimerValues();
			}
		};
		timer.schedule(updateTimerValuesTask, 1000, 1000);
	}

	/**
	 * 取消计时
	 */
	private synchronized void cancelTimer() {
		if (timer != null) {
			Logger.d("Canceling timer");
			timer.cancel();
			timer = null;
		}
	}

	protected synchronized void updateTimerValues() {

		currentIndividualStatusSeconds++;
		if (remainingMeetingSeconds > 0) {
			remainingMeetingSeconds--;
			if (remainingMeetingSeconds == 0) {
				Logger.d("Playing the airhorn sound");
				Logger.d("Timer is End");
				workFinish();
			}
		}

		updateDisplayHandler.sendEmptyMessage(0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu taskMenu = menu.addSubMenu(1, 1, 1, R.string.menu_task);
		SubMenu settingMenu = menu.addSubMenu(1, 2, 2, R.string.menu_setting);
		settingMenu.setIcon(android.R.drawable.ic_menu_manage);
		taskMenu.setIcon(android.R.drawable.ic_menu_agenda);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_start:
			Logger.d("点击开始按钮");
			startTimer();
			break;
		case R.id.bt_stop:
			Logger.d("停止计时");
			cancelTimer();

			break;
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent evt) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && evt.getRepeatCount() == 0) {
	// // onDestroy();
	// return false;
	// }
	// return false;
	// }

	@Override
	protected void onDestroy() {

		super.onDestroy();

		System.exit(0);

	}

	private void initializeTimer() {
		work = WORK_DEFAULT;
		relax = RELAX_DEFAULT;
		loadState(work);
	}

	private void initializeButtonListeners() {
		View btStart = this.findViewById(R.id.bt_start);
		// 开始
		btStart.setOnClickListener(this);
		// 停止
		View btStop = this.findViewById(R.id.bt_stop);
		btStop.setOnClickListener(this);

	}

	protected synchronized void loadState(int meetingLength) {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		remainingMeetingSeconds = preferences.getInt(REMAINING_MEETING_SECONDS, (meetingLength * 1));
		Logger.d("remainingMeetingSeconds:" + remainingMeetingSeconds);

	}

	protected void workFinish() {
		Logger.d("Work Finish");
		// 判断当前工作模式
		if (currentModel == WORK_MODEL) {// 当前为工作模式，切换到休息模式
			relax = RELAX_DEFAULT;
			currentModel = RELAX_MODEL;
			loadState(relax);
		} else if (currentModel == RELAX_MODEL) {// 当前为休息模式，切换到工作模式
			work = WORK_DEFAULT;
			currentModel = WORK_MODEL;
			loadState(work);
		}
		cancelTimer();
	}
}