package stejasvin.seekgds;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SeekDataDownloadService extends IntentService {

    private static final String EXTRA_SEARCH = "searchString";

    public SeekDataDownloadService() {
        super("SeekDataDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            //if (ACTION_FOO.equals(action)) {
            final String param1 = intent.getStringExtra(EXTRA_SEARCH);
            handleActionFoo(param1);
            //}
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String sSearch) {
        // TODO: Handle action Foo

        String urlGetData = Constants.ROOT_URL + sSearch + "/";
        String resp = "";
        boolean success = false;
        try {
            URL url;
            url = new URL(urlGetData);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams
                    .setConnectionTimeout(httpParameters, 50000);
            HttpConnectionParams.setSoTimeout(httpParameters, 50000);

            HttpGet httpGet = new HttpGet(url.toString());
            //HttpPost httppost = new HttpPost(url.toString());
            //httppost.setEntity(new UrlEncodedFormEntity(params));
            httpGet.setParams(httpParameters);
            HttpResponse response = httpClient
                    .execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

//                JSONArray jsonArray = Utilities.JSONAParse(is);

            resp = Utilities.IsParse(is);
            //Log.i(TAG, resp);
            Log.i("SearchString", sSearch);
            success = true;


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<SearchResult> searchResultArrayList = new ArrayList<SearchResult>();
        if(success) {

            try {
                JSONArray jsonArray = new JSONArray(resp);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SearchResult searchResult1 = new SearchResult();
                    searchResult1.setFileName(jsonObject.getString(Constants.JSON_NAME));
                    searchResult1.setFilePath(jsonObject.getString(Constants.JSON_URL));
                    searchResult1.setSubtitle(jsonObject.getString(Constants.JSON_TEXT));

                    String seekTime = jsonObject.getString(Constants.JSON_SEEK);
                    int hrs0=0,hrs1=0,min0=0,min1=0,sec0=0,sec1=0,milliTime = 0,sec=0,min=0;
                    if(!seekTime.equals("")&&seekTime.contains(":")) {
                        String times[] = seekTime.split(":");
                        try {
                            hrs0 = Integer.decode(times[0].charAt(0)+"");
                            hrs1 = Integer.decode(times[0].charAt(1)+"");
                            min0 = Integer.decode(times[1].charAt(0)+"");
                            min1 = Integer.decode(times[1].charAt(1)+"");
                            sec0 = Integer.decode(times[2].charAt(0)+"");
                            sec1 = Integer.decode(times[2].charAt(1)+"");
                            min = 10*min0+min1;
                            sec = sec0*10+sec1;
                        }catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        milliTime = (sec0*10+sec1) + 60 * (60 * (10*hrs0+hrs1) + (10*min0+min1));
                    }
                    searchResult1.setSeekTime(milliTime);
                    searchResult1.setSeekString(seekTime);
                    searchResultArrayList.add(searchResult1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent broadcastIntent = new Intent(Utilities.DOWN_DATA);
        broadcastIntent.putExtra("onlineSearchList", searchResultArrayList);
        broadcastIntent.putExtra("success", success);
        sendBroadcast(broadcastIntent);
        //Intent broadcastIntent = new Intent(Utilities.UPLOAD_NOTE_ACTION);
        //sendBroadcast(broadcastIntent);


    }

}
