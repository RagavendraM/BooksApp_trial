package com.example.mragavendra.booksapp.AsyncLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.mragavendra.booksapp.Helper.NetworkUtils;

/**
 * Created by m.ragavendra on 16-11-2017.
 */

public class FetchBookAsyncLoaderTask extends AsyncTaskLoader<String> {

	String mqueryString;

	public FetchBookAsyncLoaderTask(Context context,String queryString) {
		super(context);
		mqueryString=queryString;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();

	}

	@Override
	public String loadInBackground() {
		return NetworkUtils.getBookInfo(mqueryString);
	}
}
