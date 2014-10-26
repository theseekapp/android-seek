


package stejasvin.seekgds;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    MediaPlayer mp = new MediaPlayer();

    public SearchListAdapter(Context context, ArrayList<SearchResult> searchList) {
        super(context, R.layout.single_list_item_string_search, searchList);
        this.context = context;
        this.searchList = searchList;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(textViewResourceId, parent, false); // inflate view from xml file
        }

        final SearchResult searchResult = searchList.get(position);

        TextView tvName = (TextView) row.findViewById(R.id.tv_filename_search_sli);
        tvName.setText(searchResult.getFileName() + " : " + searchResult.getSeekString());

        TextView tvSubs = (TextView) row.findViewById(R.id.tv_search_sli);
        tvSubs.setText(searchResult.getSubtitle());

        Button bPlay = (Button) row.findViewById(R.id.b_play_sli);
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp.isPlaying())
                    mp.stop();
                else {
                    try {
                        mp.setDataSource(Constants.LIB_PATH + "/Oh Penne.mp3");
                        mp.start();
                        mp.seekTo(searchResult.seekTime);
                    } catch (IOException e) {
                        Toast.makeText(context, "Error in playing.. " + searchResult.getSubtitle(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        return row;
    }

    /*@Override
    public int getCount() {
        return stringList.size()+1;
    }*/
}