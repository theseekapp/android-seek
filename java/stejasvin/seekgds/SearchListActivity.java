package stejasvin.seekgds;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SearchListActivity extends ActionBarActivity {

    private Button bPlay;
    private Button bPause;
    MediaPlayer mediaPlayer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        mediaPlayer = MediaPlayer.create(this,Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/SeekLib/Oh Penne.mp3")));

        ArrayList<SearchResult> searchArray = getIntent().getParcelableArrayListExtra("searchList");
        if(searchArray == null || searchArray.size()==0) {
            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_LONG).show();
            return;
        }

        bPlay = (Button)findViewById(R.id.b_play_search);
        bPause = (Button)findViewById(R.id.b_pause_search);

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (mediaPlayer.getDuration()>1)
                    //mediaPlayer.();
                //else {
                    try {
                        if(mediaPlayer.getDuration()==0)
                            mediaPlayer.setDataSource(Constants.LIB_PATH + "/Oh Penne.mp3");
                        mediaPlayer.start();
                        //mediaPlayer.seekTo(searchResult.seekTime);
                    } catch (IOException e) {
                        Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            //}
        });

        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying())
                    mediaPlayer.pause();

                //else {
//                try {
//                    mediaPlayer.setDataSource(Constants.LIB_PATH + "/Oh Penne.mp3");
//                    mediaPlayer.start();
//                    //mediaPlayer.seekTo(searchResult.seekTime);
//                } catch (IOException e) {
//                    Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
                //}
            }
        });

        //ArrayList<SearchResult> stringList = Arrays.asList(searchArray);
        //TODO Make this list hold checkbox also, maybe use sharedprefs

        ListView list = (ListView)findViewById(R.id.list_search);
        list.setAdapter(new SearchListAdapter(this,searchArray,mediaPlayer));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
