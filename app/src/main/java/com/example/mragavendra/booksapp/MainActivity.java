package com.example.mragavendra.booksapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xenione.libs.accordion.AccordionView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

	private TextView title, author;
	private EditText SearchET;
	private ImageButton shareButton;
	AccordionView accordionView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SearchET = (EditText) findViewById(R.id.SearchET);
		title = (TextView) findViewById(R.id.Title);
		author = (TextView) findViewById(R.id.Author);
		shareButton = (ImageButton) findViewById(R.id.imageButton);
		accordionView = (AccordionView) findViewById(R.id.accordionView);


		// add PhoneStateListener
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	public void SearchBooks(View view) {
		String queryString = SearchET.getText().toString();

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
//			new FetchBookAsyncTask(title,author).execute(queryString);
			//bundle sending to Loader
			Bundle queryBundle = new Bundle();
			queryBundle.putString("queryString", queryString);
			getSupportLoaderManager().restartLoader(0, queryBundle, this);
			title.setText("");
			author.setText("Loading..");
		} else {
			if (queryString.length() == 0) {
				author.setText("");
				title.setText("Please enter Book Name");
			} else {
				author.setText("");
				title.setText("Please check Network Connection and try again");
			}
		}
	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle args) {
//		return new FetchBookAsyncLoaderTask(this,args.getString("queryString"));
		return null;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray itemsArray = jsonObject.getJSONArray("items");

			for (int i = 0; i < itemsArray.length(); i++) {
				JSONObject book = itemsArray.getJSONObject(i);
				String stitle = null;
				String sauthor = null;
				JSONObject volume = book.getJSONObject("volumeInfo");
				try {
					stitle = book.getString("title");
					sauthor = volume.getString("authors");
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (stitle != null && sauthor != null) {
					title.setText(stitle);
					author.setText(sauthor);
					return;
				}
			}
			title.setText("NO Results Found");
			author.setText("");

		} catch (JSONException e) {
			title.setText("NO Results Found");
			author.setText("");
			e.printStackTrace();
		}
	}


	@Override
	public void onLoaderReset(Loader<String> loader) {

	}

	public void ShareApp(View view) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		i.putExtra(Intent.EXTRA_TEXT, " http://downloads.sharekhan.com/download/sharemobile/");
		startActivity(Intent.createChooser(i, "Share App to..."));
	}


	public void CallCustomerCare(View view) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:1800227500"));
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.

			return;
		}
		startActivity(callIntent);
	}
	private class PhoneCallListener extends PhoneStateListener {

		private boolean isPhoneCalling = false;

		String LOG_TAG = "LOGGING 123";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");

				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended,
				// need detect flag from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");

				if (isPhoneCalling) {

					Log.i(LOG_TAG, "restart app");

					// restart app
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}

}
