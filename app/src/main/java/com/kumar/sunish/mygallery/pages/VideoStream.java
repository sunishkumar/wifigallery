package com.kumar.sunish.mygallery.pages;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 23/8/15.
 */
public class VideoStream implements DynamicPage {

    MyHTTPD server;

    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {

        long range;
        Map<String, String> parms = session.getParms();
        Map<String,String> headers = session.getHeaders();
        String mimeType = "text/plain";
        if (headers.containsKey("range"))
        {
            String contentRange = headers.get("range");
            range = Integer.parseInt(contentRange.substring(contentRange.indexOf("=") + 1, contentRange.indexOf("-")));
        }
        else
            range = 0;

        byte[] buffer;
        int constantLength = 256000;
        long bufLength=0;
        boolean isLastPart=false;
        try {

            MediaUtils util = new MediaUtils(server);

            String fileName = util.getFilePath(MediaUtils.VIDEO, parms.get("id"));
            mimeType = server.getMimeType(fileName);
            RandomAccessFile ff =new RandomAccessFile(new File(fileName),"rw" );
            long remainingChunk = ff.length() - range; //remaining
            if (remainingChunk < constantLength){
                bufLength= remainingChunk; //means last part
                isLastPart = true;
            }

            else
            bufLength = constantLength;
            if (range !=0)
                ff.seek(range);
            buffer= new byte[(int)bufLength];

            ff.read(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            buffer = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            buffer = new byte[0];
        }
        NanoHTTPD.Response response;

        response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.PARTIAL_CONTENT,mimeType,new ByteArrayInputStream(buffer),891064);

        response.addHeader("Content-Length","891064");
        response.addHeader("Content-Range", String.format("bytes %s-%s/%s", range, (range + bufLength), "891064"));

        return response;
    }
}
