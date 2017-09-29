package trainedge.crawlmine.model;

/**
 * Created by Jalaj on 9/16/2017.
 */

public class ScanModel {

    public String getScantext() {
        return scantext;

    }



    public void setScantext(String scantext) {
        this.scantext = scantext;
    }

    public ScanModel() {

    }

    public ScanModel(String scantext) {

        this.scantext = scantext;
    }

    String scantext;

}
