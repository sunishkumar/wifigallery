package com.kumar.sunish.mygallery.pages;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.kumar.sunish.mygallery.MyHTTPD;

import java.io.FileNotFoundException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 13/8/15.
 */
public class MediaAudio implements  DynamicPage {


    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {


        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE
        };

        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Audio.Media.ALBUM+" , "+MediaStore.Audio.Media.DATE_ADDED+" DESC ";

        // Make the query.
        Cursor cur = server.getContext().getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                orderBy         // Ordering
        );

        String msg ="<html><head><title>Media</title></head><body>";

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int albumId = cur.getColumnIndex(
                    MediaStore.Audio.Media.ALBUM_ID);
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Audio.Media.ALBUM);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Audio.Media.DATE_ADDED);

            int dataColumn = cur.getColumnIndex(
                    MediaStore.Audio.Media.DATA);

            int sizeColumn = cur.getColumnIndex(
                    MediaStore.Audio.Media.SIZE);

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);

                msg += cur.getString(bucketColumn)+" : "+ cur.getString(dateColumn)+" : "+cur.getString(sizeColumn)+" : "+cur.getString(dataColumn)+"<br >";


//                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//                Uri uri = ContentUris.withAppendedId(sArtworkUri, cur.getLong(albumId));
//                ContentResolver res = server.getContext().getContentResolver();
//                InputStream in = null;
//                try {
//                    in = res.openInputStream(uri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                Bitmap artwork = BitmapFactory.decodeStream(in);

            } while (cur.moveToNext());

        }

         msg +="</body></html>";
         return server.newFixedLengthResponse(msg );
    }
}
