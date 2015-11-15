package com.kumar.sunish.mygallery;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sunish on 13/8/15.
 */
public class MediaUtils {


    public static final int IMAGE = 10;
    public static final int VIDEO = 20;
    public static final int AUDIO = 30;


    private MyHTTPD server;

    public MediaUtils(MyHTTPD server) {
        this.server = server;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public ArrayList<Map<String, String>> getGroups(int mediaType) {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();

        switch (mediaType) {
            case IMAGE: {
                return fetchImageAlbums();

            }
            case VIDEO: {
                return fetchVideoAlbums();
            }

            default: {

            }
        }

        return null;
    }

    public ArrayList<Map<String, String>> getListForGroup(int mediaType, String groupId) {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();

        switch (mediaType) {
            case IMAGE: {
                return fetchImagesInAlbum(groupId);

            }
            case VIDEO: {
                return fetchVideosInAlbum(groupId);
            }

            default: {

            }
        }

        return null;
    }

    private ArrayList<Map<String, String>> fetchVideosInAlbum(String groupId) {

        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE

        };

        // content:// style URI for the "primary" external storage volume
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Video.Media.DATE_TAKEN + " DESC";

        // Make the query.
        Cursor cur = server.getContext().getContentResolver().query(uri,
                projection, // Which columns to return
                MediaStore.Video.Media.BUCKET_ID + "=?",       // Which rows to return (all rows)
                new String[]{groupId},       // Selection arguments (none)
                orderBy         // Ordering
        );

        if (cur.moveToFirst()) {
            int idxId = cur.getColumnIndex(MediaStore.Video.Media._ID);
            int idxDate = cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
            int idxName = cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            int idxSize = cur.getColumnIndex(MediaStore.Video.Media.SIZE);



            do {
                Map<String, String> row = new HashMap<String, String>();
                row.put("ID", cur.getString(idxId));
                row.put("NAME", cur.getString(idxName));
                row.put("DATE", getDate(cur.getLong(idxDate)));
                row.put("SIZE", readableFileSize(cur.getLong(idxSize)));

                result.add(row);

            } while (cur.moveToNext());

        }


        return result;
    }

    private ArrayList<Map<String, String>> fetchImagesInAlbum(String groupId) {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.SIZE

        };

        // content:// style URI for the "primary" external storage volume
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        // Make the query.
        Cursor cur = server.getContext().getContentResolver().query(uri,
                projection, // Which columns to return
                MediaStore.Images.Media.BUCKET_ID + "=?",       // Which rows to return (all rows)
                new String[]{groupId},       // Selection arguments (none)
                orderBy         // Ordering
        );

        if (cur.moveToFirst()) {
            int idxId = cur.getColumnIndex(MediaStore.Images.Media._ID);
            int idxDate = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int idxName = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int idxSize = cur.getColumnIndex(MediaStore.Images.Media.SIZE);



            do {
                Map<String, String> row = new HashMap<String, String>();
                row.put("ID", cur.getString(idxId));
                row.put("NAME", cur.getString(idxName));
                row.put("DATE", getDate(cur.getLong(idxDate)));
                row.put("SIZE", readableFileSize(cur.getLong(idxSize)));

                result.add(row);

            } while (cur.moveToNext());

        }


        return result;
    }

    private ArrayList<Map<String, String>> fetchImageAlbums() {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String[] projection = new String[]{
                 MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                "MIN("+ MediaStore.Images.Media._ID+") _ID "
        };

        // content:// style URI for the "primary" external storage volume
        Uri videos = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Images.Media.BUCKET_ID+","+MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

        // Make the query.
        Cursor cur = server.getContext().getContentResolver().query(videos,
                projection, // Which columns to return
                "1) GROUP BY 1,(2",       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                orderBy         // Ordering
        );

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int nameIndex = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int idIndex = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_ID);

            int idThumb = cur.getColumnIndex("_ID");

            do {
                Map<String, String> row = new HashMap<String, String>();
                row.put("ID", cur.getString(idIndex));
                row.put("NAME", cur.getString(nameIndex));
                row.put("_ID", cur.getString(idThumb));
//                String thumb = cur.getString(idThumb);
//                row.putAll(getThumbDetails(IMAGE, thumb));
                result.add(row);

            } while (cur.moveToNext());

        }


        return result;
    }

    private ArrayList<Map<String, String>> fetchVideoAlbums() {
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String[] projection = new String[]{
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                "MAX("+ MediaStore.Video.Media._ID+") _ID"
        };

        // content:// style URI for the "primary" external storage volume
        Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Video.Media.BUCKET_DISPLAY_NAME;

        // Make the query.
        Cursor cur = server.getContext().getContentResolver().query(videos,
                projection, // Which columns to return
                "1) GROUP BY 1,(2",
                null,       // Selection arguments (none)
                orderBy         // Ordering
        );

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int nameIndex = cur.getColumnIndex(
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

            int idIndex = cur.getColumnIndex(
                    MediaStore.Video.Media.BUCKET_ID);

            int idThumb = cur.getColumnIndex("_ID");


            do {
                Map<String, String> row = new HashMap<String, String>();
                row.put("ID", cur.getString(idIndex));
                row.put("NAME", cur.getString(nameIndex));
                row.put("_ID", cur.getString(idThumb));
//                String thumb = cur.getString(idThumb);
//                row.putAll(getThumbDetails(VIDEO, thumb));

                result.add(row);

            } while (cur.moveToNext());

        }


        return result;
    }

    public String getFilePath(int mediaType, String id) {

        Map<String, String> result = new HashMap<String, String>();
        String[] projection = null;
        Uri uri = null;

        String data = null;

        switch (mediaType) {
            case IMAGE: {
                projection = new String[]{
                        MediaStore.Images.Media.DATA
                };

                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                Cursor cur = server.getContext().getContentResolver().query(uri,
                        projection, // Which columns to return
                         MediaStore.Images.Media._ID + "=?",       // Which rows to return (all rows)
                        new String[]{id},       // Selection arguments (none)
                        null         // Ordering
                );

                if (cur.moveToFirst()) {
                    int idxData = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                    data = cur.getString(idxData);


                }
                cur.close();
                break;
            }

            case VIDEO: {
                projection = new String[]{
                        MediaStore.Video.Media.DATA
                };

                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                Cursor cur = server.getContext().getContentResolver().query(uri,
                        projection, // Which columns to return
                        MediaStore.Video.Media._ID + "=?",       // Which rows to return (all rows)
                        new String[]{id},       // Selection arguments (none)
                        null         // Ordering
                );

                if (cur.moveToFirst()) {
                    int idxData = cur.getColumnIndex(MediaStore.Video.Media.DATA);

                    data = cur.getString(idxData);


                }
                cur.close();
                break;
            }
        }

        return data;
    }


    public byte[] createThumb(int mediaType, long id) {


        ContentResolver crThumb = server.getContext().getContentResolver();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap curThumb =null;
        if(VIDEO==mediaType) {
            curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
        }
        else if(IMAGE==mediaType){
            curThumb = MediaStore.Images.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
        }

        if(null != curThumb){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            curThumb.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            return bos.toByteArray();
        }


        return null;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy  HH:mm:ss", cal).toString();
        return date;
    }
}
