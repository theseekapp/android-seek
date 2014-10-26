package stejasvin.seekgds;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by stejasvin on 10/26/2014.
 */
public class SearchResult implements Parcelable{

    String fileName, filePath, subtitle, seekString;
    int seekTime;

    public SearchResult(){}

    public SearchResult(Parcel in) {
        Log.d("ParcelSearchResult", "parcel in");
        fileName = in.readString();
        filePath = in.readString();
        subtitle = in.readString();
        seekString = in.readString();
        seekTime = in.readInt();

    }

    public static final Parcelable.Creator<SearchResult> CREATOR
            = new Parcelable.Creator<SearchResult>() {
        public SearchResult createFromParcel(Parcel in) {
            Log.d("ParcelSearchResult", "createFromParcel()");
            return new SearchResult(in);
        }

        public SearchResult[] newArray(int size) {
            Log.d("ParcelSearchResult", "createFromParcel() newArray ");
            return new SearchResult[size];
        }


    };

       @Override
    public int describeContents() {
        //Log.d (TAG, "describe()");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Log.d (TAG, "writeToParcel");
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeString(subtitle);
        dest.writeString(seekString);
        dest.writeInt(seekTime);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSeekString() {
        return seekString;
    }

    public void setSeekString(String seekString) {
        this.seekString = seekString;
    }

    public int getSeekTime() {
        return seekTime;
    }

    public void setSeekTime(int seekTime) {
        this.seekTime = seekTime;
    }
}
