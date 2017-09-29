package trainedge.crawlmine.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by jalaj on 27-08-2017.
 */

public class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    // storage location for camera files
    private static final String CAMERA_DIR = "/crawlmine/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
