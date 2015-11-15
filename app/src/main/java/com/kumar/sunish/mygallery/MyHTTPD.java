package com.kumar.sunish.mygallery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.kumar.sunish.mygallery.pages.DynamicPage;
import com.kumar.sunish.mygallery.pages.IndexPage;

import fi.iki.elonen.NanoHTTPD;

public class MyHTTPD extends NanoHTTPD {

    private Handler handler = new Handler();
    private Context context;

    public static int PORT = 8867;

    private static Map<String,DynamicPage> cachedServices = new HashMap<String,DynamicPage>();

    public MyHTTPD(Context context) throws IOException {
        super(MyHTTPD.PORT);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {


        String uri = session.getUri();


        if(uri.equals("/")){
            return ( new IndexPage()).createResponse(this,session);
        }
        else if(uri.startsWith("/pages")) {
            try {
                DynamicPage service = cachedServices.get(uri);
                if(service==null) {
                    service = (DynamicPage) Class.forName("com.kumar.sunish.mygallery.pages." + uri.replaceAll("/pages/", "").replaceAll("/","")).newInstance();
                    cachedServices.put(uri,service);
                }
                return service.createResponse(this,session);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(Response.Status.NOT_FOUND,"text/plain","File Not Found");
        }
        else{
            InputStream fis = null;
            String mime = getMimeType(uri);
            try {
                fis =  context.getAssets().open(uri.substring(1));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.NOT_FOUND,"text/plain","File Not Found");
            }
            return newChunkedResponse(Response.Status.OK, mime, fis);

        }

//        if (parms.get("username") == null) {
//            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//        } else {
//            msg += "<p>Hello, " + parms.get("username") + "!</p>";
//        }


    }





    public  String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}