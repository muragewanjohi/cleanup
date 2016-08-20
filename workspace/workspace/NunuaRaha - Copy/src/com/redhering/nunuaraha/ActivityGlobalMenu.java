package com.redhering.nunuaraha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;

public class ActivityGlobalMenu extends Activity {
	
	Button menuIcon;
	TextView home;
	TextView pastOrdersReceipts;
	TextView changeInfo;
	TextView changeAddress;
	TextView userGuide;
	TextView terms;
	TextView feedback;
	TextView exit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.global_main_nav);
		overridePendingTransition(R.anim.slide_menu_in,R.anim.slide_menu_out);//SlideIn animation
		menuIcon  = (Button) findViewById(R.id.menu_icon);
    	home = (TextView) findViewById(R.id.home);
    	pastOrdersReceipts = (TextView) findViewById(R.id.pastOrdersReceipts);
    	changeInfo = (TextView) findViewById(R.id.changeInfo);
    	changeAddress = (TextView) findViewById(R.id.changeAddress);
    	userGuide = (TextView) findViewById(R.id.userGuide);
    	terms = (TextView) findViewById(R.id.terms);
    	feedback = (TextView) findViewById(R.id.feedback);
    	exit = (TextView) findViewById(R.id.exit);
    	
    	menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
        });
    	
    	home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, ActivityMain.class);
	    	    startActivity(intent);
            }
        });
    	
    	pastOrdersReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, ActivityMyAccountOrders.class);
	    	    startActivity(intent);
            }
        });
    	
    	changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, ActivityMyAccountProfile.class);
	    	    startActivity(intent);
            }
        });
    	
    	changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, ActivityMyAccountAddresses.class);
	    	    startActivity(intent);
            }
        });
    	
    	userGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, Userguide.class);
	    	    startActivity(intent);
            }
        });
    	
    	
    	terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, Terms.class);
	    	    startActivity(intent);
            }
        });
    	
    	
    	
    	
    	feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(ActivityGlobalMenu.this, ActivityTalkToUs.class);
	    	    startActivity(intent);
            }
        });
    	
    	exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onBackPressed();
            }
        });
    	
		
	}
	
	@Override
	//public void onBackPressed() {
		//finish();
	//}
	
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Exit")
	        .setMessage("Are you sure you want to exit?")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
	        
	        
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	overridePendingTransition(R.anim.slide_menu_in,R.anim.slide_menu_out);//SlideIn animation
	        	Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	intent.putExtra("EXIT", true);
            	startActivity(intent); 
	        	finish();
	        }

	    })
	    .setNegativeButton("No", null)
	    .show();
	}
	
	@Override
	public void finish() {
	    super.finish();
		overridePendingTransition(R.anim.slide_menu_in,R.anim.slide_menu_out);//SlideIn animation
	}
	
	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		overridePendingTransition(R.anim.slide_menu_in,R.anim.slide_menu_out);//SlideIn animation
		
	}
	
}