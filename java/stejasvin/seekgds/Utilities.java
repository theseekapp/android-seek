package stejasvin.seekgds;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by stejasvin on 11/15/2014.
 */
public class Utilities {
    public static final String DOWN_DATA = "seek.searchdata";

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

}
