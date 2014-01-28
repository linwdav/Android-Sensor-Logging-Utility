package org.davidlin.sensorloggingutility.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.davidlin.sensorloggingutility.FileExplore;
import org.davidlin.sensorloggingutility.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ScriptFragment extends Fragment implements OnClickListener {
	
	private View scriptFragmentView;
	private Button runScriptButton;
	private TextView shellOutputView;
	private ProgressDialog progressDialog;
	private static ScriptOperation scriptInstance;
	private static volatile String results = "";
	private static volatile boolean running = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		scriptFragmentView = inflater.inflate(R.layout.fragment_script, container, false);
		setupViews();
		return scriptFragmentView;
	}
	
	private void setupViews() {
		runScriptButton = (Button) scriptFragmentView.findViewById(R.id.btRunScript);
		shellOutputView = (TextView) scriptFragmentView.findViewById(R.id.tvShellOutput);
		if (running) {
			runScriptButton.setText("Stop running shell script");
		}
		shellOutputView.setText(results);
		runScriptButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
    	case R.id.btRunScript:
    		if (running == false) {
    			Intent i = new Intent(getActivity(), FileExplore.class);
    			startActivityForResult(i, 1);
    		}
    		else {
    			progressDialog = ProgressDialog.show(getActivity(), "", "Stopping shell script...");
    			scriptInstance.cancel(true);
    		}
    		break;
		}
	}
	
	private class ScriptOperation extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			running = true;
			String cmd = params[0];
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(cmd);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = "";
				while (this.isCancelled() == false && (line = bufferedReader.readLine()) != null) {
					publishProgress(line);
					Log.d("msdk", line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}
		
		@Override
        protected void onProgressUpdate(String... result) {
			shellOutputView.setText(shellOutputView.getText().toString() + result[0] + "\n");
			results = shellOutputView.getText().toString();
		}
		
		@Override
		protected void onPostExecute(String result) {
		    stopRunning();
		}
		
		@Override
		protected void onCancelled() {
			progressDialog.dismiss();
			stopRunning();
		}
		
		private void stopRunning() {
			running = false;
		    runScriptButton.setText("Run a shell script");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String scriptPath = data.getExtras().getString("scriptPath");
		Log.d("msdk", "THE SCRIPTPATH IS " + scriptPath);
		if (!scriptPath.isEmpty()) {
			shellOutputView.setText("");
			runScriptButton.setText("Stop running shell script");
			scriptInstance = new ScriptOperation();
			scriptInstance.execute("sh " + scriptPath);
		}
	}
	
}
