package org.teenguard.child.utils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris on 26/10/16.
 * http://stackoverflow.com/questions/15662258/how-to-save-a-bitmap-on-internal-storage
 */

public class ImageUtils {

    public static Uri getImageUriFromPhoneId(int phoneId) {
        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Integer.toString(phoneId));
        return uri;
    }


    public static Bitmap getBitmapFromDataPath(String dataPath) {
        System.out.println("getBitmapFromDataPath :loading bitmap " + dataPath);
        return BitmapFactory.decodeFile(dataPath);
    }

    public static Bitmap getImageBitmapFromURI(Uri uri) {
        Bitmap bitmap = null;
        try {
            //bitmap = MediaStore.Images.Media.getBitmap(MyApp.getContext().getContentResolver(),uri);
            Bitmap bm = BitmapFactory.decodeStream(MyApp.getContext().getContentResolver().openInputStream(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<< bitmap uri = " + uri);
        //System.out.println("bitmap.getAllocationByteCount() = " + bitmap.getAllocationByteCount());
        System.out.println("bitmap.getHeight() = " + bitmap.getHeight());
        System.out.println("bitmap.getWidth() = " + bitmap.getWidth());
        return bitmap;
    }

    // TODO: 25/10/16  return bitmap.compress();
  /*public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/
   public static Uri getUriFromPhoneId(int phoneId) {
        final Resources resources = MyApp.getContext().getResources();
        final Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(phoneId))
                .appendPath(resources.getResourceTypeName(phoneId))
                .appendPath(resources.getResourceEntryName(phoneId))
                .build();
       return uri;
    }

    public static Bitmap compress(Bitmap inImage, int quality) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         inImage.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        return inImage;
    }

    public static Bitmap resize(Bitmap b,int reqWidth, int reqHeight) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
    }

    public static void dump(Bitmap bitmap) {
        System.out.println("bitmap size" + bitmap.getByteCount());
        System.out.println("bitmap Height = " + bitmap.getHeight());
        System.out.println("bitmap getWidth = " + bitmap.getWidth());
    }

    public static File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            System.out.println("Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(" Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }

    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + MyApp.getContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        System.out.println("SAVED mediaFile.getAbsolutePath() = " + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    /**
     *
     * @param mBitmap
     * @param scaleSize max Height or width to Scale
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap mBitmap,int scaleSize) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float excessSizeRatio = width > height ? width / scaleSize : height / scaleSize;
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0,(int) (width/excessSizeRatio),(int) (height/excessSizeRatio));
        //mBitmap.recycle();// if you are not using mBitmap Obj
        return bitmap;
    }


    public static Uri uriFromFilename(String displayFileName) {
        String completePath = Environment.getExternalStorageDirectory() + "/" + displayFileName;
        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

}
