package com.paripper.paripper.Adapter;  
  
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
  
public class CustomPageAdapter extends PagerAdapter {  
      
    private final List<String> urls;  
    private final Context context;  
      
      
    public CustomPageAdapter(Context context, List<String> urls) {  
        super();  
        this.urls = urls;  
        this.context = context;  
    }  
  
    @Override  
    public void destroyItem(View collection, int position, Object view) {  
        ((ViewPager) collection).removeView((LinearLayout) view);  
    }  
  
    @Override  
    public void finishUpdate(View arg0) {  
        // TODO Auto-generated method stub  
    }  
  
    @Override  
    public int getCount() {  
        return urls.size();  
    }  
  
    @Override  
    public Object instantiateItem(View collection, int position) {        
          
        LinearLayout linearLayout = new LinearLayout(context);  
        linearLayout.setOrientation(1);  
          
        //final TextView textView = new TextView(context);  
        //textView.setText("Position: " + position);  
          
        final ImageView image = new ImageView(context); 
        InputStream in = null;
		try {
			in = (InputStream) new URL(urls.get(position)).getContent();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        image.setImageBitmap(BitmapFactory.decodeStream(in));
        //linearLayout.addView(textView);  
        linearLayout.addView(image);  
          
        ((ViewPager) collection).addView(linearLayout,0);  
        return linearLayout;  
    }  
  
    @Override  
    public boolean isViewFromObject(View view, Object object) {  
         return view==((LinearLayout)object);  
    }  
  
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
        // TODO Auto-generated method stub  
          
    }  
  
    @Override  
    public Parcelable saveState() {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
        // TODO Auto-generated method stub  
          
    }  
  
}  