package com.paripper.paripper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.paripper.paripper.Adapter.TweetAdapter;
import com.paripper.paripper.ListView.PullToRefreshListView;
import com.paripper.paripper.ListView.PullToRefreshListView.OnRefreshListener;
import com.paripper.paripper.util.Constants;

public class TimeLine extends Activity {
	Twitter twitter = null;
	String token = null;
	String secret = null;
	List<Status> stTimeLine = null;
	PullToRefreshListView lv = null; 
	ProgressDialog progDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitle("Timeline");
		Bundle extras = getIntent().getExtras();
		token = extras.getString("token");
		secret = extras.getString("secret");
		
		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		//setContentView(R.layout.timeline);
		setContentView(R.layout.refresh_timeline);
		lv = (PullToRefreshListView)findViewById(R.id.timelinelist);
		
		// Set a listener to be invoked when the list should be refreshed.
        lv.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
            	try {
					new getTimeLineTask().execute().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
            	lv.invalidateViews();
            }
        });
        
		try {
			new getTimeLineTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private class getTimeLineTask extends AsyncTask<Void, Void, List<twitter4j.Status>> {
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
	    	try {
	    		twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SEC);
				AccessToken at = new AccessToken(token, secret);
				twitter.setOAuthAccessToken(at);
				return twitter.getHomeTimeline();
	    	} catch (TwitterException e) {
				// TODO Auto-generated catch block
	    		System.out.println("This is in the asynctask.");
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> result) {
			TweetAdapter tad = new TweetAdapter(getApplicationContext(), result);
	        lv.setAdapter(tad);
	        lv.onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, Constants.REFRESH_ID, 0, R.string.refresh);
        return true;
    }
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case Constants.REFRESH_ID:
            	try {
        			new getTimeLineTask().execute().get();
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		} catch (ExecutionException e) {
        			e.printStackTrace();
        		}
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
