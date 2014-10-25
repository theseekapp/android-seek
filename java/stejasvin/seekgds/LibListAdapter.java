


package stejasvin.seekgds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * This class is a customized array adapter used to display the List of
 * messages and their details in a list.
 *
 * @author stejasvin
 * @since v1.0
 */

public class LibListAdapter extends ArrayAdapter {

    List<String> stringList;
    int textViewResourceId = R.layout.single_list_item_string_list;

    /**
     * Context
     */
    private Context context;


    public LibListAdapter(Context context, List<String> stringList) {
        super(context, R.layout.single_list_item_string_list, stringList);
        this.context = context;
        this.stringList = stringList;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(textViewResourceId, parent, false); // inflate view from xml file
        }

        CheckBox checkbox = (CheckBox) row.findViewById(R.id.cb_lib_sli);
        TextView tv = (TextView) row.findViewById(R.id.tv_lib_sli);
        tv.setText(stringList.get(position));

        /*if(position==0) {
            tvCourse.setText("List of Strings");
            tvCourse.setTextSize(30);
            tvCourse.setPadding(5,10,5,10);

        }else {
            tvCourse.setTextSize(20);
            tvCourse.setPadding(5,10,5,10);
            tvCourse.setText(stringList.get(position-1).getName());
            tvCenter.setText(stringList.get(position-1).getCenter());
        }*/
        return row;
    }

    /*@Override
    public int getCount() {
        return stringList.size()+1;
    }*/
}