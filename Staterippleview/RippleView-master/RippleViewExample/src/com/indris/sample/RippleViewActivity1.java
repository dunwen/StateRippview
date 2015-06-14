package com.indris.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.indris.material.RippleView;

public class RippleViewActivity1 extends Activity {

	RippleView mButton;
	RippleView mButton1;
	RippleView mButton2;
	RippleView mButton3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ripple_view);
		mButton = (RippleView) findViewById(R.id.btn);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mHandler.sendEmptyMessageDelayed(2, 5000);
				mHandler.sendEmptyMessageDelayed(1, 3000);

			}
		});
		mButton1 = (RippleView) findViewById(R.id.btn1);
		mButton1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mHandler.sendEmptyMessageDelayed(3, 6000);

			}
		});

	}

	Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mButton.setState(RippleView.State.FINISH);

				break;

			case 2:

				mButton.setState(RippleView.State.IDIE);
				break;
			
			case 3:
				mButton1.setState(RippleView.State.ERROR);

				break;


				

			default:
				break;
			}

		};
	};

}
