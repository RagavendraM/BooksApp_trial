package com.example.mragavendra.booksapp.Helper;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.mragavendra.booksapp.WebURL.Domain.BOOK_BASE_URL;


/**
 * Created by m.ragavendra on 16-11-2017.
 */

public class NetworkUtils {
	public static final String LOG_TAG=NetworkUtils.class.getName();

  public static String getBookInfo(String queryString){
			HttpURLConnection httpURLConnection=null;
			BufferedReader bufferedReader=null;
			String bookJsonString = null;

		try{

			Uri builtUri= Uri.parse(BOOK_BASE_URL).buildUpon().build();

			/*Uri builtUri= Uri.parse(BOOK_BASE_URL).buildUpon()
					.appendQueryParameter(QUERY_PARAM,queryString)
					.appendQueryParameter(MAX_RESULTS,"10")
					.appendQueryParameter(PRINT_TYPE,"books").build();*/

			URL requestURL = new URL(builtUri.toString());

			//URL CONNECTION
			 httpURLConnection= (HttpURLConnection) requestURL.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();

			Log.d(LOG_TAG,"Connected");
			InputStream inputStream=httpURLConnection.getInputStream();
			StringBuffer buffer=new StringBuffer();
			if(inputStream==null)
			{
				return null;
			}

			bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while((line=bufferedReader.readLine())!=null)
			{
				buffer.append(line+"\n");
			}
			if(buffer.length()==0)
			{
				return null;
			}
			bookJsonString=buffer.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if(httpURLConnection !=null)
			{
				httpURLConnection.disconnect();
			}
			if(bufferedReader!=null)
			{
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	  Log.d(LOG_TAG,bookJsonString);
	  return bookJsonString;
	}
}
