package trainedge.crawlmine.utils;

import android.os.Environment;

import java.io.File;

import trainedge.crawlmine.activity.BaseActivity;

/**
 * Created by jalaj on 27-08-2017.
 */

public class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
