package com.paripper.paripper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.paripper.paripper.ListView.TimeLineListView;
import com.paripper.paripper.util.Constants;

//public class TimeLine extends ListActivity {
public class TimeLine extends Activity {
	Twitter twitter = null;
	String token = null;
	String secret = null;
	List<Status> stTimeLine = null;
	TimeLineListView lv = null; 
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
		
		setContentView(R.layout.timeline);
		lv = (TimeLineListView)findViewById(R.id.timelinelist);
		fillTimeLine();
	}
	
	private void fillTimeLine() {
        List<HashMap<String, Object>> inst = getTweets(); 
        TweetAdapter tad = new TweetAdapter(this, inst);
        lv.setAdapter(tad);	
	}
	
	private List<HashMap<String, Object>> getTweets() {
		List<HashMap<String, Object>> tweets = new ArrayList<HashMap<String, Object>>();
    	// get tweets
    	try {
    		stTimeLine = new getTimeLineTask().execute().get();
    		for(int i=0; i < stTimeLine.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Status st = stTimeLine.get(i);
				map.put("text", st.getText());
				map.put("date", st.getCreatedAt());
				map.put("user", st.getUser().getName());
				map.put("id", "" + st.getId());
				map.put("userid", st.getUser().getScreenName());
				map.put("via", st.getSource());
				map.put("avatar", st.getUser().getProfileImageURL());
				
				tweets.add(map);
			}
			//ArrayAdapter<String> ads = new ArrayAdapter<String>(this, R.layout.timeline, statuses);
			//setListAdapter(ads);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return tweets;
	}
	
	private class getTimeLineTask extends AsyncTask<Void, Void, List<twitter4j.Status>> {
		List<twitter4j.Status> lt = null;
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			//List of hashmaps with needed info
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
			super.onPostExecute(result);
			lt = result;
			progDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDialog.setMessage(getString(R.string.loading));
			progDialog.show();
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
                fillTimeLine();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
}
