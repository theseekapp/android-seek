


package stejasvin.seekgds;

import android.content.Context;
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

    public SearchListAdapter(Context context, ArrayList<SearchResult> searchList, MediaPlayer mediaPlayer,
                             SeekBar seekbar,Handler seekHandler,Runnable runnable,Button bPause, Button bPlay) {
        super(context, R.layout.single_list_item_string_search, searchList);
        this.context = context;
        this.searchList = searchList;
        this.mp = mediaPlayer;
        this.seekBar = seekbar;
        this.handler = seekHandler;
        this.runnable = runnable;
        this.bPlay = bPlay;
        this.bPause = bPause;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(textViewResourceId, parent, false); // inflate view from xml file
        }

        final SearchResult searchResult = searchList.get(position);

        TextView tvName = (TextView) row.findViewById(R.id.tv_filename_search_sli);
        tvName.setText(searchResult.getFileName().replace(".txt", ".mp3") + " : " + searchResult.getSeekString());

        TextView tvSubs = (TextView) row.findViewById(R.id.tv_search_sli);
        tvSubs.setText(searchResult.getSubtitle());

        final Button bSniPlay = (Button) row.findViewById(R.id.b_play_sli);
        bSniPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory() + "/SeekLib/" + searchResult.getFileName().replace(".txt", ".mp3");
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
                            seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                            handler.postDelayed(runnable,100);
                            bPlay.setEnabled(false);
                            bPause.setEnabled(true);
                        }
                    });
                    mp.prepareAsync();

                    //mp.start();
                    //mp.seekTo(searchResult.seekTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }

            }
        });

        return row;
    }

}