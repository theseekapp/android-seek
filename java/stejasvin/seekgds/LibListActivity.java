package stejasvin.seekgds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class LibListActivity extends ActionBarActivity {


    private static final int REQ_CODE_SPEECH_INPUT = 1;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        etSearch = (EditText)findViewById(R.id.et_lib);

        String searchTemp = getIntent().getStringExtra("searchString");
        if(searchTemp!=null)
            etSearch.setText(searchTemp);

        Button bGen = (Button)findViewById(R.id.b_gen_main);
        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {
                    Intent intent = new Intent(LibListActivity.this,SearchListActivity.class);
                    intent.putExtra("searchString",etSearch.getText().toString());
                    startActivity(intent);
                }
                else
                    Toast.makeText(LibListActivity.this,"Enter valid stuff",Toast.LENGTH_SHORT).show();
            }
        });


        Button bMic = (Button) findViewById(R.id.b_mic_main);
        bMic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.promptSpeechInput(LibListActivity.this);
            }
        });

        Button bClose = (Button) findViewById(R.id.b_close_main);
        bClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });


        List<String> stringFileList = new ArrayList<String>();

        //If this pathname does not denote a directory, then listFiles() returns null.

        stringFileList = Utilities.getAllMp3Files();
        //stringList.add("Pinocchio.mp3");
        //stringList.add("WarAndPeace.mp3");
        //stringList.add("OliverTwist.mp3");

        //TODO Make this list hold checkbox also, maybe use sharedprefs

        ListView list = (ListView)findViewById(R.id.list_list);
        list.setAdapter(new LibListAdapter(this,stringFileList,MainActivity.cbMap));
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



}
