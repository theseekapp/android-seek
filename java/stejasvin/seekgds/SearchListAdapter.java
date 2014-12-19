


package stejasvin.seekgds;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a customized array adapter used to display the List of
 * messages and their details in a list.
 *
 * @author stejasvin
 * @since v1.0
 */

public class SearchListAdapter extends ArrayAdapter {

    List<SearchResult> searchList;
    int textViewResourceId = R.layout.single_list_item_string_search;

    /**
     * Context
     */
    private Context context;
    MediaPlayer mp;
    SeekBar seekBar;
    Handler handler;
    Runnable runnable;
    Button bPause,bPlay;
    TextView fileName;
    String searchString;

    public SearchListAdapter(Context context, ArrayList<SearchResult> searchList, MediaPlayer mediaPlayer,
                             SeekBar seekbar,Handler seekHandler,Runnable runnable,Button bPause, Button bPlay,
                             TextView fileName,String searchString) {
        super(context, R.layout.single_list_item_string_search, searchList);
        this.context = context;
        this.searchList = searchList;
        this.mp = mediaPlayer;
        this.seekBar = seekbar;
        this.handler = seekHandler;
        this.runnable = runnable;
        this.bPlay = bPlay;
        this.bPause = bPause;
        this.fileName = fileName;
        this.searchString = searchString;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        //View row = convertView;
        View row=null;
        //if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(textViewResourceId, parent, false); // inflate view from xml file
        //}

        final SearchResult searchResult = searchList.get(position);

        TextView tvName = (TextView) row.findViewById(R.id.tv_filename_search_sli);
        tvName.setText(searchResult.getFileName().replace(".txt", ".mp3") + " : " + searchResult.getSeekString());

        final TextView tvSubs = (TextView) row.findViewById(R.id.tv_search_sli);
        String processedString = Utilities.processSubtitle(searchResult.getSubtitle(),searchString);
        tvSubs.setText(processedString);

        final TextView tvStatus = (TextView) row.findViewById(R.id.tv_status_sli);
        if(searchResult.filePath.contains("https://"))
            tvStatus.setText("online");
        else
            tvStatus.setText("local");

        final Button bSniPlay = (Button) row.findViewById(R.id.b_play_sli);
        bSniPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //online youtube link
                if (searchResult.filePath.contains("https://")) {
                    if (mp != null && mp.isPlaying()) {
                        mp.pause();
                        bPause.setEnabled(false);
                        bPlay.setEnabled(true);
                        bPlay.setVisibility(View.VISIBLE);
                        bPause.setVisibility(View.GONE);
                    }

                    int mintot = 0, hrs = 0, hrs0 = 0, hrs1 = 0, min0 = 0, min1 = 0, sec0 = 0, sec1 = 0, milliTime = 0, sec = 0, min = 0;
                    String seekTime = searchResult.getSeekString();
                    String times[] = seekTime.split(":");
                    boolean flag = false;
                    try {

                        hrs0 = Integer.decode(times[0].charAt(0) + "");
                        hrs1 = Integer.decode(times[0].charAt(1) + "");
                        min0 = Integer.decode(times[1].charAt(0) + "");
                        min1 = Integer.decode(times[1].charAt(1) + "");
                        sec0 = Integer.decode(times[2].charAt(0) + "");
                        sec1 = Integer.decode(times[2].charAt(1) + "");
                        hrs = 10 * hrs0 + hrs1;
                        min = 10 * min0 + min1;
                        mintot = hrs * 60 + min;
                        sec = sec0 * 10 + sec1;
                        flag = true;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    milliTime = (sec0 * 10 + sec1) + 60 * (60 * (10 * hrs0 + hrs1) + (10 * min0 + min1));
                    final Intent intent;
                    if (flag)
                        if (hrs1 != 0 || hrs0 != 0)
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult.filePath + "#t=" + mintot + "m" + times[2] + "s"));
                        else
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult.filePath + "#t=" + times[1] + "m" + times[2] + "s"));
                    else
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult.filePath));

                    context.startActivity(intent);
                }
                //local repo link
                else {

                    String path = Constants.ROOT_LOCAL_PATH + searchResult.getFileName().replace(".txt", ".mp3");
                    File file = new File(path);
                    Uri uri = Uri.fromFile(file);
                    try {

                        mp.reset();
                        mp.setDataSource(context, uri);
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                                mediaPlayer.seekTo(searchResult.seekTime);
                                //seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                                handler.postDelayed(runnable, 100);
                                bPlay.setEnabled(false);
                                bPause.setEnabled(true);
                                bPlay.setVisibility(View.GONE);
                                bPause.setVisibility(View.VISIBLE);
                            }
                        });
                        mp.prepareAsync();
                        fileName.setText(searchResult.getFileName().replace(".txt", ".mp3"));
                        //mp.start();
                        //mp.seekTo(searchResult.seekTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        return row;
    }

}