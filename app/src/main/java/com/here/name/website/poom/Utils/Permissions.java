package com.here.name.website.poom.Utils;

import android.Manifest;

/**
 * Created by Charles on 12/16/2017.
 */

public class Permissions {

    public static final String[] PERMISSIONS= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] CAMERA_PERMISSION= {
            Manifest.permission.CAMERA
    };

    public static final String[] WRITE_STORAGE_PERMISSION= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] READ_STORAGE_PERMISSION= {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}
