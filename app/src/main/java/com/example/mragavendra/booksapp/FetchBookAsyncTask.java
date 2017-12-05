package com.example.mragavendra.booksapp;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.mragavendra.booksapp.Helper.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by m.ragavendra on 16-11-2017.
 */

public class FetchBookAsyncTask extends AsyncTask<String,Void,String> {

private TextView mtitle,mauthor;

	public FetchBookAsyncTask(TextView title,TextView author) {
		mtitle=title;
		mauthor=author;
	}

	@Override
	protected String doInBackground(String... strings) {
		return NetworkUtils.getBookInfo(strings[0]);
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		try {
			JSONObject jsonObject=new JSONObject(s);
			JSONArray itemsArray=jsonObject.getJSONArray("items");

			for(int i=0;i<itemsArray.length();i++) {
				JSONObject book=itemsArray.getJSONObject(i);
				String title=null;
				String author=null;
				JSONObject volume=book.getJSONObject("volumeInfo");
				try
				{
					title=book.getString("title");
					author=volume.getString("authors");
				}
				catch (Exception e)
				{
				 e.printStackTrace();
				}

				if(title!=null && author!=null)
				{
					mtitle.setText(title);
					mauthor.setText(author);
					return;
				}
			}
			mtitle.setText("NO Results Found");
			mauthor.setText("");

		} catch (JSONException e) {
			mtitle.setText("NO Results Found");
			mauthor.setText("");
			e.printStackTrace();
		}
	}
}
