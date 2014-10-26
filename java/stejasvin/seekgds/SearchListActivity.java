package stejasvin.seekgds;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SearchListActivity extends ActionBarActivity {

    private Button bPlay;
    private Button bPause;
    public TextView songName,startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    public static int oneTimeOnly = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myHandler.removeCallbacks(UpdateSongTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        String filePath = Environment.getExternalStorageDirectory()+"/SeekLib/Pinocchio.mp3";
        mediaPlayer = MediaPlayer.create(this,Uri.fromFile(new File(filePath)));
        startTimeField =(TextView)findViewById(R.id.tv_start_search);
        endTimeField =(TextView)findViewById(R.id.tv_end_search);
        seekbar = (SeekBar)findViewById(R.id.sb_search);
        bPlay = (Button)findViewById(R.id.b_play_search);
        bPause = (Button)findViewById(R.id.b_pause_search);

        seekbar.setClickable(false);
        bPause.setEnabled(false);

        ArrayList<SearchResult> searchArray = getIntent().getParcelableArrayListExtra("searchList");
        if(searchArray == null || searchArray.size()==0) {
            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_LONG).show();
            return;
        }

        //First run
        try {
            if(mediaPlayer.getDuration()==0)
                mediaPlayer.setDataSource(filePath);
            //So that audio doesnt play
            //mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }

            endTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) finalTime)))
            );
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime,100);
            bPlay.setEnabled(true);
            bPause.setEnabled(false);
            //mediaPlayer.seekTo(searchResult.seekTime);
        } catch (IOException e) {
            Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    playAudio(Constants.LIB_PATH + "/Oh Penne.mp3");
                }
            //}
        });

//        public void play(View view){
//            Toast.makeText(getApplicationContext(), "Playing sound",
//                    Toast.LENGTH_SHORT).show();
//            mediaPlayer.start();
//            finalTime = mediaPlayer.getDuration();
//            startTime = mediaPlayer.getCurrentPosition();
//            if(oneTimeOnly == 0){
//                seekbar.setMax((int) finalTime);
//                oneTimeOnly = 1;
//            }
//
//            endTimeField.setText(String.format("%d min, %d sec",
//                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) finalTime)))
//            );
//            startTimeField.setText(String.format("%d min, %d sec",
//                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) startTime)))
//            );
//            seekbar.setProgress((int)startTime);
//            myHandler.postDelayed(UpdateSongTime,100);
//            pauseButton.setEnabled(true);
//            playButton.setEnabled(false);
//        }

        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    bPause.setEnabled(false);
                    bPlay.setEnabled(true);
                }
            }
        });


        //ArrayList<SearchResult> stringList = Arrays.asList(searchArray);
        //TODO Make this list hold checkbox also, maybe use sharedprefs

        ListView list = (ListView)findViewById(R.id.list_search);
        list.setAdapter(new SearchListAdapter(this,searchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime));
    }

    public void playAudio(String filePath){
        try {
            if(mediaPlayer.getDuration()==0)
                mediaPlayer.setDataSource(filePath);
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }

            endTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) finalTime)))
            );
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime,100);
            bPause.setEnabled(true);
            bPlay.setEnabled(false);
            //mediaPlayer.seekTo(searchResult.seekTime);
        } catch (IOException e) {
            Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


}
