package com.righthere.efam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String MY_SESSION = "mySession";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SQLiteDatabase nunuaRahaDatabase;

    LinearLayout layout_progressbar;
    TextView app_name;
    TextView InternetIsConnected;
    Button startshopping;
    Button shopping_trolley2;
    Typeface EkMukta_SemiBold;
    Typeface EkMukta_Light;
    String extStorageDirectory;
    Bitmap bm;
    Calendar dateTime;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Exit App
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        initViews();

        // just add the following code to your onCreate() method to disable
        // check (requires API lvl 9 or above)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        extStorageDirectory = Environment.getExternalStorageState().toString();
        extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();

        // APPLY FONT_EKMUKTA_LIGHT TO THE WHOLE LAYOUT
        HelperFont.applyFont(findViewById(R.id.mainParent),
                getApplicationContext());

    }

    /*--- initialize layout components ---*/
    private void initViews() {
        // layout_progressbar = (LinearLayout)
        // findViewById(R.id.progressbar_view);
        nunuaRahaDatabase = openOrCreateDatabase("nunuaRahaDatabase",
                MODE_PRIVATE, null);
        // layout_progressbar.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences(MY_SESSION, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        EkMukta_SemiBold = Typeface.createFromAsset(getAssets(),
                "fonts/ek_mukta/EkMukta-SemiBold.ttf");
        EkMukta_Light = Typeface.createFromAsset(getAssets(),
                "fonts/ek_mukta/EkMukta-Light.ttf");
        app_name = (TextView) findViewById(R.id.app_name);
        InternetIsConnected = (TextView) findViewById(R.id.InternetIsConnected);
        startshopping = (Button) findViewById(R.id.startShopping);
        shopping_trolley2 = (Button) findViewById(R.id.shopping_trolley);

        app_name.setTypeface(EkMukta_SemiBold);

        // check if you are connected or not
        if (isConnected()) {
            dateTime = Calendar.getInstance();
            Integer DAY = dateTime.get(Calendar.DATE);
            Integer MONTH = dateTime.get(Calendar.MONTH);
            Integer YEAR = dateTime.get(Calendar.YEAR);
            today = DAY.toString() + "-" + MONTH.toString() + "-"
                    + YEAR.toString();

            // sharedPreferences.edit().remove("updateDate").commit(); //reset
            // database update

            if (sharedPreferences.contains("updateDate")) {
                String lastUpdateDateString = sharedPreferences.getString(
                        "updateDate", null);
                Log.i("last database update", " --> " + lastUpdateDateString);

                if (!lastUpdateDateString.equals(today)) {
                    // Update Database
                    Log.i("updating database", " --> " + today);
                    new HttpAsyncTask()
                            .execute("http://www.e-fam.com/mobile_trolley_app/getdatabase.php");
                }

            } else {
                // Get Database
                Log.i("creating database", " --> " + today);
                new HttpAsyncTask()
                        .execute("http://www.e-fam.com/mobile_trolley_app/getdatabase.php");
            }

            // InternetIsConnected.setText("THE NEW SHOPPING EXPERIENCE");
            shopping_trolley2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,
                            ActivityStep01ListOutlets.class);
                    startActivity(intent);
                }
            });

        } else {
            // layout_progressbar.setVisibility(View.GONE);
            InternetIsConnected.setVisibility(View.VISIBLE);
            startshopping.setVisibility(View.VISIBLE);
            InternetIsConnected
                    .setText("You are not connected to the internet. Make sure your data is enabled then reload");
            startshopping.setText("Reload");
            startshopping.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    finish();
                    startActivity(getIntent());
                }
            });

        }

        // sharedPreferences.edit().clear().commit();//Remove all shared
        // preferences
        // sharedPreferences.edit().remove("customerInfo").commit();

    }

    private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        } catch (IOException e1) {
        }
        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // closing database connection
        if (nunuaRahaDatabase != null)
            nunuaRahaDatabase.close();
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
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

    private class HttpAsyncTask extends AsyncTask<String, Integer, String> {
        private Context context = MainActivity.this;
        private ProgressDialog progressDialog;

        public void BackgroundTask() {
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog
                        .setMessage("Please wait while we update your shops with todays product lists");
                progressDialog.setIndeterminate(true);
                progressDialog.setProgress(0);
                progressDialog.show();

                WindowManager.LayoutParams lp = progressDialog.getWindow()
                        .getAttributes();
                lp.dimAmount = 0.0f;
                progressDialog.getWindow().setAttributes(lp);
                progressDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

				/*
				 * progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				 * progressDialog.setContentView(R.layout.dialog_login);
				 * progressDialog.getWindow().setBackgroundDrawable(new
				 * ColorDrawable(android.graphics.Color.TRANSPARENT));
				 */
                Thread.sleep(6000);
                final int totalProgressTime = 100;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;

                        while (jumpTime < totalProgressTime) {
                            try {
                                sleep(200);
                                jumpTime += 5;
                                progressDialog.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();

                // progressDialog = ProgressDialog.show(ActivityMain.this, "",
                // "message", true);
            } catch (final Throwable th) {
                // TODO
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("makemachine",
                    "onProgressUpdate(): " + String.valueOf(values[0]));
            Integer percentageProgress = values[0] * 2;
            setProgress(percentageProgress);
            // setProgress(values[0]);
        }

        /**
         * @Override protected void onPreExecute() {
         *           layout_progressbar.setVisibility(View.VISIBLE);
         *           super.onPreExecute(); }
         **/

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // layout_progressbar.setVisibility(View.GONE);

            // super.onPostExecute(result);

            Toast.makeText(getBaseContext(), "Outlets Received!",
                    Toast.LENGTH_LONG).show();
            Log.i("Database", " --> " + result);

            if (result.equals("")) {
                // Results not found, query for database again
                // new
                // HttpAsyncTask().execute("http://www.e-fam.com/mobile_trolley_app/getdatabase.php");

                Toast.makeText(getBaseContext(), "Slow server connection",
                        Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {

                Gson gson = new Gson();
                List[] jsonDbList = gson.fromJson(result, List[].class);
                List jsonDbTableListObj = Arrays.asList(jsonDbList);

                for (int i = 0; i < jsonDbTableListObj.size(); i++) {
                    Object getDbTableObj = jsonDbTableListObj.get(i);
                    ArrayList dbTable = (ArrayList) getDbTableObj;

                    // TABLE NAME
                    String tableName = dbTable.get(0).toString();

                    Log.i("Table", " --> " + tableName);

                    // TABLE FIELDS
                    String createFields = "";
                    Object getTableFieldsObj = dbTable.get(1);
                    ArrayList dbTableFields = (ArrayList) getTableFieldsObj;
                    for (int j = 0; j < dbTableFields.size(); j++) {
                        String tableField = dbTableFields.get(j).toString();

                        Log.i("Fields", " --> " + tableField);

                        if (j == 0) {
                            createFields = createFields
                                    + dbTableFields.get(j).toString()
                                    + " integer primary key,";
                        } else if (j == dbTableFields.size() - 1) {
                            createFields = createFields
                                    + dbTableFields.get(j).toString()
                                    + " VARCHAR";
                        } else {
                            createFields = createFields
                                    + dbTableFields.get(j).toString()
                                    + " VARCHAR,";
                        }
                    }

                    // DROP TABLE
                    nunuaRahaDatabase.execSQL("DROP TABLE IF EXISTS "
                            + tableName);

                    // CREATE TABLE
                    nunuaRahaDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                            + tableName + "(" + createFields + ");");
                    // Log.i("Fields"," --> "+createFields);

                    Object getTableRowsObj = dbTable.get(2);
                    String jsonTableRows = gson.toJson(getTableRowsObj);
                    try {
                        JSONArray jsonRowsArray = new JSONArray(jsonTableRows);
                        for (int k = 0; k < jsonRowsArray.length(); k++) {
                            JSONObject rowObj = jsonRowsArray.getJSONObject(k);
                            ContentValues dbRowData = new ContentValues();

                            // INSERT NEW ENTRY
                            for (int j = 0; j < dbTableFields.size(); j++) {
                                String tableField = dbTableFields.get(j)
                                        .toString();
                                dbRowData.put(tableField,
                                        rowObj.getString(tableField)
                                                .replaceAll("'", "\'"));
                                // Log.i("Row"," --> "+rowObj.getString(tableField).replaceAll("'","\'"));
                            }
                            nunuaRahaDatabase
                                    .insert(tableName, null, dbRowData);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (i == jsonDbTableListObj.size() - 1) {
                        progressDialog.dismiss();
                    }
                }

                // Save update time
                editor.putString("updateDate", today).commit();

                // Get Images
                new SaveDownloadImagesTask()
                        .execute("http://www.e-fam.com/mobile_trolley_app/getimages.php");

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

        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);

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

    private class SaveDownloadImagesTask extends
            AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Log.i("images"," --> "+result);

            try {
                File folder = new File(
                        Environment.getExternalStorageDirectory()
                                + "/Efam");
                if (!folder.exists()) {
                    File imagesFolder = new File(extStorageDirectory
                            + "/Efam/");
                    imagesFolder.mkdirs();
                }

                JSONArray jsonArr = new JSONArray(result);
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String image_name = jsonObj.getString("logo");
                    String image_URL = "http://e-fam.com/mobiletrolley/img/uploads/"
                            + image_name;
                    // Log.i("image"," --> "+image_name);

                    BitmapFactory.Options bmOptions;
                    bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 1;
                    bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bm = LoadImage(image_URL, bmOptions);

                    OutputStream outStream = null;
                    File file = new File(extStorageDirectory + "/Efam/",
                            image_name);
                    if (file.exists()) {
                        file.delete();
                    }

                    try {
                        outStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();

                        // Toast.makeText(ActivityMain.this,
                        // "Images setup successfull",Toast.LENGTH_LONG).show();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
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

}
