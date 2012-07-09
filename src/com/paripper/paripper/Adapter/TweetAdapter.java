package com.paripper.paripper.Adapter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paripper.paripper.R;
import com.paripper.paripper.util.Constants;

public class TweetAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private final Context context;
	private final List<HashMap<String, Object>> values;
	
	public TweetAdapter(Context context, List<HashMap<String, Object>> objects) {
		super(context, R.layout.tweet, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 	View rowView = inflater.inflate(R.layout.tweet, parent, false);
	 	
	 	HashMap<String, Object> rowData = values.get(position); 
	 	
	 	TextView tweetID = (TextView) rowView.findViewById(R.id.tweetID);
	 	TextView tweetUser = (TextView) rowView.findViewById(R.id.tweetUser);
	 	TextView tweetUserId = (TextView) rowView.findViewById(R.id.tweetUserId);
	 	TextView tweetText = (TextView) rowView.findViewById(R.id.tweetText);
	 	tweetText.setMovementMethod(LinkMovementMethod.getInstance());
	 	TextView tweetDate = (TextView) rowView.findViewById(R.id.tweetDate);
	 	TextView tweetVia = (TextView) rowView.findViewById(R.id.tweetVia);
	 	TextView fixedVia = (TextView) rowView.findViewById(R.id.fixedVia);
	 	ImageView tweetAvatar = (ImageView) rowView.findViewById(R.id.tweetAvatar);
	 	
	 	tweetID.setText(rowData.get("id").toString());
 		tweetUser.setText(rowData.get("user").toString());
	 	tweetUserId.setText("(@" + rowData.get("userid").toString() + ")");

 		String txt = rowData.get("text").toString();
	 	List<String> matches = regexExtract(txt, Constants.URL_REGEX);
	 	if (matches.size() > 0) {
	 		//there are URLs
	 		for (int i=0; i < matches.size(); i++) {
	 			String url = matches.get(i);
	 			txt = txt.replace(url, "<a href=\"" + url + "\">" + url + "</a>");
	 		}
	 	}
	 	matches = regexExtract(txt, Constants.USER_REGEX);
	 	if (matches.size() > 0) {
	 		//there are URLs
	 		for (int i=0; i < matches.size(); i++) {
	 			String user = matches.get(i).trim();
	 			txt = txt.replace(user, "<a href=\"testtw://user?" + user.replace("@", "") + "\">" + user + "</a>");
	 		}
	 	}
	 	matches = regexExtract(txt, Constants.HTAG_REGEX);
	 	if (matches.size() > 0) {
	 		//there are URLs
	 		for (int i=0; i < matches.size(); i++) {
	 			String htag = matches.get(i).trim();
	 			txt = txt.replace(htag, "<a href=\"testtw://htag?" + htag.replace("#", "") + "\">" + htag + "</a>");
	 		}
	 	}
	 	tweetText.setText(Html.fromHtml(txt));
	 	//tweetText.setAutoLinkMask(Linkify.ALL);

	 	tweetDate.setText(rowData.get("date").toString());
	 	tweetVia.setText(Html.fromHtml(rowData.get("via").toString()));
	 	fixedVia.setText(" via ");
	 	try {
	 		tweetAvatar.setImageDrawable(new getAvatarTask().execute((URL)rowData.get("avatar")).get());
	 	} catch (ExecutionException exex) {
	 		Log.e("Error con la descarga del avatar", exex.getMessage());
	 	} catch (InterruptedException exex) {
	 		Log.e("Error con la descarga del avatar", exex.getMessage());
	 	}
		return rowView;
	}
	
	private class getAvatarTask extends AsyncTask<URL, Void, Drawable> {
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
