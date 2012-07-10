package com.paripper.paripper.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class AvatarDownloader extends AsyncTask<String, Void, Void> {
	private Context context = null;
	
	public AvatarDownloader(Context ctx) {
		context = ctx;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... params) {
		for (int i = 0; i < params.length; i++) {
			String username = params[i];
			try {
				SharedPreferences sp = context.getSharedPreferences(Constants.SCHEME, Activity.MODE_PRIVATE);
	        	String token = sp.getString("token", null);
	        	String secret = sp.getString("secret", null);
	    		Twitter t = new TwitterFactory().getInstance();
				t.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SEC);
				AccessToken at = new AccessToken(token, secret);
				t.setOAuthAccessToken(at);
				User user = t.showUser(username);
				
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			    HttpGet getRequest = new HttpGet(user.getProfileBackgroundImageUrl());

		        HttpResponse response = client.execute(getRequest);
		        int statusCode = response.getStatusLine().getStatusCode();
		        if (statusCode != HttpStatus.SC_OK) { 
		            Log.w("AvatarDownloader", "Error " + statusCode + " while retrieving bitmap from " + user.getProfileBackgroundImageUrl());
		            return null;
		        }
		        
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            InputStream inputStream = null;
		            OutputStream outputStream = null;
		            try {
		                inputStream = entity.getContent(); 
		                outputStream = context.openFileOutput(
		                				context.getExternalCacheDir().getAbsolutePath() +
		                				File.separator +
		                				username, 
		                				Context.MODE_PRIVATE);
		                byte[] buffer = new byte[1024];
		                int len;
		                while ((len = inputStream.read(buffer)) != -1) {
		                	outputStream.write(buffer, 0, len);
		                }
		                outputStream.flush();
		                outputStream.close();
		            } finally {
		                if (inputStream != null) {
		                    inputStream.close();  
		                }
		                entity.consumeContent();
		            }
		        }
		        client.close();
		    } catch (Exception e) {
		        Log.w("AvatarDownloader :: Error while retrieving bitmap from ", e.toString());
		        e.printStackTrace();
		    }
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}
