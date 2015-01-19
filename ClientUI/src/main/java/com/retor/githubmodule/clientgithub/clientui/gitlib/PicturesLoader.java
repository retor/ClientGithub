package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import com.retor.githubmodule.clientgithub.clientui.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by retor on 17.01.2015.
 */
public final class PicturesLoader {
    private static volatile PicturesLoader instance = null;
    private int cacheSize = 8 * 1024 * 1024;
    private LruCache picturesCahche;
    private List<String> stringsCache = new ArrayList<String>();

    protected PicturesLoader(){
        picturesCahche = new LruCache(cacheSize){
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }};
    }

    public static PicturesLoader instance(){
        PicturesLoader localInstance = instance;
        if (localInstance == null) {
            synchronized (PicturesLoader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PicturesLoader();
                }
            }
        }
        return instance;
    }

    public void loadImage(ImageView imageView, String url){
        final String imageKey = url;
        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(url);
            Log.d("Loader status", task.getStatus().name()+ " " +url);
        }
    }

    public void fillCache(String url){
        ChacheFiller filler = new ChacheFiller();
        if (!stringsCache.contains(url)) {
            stringsCache.add(url);
            filler.execute(url);
        }
/*        ChacheFiller filler = new ChacheFiller();
        Bitmap bitmap = getBitmapFromMemCache(url);
        if (bitmap==null)
            filler.execute(url);*/
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            picturesCahche.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap)picturesCahche.get(key);
    }

    private class ChacheFiller extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bit_out = null;
            HttpGet httpRequest = null;
            try {
                httpRequest = new HttpGet(new URL(params[0]).toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            InputStream instream=null;
            try {
                response = (HttpResponse) httpclient.execute(httpRequest);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                instream = bufHttpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bit_out = BitmapFactory.decodeStream(instream);
            addBitmapToMemoryCache(params[0], bit_out);
            return bit_out;
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;
        BitmapWorkerTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("LoaderPic", "started");

            Bitmap bit_out = null;
            HttpGet httpRequest = null;
            try {
                httpRequest = new HttpGet(new URL(params[0]).toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            InputStream instream=null;
            try {
                response = (HttpResponse) httpclient.execute(httpRequest);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                instream = bufHttpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bit_out = BitmapFactory.decodeStream(instream);
            addBitmapToMemoryCache(params[0], bit_out);
            return bit_out;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
        }
    }
}