package stejasvin.seekgds;

import android.os.Environment;

/**
 * Created by stejasvin on 10/25/2014.
 */
public class Constants {

    public static final String LIB_PATH = Environment.getExternalStorageDirectory()+"/SeekLib";
    //public static final String[] files = {"Pinocchio","WarAndPeace","OliverTwist"};
    public static final String ROOT_URL = "http://www.shaastra.org/personal/varshaa/";
    public static final String ROOT_LOCAL_PATH = Environment.getExternalStorageDirectory() + "/SeekLib/";

    //Json constants
    public static final String JSON_NAME = "file";
    public static final String JSON_URL = "link";
    public static final String JSON_MILLI = "milli";
    public static final String JSON_SEEK = "prev_line";
    public static final String JSON_TEXT = "line";


}
