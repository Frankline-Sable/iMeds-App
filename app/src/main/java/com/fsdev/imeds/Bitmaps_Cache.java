package com.fsdev.imeds;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Frankline Sable on 18/11/2017.
 **/
class Bitmaps_Cache {
    private Bitmap bitmap;
    private String folderName;
    private String userName;
    private Uri imageUri;
    private String uriPathString = "0";

    Bitmaps_Cache(Bitmap bitmap, String folderName, String userName) {
        this.bitmap = bitmap;
        this.folderName = folderName;
        this.userName = userName;
    }

    String createImageFile() {

        final String imageName = userName + ".jpeg";
        File imageFile = null;

        File storageDir = Environment.getExternalStorageDirectory();
        if (storageDir.exists() && storageDir.canWrite()) {

            final File capturedFolder = new File(storageDir.getAbsolutePath() + "/iMeds App/" + folderName + "/");

            if (!capturedFolder.exists()) {
                capturedFolder.mkdirs();
            }
            if (capturedFolder.exists() && capturedFolder.canWrite()) {
                FileOutputStream outputStream = null;
                try {
                    imageFile = new File(capturedFolder.getAbsolutePath() + "/" + imageName);
                    if (imageFile.createNewFile()) {
                        imageUri = Uri.fromFile(imageFile.getAbsoluteFile());
                        uriPathString = String.valueOf(imageUri);
                    } else {
                        imageUri = Uri.fromFile(imageFile.getAbsoluteFile());
                        uriPathString = String.valueOf(imageUri);
                    }
                    outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    uriPathString = "0";
                    Log.i("i-meds", e.getMessage());
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            //Swallow
                        }
                    }
                }

            } else
                uriPathString = "0";
        } else {
            uriPathString = "0";
            Log.i("i-meds", "error io exp");
        }
        return uriPathString;
    }
}
