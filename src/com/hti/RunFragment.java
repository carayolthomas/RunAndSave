package com.hti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment from the MainActivity which allow the user to start/stop a ride and
 * check his personal information
 * 
 * @author hti
 * 
 */
public class RunFragment extends Fragment {

	/** Button run */
	public Button mButtonRun;

	/** Allows me to change the text */
	public static String mStartText = "Let's run !";
	public static String mStopText = "I'm done !";

	/** In order to know if the user start a run */
	private boolean mIsStart = false;

	public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
			Bundle pSavedInstanceState) {
		View lView = pInflater.inflate(R.layout.fragment_main_run, pContainer,
				false);

		mButtonRun = (Button) lView.findViewById(R.id.buttonRun);
		mButtonRun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!mIsStart) {
					MainActivity.startLogPosition();
					mButtonRun.setText(mStopText);
					mIsStart = true;
				} else {
					MainActivity.stopLogPosition();
					mButtonRun.setText(mStartText);
					mIsStart = false;
				}
			}
		});

		return lView;
	}
}
