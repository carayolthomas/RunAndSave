package com.hti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RunFragment extends Fragment {
	
	public Button buttonRun;
	public Button buttonDisplay;
	public Button buttonDisplayWifi;
	public Button buttonClean;
	public static String startText = "Start running !";
	public static String stopText = "Stop running !";
	private boolean isStart = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_main_run, container, false);
		
		
		buttonRun = (Button) view.findViewById(R.id.buttonRun);
		buttonRun.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						if(buttonRun.getText().equals(startText)) {
							//TODO autre fonction à appeller avec le timer
							MainActivity.startLogPosition();
							buttonRun.setText(stopText);
							isStart = true ;
						} else {
							MainActivity.stopLogPosition();
							buttonRun.setText(startText);
							isStart = false ;
						}
					}
				});
		
		
		return view;
	}
}
