package com.redhering.nunuaraha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;

public class Userguide extends Activity {
	SQLiteDatabase nunuaRahaDatabase;
	public static final String MY_SESSION = "mySession";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    Bundle extras;
    ArrayAdapter<String> dataAdapter;
    
	TextView app_name;
	LinearLayout layout_progressbar;
	Button menuIcon;
	TextView headerText;
	Button shoppingButton;
	Button cartButton;
	TextView cartButtonNotification;
	

	Typeface EkMukta_Light;
	
	ImageView userguideimage1;
	ImageView userguideimage2;
	private	ImageView headerTextimage2;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		setContentView(R.layout.userguide);
		menuIcon  = (Button) findViewById(R.id.menu_icon);
		sharedPreferences = getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		gson = new Gson();
		extras = getIntent().getExtras();
  		headerText = (TextView) findViewById(R.id.headerText);
  		app_name = (TextView) findViewById(R.id.app_name);
  		shoppingButton = (Button) findViewById(R.id.shoppingButton);
  		cartButton = (Button) findViewById(R.id.cartButton);
  		
  	


  		ImageView image = (ImageView)findViewById(R.id.userguideimage1);
  		ImageView image2 = (ImageView)findViewById(R.id.userguideimage2);
  		
  		
  		
		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
  		
  	
  	//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,Userguide.this,sharedPreferences);
  		
  		
  		headerText.setText("Help");
		headerTextimage2 = (ImageView) findViewById(R.id.headerTextimage);
		headerTextimage2.setVisibility(View.VISIBLE);
		headerTextimage2.setImageResource(R.drawable.menuhelpactive);
  		
  	}
	

	  
	@Override
		public void onBackPressed() {
			finish();
			overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		}
		
		@Override
		public void finish() {
		    super.finish();
			overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		}
		
		@Override
		public void onResume() {
			super.onResume();  // Always call the superclass method first
			overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		}
		
}