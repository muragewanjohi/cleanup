package com.redhering.nunuaraha;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;
public class ActivityMyAccountOrdersLoyalty extends Activity {
	
	SQLiteDatabase nunuaRahaDatabase;
	public static final String MY_SESSION = "mySession";
	private static final int TIMEOUT_MILLISEC = 0;
	 public static String MY_PHONE_NUMBER;
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
	

	private TextView text1;
	
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		setContentView(R.layout.loyaltypoints);
		menuIcon  = (Button) findViewById(R.id.menu_icon);
		sharedPreferences = getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		gson = new Gson();
		extras = getIntent().getExtras();
  		headerText = (TextView) findViewById(R.id.headerText);
  		app_name = (TextView) findViewById(R.id.app_name);
  		shoppingButton = (Button) findViewById(R.id.shoppingButton);
  		cartButton = (Button) findViewById(R.id.cartButton);
  		

		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
  		
  	
  	//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,ActivityMyAccountOrdersLoyalty.this,sharedPreferences);
  		
  		MY_PHONE_NUMBER = sharedPreferences.getString("userPhoneNumber", null);
  		
  		headerText.setText("Loyalty Points");
  		
  		
  		
  	// Preparing post params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("customer_phone",sharedPreferences.getString("userPhoneNumber", null)));
		//nameValuePairs.add(new BasicNameValuePair("customer_phone",sharedPreferences.getString("userPhoneNumber", null)));
        

        ServiceHandler serviceClient = new ServiceHandler();
        String jsond = serviceClient.makeServiceCall("http://www.smokesignal.co.ke/mobile_trolley_app/getmyloyalty.php", ServiceHandler.POST, params);

        Log.d("SERVER Request: ", "> " + jsond);

        if (jsond != null) {
            try {
               	JSONArray jArray = new JSONArray(jsond);
  		            JSONObject json = jArray.getJSONObject(0);
                // checking for error node in json
               
                	// Log.i("Read from server", json.getString("loyaltypoints"));
                	 //Log.i("Read from server", "You Have" + " " + json.getString("loyaltypoints")+ " "+ "Points");
  		          
  		            text1 = (TextView)findViewById(R.id.name); 
  		          String ponites = "You Have " + json.getString("loyaltypoints")+ " Points";
  		            text1.setText(ponites);	
  		            
               
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "JSON data error!");
        }
        
    
  		
  		         
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
		
		private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	 
	    }
	
}
		