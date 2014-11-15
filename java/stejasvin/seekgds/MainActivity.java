package stejasvin.seekgds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public final int REQ_CODE_SPEECH_INPUT = 0;
    public static HashMap<String,String> cbMap = new HashMap<String, String>();

    EditText etSearch;  //Search string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating lib
        File file = new File(Constants.ROOT_LOCAL_PATH);
        file.mkdirs();

        //initializing

//        cbMap.put(Constants.files[0],"1");
//        cbMap.put(Constants.files[1],"1");
//        cbMap.put(Constants.files[2],"1");
        initCbMap();

        File dir = new File(Constants.LIB_PATH);
        dir.mkdirs();

        etSearch = (EditText)findViewById(R.id.et_main);
        Button bGen = (Button)findViewById(R.id.b_gen_main);
        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {
                    Intent intent = new Intent(MainActivity.this,SearchListActivity.class);
                    intent.putExtra("searchString",etSearch.getText().toString());
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivity.this,"Enter valid stuff",Toast.LENGTH_SHORT).show();
            }
        });

        Button bLib = (Button)findViewById(R.id.b_lib_main);
        bLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LibListActivity.class);
                intent.putExtra("searchString",etSearch.getText().toString());
                startActivity(intent);
            }
        });

        Button bMic = (Button) findViewById(R.id.b_mic_main);
        bMic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utilities.promptSpeechInput(MainActivity.this);
            }
        });

        Button bClose = (Button) findViewById(R.id.b_close_main);
        bClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
    }

    void initCbMap(){
        List<String> tempList = Utilities.getAllMp3Files();
        for(int i=0;i<tempList.size();i++) {
            cbMap.put(tempList.get(i).replace(".mp3",""),"1");
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



}
