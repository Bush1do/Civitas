package com.here.name.website.poom.Utils;

import android.os.Environment;

/**
 * Created by Charles on 12/17/2017.
 */

public class FilesPaths {

    //"/storage/emulated/0"
    public String ROOT_DIR= Environment.getExternalStorageDirectory().getPath();

    public String PICTURES= ROOT_DIR+"/Pictures";
    public String CAMERA= ROOT_DIR+"/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE= "photos/users/";
}
