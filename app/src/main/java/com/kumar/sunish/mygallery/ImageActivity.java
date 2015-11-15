package com.kumar.sunish.mygallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kumar.sunish.mygallery.util.ExtendedViewPager;
import com.kumar.sunish.mygallery.util.TouchImageView;


public class ImageActivity extends ActionBarActivity {


    private String[] imageIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        imageIds = AppData.getInstance().getAllImgIds().split(",");
        int viewPagerStart =0;
        for(int i=0;i<imageIds.length;i++){
            if(imageIds[i].equals(id)){
                viewPagerStart=i;
                break;
            }
        }



        setContentView(R.layout.activity_image);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(viewPagerStart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Uri getUri(int position){

        return Uri.parse(AppData.getInstance().getUrl()+"/pages/Gallery?type=" + MediaUtils.IMAGE + "&id=" + imageIds[position] );
    }

    private class ImagePagerAdapter extends PagerAdapter {




        @Override
        public int getCount() {
            return imageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Context context = ImageActivity.this;
            ImageView imageView = new ImageView(context);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            //   imageView.setDoubleTapEnabled(true);
//
//                    picasso
//                    .load(getUri(position))
//                    .placeholder(R.drawable.blank_img)
//                    .error(R.drawable.ic_launcher)
//                    .into(imageView);
         //   imageView.setImageResource(mImages[position]);

  //           TouchImageView imageView = new TouchImageView(context);
            Glide.with(context).load(getUri(position)).into(imageView);
//            Glide.with(context).load(getUri(position)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    imageView.setImageBitmap(resource);
//                }
//            });

            container.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }
    }


}
