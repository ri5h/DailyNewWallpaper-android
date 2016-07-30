package in.rishirajpurohit.android.dailynewwallpaper;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public Bitmap mainimage;

    public void btn_setWallpaper_clicked(View view){

        //Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.test_image);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setBitmap(mainimage);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private class JsonData extends AsyncTask<URL, Integer, String> {

        ArrayList<String> items = new ArrayList<>();

        protected String doInBackground(URL... processurl) {
            int j = 0;
            String myurlbase;
            String myimageurl = null;
            publishProgress(j);

            /*try and catch json parser*/
            try {
                URL url = processurl[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // gets the server json data
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String next;
                while ((next = bufferedReader.readLine()) != null) {

                    JSONObject jo = new JSONObject(next);

                    // TEST CASE : myurlbase = jo.toString();
                    JSONArray images = jo.getJSONArray("images");
                    /*myurlbase = images.getString("urlbase");*/
                    JSONObject job = (JSONObject) images.opt(0);
                    myurlbase = job.getString("urlbase");

                    myimageurl = "http://bing.com" + myurlbase + "_800x600.jpg";


                    //Get the instance of JSONArray that contains JSONObjects
                    //JSONArray jsonArray = jo.optJSONArray("images");

                    //jsonArray.getString()

                }
            } catch (IOException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return myimageurl;
        }
            /* END : try and catch json parser*/


        protected void onProgressUpdate(Integer... progress) {

            Toast.makeText(getApplicationContext(), "Started at "+ progress[0] +"percent", Toast.LENGTH_SHORT).show();
        }

        protected void onPostExecute(String result) {
            TextView tt = (TextView) findViewById(R.id.textViewjson);
            if (result != null){
                tt.setText(result);
            }else{
                tt.setText("null value returned");
            }

            URL myimageurl = null;
            try {
                myimageurl = new URL(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new getBitmap().execute(myimageurl);



        }

    }


    /* Class to Get Bitmap image */

    public class getBitmap extends AsyncTask<URL, Integer, Bitmap> {

        protected Bitmap doInBackground(URL... processurl) {

            Bitmap btmp = null;
            URL url = processurl[0];

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = (ImageView) findViewById(R.id.mainImage);
            imageView.setImageBitmap(result);
            mainimage = result;

        }

    }

    /*END : class to get bitmap image*/

    public void doShit(View view){

        final ProgressDialog progressDialog = ProgressDialog.show(this, "title", "Message");

        Thread th = new Thread(){
            public void run(){
                URL url = null;
                try {
                    url = new URL("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US");
                }catch (IOException e){
                    e.printStackTrace();
                }
                new JsonData().execute(url);

                TextView tt = (TextView) findViewById(R.id.textViewjson);
                String imageurl = tt.getText().toString();
                progressDialog.dismiss();
            }
        };
        th.start();





    }
}
