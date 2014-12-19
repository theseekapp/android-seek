package stejasvin.seekgds;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by stejasvin on 11/15/2014.
 */
public class Utilities {
    public static final String DOWN_DATA = "seek.searchdata";
    public static final int REQ_CODE_SPEECH_INPUT=1;

    public static String IsParse(InputStream is) {
        String json = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");

            is.close();

            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // return JSON String
        return json;
    }

    public static ArrayList<SearchResult> findWord(String word, File file){
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

    //TODO hardcoded for mp3 files.. extend it
    public static  ArrayList<SearchResult> searchThruFiles(String s) {
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
        if(listFiles == null )
            return totSearchList;
        if (listFiles.length > 0) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isFile() && MainActivity.cbMap.get(listFiles[i].getName().replace(".txt","")).equals("1")) {
                    totSearchList.addAll(findWord(s,listFiles[i]));
                }
            }
        }
        return totSearchList;
    }

    /**
     * Showing google speech input dialog
     * */
    public static void promptSpeechInput(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                activity.getString(R.string.speech_prompt));
        try {
            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static List<String> getAllMp3Files() {
        //TODO Only mp3 files supported for now.. extend this
        List<String> stringFileList = new ArrayList<String>();

        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(s.contains("mp3"))
                    return true;
                else
                    return false;

            }
        };

        File[] files = new File(Environment.getExternalStorageDirectory() + "/SeekLib/").listFiles(fileFilter);
        for (File file : files) {
            if (file.isFile()) {
                stringFileList.add(file.getName());
            }
        }
        return stringFileList;
    }

    public static String processSubtitle(String subs,String search){
        int index = subs.indexOf(search,0);
        String processed1,processed2;
        if(index>30) {
            //processed = subs.substring(index - 25);
            processed1 = subs.substring(subs.indexOf(" ",15));//start of string
        }
        else
            processed1=subs;

        if(processed1.length()-index>30) {
            int iEnd = processed1.indexOf(" ",index+15);
            if(iEnd!=-1)
                processed2 = processed1.substring(0,iEnd);//end of string
            else
                processed2 = processed1;
        }
        else
            processed2=processed1;

        return processed2;
    }
}
