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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.redhering.nunuaraha.R;
import com.redhering.nunuaraha.AnimatedGifImageView.TYPE;

public class ActivityStep09ListDeliveryOptionsAddresses extends Activity {
	public static final String MY_SESSION = "mySession";
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	public static ArrayList<RequestedResultsSimpleList> MYADDRESSES;
	AdapterSimpleList myadapter;

	LinearLayout layout_progressbar;
	RelativeLayout headersection;
	RelativeLayout centerwrap;
	RelativeLayout footersection;
	ListView listView;
	public static String SELECTED_BRANCH_ID;
	public static String SELECTED_BRANCH_TITLE;
	public static String SELECTED_BRAND_ID;
	public static String SELECTED_BRAND_TITLE;
	public static String SELECTED_BRAND_LOGO;
	public static String SELECTED_OUTLET_ID;
	public static String SELECTED_OUTLET_TITLE;
	public static String USER_PHONE_NUMBER;
	Typeface EkMukta_Light;
	TextView app_name;
	Button menuIcon;
	TextView cartButtonNotification;
	TextView headerText;
	Button shoppingButton;
	Button cartButton;
	ImageView shopLogoview;
	String extStorageDirectory;
	Bitmap bm;

	Button proceedToCheckout;
	Button clearcart;

	private ImageView headerTextimage2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroller_simple_list);
		overridePendingTransition(R.anim.slide_page_in, R.anim.slide_page_out);// SlideIn
																				// animation
		initViews();

		if (sharedPreferences.contains("loggedIn")
				&& sharedPreferences.contains("customerInfo")) {
			headerText.setText("Select Address");
			headerTextimage2 = (ImageView) findViewById(R.id.headerTextimage);
			headerTextimage2.setVisibility(View.VISIBLE);
			headerTextimage2.setImageResource(R.drawable.address);

			// LOAD QUICKLINKS
			HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
			helperQuickLinks.create(menuIcon, shoppingButton, cartButton,
					cartButtonNotification,
					ActivityStep09ListDeliveryOptionsAddresses.this,
					sharedPreferences);

			// DISPLAY OUTLET LOGO
			// File file = new File(extStorageDirectory+"/NunuaRaha/",
			// SELECTED_BRAND_LOGO);
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
			// options);
			// shopLogoview.setImageBitmap(bitmap);

			// LIST VIEW
			new HttpAsyncTask()
					.execute("http://www.e-fam.com/mobile_trolley_app/getcustomeraddresses.php");
			MYADDRESSES = new ArrayList<RequestedResultsSimpleList>();
			myadapter = new AdapterSimpleList(
					ActivityStep09ListDeliveryOptionsAddresses.this,
					MYADDRESSES, EkMukta_Light);
			listView.setAdapter(myadapter);
			new populateListViewTask().execute();

		} else {
			Intent intent = new Intent(
					ActivityStep09ListDeliveryOptionsAddresses.this,
					ActivityMyAccountLogin.class);
			intent.putExtra("from", "ActivityStep09ListDeliveryOptions");
			startActivity(intent);
		}
	}

	private void initViews() {
		extStorageDirectory = Environment.getExternalStorageState().toString();
		extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		listView = (ListView) findViewById(R.id.simpleListView);
		layout_progressbar = (LinearLayout) findViewById(R.id.progressbar_view);
		headersection = (RelativeLayout) findViewById(R.id.header);
		sharedPreferences = getSharedPreferences(MY_SESSION,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		SELECTED_OUTLET_ID = sharedPreferences.getString("selectedOutletId",
				null);
		SELECTED_OUTLET_TITLE = sharedPreferences.getString("selectedOutlet",
				null);
		SELECTED_BRAND_ID = sharedPreferences
				.getString("selectedBrandId", null);
		SELECTED_BRAND_TITLE = sharedPreferences.getString("selectedBrand",
				null);
		SELECTED_BRAND_LOGO = sharedPreferences.getString("selectedBrandLogo",
				null);
		SELECTED_BRANCH_ID = sharedPreferences.getString("selectedBranchId",
				null);
		SELECTED_BRANCH_TITLE = sharedPreferences.getString("selectedBranch",
				null);
		USER_PHONE_NUMBER = sharedPreferences.getString("userPhoneNumber", null);
		
		EkMukta_Light = Typeface.createFromAsset(getAssets(),
				"fonts/ek_mukta/EkMukta-Light.ttf");
		app_name = (TextView) findViewById(R.id.app_name);
		menuIcon = (Button) findViewById(R.id.menu_icon);
		headerText = (TextView) findViewById(R.id.headerText);
		shoppingButton = (Button) findViewById(R.id.shoppingButton);
		cartButton = (Button) findViewById(R.id.cartButton);
		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
		shopLogoview = (ImageView) findViewById(R.id.shopLogo);

		proceedToCheckout = (Button) findViewById(R.id.proceedToCheckout);
		clearcart = (Button) findViewById(R.id.clearcart);

		clearcart.setVisibility(View.VISIBLE);
		clearcart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (sharedPreferences.contains("myTrolley")) {
					try {
						showDialogEmptyTrolley("Are you sure?");
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(
							ActivityStep09ListDeliveryOptionsAddresses.this,
							"No items in your cart to clear!",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		proceedToCheckout.setVisibility(View.VISIBLE);
		proceedToCheckout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Intent intent = new Intent(ActivityMyAccountAddresses.this,
				// ActivityStep09ListCart.class);
				// startActivity(intent);
				if (sharedPreferences.contains("myTrolley")) {
					// Intent intent = new
					// Intent(ActivityMyAccountAddresses.this,
					// ActivityStep09ListDeliveryOptions.class);
					// startActivity(intent);
					Toast.makeText(
							ActivityStep09ListDeliveryOptionsAddresses.this,
							"click your cart to confirm first",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							ActivityStep09ListDeliveryOptionsAddresses.this,
							"No items in your cart !", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_page_in, R.anim.slide_page_out);// SlideIn
																				// animation
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_page_in, R.anim.slide_page_out);// SlideIn
																				// animation
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		overridePendingTransition(R.anim.slide_page_in, R.anim.slide_page_out);// SlideIn
																				// animation
		myadapter = new AdapterSimpleList(
				ActivityStep09ListDeliveryOptionsAddresses.this, MYADDRESSES,
				EkMukta_Light);
		listView.setAdapter(myadapter);
		myadapter.notifyDataSetChanged();

	}

	private class populateListViewTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return "Done";
		}

		protected void onPostExecute(String params) {
			listView.setDividerHeight(0); // remove the default divider line
			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RequestedResultsSimpleList item = (RequestedResultsSimpleList) listView
							.getItemAtPosition(position);
					if (item.item_id.equals("0")) {
						Intent intent = new Intent(
								ActivityStep09ListDeliveryOptionsAddresses.this,
								ActivityMyAccountAddresses.class);
						intent.putExtra("from",
								"ActivityStep09ListDeliveryOptionsAddresses");
						startActivity(intent);
					} else {

						editor.putString("deliveryAddress", item.item_title)
								.commit();
						Intent intent = new Intent(
								ActivityStep09ListDeliveryOptionsAddresses.this,
								ActivityStep10ListPaymentOptions.class);
						startActivity(intent);

					}
				}
			});
		}

	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			layout_progressbar.setVisibility(View.VISIBLE);

			AnimatedGifImageView animatedGifImageView = ((AnimatedGifImageView) findViewById(R.id.animatedGifImageView));
			animatedGifImageView.setAnimatedGif(R.drawable.loading_bar,
					TYPE.FIT_CENTER);
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

			// Toast.makeText(getBaseContext(), "Pick up options!",
			// Toast.LENGTH_LONG).show();

			try {
				Log.i("Results", " --> " + result);

				JSONArray jsonArr = new JSONArray(result);
				String[] list_item_ids = new String[jsonArr.length()];

				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonObj = jsonArr.getJSONObject(i);
					String item_id = jsonObj.getString("id");
					String item_title;

					if (item_id.equals("0")) {
						item_title = "No address found close to "
								+ SELECTED_BRAND_TITLE + " "
								+ SELECTED_BRANCH_TITLE
								+ ". \n Click to add address";
					} else {

						String item_neighbourhood_id = jsonObj
								.getString("location_id");
						String item_neighbourhood = jsonObj
								.getString("title");
						String item_apt_name = jsonObj
								.getString("apt_name");
						String item_apt_number = jsonObj
								.getString("apt_number");
						String item_landmark = jsonObj
								.getString("landmark");
						String item_road = jsonObj.getString("road");
						String item_description = jsonObj
								.getString("description");
						
						item_title = item_neighbourhood + "\n"
								+ item_landmark  + "\n" + item_road 
								+ "\n" + item_apt_name + "\n" + item_apt_number
								+ "\n" + item_description;
						
						list_item_ids[i] = item_id;
					}

					RequestedResultsSimpleList d = new RequestedResultsSimpleList();

					d.setTitle(item_title);

					d.item_id = item_id;
					d.item_title = item_title;
					MYADDRESSES.add(d);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("phone", USER_PHONE_NUMBER));

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
			if (inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public void showDialogEmptyTrolley(final String message) throws Exception {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityStep09ListDeliveryOptionsAddresses.this);
		builder.setMessage("You will lose all the items that you have selected. "
				+ message);

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Clear Cart Session
				sharedPreferences.edit().remove("myTrolley").commit();

				Intent intent = new Intent(
						ActivityStep09ListDeliveryOptionsAddresses.this,
						ActivityStep05ListAisles.class);
				startActivity(intent);

				// Go back to previous page
				// finish();
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();
	}

}