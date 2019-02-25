package com.giovannimartinus.userlist;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private Button submitButton;
    private ListView listView;
    private RelativeLayout newUserLayout;

    private ArrayList<String> firstNames;
    private ArrayList<String> lastNames;
    private ArrayList<String> imgUrls;


    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                // connect to browser with given url
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // read&&download the input stream
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);

                int data = streamReader.read();

                // add scraped web content to result
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = streamReader.read();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                // get and convert string of web content to a JSON object
                JSONObject jsonObject = new JSONObject(result);

                // convert string of data to JSON objects and pass to JSON array
                String userData = jsonObject.getString("data");
                JSONArray jsonArray = new JSONArray(userData);

                // iterate through JSON array, get objects, and add to ArrayLists
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String firstName = jsonPart.getString("first_name");
                    firstNames.add(firstName);

                    String lastName = jsonPart.getString("last_name");
                    lastNames.add(lastName);

                    String imgUrl = jsonPart.getString("avatar");
                    imgUrls.add(imgUrl);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.addButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        listView = (ListView) findViewById(R.id.listView);
        newUserLayout = (RelativeLayout) findViewById(R.id.newUserLayout);

        firstNames = new ArrayList<String>();
        lastNames = new ArrayList<String>();
        imgUrls = new ArrayList<String>();
    }
}
