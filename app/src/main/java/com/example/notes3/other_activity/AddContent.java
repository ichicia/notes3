package com.example.notes3.other_activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.example.notes3.R;
import com.example.notes3.database.NotesDB;
import com.example.notes3.tools.TimeManage;
import com.example.notes3.tools.file.PermisionUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


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

    /**
     * 便签类型
     * 1:普通便签
     * 777：事务便签
     */
    private int type = 1;

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
        if (requestCode == 2) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            c_img.setImageBitmap(bitmap);
        } else if (requestCode == 3) {
            v_video.setVideoURI(Uri.fromFile(videoFile));
            v_video.start();
        }
    }

    private void init() {
        //view
        whatValue = getIntent().getStringExtra("what");
        Toast.makeText(this, whatValue, Toast.LENGTH_LONG).show();
        saveButton = (Button) findViewById(R.id.save_add);
        cancelButton = (Button) findViewById(R.id.cancel_add);
        ettext = (EditText) findViewById(R.id.ettext_add);
        checkBox = (CheckBox) findViewById(R.id.check);
        c_img = (ImageView) findViewById(R.id.c_img_add);
        v_video = (VideoView) findViewById(R.id.v_video_add);
        //判断MainActivity传过来的值，确定用户需要添加的是什么内容
        switch (whatValue) {
            case "text":
                c_img.setVisibility(View.GONE);
                v_video.setVisibility(View.GONE);
                break;
            case "img":
                c_img.setVisibility(View.VISIBLE);
                v_video.setVisibility(View.GONE);
                Intent oImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
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
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
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
                if (b) {
                    pvTime.show(checkBox);
                } else {
                    type = 1;
                }
            }
        });

        bulidTimeView();

    }

    TimePickerView pvTime;

    private void bulidTimeView() {

        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2040, 11, 31);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                CheckBox checkBox = (CheckBox) v;
                checkBox.setText(getTimes(date));
                type = 777;
            }
        })
                //年月日时分秒的显示与否，不设置则默认全部显示，这里可自行定制
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel(" 年", "月", "日", "时", "分", "")
                .isCenterLabel(true)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView(null)
                .build();
    }

    private String getTimes(Date date) {//可根据需要自行格式化数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

//        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_time, null, false);
//        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        popupWindow.setBackgroundDrawable(new ColorDrawable());//设置背景色
//        popupWindow.setOutsideTouchable(true);//设置popwindow  外部可点击
//
//        popupWindow.setFocusable(true);//聚焦
//
//        popupWindow.setAnimationStyle(R.style.Widget_AppCompat_Light_PopupMenu);//设置动画
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
//    }

//    private void setBackGroundAlpha(float alpha) {
//        //window管理器
//        WindowManager.LayoutParams attributes = getWindow().getAttributes();//得到popwindow 属性
//
//        attributes.alpha = alpha;//透明度
//        getWindow().setAttributes(attributes);//设置属性
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_add:
                addDataToDB();
                finish();
                break;
            case R.id.cancel_add:
                finish();
                break;
        }
    }

    private void addDataToDB() {
        String s = "";
        if (type == 777) {
            s = checkBox.getText().toString();
        }
        ContentValues values = new ContentValues();
        values.put(NotesDB.CONTENT, ettext.getText().toString());
        values.put(NotesDB.TIME, TimeManage.getTime());
        values.put(NotesDB.PATH, imgFile + "");
        values.put(NotesDB.VIDEO, videoFile + "");
        values.put(NotesDB.USER, NotesDB.currUser);
        values.put(NotesDB.OK, 1);
        values.put(NotesDB.PLAN_TIME, s);
        long insert = dbWriter.insert(NotesDB.TABLE_NAME, null, values);

        if (type == 777 && insert != 0) {
            Long[] diff = getDiff(transform(s));
            Log.e("addDataToDB: ",diff.toString());
            startAlarm(Integer.parseInt(String.valueOf(diff[0]))
                    ,Integer.parseInt(String.valueOf(diff[1]))
                    ,Integer.parseInt(String.valueOf(diff[2])));
        }
    }

    public Long[] getDiff(Long expirationTime){
        //获得当前时间戳
        Long[] longs = new Long[3];
        long timeStamp = System.currentTimeMillis();
        //格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //转换为String类型
        String endDate = formatter.format(expirationTime);//结束的时间戳
        String startDate = formatter.format(timeStamp);//开始的时间戳
        // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
        // 计算的时间差
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(endDate);//后的时间
            Date d2 = df.parse(startDate); //前的时间
            Long diff = d1.getTime() - d2.getTime(); //两时间差，精确到毫秒
            Long day = diff / (1000 * 60 * 60 * 24); //以天数为单位取整
            Long hour=(diff/(60*60*1000)-day*24); //以小时为单位取整
            Long min=((diff/(60*1000))-day*24*60-hour*60); //以分钟为单位取整
            Long second=(diff/1000-day*24*60*60-hour*60*60-min*60);//秒
            Log.e("tag","day =" +day);
            Log.e("tag","hour =" +hour);
            Log.e("tag","min =" +min);
            Log.e("tag","second =" +second);
            longs[0] = day;
            longs[1] = hour;
            longs[2] = min;
            return longs;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return longs;
    }

    /**
     * 开启闹钟
     * @param hour 小时
     * @param minute 分钟
     */
    private void startAlarm(int day,int hour, int minute) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());//获取当前时间
            //获取当前毫秒值
            long systemTime = System.currentTimeMillis();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8"));//设置时区
            c.set(Calendar.DAY_OF_MONTH, day);//设置几天提醒
            c.set(Calendar.HOUR_OF_DAY, hour);//设置几点提醒
            c.set(Calendar.MINUTE, minute);//设置几分提醒
            //获取上面设置的时间
            long selectTime = c.getTimeInMillis();
            // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
            if (systemTime > selectTime) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            /* 闹钟时间到了的一个提醒类 */
            Intent intent = new Intent(this, TransactionActivity.class);
            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            //得到AlarmManager实例
            AlarmManager am = (AlarmManager)this.getSystemService(ALARM_SERVICE);
            //重复提醒
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 60*60*1000*24, pi);
            Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long transform(String from) {
        String to = "";

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // SimpleDateFormat simple = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 本地时区
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        // 设定SDF的时区为本地
        simple.setTimeZone(localZone);

        SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // SimpleDateFormat simple1 = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置 DateFormat的时间区域为GMT
        simple1.setTimeZone(TimeZone.getTimeZone("GMT"));

        // 把字符串转化为Date对象，然后格式化输出这个Date
        Date fromDate = new Date();
        // 时间string解析成GMT时间
        try {
            fromDate = simple1.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // GMT时间转成当前时区的时间
        return fromDate.getTime();
    }

}
