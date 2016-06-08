package com.example.vamsee.currencyconverter;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText mSourceEdit;
    EditText mTargetEdit;

    Spinner mSourceSpinner;
    Spinner mTargetSpinner;
    Button mSubmitButton;

    Map<String, String> categoriesTarget;
    List<String> categoriesSource;

    public static final String ACCESS_KEY = "13ae1e1a7c99e1871a3dc57f73d67981";
    public static final String BASE_URL = "http://apilayer.net/api/";
    public static final String ENDPOINT = "live";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner Drop down elements
        categoriesTarget = new HashMap<String, String>();
        categoriesTarget.put("Indian Rupee", "INR");
        //categoriesTarget.add("USD");//US Dollar
        categoriesTarget.put("British Pound", "GBP");
        categoriesTarget.put("Canadian Dollar", "CAD");
        categoriesTarget.put("Australian Dollar", "AUD");
        categoriesTarget.put("Canadian Dollar", "CAD");
        categoriesTarget.put("Euro", "EUR");
        categoriesTarget.put("UAE Dirham", "AED");
        categoriesTarget.put("Afghan Afghani", "AFN");
        categoriesTarget.put("Albanian Lek", "ALL");
        categoriesTarget.put("Armenian Dram", "AMD");
        categoriesTarget.put("Netherlands Guilder", "ANG");

        categoriesSource = new ArrayList<String>();
        categoriesSource.add("USD");//Indian Rupee

        mSourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        mTargetSpinner = (Spinner) findViewById(R.id.target_spinner);
        mSourceEdit = (EditText) findViewById(R.id.source_edit);
        mTargetEdit = (EditText) findViewById(R.id.target_edit);
        mSubmitButton = (Button) findViewById(R.id.submit_button);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterSource = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesSource);
        dataAdapterSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterTarget = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(categoriesTarget.keySet()));
        dataAdapterTarget.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style

        // attaching data adapter to spinner
        mSourceSpinner.setAdapter(dataAdapterSource);
        mTargetSpinner.setAdapter(dataAdapterTarget);
    }

    public void submitValues(View view){
        //Check for network connectivity
        if(isConnected()){
            String sourceText = mSourceEdit.getText().toString().trim();
            int source = -1;
            if(sourceText != null && !TextUtils.isEmpty(sourceText)){
                source = Integer.parseInt(sourceText);
            }else{
                source = 0;
            }

            if(source <= 0){
                source = 1;
                mSourceEdit.setText("1");
            }

            //Call the background task to fetch the results
            FetchAsync fetchAsync = new FetchAsync();
            fetchAsync.execute(categoriesTarget.get(mTargetSpinner.getSelectedItem()));
        }else{
            Toast.makeText(MainActivity.this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }

    }

    public class FetchAsync extends AsyncTask<String, Void, Double>{

        @Override
        protected Double doInBackground(String... params) {
            Log.e("VMC", "DO IN BG ----> "+params[0]);
            HttpClient httpClient = new DefaultHttpClient();
            String siteURL = BASE_URL + ENDPOINT+"?access_key="+ACCESS_KEY+"&currencies="+params[0];
            HttpGet httpGetObj = new HttpGet(siteURL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {
                //String serverResponce = ;
                HttpResponse response = httpClient.execute(httpGetObj);
                HttpEntity entity = response.getEntity();

                JSONObject exchangeRates = new JSONObject(EntityUtils.toString(entity));

                return exchangeRates.getJSONObject("quotes").getDouble("USD"+params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }


            return 0d;
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);

            int sourceValue = Integer.parseInt(mSourceEdit.getText().toString());

            mTargetEdit.setText((sourceValue * result)+"");
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
