package com.faultexception.gtvr;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.anymote.Key.Code;

public class ChannelActivity extends BaseActivity implements OnClickListener {
	private static final int BUTTON_IDS[] = new int[] { R.id.button0,
			R.id.button1, R.id.button2, R.id.button3, R.id.button4,
			R.id.button5, R.id.button6, R.id.button7, R.id.button8,
			R.id.button9 };
	private static final Code NUM_CODES[] = new Code[] { Code.KEYCODE_0,
			Code.KEYCODE_1, Code.KEYCODE_2, Code.KEYCODE_3, Code.KEYCODE_4,
			Code.KEYCODE_5, Code.KEYCODE_6, Code.KEYCODE_7, Code.KEYCODE_8,
			Code.KEYCODE_9 };

	private String mCurrentChannel;

	private TextView mDisplay;
	private Button mBackButton;
	private Button mOKButton;
	private boolean mLocked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);
		setTitle(R.string.channel_title);

		mDisplay = (TextView) findViewById(R.id.display);
		for (int i = 0; i < 10; ++i) {
			int id = BUTTON_IDS[i];
			Button button = (Button) findViewById(id);
			button.setTag(Integer.valueOf(i));
			button.setOnClickListener(this);
		}

		mBackButton = (Button) findViewById(R.id.back);
		mBackButton.setOnClickListener(this);

		mOKButton = (Button) findViewById(R.id.ok);
		mOKButton.setOnClickListener(this);

		mCurrentChannel = "";
		updateDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mBackButton) {
			if (mCurrentChannel.length() > 0) {
				mCurrentChannel = mCurrentChannel.substring(0,
						mCurrentChannel.length() - 1);
				updateDisplay();
			}
		} else if (v == mOKButton) {
			sendKeys();
		} else {
			Integer ni = (Integer) v.getTag();
			int i = ni.intValue();
			if (i >= 0 && i <= 9) {
				mCurrentChannel += String.valueOf(i);
				updateDisplay();
			}
		}
	}

	private void updateDisplay() {
		mDisplay.setText(mCurrentChannel);
		if (mCurrentChannel.isEmpty() || mLocked) {
			mBackButton.setEnabled(false);
		} else {
			mBackButton.setEnabled(true);
		}
	}

	private void lock() {
		mLocked = true;
		for (int i = 0; i < 10; ++i) {
			int id = BUTTON_IDS[i];
			Button button = (Button) findViewById(id);
			button.setEnabled(false);
		}
		mOKButton.setEnabled(false);
	}

	private void sendKeys() {
		final Handler handler = new Handler();

		String number = mCurrentChannel;
		mCurrentChannel = "";

		for (int j = 0; j < number.length(); ++j) {
			final int i = Integer.parseInt(number.substring(j, j + 1));
			final boolean last = j == number.length() - 1;
			final int n = j;
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					getCommands().keyPress(NUM_CODES[i]);
					mCurrentChannel = mCurrentChannel.substring(0, n) + i
							+ mCurrentChannel.substring(n + 1);
					updateDisplay();

					if (last) {
						finish();
					}
				}
			}, (j + 1) * 340);
			mCurrentChannel += "-";
		}

		lock();
		updateDisplay();
	}
}
