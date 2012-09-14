package com.paripper.paripper.Adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paripper.paripper.MediaBrowser;
import com.paripper.paripper.R;
import com.paripper.paripper.util.Constants;

public class TweetAdapter extends ArrayAdapter<Status> {
	private final Context context;
	private final List<Status> values;
	private ImageView tweetAvatar = null;
	private ImageView mediaIcon = null;
	
	public TweetAdapter(Context context, List<Status> objects) {
		super(context, R.layout.tweet, objects);
		this.context = context;
		this.values = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String scheme = Constants.SCHEME;
	 	Status tweet = values.get(position); 
	 	View rowView = null;

	 	MediaEntity[] media = tweet.getMediaEntities();
	 	if (media != null) {
	 		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 		rowView = inflater.inflate(R.layout.tweet_media, parent, false);	
	 	} else {
	 		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 	rowView = inflater.inflate(R.layout.tweet, parent, false);
	 	}
	 	
	 	HashMap<View,twitter4j.Status> mp = new HashMap<View,twitter4j.Status>();
	 	mp.put(rowView, tweet);
	 	new getAvatarTask().execute(tweet.getUser());
	 	
	 	if (media != null) {
	 		mediaIcon = (ImageView)rowView.findViewById(R.id.tweetMedia);
	 		new getTweetMedia().execute(media[0].getMediaURL());
 			mediaIcon.setOnClickListener(new tweetMediaOnClickListener(context, tweet));
	 	}
	 		 	
	 	TextView tweetID = (TextView) rowView.findViewById(R.id.tweetID);
	 	TextView tweetUser = (TextView) rowView.findViewById(R.id.tweetUser);
	 	TextView tweetUserId = (TextView) rowView.findViewById(R.id.tweetUserId);
	 	tweetUserId.setMovementMethod(LinkMovementMethod.getInstance());
	 	TextView tweetText = (TextView) rowView.findViewById(R.id.tweetText);
	 	tweetText.setMovementMethod(LinkMovementMethod.getInstance());
	 	TextView tweetDate = (TextView) rowView.findViewById(R.id.tweetDate);
	 	TextView tweetVia = (TextView) rowView.findViewById(R.id.tweetVia);
	 	tweetVia.setMovementMethod(LinkMovementMethod.getInstance());
	 	tweetAvatar = (ImageView) rowView.findViewById(R.id.tweetAvatar);

	 	tweetID.setText(tweet.getId()+"");
	 	tweetUser.setText(tweet.getUser().getName());
	 	String twUser = "<a href=\"" + scheme + "://user?" + 
	 					tweet.getUser().getScreenName() + "\">@" + tweet.getUser().getScreenName()
	 					+ "</a>";
 		tweetUserId.setText(Html.fromHtml(twUser));

 		String txt = tweet.getText();
 		
 		if (media != null) {
 			for (int i = 0; i < media.length; i++) {
 				txt = txt.replace(media[i].getURL().toString(), "");
 			}
 		}
 		
 		URLEntity[] urls = tweet.getURLEntities();
 		for (int i=0; i < urls.length; i++) {
 			URLEntity url = urls[i];
 			txt = txt.replace(url.getURL().toString(), "<a href=\"" + url.getURL().toString()
 							+ "\">" + url.getDisplayURL() + "</a>");
 		}

 		HashtagEntity[] htags = tweet.getHashtagEntities();
 		for (int i=0; i < htags.length; i++) {
 			HashtagEntity ht = htags[i];
 			txt = txt.replace("#" + ht.getText(), "<a href=\"" + scheme + "://htag?" + 
 						ht.getText() + "\">#" + ht.getText() + "</a>");
 		}

 		UserMentionEntity[] users = tweet.getUserMentionEntities();
 		for (int i=0; i < users.length; i++) {
 			UserMentionEntity user = users[i];
 			txt = txt.replace("@" + user.getScreenName(), "<a href=\"" + scheme + "://htag?" + 
 					user.getScreenName() + "\">@" + user.getScreenName() + "</a>");
 		}
	 	tweetText.setText(Html.fromHtml(txt));
	 	
	 	tweetDate.setText(DateFormat.format("dd/MM/yyy hh:mm", tweet.getCreatedAt()));
	 	
	 	tweetVia.setText(Html.fromHtml(tweet.getSource()));

		return rowView;
	}
	
	//private class getAvatarTask extends AsyncTask<twitter4j.Status, Void, Drawable> {
	private class getAvatarTask extends AsyncTask<User, Void, Drawable> {
		@Override
		protected Drawable doInBackground(User... params) {
			try {
				String user = params[0].getScreenName();
				File av = new File(context.getExternalCacheDir(), "avatar_" + user);
				URL url = params[0].getProfileImageURL();
				Date urlDate = new Date(url.openConnection().getIfModifiedSince());
				if (av.exists() && new Date(av.lastModified()).after(urlDate)) {
					Drawable d = Drawable.createFromPath(av.toString());
					return d;
				} else {
					InputStream is = (InputStream) params[0].getProfileImageURL().getContent();
					Drawable d = Drawable.createFromStream(is, "src name");
					saveCacheBitmap(((BitmapDrawable)d).getBitmap(), "avatar_" + user);
					return d;
				}
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Drawable result) {
			tweetAvatar.setImageDrawable(result);
			super.onPostExecute(result);
		}
	}
	
	private class getTweetMedia extends AsyncTask<URL, Void, Drawable> {
		@Override
		protected Drawable doInBackground(URL... params) {
			try {
				URL url = params[0];
				InputStream is = (InputStream) url.getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				return d;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Drawable result) {
			mediaIcon.setImageDrawable(result);
			super.onPostExecute(result);
		}
	}
	
	/**
	 * Loads image from file
	 * @param file file to read from
	 * @return Bitmap image
	 */
	private Bitmap getCacheBitmap(String file) {
		Bitmap bm = null; 
		File cache = context.getExternalCacheDir();
		File inf = new File(cache, file);
		bm = BitmapFactory.decodeFile(inf.toString());
		return bm;
	}
	
	/**
	 * Stores source Bitmap on file
	 * @param bm Source Bitmap object to save
	 * @param file File to save the Bitmap to
	 * @return 0 if could save correctly, -1 otherwise
	 */
	private int saveCacheBitmap(Bitmap bm, String file) {
		try {
			File cache = context.getExternalCacheDir();
			File of = new File(cache, file);
			FileOutputStream out = new FileOutputStream(of);
			bm.compress(Bitmap.CompressFormat.PNG, 85, out);
			out.flush();
			out.close();
			return 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
 	private class doGet extends AsyncTask<String, Void, Header[]> {
		@Override
		protected Header[] doInBackground(String... params) {
			try {
				String url = params[0].toString();
	 			HttpClient hc = new DefaultHttpClient();
	 			HttpGet get = new HttpGet(url);

	 			HttpResponse rp = hc.execute(get);

	 			if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	 			{
	 				return rp.getHeaders("Content-type");
	 			}
	 			return null;
 			} catch (Exception e){
 				e.printStackTrace();
 				return new Header[0];
 			}
		}
	}
	
	private List<String> regexExtract(String input, String regex) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }


}
