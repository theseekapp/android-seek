package stejasvin.seekgds;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;



public class MainActivity extends ActionBarActivity {

    public final int REQ_CODE_SPEECH_INPUT = 0;

    EditText etSearch;  //Search string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dir = new File(Constants.LIB_PATH);
        dir.mkdirs();

        etSearch = (EditText)findViewById(R.id.et_main);
        Button bGen = (Button)findViewById(R.id.b_gen_main);
        bGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etSearch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, etSearch.getText().toString() + "\"Seeking\"...", Toast.LENGTH_LONG).show();

                    ArrayList<SearchResult> totList = searchThruFiles(etSearch.getText().toString());
                    if(totList!=null && totList.size()>0){
                        String[] tempList = new String[100];
                        Intent intent = new Intent(MainActivity.this,SearchListActivity.class);
                        //totList.toArray(tempList);
                        intent.putParcelableArrayListExtra("searchList",totList);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, etSearch.getText().toString() + "Not Found", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"Enter valid stuff",Toast.LENGTH_LONG).show();
            }
        });

        Button bLib = (Button)findViewById(R.id.b_lib_main);
        bLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public ArrayList<SearchResult> findWord(String word, File file){
        ArrayList<SearchResult> searchList=new ArrayList<SearchResult>();
        try{
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file)));
            String line;

            ArrayList<Integer> list=new ArrayList<Integer>();
            while((line=input.readLine())!=null){
                if(line.indexOf(word)>-1){
                    list.add(line.indexOf(word));
                    SearchResult searchResult = new SearchResult();
                    searchResult.setFileName(file.getName());
                    searchResult.setFilePath(file.getPath());
                    searchResult.setSeekString("1:15");
                    searchResult.setSeekTime(12000);
                    searchResult.setSubtitle(line);
                    searchList.add(searchResult);
                }
            }

            input.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return searchList;
    }
}
