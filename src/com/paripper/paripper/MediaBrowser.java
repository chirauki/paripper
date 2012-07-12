package com.paripper.paripper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.paripper.paripper.Adapter.CustomPageAdapter;

public class MediaBrowser extends Activity {
	private PagerAdapter pageAdapter;
	private ViewPager pager;
	private TextView tvSearch;
	private Context context;
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
        
        configureTvSearch();
        configureButton();
        
    }

	private void configureTvSearch() {
		tvSearch = (TextView) findViewById(R.id.etQuery);
	}

	private void configureButton() {
		final Button btSearch = (Button) findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String textToSearch = tvSearch.getText().toString().trim();
				try {
					pageAdapter = new CustomPageAdapter(context, Search.doSearch(textToSearch));
					pager = (ViewPager) findViewById(R.id.viewPagerId);
					pager.setAdapter(pageAdapter);
					hideInputMethod();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void hideInputMethod(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tvSearch.getWindowToken(), 0);
	}

}
