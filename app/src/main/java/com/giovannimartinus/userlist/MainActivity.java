package com.giovannimartinus.userlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    final ItemList itemList = new ItemList();
    final DownloadTask downloadTask = new DownloadTask();

    private ListView listView;
    private RelativeLayout newUserLayout;

    private ArrayList<String> firstNames;
    private ArrayList<String> lastNames;
    private ArrayList<String> imgUrls;
    private ArrayList<String> fullNames;
    private ArrayList<Bitmap> avatars;

    private int length;

    private class ItemList {

        // concatenate firstNames and lastNames into a new ArrayList
        private void concatenateLists() {
            length = firstNames.size();

            for (int i = 0; i < length; i++) {
                fullNames.add(firstNames.get(i) + " " + lastNames.get(i));
            }
        }

        // download images and add to ArrayList of bitmaps
        private void avatarDownload() {
            for (int i = 0; i < fullNames.size(); i++) {
                DownloadImage downloadImage = new DownloadImage();
                Bitmap bitmap;

                try {
                    bitmap = downloadImage.execute(imgUrls.get(i)).get();
                    avatars.add(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void newAvatar(String url) {
            DownloadImage downloadImage = new DownloadImage();
            Bitmap bitmap;

            try {
                bitmap = downloadImage.execute(url).get();
                avatars.add(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // create custom Adapter to pass both ImageView and TextView to ListView
    public class CustomAdapter extends BaseAdapter {
        ArrayList<String> names;
        ArrayList<Bitmap> avatars;

        public CustomAdapter(ArrayList<String> names, ArrayList<Bitmap> avatars) {
            this.names = names;
            this.avatars = avatars;
        }

        public int getCount() {
            return names.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            View row = inflater.inflate(R.layout.custom, parent, false);
            TextView namesView = (TextView) row.findViewById(R.id.names);
            ImageView avatarsView = (ImageView) row.findViewById(R.id.avatars);

            namesView.setText(names.get(position));
            avatarsView.setImageBitmap(avatars.get(position));

            return row;
        }

    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap;
            URL url;
            HttpURLConnection urlConnection;

            try {
                // connect to browser with given url
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // read&&download the input stream and convert to bitmap
                InputStream inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

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

                // add names and images urls to ArrayLists
                itemList.concatenateLists();
                itemList.avatarDownload();

                // call custom adapter
                listView.setAdapter(new CustomAdapter(fullNames, avatars));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // onClick method to create new user
    public void createUser(View view) {
        newUserLayout.setVisibility(View.VISIBLE);
    }

    // onClick method to add new user to ListView
    public void addUser(View view) {
        newUserLayout.setVisibility(View.GONE);

        TextView nameText, urlText;
        nameText = (TextView) findViewById(R.id.nameText);
        urlText = (TextView) findViewById(R.id.urlText);

        String imgUrl = (String) urlText.getText().toString();

        fullNames.add(nameText.getText().toString());

        itemList.newAvatar(imgUrl);

        listView.setAdapter(new CustomAdapter(fullNames, avatars));

        Toast.makeText(MainActivity.this, "NEW USER ADDED", Toast.LENGTH_SHORT);


        nameText.setText("");
        urlText.setText("");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = (ListView) findViewById(R.id.listView);
        newUserLayout = (RelativeLayout) findViewById(R.id.newUserLayout);

        firstNames = new ArrayList<String>();
        lastNames = new ArrayList<String>();
        imgUrls = new ArrayList<String>();
        fullNames = new ArrayList<String>(length);
        avatars = new ArrayList<Bitmap>();

        try {
            downloadTask.execute("https://reqres.in/api/users");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
