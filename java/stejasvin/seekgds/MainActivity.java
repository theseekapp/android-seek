package stejasvin.seekgds;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;


public class MainActivity extends ActionBarActivity {

    public final int REQ_CODE_SPEECH_INPUT = 0;
    public static HashMap<String,String> cbMap = new HashMap<String, String>();

    EditText etSearch;  //Search string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing
        cbMap.put(Constants.files[0],"1");
        cbMap.put(Constants.files[1],"1");
        cbMap.put(Constants.files[2],"1");

        File dir = new File(Constants.LIB_PATH);
        dir.mkdirs();

        etSearch = (EditText)findViewById(R.id.et_main);
        Button bGen = (Button)findViewById(R.id.b_gen_main);
        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this,  "\"Seeking\"...", Toast.LENGTH_SHORT).show();

                    ArrayList<SearchResult> totList = searchThruFiles(etSearch.getText().toString());
                    if(totList!=null && totList.size()>0){
                        String[] tempList = new String[100];
                        Intent intent = new Intent(MainActivity.this,SearchListActivity.class);
                        //totList.toArray(tempList);
                        intent.putExtra("searchString",etSearch.getText().toString());
                        intent.putParcelableArrayListExtra("searchList",totList);
                        startActivity(intent);

                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    }else{
                        Toast.makeText(MainActivity.this, "Result Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"Enter valid stuff",Toast.LENGTH_SHORT).show();
            }
        });

        Button bLib = (Button)findViewById(R.id.b_lib_main);
        bLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.putExtra("searchString",etSearch.getText().toString());
                startActivity(intent);
            }
        });

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
    }

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
                if (listFiles[i].isFile()&& MainActivity.cbMap.get(listFiles[i].getName().substring(0,listFiles[i].getName().indexOf("."))).equals("1")) {
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
