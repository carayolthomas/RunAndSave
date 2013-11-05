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

public class UserInformationFragment extends Fragment {

	/** Asynctask in order to update the user weight in the database */
	private UpdateUserWeight mUpdateUserWeight;
	
	/** Button updateWeight */
	public Button mButtonUpdateWeight;
	
	/** Weight input */
	public EditText mWeightInput;
	
	/** Text connectedAs */
	public TextView mConnectedAs;

	/** Text weight */
	public TextView mWeight;
	
	public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
			Bundle pSavedInstanceState) {
		View lView = pInflater.inflate(R.layout.fragment_main_user_informations,
				pContainer, false);
		
		mConnectedAs = (TextView) lView.findViewById(R.id.userEmail);
		mConnectedAs.setText(mConnectedAs.getText() + " " + LoginActivity.mUser.getUserEmail());
		
		mWeight = (TextView) lView.findViewById(R.id.userWeight);
		mWeight.setText(mWeight.getText() + " " + String.valueOf(LoginActivity.mUser.getUserWeight()));
		
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
	 * FragmentActivity is great but it doesn't keep the content of a fragment if you slide twice away from it, so I have to reload it.
	 */
	@Override
	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible) {
			MainActivity.mIsNewRide = true;
		}
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
