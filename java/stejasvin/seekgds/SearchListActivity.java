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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    //private SeekBar seekbar1;
    public static int oneTimeOnly = 0;
    private EditText etSearch;
    private ArrayList<SearchResult> searchArray = new ArrayList<SearchResult>();
    private ArrayList<SearchResult> onlineSearchArray = new ArrayList<SearchResult>();
    private SearchListAdapter searchListAdapter;
    private SearchListAdapter searchOnlineListAdapter;
    private ListView onlineListView;
    private ListView listView;
    private TextView fileName;
    private TextView listEmpty;
    private BroadcastReceiver uploadReceiver;
    private LinearLayout llOnline;
    private LinearLayout llPlayer;
    private ProgressBar pbOnline;

    private int globalMode = Constants.RB_ALL;

    Button rbOnline;
    Button rbLocal;
    Button rbAll;
    RadioGroup rgMode;


    public static class manageMedia{

        public static Button bMainPlay;
        public static int currPlay;
        public static List<Button> bPlayList;
        public static boolean flagFirst=true; //for initialzing media

        public static void init(Button main,List<Button> list){
            bMainPlay = main;
            bPlayList = list;
            flagFirst=true;
            currPlay=0;
        }

        public static void play(){

        }

    }

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
                    //onlineListView = (ListView)findViewById(R.id.online_list_search);
                    //searchOnlineListAdapter = new SearchListAdapter(SearchListActivity.this,onlineSearchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,bPause,bPlay,fileName);
                    //onlineListView.setAdapter(searchOnlineListAdapter);

                    //testing
                    //SearchResult searchResult = new SearchResult();
                    //searchResult.setFileName("DUmmy");
                    searchArray.addAll(onlineSearchArray);
                    searchListAdapter.notifyDataSetChanged();
                    pbOnline.setVisibility(View.GONE);

                    if(searchArray.size()>0)
                        listEmpty.setVisibility(View.GONE);
                    else
                        listEmpty.setVisibility(View.VISIBLE);
//                    if(onlineSearchArray.size()==0) {
//                        onlineListView.setVisibility(View.GONE);
//                        llOnline.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        onlineListView.setVisibility(View.VISIBLE);
//                        llOnline.setVisibility(View.GONE);
//                    }

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
        String filePath;

        mediaPlayer = new MediaPlayer();

        fileName = (TextView)findViewById(R.id.tv_filename_search);
        listEmpty = (TextView)findViewById(R.id.tv_listempty_search);
        startTimeField =(TextView)findViewById(R.id.tv_start_search);
        endTimeField =(TextView)findViewById(R.id.tv_end_search);
        seekbar = (SeekBar)findViewById(R.id.sb_search);
        bPlay = (Button)findViewById(R.id.b_play_search);
        bPause = (Button)findViewById(R.id.b_pause_search);
        onlineListView = (ListView)findViewById(R.id.online_list_search);
        llOnline =(LinearLayout)findViewById(R.id.ll_online_search);
        llPlayer =(LinearLayout)findViewById(R.id.player_layout_main);
        llPlayer.setVisibility(View.GONE);
        pbOnline = (ProgressBar)findViewById(R.id.pb_online_search);
        etSearch = (EditText)findViewById(R.id.et_main);

        String sSearch = getIntent().getStringExtra("searchString");
        if(sSearch!=null)
            etSearch.setText(sSearch);

        rbAll=(Button)findViewById(R.id.rb_all_search);
        //rbAll.setChecked(true);
        rbLocal=(Button)findViewById(R.id.rb_local_search);
        rbOnline=(Button)findViewById(R.id.rb_online_search);

        rbAll.setBackgroundResource(R.drawable.all_active);
        rbLocal.setBackgroundResource(R.drawable.local);
        rbOnline.setBackgroundResource(R.drawable.online);
        globalMode=Constants.RB_ALL;


        rbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(!rbAll.is()) {
                rbAll.setBackgroundResource(R.drawable.all_active);
                rbLocal.setBackgroundResource(R.drawable.local);
                rbOnline.setBackgroundResource(R.drawable.online);
                globalMode=Constants.RB_ALL;
                startPopulatingList();
            }
        });

        rbLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbAll.setBackgroundResource(R.drawable.all);
                rbLocal.setBackgroundResource(R.drawable.local_active);
                rbOnline.setBackgroundResource(R.drawable.online);
                globalMode=Constants.RB_LOCAL;
                startPopulatingList();

            }
        });

        rbOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbAll.setBackgroundResource(R.drawable.all);
                rbLocal.setBackgroundResource(R.drawable.local);
                rbOnline.setBackgroundResource(R.drawable.online_active);
                globalMode=Constants.RB_ONLINE;
                startPopulatingList();
            }
        });

        seekbar.setClickable(false);
        bPause.setEnabled(false);
        //onlineListView.setVisibility(View.GONE);
        //llOnline.setVisibility(View.VISIBLE);



        startPopulatingList();
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
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                startPopulatingList();

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
                Utilities.promptSpeechInput(SearchListActivity.this);
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

