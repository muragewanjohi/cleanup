package com.redhering.nunuaraha;

import java.io.File;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.redhering.nunuaraha.AnimatedGifImageView;
import com.redhering.nunuaraha.AnimatedGifImageView.TYPE;

public class ActivityStep07ListProducts extends Activity {
	SQLiteDatabase nunuaRahaDatabase;
	public static final String MY_SESSION = "mySession";
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	Gson gson;

	ArrayList<RequestedResults> PRODUCTSBRANDSLIST;
	AdapterProducts myadapter;

	LinearLayout layout_progressbar;
	RelativeLayout headersection;
	RelativeLayout centerwrap;
	RelativeLayout footersection;
	LinearLayout listViewCont;
	ListView listView;
	public static String SELECTED_PRODUCT_ID;
	public static String SELECTED_PRODUCT_TITLE;
	public static String SELECTED_AISLE_ID;
	public static String SELECTED_AISLE_TITLE;
	public static String SELECTED_BRANCH_ID;
	public static String SELECTED_BRANCH_TITLE;
	public static String SELECTED_BRAND_ID;
	public static String SELECTED_BRAND_TITLE;
	public static String SELECTED_BRAND_LOGO;
	public static String SELECTED_LOCATION_ID;
	public static String SELECTED_LOCATION_TITLE;
	public static String SELECTED_OUTLET_ID;
	public static String SELECTED_OUTLET_TITLE;
	public static String PREVGOODSLISTID;
	public static String PREVGOODSLISTTITLE;
	public static String NEXTGOODSLISTID;
	public static String NEXTGOODSLISTTITLE;
	public static Integer CURRENT_GOODS_LIST_ID_POSITION;
	public static Integer PREV_GOODS_LIST_ID_POSITION;
	public static Integer NEXT_GOODS_LIST_ID_POSITION;
	public static Integer CURRENT_AISLE_ID_POSITION;
	Typeface EkMukta_Light;
	TextView app_name;
	TextView headerText;
	Button menuIcon;
	Button shoppingButton;
	Button cartButton;
	TextView cartButtonNotification;
	TextView ItemperPrice;
	Button backToAisles;
	Button backToCategory;
	TextView aisleTitle;
	ImageView shopLogoview;
	String extStorageDirectory;
	Bitmap bm;

