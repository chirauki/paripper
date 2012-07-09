package com.paripper.paripper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.paripper.paripper.util.Constants;

public class startActivity extends Activity {
	Twitter twitter = null;
	RequestToken requestToken = null;
	String CALLBACKURL = Constants.CALLBACKURL;
	String token = null;
	String secret = null;
	ProgressDialog progDialog = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        progDialog = new ProgressDialog(this);
        setTitle("Paripper");
        SharedPreferences sp = getSharedPreferences(Constants.SCHEME, MODE_PRIVATE);
        String token = null;
        String secret = null;
        if (sp.contains("token")) {
        	token = sp.getString("token", null);
        	secret = sp.getString("secret", null);
        	loadTimeline(token, secret);
        } else {
        	setContentView(R.layout.activity_start);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_start, menu);
        return true;
    }

    public void validate(View view) {
    	new validateTask().execute(-1);
    }

	/*
	 * - Called when WebView calls your activity back.(This happens when the user has finished signing in)
	 * - Extracts the verifier from the URI received
	 * - Extracts the token and secret from the URL 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		try {
			new callbackTask().execute(uri);
			loadTimeline(token, secret);
		} catch (Exception ex) {
			Log.e("Main.onNewIntent", "" + ex.getMessage());
		}
	}   
	
	private void loadTimeline(String token, String secret) {
		Intent in = new Intent(this, TimeLine.class);
		in.putExtra("token", token);
		in.putExtra("secret", secret);
		startActivity(in);
	}
	
	private class validateTask extends AsyncTask<Integer, Void, Integer> {
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SEC);
				requestToken = twitter.getOAuthRequestToken(CALLBACKURL);
				String authUrl = requestToken.getAuthenticationURL();
				Intent twpage = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
				startActivity(twpage);
			} catch (TwitterException ex) {
				ex.printStackTrace();
				Log.e("in Main.OAuthLogin", ex.getMessage());
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progDialog.dismiss();

		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDialog.setMessage(getString(R.string.redirect_twitter));
			progDialog.show();
		}
	}
	
	private class callbackTask extends AsyncTask<Uri, Void, Integer> {
		@Override
		protected Integer doInBackground(Uri... params) {
			try {
				Uri uri = params[0];
				String verifier = uri.getQueryParameter("oauth_verifier");
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,	verifier);
				token = accessToken.getToken();
				secret = accessToken.getTokenSecret();
				
				SharedPreferences sp = getSharedPreferences(Constants.SCHEME, MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("token", token);
				editor.putString("secret", secret);
				editor.commit();
			} catch (TwitterException ex) {
				ex.printStackTrace();
				Log.e("in Main.OAuthLogin", ex.getMessage());
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progDialog.dismiss();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDialog.show();
		}
	}
}
