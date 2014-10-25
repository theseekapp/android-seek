package stejasvin.seekgds;

/**
 * Created by stejasvin on 10/26/2014.
 */
public class SearchResult {

    String fileName, filePath, subtitle, seekString;
    int seekTime;

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
