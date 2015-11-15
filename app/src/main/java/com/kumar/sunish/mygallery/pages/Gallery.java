package com.kumar.sunish.mygallery.pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 2/8/15.
 */
public class Gallery implements DynamicPage {

    MyHTTPD server;

    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {
        this.server = server;
        Map<String, String> parms = session.getParms();
        Map<String,String> headers = session.getHeaders();
        String mimeType = "text/plain";
        if (parms.get("type") != null && parms.get("id")!=null ) {
            int type = 0;

            try {
                type = Integer.parseInt(parms.get("type"));

            } catch (Exception x) {
            }

            MediaUtils util = new MediaUtils(server);

            String fileName = util.getFilePath(type, parms.get("id"));

            String range = null;

            for (String key : headers.keySet()) {

                if ("range".equals(key)) {
                    range = headers.get(key);
                }
            }

            mimeType = server.getMimeType(fileName);

            try {
                if (range == null) {
                    return getFullResponse(mimeType,fileName);
                } else {
                    return getPartialResponse(mimeType,fileName, range);
                }
            } catch (IOException e) {

            }
        }



        return server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
    }


    private NanoHTTPD.Response getFullResponse(String mimeType, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mimeType, fileInputStream,file.length());
    }

    private NanoHTTPD.Response getPartialResponse(String mimeType, String filePath, String rangeHeader) throws IOException {
        File file = new File(filePath);
        String rangeValue = rangeHeader.trim().substring("bytes=".length());
        long fileLength = file.length();
        long start, end;
        if (rangeValue.startsWith("-")) {
            end = fileLength - 1;
            start = fileLength - 1
                    - Long.parseLong(rangeValue.substring("-".length()));
        } else {
            String[] range = rangeValue.split("-");
            start = Long.parseLong(range[0]);
            end = range.length > 1 ? Long.parseLong(range[1])
                    : fileLength - 1;
        }
        if (end > fileLength - 1) {
            end = fileLength - 1;
        }
        if (start <= end) {
            long contentLength = end - start + 1;

            FileInputStream fileInputStream = new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            fileInputStream.skip(start);
            NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.PARTIAL_CONTENT, mimeType, fileInputStream,contentLength);
            response.addHeader("Content-Length", contentLength + "");
            response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.addHeader("Content-Type", mimeType);
            return response;
        } else {
            return  server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
        }
    }
}