	Button proceedToCheckout;
	Button clearcart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scroller_products);
		overridePendingTransition(R.anim.slide_page_in, R.anim.slide_page_out);// SlideIn
																				// animation
		initViews();

		// LIST VIEW
		new retrieveFromDBTask().execute();
		PRODUCTSBRANDSLIST = new ArrayList<RequestedResults>();
		myadapter = new AdapterProducts(ActivityStep07ListProducts.this,
				PRODUCTSBRANDSLIST, cartButtonNotification, layout_progressbar);
		listView.setAdapter(myadapter);
		myadapter.notifyDataSetChanged();

	}

	private void initViews() {
		extStorageDirectory = Environment.getExternalStorageState().toString();
		extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		nunuaRahaDatabase = openOrCreateDatabase("nunuaRahaDatabase",
				MODE_PRIVATE, null);
		listView = (ListView) findViewById(R.id.productsListView);
		layout_progressbar = (LinearLayout) findViewById(R.id.progressbar_view);
		headersection = (RelativeLayout) findViewById(R.id.header);
		footersection = (RelativeLayout) findViewById(R.id.footer);
		listViewCont = (LinearLayout) findViewById(R.id.listViewCont);
		sharedPreferences = getSharedPreferences(MY_SESSION,
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		gson = new Gson();
		EkMukta_Light = Typeface.createFromAsset(getAssets(),
				"fonts/ek_mukta/EkMukta-Light.ttf");
		app_name = (TextView) findViewById(R.id.app_name);
		headerText = (TextView) findViewById(R.id.headerText);
		menuIcon = (Button) findViewById(R.id.menu_icon);
		shoppingButton = (Button) findViewById(R.id.shoppingButton);
		cartButton = (Button) findViewById(R.id.cartButton);
		cartButtonNotification = (TextView) findViewById(R.id.cartButtonNotification);
		backToAisles = (Button) findViewById(R.id.backToAisles);
		backToCategory = (Button) findViewById(R.id.backButton);
		proceedToCheckout = (Button) findViewById(R.id.proceedToCheckout);
		aisleTitle = (TextView) findViewById(R.id.aisleTitle);
		// shopLogoview = (ImageView) findViewById(R.id.shopLogo);

		proceedToCheckout = (Button) findViewById(R.id.proceedToCheckout);
		clearcart = (Button) findViewById(R.id.clearcart);

		SELECTED_OUTLET_ID = sharedPreferences.getString("selectedOutletId",
				null);
		SELECTED_OUTLET_TITLE = sharedPreferences.getString("selectedOutlet",
				null);
		SELECTED_LOCATION_ID = sharedPreferences.getString(
				"selectedLocationId", null);
		SELECTED_LOCATION_TITLE = sharedPreferences.getString(
				"selectedLocation", null);
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
		SELECTED_AISLE_ID = sharedPreferences
				.getString("selectedAisleId", null);
		SELECTED_AISLE_TITLE = sharedPreferences.getString("selectedAisle",
				null);
		Bundle extras = getIntent().getExtras();
		SELECTED_PRODUCT_ID = extras.getString("selectedProductId");
		SELECTED_PRODUCT_TITLE = extras.getString("selectedProductTitle");
		app_name.setTypeface(EkMukta_Light);
		headerText.setText(SELECTED_BRANCH_TITLE);
		aisleTitle.setText(SELECTED_AISLE_TITLE + " : "
				+ SELECTED_PRODUCT_TITLE);

		// backToCategory.setText("Back to "+SELECTED_AISLE_TITLE);

		ImageView headerTextimage2 = (ImageView) findViewById(R.id.headerTextimage);
		headerTextimage2.setVisibility(View.VISIBLE);
		headerTextimage2.setImageResource(R.drawable.superheading1);

		// LOAD QUICKLINKS
		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
		helperQuickLinks.create(menuIcon, shoppingButton, cartButton,
				cartButtonNotification, ActivityStep07ListProducts.this,
				sharedPreferences);

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
					Toast.makeText(ActivityStep07ListProducts.this,
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
					Toast.makeText(ActivityStep07ListProducts.this,
							"click your cart to confirm first",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ActivityStep07ListProducts.this,
							"No items in your cart !", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		footersection.setVisibility(View.VISIBLE);
		backToCategory.setVisibility(View.VISIBLE);
		// backToAisles.setVisibility(View.VISIBLE);

		backToCategory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActivityStep07ListProducts.this,
						ActivityStep05ListAisles.class);
				startActivity(intent);
			}
		});

		backToAisles.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		/*
		 * proceedToCheckout.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) { Intent intent = new
		 * Intent(ActivityStep07ListProducts.this,
		 * ActivityStep08ListCart.class); startActivity(intent); } });
		 */

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

		myadapter = new AdapterProducts(ActivityStep07ListProducts.this,
				PRODUCTSBRANDSLIST, cartButtonNotification, layout_progressbar);
		listView.setAdapter(myadapter);
		myadapter.notifyDataSetChanged();

		// LOAD QUICKLINKS
		HelperQuickLinks helperQuickLinks = new HelperQuickLinks();
		helperQuickLinks.create(menuIcon, shoppingButton, cartButton,
				cartButtonNotification, ActivityStep07ListProducts.this,
				sharedPreferences);
	}

	private class retrieveFromDBTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			layout_progressbar.setVisibility(View.VISIBLE);

			AnimatedGifImageView animatedGifImageView = ((AnimatedGifImageView) findViewById(R.id.animatedGifImageView));
			animatedGifImageView.setAnimatedGif(R.drawable.loading_bar,
					TYPE.FIT_CENTER);

			listView.setVisibility(View.GONE);
			super.onPreExecute();
		}

		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return "Done";
		}

		protected void onPostExecute(String params) {

			layout_progressbar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			myadapter.notifyDataSetChanged();
			// super.onPostExecute();
			JSONObject jsonMyCartObject = null;
			if (sharedPreferences.contains("myTrolley")) {
				String jsonMyCartString = sharedPreferences.getString(
						"myTrolley", null);
				try {
					jsonMyCartObject = new JSONObject(jsonMyCartString);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			Cursor resultsStockCursor = nunuaRahaDatabase.rawQuery(
					"SELECT * FROM hdjgf_shops_stocks WHERE branch_id = "
							+ SELECTED_BRANCH_ID + " AND aisle_id = "
							+ SELECTED_AISLE_ID + " AND product_id = "
							+ SELECTED_PRODUCT_ID, null);
			resultsStockCursor.moveToFirst();
			String stock_id = resultsStockCursor.getString(0);

			Cursor resultsProductsCursor = nunuaRahaDatabase.rawQuery(
					"SELECT * FROM hdjgf_shops_stocks_brands WHERE stock_id = "
							+ stock_id, null);
			resultsProductsCursor.moveToFirst();
			String[] list_item_ids = new String[resultsProductsCursor
					.getCount()];
			String[] list_item_titles = new String[resultsProductsCursor
					.getCount()];

			int k = 0;
			while (resultsProductsCursor.isAfterLast() == false) {
				String item_id = resultsProductsCursor.getString(0);
				String item_title = resultsProductsCursor.getString(1);
				String item_size = resultsProductsCursor.getString(2);
				String item_price = resultsProductsCursor.getString(3);
				String item_thumbnail_url = resultsProductsCursor.getString(4);

				Integer item_units_in_cart = 0;
				if (jsonMyCartObject != null) {
					try {
						item_units_in_cart = Integer.parseInt(jsonMyCartObject
								.getString(item_id));
					} catch (NumberFormatException | JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				list_item_ids[k] = item_id;
				list_item_titles[k] = item_title;

				RequestedResults d = new RequestedResults();

				d.setId(item_id);
				d.setTitle(item_title);
				d.setSize(item_size);
				d.setPrice(item_price);
				d.setThumbnailUrl("http://smokesignal.co.ke/mobiletrolley/img/uploads/"
						+ item_thumbnail_url);
				d.setUnits(item_units_in_cart);

				d.item_id = item_id;
				d.item_title = item_title;
				d.item_price = item_price;
				d.item_size = item_size;
				d.item_thumbnail_url = "http://smokesignal.co.ke/mobiletrolley/img/uploads/"
						+ item_thumbnail_url;
				d.item_units_in_cart = item_units_in_cart;

				PRODUCTSBRANDSLIST.add(d);

				k++;
				resultsProductsCursor.moveToNext();
			}

		}

	}

	public void showDialogEmptyTrolley(final String message) throws Exception {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityStep07ListProducts.this);
		builder.setMessage("You will lose all the items that you have selected. "
				+ message);

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Clear Cart Session
				sharedPreferences.edit().remove("myTrolley").commit();

				Intent intent = new Intent(ActivityStep07ListProducts.this,
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
