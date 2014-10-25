package stejasvin.seekgds;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class SearchListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        String[] strArray = getIntent().getStringArrayExtra("searchList");
        if(strArray == null || strArray.length==0) {
            Toast.makeText(SearchListActivity.this, "No results found", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> stringList = Arrays.asList(strArray);
        //TODO Make this list hold checkbox also, maybe use sharedprefs

        ListView list = (ListView)findViewById(R.id.list_search);
        list.setAdapter(new SearchListAdapter(this,stringList));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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
}
