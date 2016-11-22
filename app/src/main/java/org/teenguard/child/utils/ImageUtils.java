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

    public static void dump(String label,Bitmap bitmap) {
        System.out.println("bitmap size" + bitmap.getByteCount());
        System.out.println("bitmap Height = " + bitmap.getHeight());
        System.out.println("bitmap Width = " + bitmap.getWidth());
    }

    public static File storeCompressedImage(Bitmap bitmap) {
        System.out.println("original bitmap.getByteCount() = " + bitmap.getByteCount());
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            System.out.println("Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
            System.out.println("compressed bitmap.getByteCount() = " + bitmap.getByteCount());
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
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss.SSS").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        System.out.println("SAVED mediaFile.getAbsolutePath() = " + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    /**
     *
     * @param mBitmap
     * @param maxScaleSize max size
     * @return scaled bitmap
     */

    public static Bitmap myScaleBitmap(Bitmap mBitmap,int maxScaleSize) {
        float width = mBitmap.getWidth();
        float height = mBitmap.getHeight();
        float maxScaleFactor = 0F;
        if(width > height && width >= maxScaleSize) maxScaleFactor = width / maxScaleSize;
        if(height > width && height >= maxScaleSize) maxScaleFactor = height / maxScaleSize;
        float scaledWidth =  width/maxScaleFactor;
        float scaledHeigth = height/maxScaleFactor;
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new RectF(0, 0, scaledWidth, scaledHeigth), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);
        //return bitmap;
    }




    public static Uri uriFromFilename(String displayFileName) {
        String completePath = Environment.getExternalStorageDirectory() + "/" + displayFileName;
        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

}
