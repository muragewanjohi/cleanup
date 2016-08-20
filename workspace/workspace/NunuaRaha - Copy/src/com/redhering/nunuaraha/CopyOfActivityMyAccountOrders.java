package com.redhering.nunuaraha;

import java.io.BufferedReader;
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
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;

public class CopyOfActivityMyAccountOrders extends Activity {
	
	ListView listView;
	LinearLayout layout_progressbar;
	RelativeLayout loginForm;
	RelativeLayout ratingForm;
	Button menuIcon;
	TextView headerText;
	Button shoppingButton;
	Button cartButton;
	TextView app_name;
	Button rateButton;
	Button cancelRatingButton;
	TextView cartButtonNotification;
	
	TextView queryOne;
	TextView queryTwo;
	TextView queryThree;
	
	RadioGroup query1RadioGroup;
	RadioGroup query2RadioGroup;
	RadioGroup query3RadioGroup;
	
	RadioButton query1SelectedRadio;
	RadioButton query2SelectedRadio;
	RadioButton query3SelectedRadio;
	
	public static String RATE_VALUES;
	
	
	ArrayList<RequestedSimpleListOrders> MYORDERS;
	AdapterListOrders myadapter;

	public static final String MY_SESSION = "mySession";
	public static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
	
    public static String MY_PHONE_NUMBER;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroller_simple_list);
		overridePendingTransition(R.anim.slide_page_in,R.anim.slide_page_out);//SlideIn animation
		listView = (ListView) findViewById(R.id.simpleListView);
		layout_progressbar = (LinearLayout) findViewById(R.id.progressbar_view);
		loginForm = (RelativeLayout) findViewById(R.id.loginForm);
		ratingForm = (RelativeLayout) findViewById(R.id.ratingForm);
		menuIcon  = (Button) findViewById(R.id.menu_icon);
		sharedPreferences = getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
  		headerText = (TextView) findViewById(R.id.headerText);
  		app_name = (TextView) findViewById(R.id.app_name);
  		shoppingButton = (Button) findViewById(R.id.shoppingButton);
  		cartButton = (Button) findViewById(R.id.cartButton);
		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
  		
  		rateButton = (Button) findViewById(R.id.rateButton);
  		cancelRatingButton = (Button) findViewById(R.id.cancelRatingButton);
    	queryOne = (TextView) findViewById(R.id.queryOne);
    	queryTwo = (TextView) findViewById(R.id.queryTwo);
    	queryThree = (TextView) findViewById(R.id.queryThree);
    	
  		query1RadioGroup = (RadioGroup)findViewById(R.id.queryOneRadioGroup);
  		query2RadioGroup = (RadioGroup)findViewById(R.id.queryTwoRadioGroup);
  		query3RadioGroup = (RadioGroup)findViewById(R.id.queryThreeRadioGroup);
  		
  		//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,CopyOfActivityMyAccountOrders.this,sharedPreferences);
	    
	    //SET TEXT, APPLY FONTS, SETONCLICKLISTENERS
  		new ApplyViewParamsTask().execute();
  		
  		if(sharedPreferences.contains("userPhoneNumber")) {
  			MY_PHONE_NUMBER = sharedPreferences.getString("userPhoneNumber", null);
            
            //Call AsynTask to perform network operation on separate thread
            new HttpAsyncTask().execute("http://www.smokesignal.co.ke/mobile_trolley_app/getmyorders.php");
            
            //LIST VIEW
      		MYORDERS = new ArrayList<RequestedSimpleListOrders>();
    		myadapter = new AdapterListOrders(CopyOfActivityMyAccountOrders.this, MYORDERS,layout_progressbar,ratingForm);
    		listView.setAdapter(myadapter);
    		listView.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				RequestedSimpleListOrders  item = (RequestedSimpleListOrders) listView.getItemAtPosition(position);
    				
    				//ADD TO SESSION
    				editor.putString("order_date",item.item_one);
    	            editor.putString("order_shop",item.item_two);
    	            editor.putString("order_amount",item.item_three);
    	            editor.commit();
    				
    			    Intent intent = new Intent(CopyOfActivityMyAccountOrders.this, ActivityStep09ListDeliveryOptions.class);
    	    	    startActivity(intent);
    			}
    		});
  		}else{
  			Intent intent = new Intent(CopyOfActivityMyAccountOrders.this, ActivityMyAccountLogin.class);
  			intent.putExtra("from" , "ActivityMyAccountOrders");
    	    startActivity(intent);
  		}
        
	}
	
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first

		myadapter = new AdapterListOrders(CopyOfActivityMyAccountOrders.this, MYORDERS,layout_progressbar,ratingForm);
		listView.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();

		//LOAD QUICKLINKS
  		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
  		helperQuickLinks.create(menuIcon,shoppingButton,cartButton,cartButtonNotification,CopyOfActivityMyAccountOrders.this,sharedPreferences);
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
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
	    protected void onPreExecute() {
			layout_progressbar.setVisibility(View.VISIBLE);
	        listView.setVisibility(View.GONE);
	        super.onPreExecute();
	    }
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	layout_progressbar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            myadapter.notifyDataSetChanged();
            super.onPostExecute(result);
            
            //Toast.makeText(getBaseContext(), "Orders Received!", Toast.LENGTH_LONG).show();
            //Log.i("Orders"," --> "+result);
            
            if(result.equals("")){
            	RequestedSimpleListOrders d = new RequestedSimpleListOrders();
				
				d.setItemId("");
				d.setItemRefNumber("");
				d.setItemStatus("");
				d.setItemOne("No orders found!");
				d.setItemTwo("");
				d.setItemThree("");
				
				d.item_id = "";
				d.item_ref_number = "";
				d.item_status = "";
			    d.item_one = "No orders found!";
			    d.item_two = "";
			    d.item_three = "";
			    
			    MYORDERS.add(d);
            	
            }else{
            	Log.i("Orders"," --> "+result);
            	
		        try {
		        	JSONArray jsonArr = new JSONArray(result);
		        	String[] list_item_ids = new String[jsonArr.length()];
					String[] list_item_titles = new String[jsonArr.length()];
		        	
					for(int i = 0; i < jsonArr.length(); i++){
						JSONObject jsonObj = jsonArr.getJSONObject(i); 
						String item_id = jsonObj.getString("id");
						String item_ref_number = jsonObj.getString("ref_number");
						String item_status = jsonObj.getString("status");
						String date = jsonObj.getString("created");
						String outlet = jsonObj.getString("shop_brand_id");
						String branch = jsonObj.getString("shop_branch_id");
						String amount = jsonObj.getString("subtotal");
						String item_customer_order = jsonObj.getString("user_order");
						String item_customer_name = jsonObj.getString("first_name")+" "+jsonObj.getString("last_name");
						String item_customer_email = jsonObj.getString("email");
						String item_customer_address = jsonObj.getString("customer_address");
						
						list_item_ids[i] = item_id;
						list_item_titles[i] = date;
						
						RequestedSimpleListOrders d = new RequestedSimpleListOrders();
						
						d.setItemId(item_id);
						d.setItemRefNumber(item_ref_number);
						d.setItemStatus(item_status);
						d.setItemOne(date);
						d.setItemTwo(outlet + " " + branch);
						d.setItemThree(amount);
						d.setCustomerOrder(item_customer_order);
						d.setCustomerName(item_customer_name);
						d.setCustomerEmail(item_customer_email);
						d.setCustomerAddress(item_customer_address);
						
						d.item_id = item_id;
						d.item_ref_number = item_ref_number;
						d.item_status = item_status;
					    d.item_one = date;
					    d.item_two = outlet + " " + branch;
					    d.item_three = amount;
					    d.item_customer_order = item_customer_order;
					    d.item_customer_name = item_customer_name;
					    d.item_customer_email = item_customer_email;
					    d.item_customer_address = item_customer_address;
					    
					    MYORDERS.add(d);
					}
					
					
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					RequestedSimpleListOrders d = new RequestedSimpleListOrders();
					
					d.setItemId("");
					d.setItemRefNumber("");
					d.setItemStatus("");
					d.setItemOne("Error loading your past orders.");
					d.setItemTwo("Please contact customer care for assistance.");
					d.setItemThree("");
					
					d.item_id = "";
					d.item_ref_number = "";
					d.item_status = "";
				    d.item_one = "Error loading your past orders.";
				    d.item_two = "Please contact customer care for assistance.";
				    d.item_three = "";
				    
				    MYORDERS.add(d);
				}
            }
       }
        
        

		@Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
	}

	
	private class ApplyViewParamsTask extends AsyncTask<String, Void, String> {
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
		
		protected String doInBackground(String...params) {
			// TODO Auto-generated method stub
			return "Done";
		}
		
	    protected void onPostExecute(String params) {
  			//MENU ICON
	  		headerText.setText("My Orders");
	  		
	  		rateButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	
	            	String queryOneText = queryOne.getText().toString();
	            	String queryTwoText = queryTwo.getText().toString();
	            	String queryThreeText = queryThree.getText().toString();
	            	
	            	String radiovalue1 = "0";
	            	if (query1RadioGroup.getCheckedRadioButtonId() != -1){
	            		query1SelectedRadio = (RadioButton) findViewById(query1RadioGroup.getCheckedRadioButtonId());
	            		radiovalue1 = query1SelectedRadio.getText().toString();
	            	}
	            	
	            	String radiovalue2 = "0";
	            	if (query2RadioGroup.getCheckedRadioButtonId() != -1){
	            		query2SelectedRadio = (RadioButton) findViewById(query3RadioGroup.getCheckedRadioButtonId());
	            		radiovalue2 = query2SelectedRadio.getText().toString();
	            	}
	            	
	            	String radiovalue3 = "0";
	            	if (query3RadioGroup.getCheckedRadioButtonId() != -1){
	            		query3SelectedRadio = (RadioButton) findViewById(query3RadioGroup.getCheckedRadioButtonId());
	            		radiovalue3 = query3SelectedRadio.getText().toString();
	            	}
	          		
	            	
	            	JSONObject ratingValues = new JSONObject();
	                try {
	                	ratingValues.put(queryOneText, radiovalue1);
	                	ratingValues.put(queryTwoText, radiovalue2);
	                	ratingValues.put(queryThreeText, radiovalue3);
	                	
	                	RATE_VALUES = ratingValues.toString();
	                    new HttpAsyncRatingTask().execute("http://www.smokesignal.co.ke/mobile_trolley_app/saverating.php");//Call AsynTask to perform network operation on separate thread

	                } catch (JSONException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	        });
	  		
	  		
	  		cancelRatingButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	//Reload page
	            	startActivity(new Intent(CopyOfActivityMyAccountOrders.this, CopyOfActivityMyAccountOrders.class));
	            }
	        });
	  	
	  		
	  		
			
	    }
	    
	 }

	private class HttpAsyncRatingTask extends AsyncTask<String, Void, String> {
		@Override
	    protected void onPreExecute() {
			layout_progressbar.setVisibility(View.VISIBLE);
	        super.onPreExecute();
	    }
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	layout_progressbar.setVisibility(View.GONE);
            super.onPostExecute(result);

    		ratingForm.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), "Thank You!", Toast.LENGTH_LONG).show();
            
            //Reload page
        	startActivity(new Intent(CopyOfActivityMyAccountOrders.this, CopyOfActivityMyAccountOrders.class));
       }

		@Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
	}

	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //PARAMS FOR RETRIEVING ORDERS
        nameValuePairs.add(new BasicNameValuePair("phone_number",MY_PHONE_NUMBER));
        
        //PARAMS FOR SAVING USER RATE/REVIEW
        nameValuePairs.add(new BasicNameValuePair("rating",RATE_VALUES));
        nameValuePairs.add(new BasicNameValuePair("customer_fname",sharedPreferences.getString("userFirstName", null)));
        nameValuePairs.add(new BasicNameValuePair("customer_lname",sharedPreferences.getString("userLastName", null)));
        nameValuePairs.add(new BasicNameValuePair("customer_email",sharedPreferences.getString("userEmail", null)));
        nameValuePairs.add(new BasicNameValuePair("customer_phone",sharedPreferences.getString("userPhoneNumber", null)));
        
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            // make GET request to the given URL
            HttpResponse response = httpclient.execute(httppost);
            
            // receive response as inputStream
            inputStream = response.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null){
                result = convertInputStreamToString(inputStream);
            }else{
                result = "Did not work!";
            }
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
	

}