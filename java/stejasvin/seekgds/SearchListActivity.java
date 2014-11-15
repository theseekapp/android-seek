package stejasvin.seekgds;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private EditText etSearch;
    private ArrayList<SearchResult> searchArray;
    private ArrayList<SearchResult> onlineSearchArray;
    private SearchListAdapter searchListAdapter;
    private SearchListAdapter searchOnlineListAdapter;
    private ListView onlineListView;
    private ListView listView;
    private TextView fileName;
    private BroadcastReceiver uploadReceiver;
    private LinearLayout llOnline;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        uploadReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra("success",false);
                if(success) {
                    onlineSearchArray = intent.getParcelableArrayListExtra("onlineSearchList");
                    onlineListView = (ListView)findViewById(R.id.online_list_search);
                    searchOnlineListAdapter = new SearchListAdapter(SearchListActivity.this,onlineSearchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,bPause,bPlay,fileName);
                    onlineListView.setAdapter(searchOnlineListAdapter);

                    if(onlineSearchArray.size()==0) {
                        onlineListView.setVisibility(View.GONE);
                        llOnline.setVisibility(View.VISIBLE);
                    }
                    else {
                        onlineListView.setVisibility(View.VISIBLE);
                        llOnline.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(SearchListActivity.this,"Offline",Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(uploadReceiver, new IntentFilter(Utilities.DOWN_DATA));
    }

    @Override
    protected void onStop() {
        super.onStop();
        myHandler.removeCallbacks(UpdateSongTime);
        unregisterReceiver(uploadReceiver);
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
        onlineListView = (ListView)findViewById(R.id.online_list_search);
        llOnline =(LinearLayout)findViewById(R.id.ll_online_search);

        seekbar.setClickable(false);
        bPause.setEnabled(false);
        onlineListView.setVisibility(View.GONE);
        llOnline.setVisibility(View.VISIBLE);

        etSearch = (EditText)findViewById(R.id.et_main);
        if(getIntent().getStringExtra("searchString")!=null)
            etSearch.setText(getIntent().getStringExtra("searchString"));

        setListViews();
//        searchArray = getIntent().getParcelableArrayListExtra("searchList");
//        if(searchArray == null || searchArray.size()==0) {
//            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //ArrayList<SearchResult> stringList = Arrays.asList(searchArray);
        //ListView list = (ListView)findViewById(R.id.list_search);
//        searchListAdapter =new SearchListAdapter(this,searchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,bPause,bPlay,fileName);
//        list.setAdapter(searchListAdapter);
//
        Button bGen = (Button)findViewById(R.id.b_gen_main);
        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {

                    Toast.makeText(SearchListActivity.this, "\"Seeking\"...", Toast.LENGTH_SHORT).show();

                    //Online service started
                    Intent serviceIntent = new Intent(SearchListActivity.this,SeekDataDownloadService.class);
                    serviceIntent.putExtra("searchString",etSearch.getText().toString());
                    startService(serviceIntent);

                    onlineListView.setVisibility(View.GONE);
                    llOnline.setVisibility(View.VISIBLE);

                    ArrayList<SearchResult> totList = searchThruFiles(etSearch.getText().toString());

                    if(totList!=null && totList.size()>0){
                        searchArray.clear();
                        searchArray.addAll(totList);
                        if(searchListAdapter!=null)
                            searchListAdapter.notifyDataSetChanged();

                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    }else{
                        Toast.makeText(SearchListActivity.this, "Results Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(SearchListActivity.this,"Enter valid stuff",Toast.LENGTH_SHORT).show();
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


    }

    public void setListViews(){
        searchArray = getIntent().getParcelableArrayListExtra("searchList");
        if(searchArray == null || searchArray.size()==0) {
            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            return;
        }

        ListView list = (ListView)findViewById(R.id.list_search);
        searchListAdapter =new SearchListAdapter(this,searchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,bPause,bPlay,fileName);
        list.setAdapter(searchListAdapter);

        onlineListView.setVisibility(View.GONE);
        llOnline.setVisibility(View.VISIBLE);


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
            seekbar.setProgress((int)(startTime/finalTime));
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
            seekbar.setProgress((int)(startTime/finalTime));
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
                if (listFiles[i].isFile() && MainActivity.cbMap.get(listFiles[i].getName().substring(0,listFiles[i].getName().indexOf("."))).equals("1")) {
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
