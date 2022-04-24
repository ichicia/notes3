package com.example.notes3.other_activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.notes3.R;
import com.example.notes3.database.NotesDB;
import com.example.notes3.tools.TimeManage;
import com.example.notes3.tools.file.PermisionUtils;

import java.io.File;


/**
 * Created by flyan on 18-6-20.
 */

public class AddContent extends AppCompatActivity implements View.OnClickListener {
    public final static String FILE_SAVE_PATH = "/storage/emulated/0/HelloNotes/";

    private String whatValue;
    private Button saveButton, cancelButton;
    private EditText ettext;
    private CheckBox checkBox;
    private ImageView c_img;
    private VideoView v_video;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;


    //图文所需
    private File imgFile;
    //视频所需
    private File videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 解决FileUriExposedException
         */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_content);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            c_img.setImageBitmap(bitmap);
        }
        else if(requestCode == 3){
            v_video.setVideoURI(Uri.fromFile(videoFile));
            v_video.start();
        }
    }

    private void init(){
        //view
        whatValue = getIntent().getStringExtra("what");
        Toast.makeText(this, whatValue, Toast.LENGTH_LONG).show();
        saveButton = (Button)findViewById(R.id.save_add);
        cancelButton = (Button)findViewById(R.id.cancel_add);
        ettext = (EditText)findViewById(R.id.ettext_add);
        checkBox = (CheckBox) findViewById(R.id.check);
        c_img = (ImageView)findViewById(R.id.c_img_add);
        v_video = (VideoView)findViewById(R.id.v_video_add);
        //判断MainActivity传过来的值，确定用户需要添加的是什么内容
        switch (whatValue){
            case "text":
                c_img.setVisibility(View.GONE);
                v_video.setVisibility(View.GONE);
                break;
            case "img":
                c_img.setVisibility(View.VISIBLE);
                v_video.setVisibility(View.GONE);
                Intent oImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    imgFile = new File(FILE_SAVE_PATH + TimeManage.getTime() + ".jpg");

                    PermisionUtils.verifyStoragePermissions(this);

                    oImg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
                    startActivityForResult(oImg, 2);
                }
                break;
            case "video":
                c_img.setVisibility(View.GONE);
                v_video.setVisibility(View.VISIBLE);
                Intent oVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    videoFile = new File(FILE_SAVE_PATH + TimeManage.getTime() + ".mp4");

                    PermisionUtils.verifyStoragePermissions(this);

                    oVideo.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                    startActivityForResult(oVideo, 3);
                }
                break;
        }

        //date
        notesDB = new NotesDB(this);
        dbWriter = notesDB.getWritableDatabase();

        //event
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(boolean){
                    showTime();
                }
            }
        });

    }

    private void showTime() {
//        View inflate = LayoutInflater.from(this).inflate(R.layout.pop, null, false);
//        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        popupWindow.setBackgroundDrawable(new ColorDrawable());//设置背景色
//        popupWindow.setOutsideTouchable(true);//设置popwindow  外部可点击
//
//        popupWindow.setFocusable(true);//聚焦
//
//        popupWindow.setAnimationStyle(R.style.PopAnimation);//设置动画
//        setBackGroundAlpha(0.5f);//设置背景透明度
//
//        popupWindow.showAtLocation(inflate, Gravity.CENTER, 0, 0);//局内显示位置
//
//        //设置pop消失监听
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                setBackGroundAlpha(1f);//消失背景色恢复正常
//            }
//        });
    }

    private void setBackGroundAlpha(float alpha) {
        //window管理器
        WindowManager.LayoutParams attributes = getWindow().getAttributes();//得到popwindow 属性

        attributes.alpha = alpha;//透明度
        getWindow().setAttributes(attributes);//设置属性
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_add:
                addDataToDB();
                finish();
                break;
            case R.id.cancel_add:
                finish();
                break;
        }
    }

    private void addDataToDB(){
        ContentValues values = new ContentValues();
        values.put(NotesDB.CONTENT, ettext.getText().toString());
        values.put(NotesDB.TIME, TimeManage.getTime());
        values.put(NotesDB.PATH, imgFile + "");
        values.put(NotesDB.VIDEO, videoFile + "");
        values.put(NotesDB.USER, NotesDB.currUser);
        values.put(NotesDB.OK, 1);
        values.put(NotesDB.PLAN_TIME, "");
        dbWriter.insert(NotesDB.TABLE_NAME, null, values);
    }
}