//        //First run
//        //try {
//            //if(mediaPlayer.getDuration()==0)
//            //    mediaPlayer.setDataSource(filePath);
//            //So that audio doesnt play
//            //mediaPlayer.start();
//            finalTime = 0;//mediaPlayer.getDuration();
//            startTime = 0;//mediaPlayer.getCurrentPosition();
//            //if(oneTimeOnly == 0){
//            seekbar.setMax((int) finalTime);
//            //    oneTimeOnly = 1;
//            //}
//            endTimeField.setText("0 min, 0 sec");
////            endTimeField.setText(String.format("%d min, %d sec",
////                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
////                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
////                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
////                                            toMinutes((long) finalTime)))
////            );
////            startTimeField.setText(String.format("%d min, %d sec",
////                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
////                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
////                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
////                                            toMinutes((long) startTime)))
////            );
//            //seekbar.setProgress((int)(startTime/finalTime*100));
//            myHandler.postDelayed(UpdateSongTime,100);
//            bPlay.setEnabled(false);
//            bPause.setEnabled(false);
//            bPause.setVisibility(View.GONE);
//            bPlay.setVisibility(View.VISIBLE);
//            //mediaPlayer.seekTo(searchResult.seekTime);
//        //}
////        catch (IOException e) {
////            Toast.makeText(SearchListActivity.this, "Error in playing.. " + e.getMessage(), Toast.LENGTH_SHORT).show();
////            e.printStackTrace();
////        }
    }


    public void setListViews(){
//        //searchArray = getIntent().getParcelableArrayListExtra("searchList");
//        if(searchArray == null || searchArray.size()==0) {
//            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_SHORT).show();
//            searchListAdapter.clear();
//            return;
//        }
        ListView list = (ListView)findViewById(R.id.list_search);
        searchListAdapter =new SearchListAdapter(this,searchArray,mediaPlayer,seekbar,myHandler,UpdateSongTime,
                bPause,bPlay,fileName,etSearch.getText().toString(),llPlayer);
        list.setAdapter(searchListAdapter);
        //onlineListView.setVisibility(View.GONE);
        //llOnline.setVisibility(View.VISIBLE);


    }

    public void playAudio(String filePath) throws IllegalStateException{//,MediaPlayer mediaPlayer,int finalTime,int startTime,int oneTimeOnly,SeekBar seekbar,){
        try {
            if(mediaPlayer.getDuration()==0)
                mediaPlayer.setDataSource(filePath);
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            //if(oneTimeOnly == 0){
            seekbar.setMax((int) finalTime);
            //    oneTimeOnly = 1;
            //}

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
            //seekbar.setProgress((int)(startTime/finalTime));
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
            finalTime = mediaPlayer.getDuration();
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            endTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) finalTime)))
            );
            //int progressTime = (int)(startTime/finalTime*100);
            seekbar.setProgress(mediaPlayer.getCurrentPosition());
            seekbar.setMax(mediaPlayer.getDuration());
            myHandler.postDelayed(this, 100);
        }
    };


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

    public void callDownloadService(){
        pbOnline.setVisibility(View.VISIBLE);
        Intent serviceIntent = new Intent(SearchListActivity.this,SeekDataDownloadService.class);
        serviceIntent.putExtra("searchString",etSearch.getText().toString());
//        serviceIntent.putExtra("mode",mode);
        startService(serviceIntent);

    }

    public void startPopulatingList(){
        int mode = globalMode;
        if(!etSearch.getText().toString().equals("")) {
            listEmpty.setVisibility(View.GONE);
            ArrayList<SearchResult> totList = Utilities.searchThruFiles(etSearch.getText().toString());

            boolean goFlag = true;
            boolean onlineFlag = false;
            if(searchListAdapter!=null)
                searchListAdapter.clear();

            if(mode==Constants.RB_ONLINE) {
                totList.clear();
                callDownloadService();
                onlineFlag = true;
                goFlag = false;
            }
            else if(mode==Constants.RB_LOCAL) {
                //continue
            }
            else{
                callDownloadService();
                onlineFlag = true;
            }

            if(goFlag && totList!=null && totList.size()>0){

                searchArray.clear();
                searchArray.addAll(totList);
                if(searchListAdapter!=null)
                    searchListAdapter.notifyDataSetChanged();
                listEmpty.setVisibility(View.GONE);

            }else if(goFlag && !onlineFlag){ //local list is empty and no online search happening
                listEmpty.setVisibility(View.VISIBLE);
            }

        }
        //else
        //    Toast.makeText(SearchListActivity.this,"Enter valid stuff",Toast.LENGTH_SHORT).show();
    }

}
