package com.example.akash.bloombergproject3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public ArrayList<Movies> movies = new ArrayList<Movies>();
    ListView list;
    RadioGroup radioGroup;
    String listType;
    static JSONObject themovie = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.movielist);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        listAccess access = new listAccess();
        access.execute("now_playing");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (radioGroup.getCheckedRadioButtonId()== R.id.toprated){
                    listType = "top_rated";
                    listAccess access = new listAccess();
                    access.execute(listType);
                }
                if (radioGroup.getCheckedRadioButtonId()== R.id.popular){
                    listType = "popular";
                    listAccess access = new listAccess();
                    access.execute(listType);
                }
                if(radioGroup.getCheckedRadioButtonId() ==R.id.upcoming){
                    listType = "upcoming";
                    listAccess access = new listAccess();
                    access.execute(listType);
                }
                if(radioGroup.getCheckedRadioButtonId() ==R.id.latest){
                    listType = "now_playing";
                    listAccess access = new listAccess();
                    access.execute(listType);
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String m = movies.get(i).getName();
                movieAccess maccess = new movieAccess();
                maccess.execute(m);
            }
        });
    }

    public class listAccess extends AsyncTask<String, Void, Void> {
        public String st;
        CustomAdapter adapter;
        @Override
        protected Void doInBackground(String ... voids) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+voids[0]+"?api_key=b6e42c33a063765b093762f7ec6da2c6");
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(reader);
                st=br.readLine();
                JSONObject jsonObject = new JSONObject(st);
                JSONArray l = jsonObject.getJSONArray("results");
                movies = new ArrayList<Movies>();
                for(int x = 0; x<20; x++){
                    JSONObject movie = l.getJSONObject(x);
                    String name = movie.getString("title");
                    String image = "https://image.tmdb.org/t/p/w200"+movie.getString("poster_path");
                    String votes = movie.getString("vote_average");
                    movies.add(new Movies(name,image, votes));
                    adapter = new CustomAdapter(MainActivity.this,R.layout.movie_adapter,movies);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{

                list.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class movieAccess extends AsyncTask<String, Void, Void> {
        public String st;
        @Override
        protected Void doInBackground(String ... voids) {
            try {
                URL url = new URL("http://www.omdbapi.com/?t="+voids[0]+"&apikey=4c7075e0");
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(reader);
                JSONObject movie = new JSONObject(br.readLine());
                themovie = movie;
                Intent i = new Intent(MainActivity.this,moviedetails.class);
                startActivityForResult(i,0);
                st=br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                JSONObject movie = new JSONObject(st);
                themovie = movie;
                String m =  movie.getString("Title");
                Toast.makeText(MainActivity.this, m+"intent ran", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getApplication(), moviedetails.class);
                startActivity(myIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CustomAdapter extends ArrayAdapter<Movies> {
        Context context;
        List list;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Movies> objects){
            super(context, resource, objects);
            this.context = context;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(R.layout.movie_adapter,null);

            TextView name = adapterView.findViewById(R.id.Name);
            TextView votes = adapterView.findViewById(R.id.info);
            ImageView picture = adapterView.findViewById(R.id.picture);

            name.setText(movies.get(position).getName());
            String vot = movies.get(position).getVotes()+"/10";
            votes.setText(vot);

            String movielink = movies.get(position).getLink();
            Log.d("Movielink",movielink);
            try {
                Bitmap bit = new DownloadImageTask().execute(movielink).get();
                picture.setImageBitmap(bit);
                Log.d("Movielink",movielink);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return adapterView;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            //bmImage.setImageBitmap(result);
        }
    }
}
