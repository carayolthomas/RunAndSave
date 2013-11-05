package com.hti;

import utils.HTIDatabaseConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment from the MainActivity which allow the user to start/stop a ride and
 * check his personal information
 * 
 * @author hti
 * 
 */
public class RunFragment extends Fragment {

	/** Button run */
	public View mView;
	
	/** Button run */
	public Button mButtonRun;
	
	/** Button updateWeight */
	public Button mButtonUpdateWeight;
	
	/** Weight input */
	public EditText mWeightInput;
	
	/** Text connectedAs */
	public TextView mConnectedAs;

	/** Text weight */
	public TextView mWeight;
	
	/** Allows me to change the text */
	public static String mStartText = "Let's run !";
	public static String mStopText = "I'm done !";

	/** In order to know if the user start a run */
	private boolean mIsStart = false;
	
	/** Asynctask in order to update the user weight in the database */
	private UpdateUserWeight mUpdateUserWeight;

	public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
			Bundle pSavedInstanceState) {
		View lView = pInflater.inflate(R.layout.fragment_main_run, pContainer,
				false);
		lView.requestFocus();
		mView = lView;
		
		mConnectedAs = (TextView) lView.findViewById(R.id.userEmail);
		mConnectedAs.setText(mConnectedAs.getText() + " " + LoginActivity.mUser.getUserEmail());
		
		mWeight = (TextView) lView.findViewById(R.id.userWeight);
		mWeight.setText(mWeight.getText() + " " + String.valueOf(LoginActivity.mUser.getUserWeight()));
		
		mButtonRun = (Button) lView.findViewById(R.id.buttonRun);
		mButtonRun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!mIsStart) {
					MainActivity.startLogPosition();
					mButtonRun.setText(mStopText);
					mIsStart = true;
				} else {
					Boolean lLongEnought = MainActivity.stopLogPosition();
					if(!lLongEnought) {
						Toast.makeText(LoginActivity.getAppContext(), "Run a little bit more please !", Toast.LENGTH_LONG).show();
					}
					mButtonRun.setText(mStartText);
					mIsStart = false;
				}
			}
		});
		
		mWeightInput = (EditText) lView.findViewById(R.id.weightInput);
		mWeightInput.setSelected(false);

		mButtonUpdateWeight = (Button) lView.findViewById(R.id.updateWeightButton);
		mButtonUpdateWeight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!mWeightInput.getText().toString().isEmpty()) {
					mUpdateUserWeight = new UpdateUserWeight();
					mUpdateUserWeight.execute(Integer.parseInt(mWeightInput.getText().toString()));
					mWeightInput.setText("");
					Toast.makeText(LoginActivity.getAppContext(), "Your weight has been updated.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(LoginActivity.getAppContext(), "Please first enter your weight.", Toast.LENGTH_LONG).show();
				}
			}
		});

		return lView;
	}
	
	/**
	 * Asynctask in order to update the user weight in the database
	 */
	public class UpdateUserWeight extends
			AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Looper.prepare();
			String lErrors = HTIDatabaseConnection.getInstance().updateUserWeight(LoginActivity.mUser, params[0]);
			if(lErrors != null) {
				/** Log erreur */
			}
			return true;
		}
	}
}
