package com.example.notes3.other_activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notes3.R;
import com.example.notes3.database.NotesDB;

/**
 * Created by flyan on 18-6-22.
 */

public class SelectActivity extends AppCompatActivity {
    private Button delectButton;
    private Button gobcakButton;
    private ImageView imageView;
    private VideoView videoView;
    private TextView content;
    private TextView time;

    private SQLiteDatabase dbWriter;

    private ButtonClickListener buttonClickListener;

    private Intent mainToHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        init();
    }

    private void init(){
        //控件　&& 对象
        delectButton = (Button)findViewById(R.id.delete_select);
        gobcakButton = (Button)findViewById(R.id.back_select);
        imageView = (ImageView)findViewById(R.id.img_select);
        videoView = (VideoView)findViewById(R.id.video_select);
        time = (TextView)findViewById(R.id.time_select);
        content = (TextView)findViewById(R.id.textView_select);
        mainToHere = getIntent();
        dbWriter = new NotesDB(this).getWritableDatabase();
        buttonClickListener = new ButtonClickListener();

        //事件
        delectButton.setOnClickListener(buttonClickListener);
        gobcakButton.setOnClickListener(buttonClickListener);

        //加载页面数据
        //从intent判断需要加载什么样的页面
        time.setText("创建时间为->" + mainToHere.getStringExtra(NotesDB.TIME));
        content.setText(mainToHere.getStringExtra(NotesDB.CONTENT));
        if(!mainToHere.getStringExtra(NotesDB.PATH).equals("null")){    //图片不为空加载图片
            Bitmap bitmap = BitmapFactory.decodeFile(mainToHere.getStringExtra(NotesDB.PATH));
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
        if(!mainToHere.getStringExtra(NotesDB.VIDEO).equals("null")) {  //视频不为空加载视频
            videoView.setVideoURI(Uri.parse(mainToHere.getStringExtra(NotesDB.VIDEO)));
            videoView.setVisibility(View.VISIBLE);
            videoView.start();
        }

    }

    private class ButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.delete_select:
                    deleteData();
                    finish();
                    break;
                case R.id.back_select:
                    finish();
                    break;
            }
        }
    }

    private void deleteData(){
        dbWriter.delete(NotesDB.TABLE_NAME, NotesDB.ID + "=" +
                mainToHere.getIntExtra(NotesDB.ID, 0), null);
    }
}
