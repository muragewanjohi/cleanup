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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;

public class ActivityStep01ListOutlets extends Activity {
    SQLiteDatabase nunuaRahaDatabase;
	public static final String MY_SESSION = "mySession";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

	public static ArrayList<RequestedResultsSimpleList> OUTLETS;
	AdapterSimpleList myadapter;
	ListView listView;
	LinearLayout layout_progressbar;
	LinearLayout listViewCont;
	Typeface EkMukta_Light;
	TextView app_name;
	TextView headerText;
	Button menuIcon;
	Button shoppingButton;
	Button cartButton;
	TextView cartButtonNotification;

	private	ImageView headerTextimage2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroller_simple_list);
		overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
	    sharedPreferences = getSharedPreferences(MY_SESSION,MODE_PRIVATE);
	    editor = sharedPreferences.edit();
		listView = (ListView) findViewById(R.id.simpleListView);
		layout_progressbar = (LinearLayout) findViewById(R.id.progressbar_view);
	    EkMukta_Light = Typeface.createFromAsset(getAssets(),"fonts/ek_mukta/EkMukta-Light.ttf");
  		app_name = (TextView) findViewById(R.id.app_name);
		menuIcon  = (Button) findViewById(R.id.menu_icon);
		headerText = (TextView) findViewById(R.id.headerText);
		shoppingButton = (Button) findViewById(R.id.shoppingButton);
		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
  		cartButton = (Button) findViewById(R.id.cartButton);
        
		layout_progressbar.setVisibility(View.VISIBLE);
    	headerText.setText("Outlets");
    	headerTextimage2 = (ImageView) findViewById(R.id.headerTextimage);
		headerTextimage2.setVisibility(View.VISIBLE);
		headerTextimage2.setImageResource(R.drawable.outlets);
	    app_name.setTypeface(EkMukta_Light);

		
  		//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,ActivityStep01ListOutlets.this,sharedPreferences);
  		
  		//LOAD LIST ITEMS
  		new retrieveFromDBTask().execute();
		OUTLETS = new ArrayList<RequestedResultsSimpleList>();
		myadapter = new AdapterSimpleList(ActivityStep01ListOutlets.this, OUTLETS,EkMukta_Light);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				RequestedResultsSimpleList  item = (RequestedResultsSimpleList) listView.getItemAtPosition(position);
				try
		        {
					String currentOutlet = sharedPreferences.getString("selectedOutlet", null);
					String currentOutletId = sharedPreferences.getString("selectedOutletId", null);
					String currentBrand = sharedPreferences.getString("selectedBrand", null);
					String currentBranch = sharedPreferences.getString("selectedBranch", null);
					
					if(sharedPreferences.contains("selectedOutletId")){
						if(sharedPreferences.contains("myTrolley") && !currentOutletId.equals(item.item_id)){
							showDialogSelectBranch(item, position, currentOutlet + " - " + currentBrand + " " + currentBranch + ".");
						}else{
						    Intent intent = new Intent(ActivityStep01ListOutlets.this, ActivityStep02ListBrands.class);
				    	    startActivity(intent);
						}
					}else{
			            
			            editor.putString("selectedOutlet",item.item_title);
			            editor.putString("selectedOutletId",item.item_id);
			            editor.commit();
					    Intent intent = new Intent(ActivityStep01ListOutlets.this, ActivityStep02ListBrands.class);
			    	    startActivity(intent);
					}
					
		        }
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }
				
			}
		});
		listView.setAdapter(myadapter);
		listView.setTextFilterEnabled(true);

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

		myadapter = new AdapterSimpleList(ActivityStep01ListOutlets.this, OUTLETS,EkMukta_Light);
		listView.setAdapter(myadapter);
		myadapter.notifyDataSetChanged();
		
		//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,ActivityStep01ListOutlets.this,sharedPreferences);
		
	}

	private class retrieveFromDBTask extends AsyncTask<String, Void, String> {
		
		@Override
	    protected void onPreExecute() {
			layout_progressbar.setVisibility(View.VISIBLE);
	        listView.setVisibility(View.GONE);
	        //menuListView.setVisibility(View.VISIBLE);
	        super.onPreExecute();
	    }
		
		protected String doInBackground(String...params) {
			// TODO Auto-generated method stub
			return "Done";
		}
		
	    protected void onPostExecute(String params) {
        	layout_progressbar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            myadapter.notifyDataSetChanged();
            //super.onPostExecute();
            
            
	    	nunuaRahaDatabase = openOrCreateDatabase("nunuaRahaDatabase",MODE_PRIVATE,null);
	    	Cursor resultsCursor = nunuaRahaDatabase.rawQuery("SELECT * FROM hdjgf_shops",null);
	    	resultsCursor.moveToFirst();

        	String[] list_item_ids = new String[resultsCursor.getCount()];
			String[] list_item_titles = new String[resultsCursor.getCount()];
			int i = 0;
	    	ArrayList<ArrayList<ArrayList<String>>> outletsIdListCont = new ArrayList<ArrayList<ArrayList<String>>>(); //Outer Array
			ArrayList<ArrayList<String>> outletsIdList = new ArrayList<ArrayList<String>>(); //Inner Array

            Log.i("creating database"," --> retrieve funtion" +resultsCursor.getCount());
            
	    	while (resultsCursor.isAfterLast() == false) {
	            
	    		Log.i("creating database"," --> loop funtion");

				String item_id = resultsCursor.getString(0);
				String item_title = resultsCursor.getString(1);
				
				ArrayList<String> outlestListInfo = new ArrayList<String>(); //Inner array
				outlestListInfo.add(item_id);
				outlestListInfo.add(item_title);
				outletsIdList.add(new ArrayList<String>(outlestListInfo)); 
				
				list_item_ids[i] = item_id;
				list_item_titles[i] = item_title;
				
				RequestedResultsSimpleList d = new RequestedResultsSimpleList();
				
				d.setTitle(item_title);
				
			    d.item_id = item_id;
			    d.item_title = item_title;
			    OUTLETS.add(d);
				
	    	    i++;
	    	    resultsCursor.moveToNext();
	    	}
			
			//ADD INNER ARRAY TO OUTER ARRAY
			outletsIdListCont.add(new ArrayList<ArrayList<String>>(outletsIdList)); 
			outletsIdList.clear();
    		Gson gson = new Gson();
            String jsonOutletsIdList = gson.toJson(outletsIdListCont);
            editor.putString("outlestIdList", jsonOutletsIdList);
            editor.commit();
	    	
	    }
	    
	 }
	
	public void showDialogSelectBranch(final RequestedResultsSimpleList item, final int position, final String message) throws Exception{
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityStep01ListOutlets.this);
        builder.setMessage("You will lose all the items in your trolley at " + message + " Are you sure?");     

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	layout_progressbar.setVisibility(View.VISIBLE);
            	
            	//Clear Cart Session
                sharedPreferences.edit().remove("myTrolley").commit();
                
                editor.putString("selectedOutlet",item.item_title);
	            editor.putString("selectedOutletId",item.item_id);
	            editor.commit();
	            
			    Intent intent = new Intent(ActivityStep01ListOutlets.this, ActivityStep02ListBrands.class);
	    	    startActivity(intent);
                
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {   
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
	
}