package com.hti;

import utils.HTIDatabaseConnection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserInformationFragment extends Fragment {

	/** Asynctask in order to update the user weight in the database */
	private UpdateUserWeightTask mUpdateUserWeightTask;
	
	/** Button updateWeight */
	public Button mButtonUpdateWeight;
	
	/** Weight input */
	public EditText mWeightInput;
	
	/** Text connectedAs */
	public TextView mConnectedAs;

	/** Text weight */
	public TextView mWeight;
	
	/** Looper created*/
	private boolean mLooperCreated;
	
	public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
			Bundle pSavedInstanceState) {
		View lView = pInflater.inflate(R.layout.fragment_main_user_informations,
				pContainer, false);
		
		mConnectedAs = (TextView) lView.findViewById(R.id.userEmail);
		mConnectedAs.setText(mConnectedAs.getText() + " " + LoginActivity.mUser.getUserEmail());
		
		mWeight = (TextView) lView.findViewById(R.id.userWeight);
		mWeight.setText(mWeight.getText() + " " + String.valueOf(LoginActivity.mUser.getUserWeight()));
		
		mButtonUpdateWeight = (Button) lView.findViewById(R.id.updateWeightButton);
		mButtonUpdateWeight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				/** Creation of alertdialog to modify */
			    AlertDialog.Builder lAlert = new AlertDialog.Builder(getActivity());                 
			    lAlert.setTitle("Update your weight");  
			    lAlert.setMessage("Enter your weight :"); 
			    lAlert.setCancelable(false);

			    final EditText lInput = new EditText(getActivity()); 
			    lInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			    
			    InputFilter[] lFilterArray = new InputFilter[1];
			    lFilterArray[0] = new InputFilter.LengthFilter(3);
			    lInput.setFilters(lFilterArray);
			    
			    lAlert.setView(lInput);
			    lAlert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {  
			        public void onClick(DialogInterface dialog, int whichButton) {  
			            String lValue = lInput.getText().toString();
			            if (!lValue.isEmpty()) {
							mUpdateUserWeightTask = new UpdateUserWeightTask();
							mUpdateUserWeightTask.execute(Integer.parseInt(lValue));
							Toast.makeText(LoginActivity.getAppContext(), "Your weight has been updated.", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(LoginActivity.getAppContext(), "Please first enter your weight.", Toast.LENGTH_LONG).show();
						}
			           }  
			         });  
			    lAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            }
		        });
			    lAlert.show();
			}
		});

		/**Looper not yet created*/
		mLooperCreated = false;
		
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
	public class UpdateUserWeightTask extends
			AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			if(!mLooperCreated) {
				Looper.prepare();
				mLooperCreated = true;
			}
			String lErrors = HTIDatabaseConnection.getInstance().updateUserWeight(LoginActivity.mUser, params[0]);
			if(lErrors != null) {
				/** Log erreur */
			}
			LoginActivity.mUser.setUserWeight(params[0]);
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean pSuccess) {
			mWeight.setText(mWeight.getText().toString().substring(0, 16) + " " + String.valueOf(LoginActivity.mUser.getUserWeight()));
		}
		
	}
}
