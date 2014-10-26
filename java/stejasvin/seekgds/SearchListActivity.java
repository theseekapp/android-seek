package stejasvin.seekgds;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SearchListActivity extends ActionBarActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 1;
    private Button bPlay;
    private Button bPause;
    public TextView songName,startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    public static int oneTimeOnly = 0;
    EditText etSearch;
    ArrayList<SearchResult> searchArray;
    SearchListAdapter searchListAdapter;
    TextView fileName;

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
        setContentView(R.layout.activity_search_list_new);
        String filePath = Environment.getExternalStorageDirectory()+"/SeekLib/Pinocchio.mp3";
        mediaPlayer = MediaPlayer.create(this,Uri.fromFile(new File(filePath)));
        fileName = (TextView)findViewById(R.id.tv_filename_search);
        startTimeField =(TextView)findViewById(R.id.tv_start_search);
        endTimeField =(TextView)findViewById(R.id.tv_end_search);
        seekbar = (SeekBar)findViewById(R.id.sb_search);
        bPlay = (Button)findViewById(R.id.b_play_search);
        bPause = (Button)findViewById(R.id.b_pause_search);
        seekbar.setClickable(false);
        bPause.setEnabled(false);

        etSearch = (EditText)findViewById(R.id.et_main);
        if(getIntent().getStringExtra("searchString")!=null)
            etSearch.setText(getIntent().getStringExtra("searchString"));
        Button bGen = (Button)findViewById(R.id.b_gen_main);

        searchArray = getIntent().getParcelableArrayListExtra("searchList");
        if(searchArray == null || searchArray.size()==0) {
            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_LONG).show();
            return;
        }

        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {
                    Toast.makeText(SearchListActivity.this, etSearch.getText().toString() + "\"Seeking\"...", Toast.LENGTH_LONG).show();

                    ArrayList<SearchResult> totList = searchThruFiles(etSearch.getText().toString());
                    if(totList!=null && totList.size()>0){
                        searchArray.clear();
                        searchArray.addAll(totList);
                        if(searchListAdapter!=null)
                            searchListAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(SearchListActivity.this, etSearch.getText().toString() + "Not Found", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(SearchListActivity.this,"Enter valid stuff",Toast.LENGTH_LONG).show();
            }
        });

//        Button bLib = (Button)findViewById(R.id.b_lib_main);
//        bLib.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SearchListActivity.this,ListActivity.class);
//                startActivity(intent);
//            }
//        });

        Button bMic = (Button) findViewById(R.id.b_mic_main);
        bMic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        Button bClose = (Button) findViewById(R.id.b_close_main);
        bClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

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
            seekbar.setProgress((int)(startTime/finalTime));
            myHandler.postDelayed(UpdateSongTime,100);
            bPlay.setEnabled(false);
            bPause.setEnabled(false);
            bPause.setVisibility(View.GONE);
            bPlay.setVisibility(View.VISIBLE);
            //mediaPlayer.seekTo(searchResult.seekTime);
        } catch (IOException e) {
            Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    playAudio(Constants.LIB_PATH + "/Oh Penne.mp3");
                bPlay.setVisibility(View.GONE);
                bPause.setVisibility(View.VISIBLE);
                bPause.setEnabled(true);
                bPlay.setEnabled(false);
                }
            //}
        });

        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    bPause.setEnabled(false);
                    bPlay.setEnabled(true);
                    bPlay.setVisibility(View.VISIBLE);
                    bPause.setVisibility(View.GONE);

                }
            }
        });


        //ArrayList<SearchResult> stringList = Arrays.asList(searchArray);
        //TODO Make this list hold checkbox also, maybe use sharedprefs

        ListView list = (ListView)findViewById(R.id.list_search);
        searchListAdapter =new SearchListAdapter(this,searchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,bPause,bPlay,fileName);
        list.setAdapter(searchListAdapter);
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

    private ArrayList<SearchResult> searchThruFiles(String s) {
        ArrayList<SearchResult> totSearchList = new ArrayList<SearchResult>();
        File libDir = new File(Constants.LIB_PATH);
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(s.endsWith(".txt"))
                    return true;
                return false;
            }
        };
        //Filtering only text files
        File[] listFiles = libDir.listFiles(filenameFilter);
        if(listFiles == null)
            return totSearchList;
        if (listFiles.length > 0) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isFile()) {
                    totSearchList.addAll(findWord(s,listFiles[i]));
                }
            }
        }
        return totSearchList;
    }


    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etSearch.setText(result.get(0));
                }
                break;
            }

        }
    }


    public ArrayList<SearchResult> findWord(String word, File file){
        ArrayList<SearchResult> searchList=new ArrayList<SearchResult>();
        try{

            Scanner read = new Scanner(file);
            read.useDelimiter("<");
            String line,temp,lastSeek;
            while(read.hasNext())
            {
                line=read.next();
                temp="<".concat(line);
                Log.i("SeekJava", temp);
                lastSeek = temp.substring(temp.indexOf("<"),temp.indexOf(">")).replace("<","").replace(">","");

                if(line.contains(word)) {
                    SearchResult searchResult = new SearchResult();
                    searchResult.setFileName(file.getName());
                    searchResult.setFilePath(file.getPath());
                    int milliTime = Integer.decode(lastSeek);
                    int min = milliTime/60000;
                    int sec = milliTime/1000;
                    searchResult.setSeekTime(milliTime);
                    searchResult.setSeekString(min+":"+sec);
                    searchResult.setSubtitle(temp.split(">")[1]);
                    searchList.add(searchResult);
                }

            }
            read.close();


        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return searchList;
    }

}
