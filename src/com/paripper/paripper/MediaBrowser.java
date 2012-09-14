package com.paripper.paripper;

import java.util.ArrayList;
import java.util.List;

import com.paripper.paripper.Adapter.CustomPageAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class MediaBrowser extends Activity {
	private PagerAdapter pageAdapter;
	private ViewPager pager;
	private TextView tvSearch;
	private Context context;
	
	private ProgressDialog pdiag; 
	/*
	 * 
	 *   http://www.adictosaltrabajo.com/tutoriales/tutoriales.php?pagina=android_viewpager 
	 * 
	 * 
	 */
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediabrowser);
        context = this;
        
        Bundle extras = getIntent().getExtras();
        ArrayList<String> urls = extras.getStringArrayList("mediaurls");
        
        try {
			pageAdapter = new CustomPageAdapter(context, urls);
			pager = (ViewPager) findViewById(R.id.viewPagerId);
			pager.setAdapter(pageAdapter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
