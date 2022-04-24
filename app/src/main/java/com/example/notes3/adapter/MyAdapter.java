package com.example.notes3.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.notes3.R;
import com.example.notes3.database.NotesDB;

/**
 * Created by flyan on 18-6-20.
 */

public class MyAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private LinearLayout layout;

    public MyAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.cell, null);
        TextView contentTv = (TextView) layout.findViewById(R.id.list_content_cell);
        TextView timeTv = (TextView) layout.findViewById(R.id.list_time_cell);
        ImageView imgIv = (ImageView) layout.findViewById(R.id.list_img_cell);
        ImageView videoIv = (ImageView) layout.findViewById(R.id.list_video_cell);

        cursor.moveToPosition(position);
        String content = cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT));
        String time = cursor.getString(cursor.getColumnIndex(NotesDB.TIME));
        String imgUri = cursor.getString(cursor.getColumnIndex(NotesDB.PATH));
        String videoUri = cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO));

        contentTv.setText(content);
        timeTv.setText(time);
        imgIv.setImageBitmap(getImageThumbnail(imgUri, 200, 200));
        videoIv.setImageBitmap(getVideoThumbnail(videoUri, 200, 200,
                MediaStore.Images.Thumbnails.MICRO_KIND));

        return layout;
    }

    /**
     * 得到一张图片的略缩图通过uri,可指定略缩图的长宽
     *
     * @param uri
     * @param width
     * @param height
     * @return
     */
    private Bitmap getImageThumbnail(String uri, int width, int height){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri, options);
        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth / width;
        int beHeight = options.outHeight / height;
        int be = 1;
        if(beWidth < beHeight){
            be = beWidth;
        }
        else {
            be = beHeight;
        }
        if(be <= 0){
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(uri, options);
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    private Bitmap getVideoThumbnail(String uri, int width, int height, int kind){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
