package com.example.notes3.other_activity;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notes3.R;

public class NotifyControlActivity extends AppCompatActivity {


    private Button notifyStart;// 启动通知服务

    private Button notifyStop;// 停止通知服务

    @Override

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nitify);

        initWidgets();

    }

    private void initWidgets() {


        notifyStart = (Button) findViewById(R.id.notifyStart);

        notifyStart.setOnClickListener(mStartListener);

        notifyStop = (Button) findViewById(R.id.notifyStop);

        notifyStop.setOnClickListener(mStopListener);

    }

    private View.OnClickListener mStartListener = new View.OnClickListener() {


        public void onClick(View v) {


// 启动Notification对应Service

            startService(new Intent(NotifyControlActivity.this,

                    NotifyingService.class));

        }

    };

    private View.OnClickListener mStopListener = new View.OnClickListener() {


        public void onClick(View v) {


// 停止Notification对应Service

            stopService(new Intent(NotifyControlActivity.this,

                    NotifyingService.class));

        }

    };

}

/**
 * 实现每5秒发一条状态栏通知的Service
 *
 * @author ldm
 * @description：
 * @date 2016-4-29 上午9:16:20
 */

class NotifyingService extends Service {


// 状态栏通知的管理类对象，负责发通知、清楚通知等

    private NotificationManager mNM;

// 使用Layout文件的对应ID来作为通知的唯一识别

    private static int MOOD_NOTIFICATIONS = R.layout.activity_nitify;

    /**
     * Android给我们提供ConditionVariable类，用于线程同步。提供了三个方法block()、open()、close()。 void
     * <p>
     * block() 阻塞当前线程，直到条件为open 。 void block(long timeout)阻塞当前线程，直到条件为open或超时
     * <p>
     * void open()释放所有阻塞的线程 void close() 将条件重置为close。
     */

    private ConditionVariable mCondition;

    @Override

    public void onCreate() {


// 状态栏通知的管理类对象，负责发通知、清楚通知等

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

// 启动一个新个线程执行任务，因Service也是运行在主线程，不能用来执行耗时操作

        Thread notifyingThread = new Thread(null, mTask, "NotifyingService");

        mCondition = new ConditionVariable(false);

        notifyingThread.start();

    }

    @Override

    public void onDestroy() {


// 取消通知功能

        mNM.cancel(MOOD_NOTIFICATIONS);

// 停止线程进一步生成通知

        mCondition.open();

    }

    /**
     * 生成通知的线程任务
     */

    private Runnable mTask = new Runnable() {


        public void run() {


            for (int i = 0; i < 4; ++i) {


                Log.e("测试", "" + i);
// 生成带stat_happy及status_bar_notifications_happy_message内容的通知

                showNotification(R.drawable.account,

                        R.string.app_name);

                if (mCondition.block(5 * 1000))

                    break;

// 生成带stat_neutral及status_bar_notifications_ok_message内容的通知

//                showNotification(R.drawable.stat_neutral,
//
//                        R.string.status_bar_notifications_ok_message);

//                if (mCondition.block(5 * 1000))
//
//                    break;

// 生成带stat_sad及status_bar_notifications_sad_message内容的通知

//                showNotification(R.drawable.stat_sad,
//
//                        R.string.status_bar_notifications_sad_message);
//
//                if (mCondition.block(5 * 1000))
//
//                    break;

            }

// 完成通知功能，停止服务。

            NotifyingService.this.stopSelf();

        }

    };

    @Override

    public IBinder onBind(Intent intent) {


        return mBinder;

    }

    @SuppressWarnings("deprecation")

    private void showNotification(int moodId, int textId) {


// 自定义一条通知内容

        CharSequence text = getText(textId);

// 当点击通知时通过PendingIntent来执行指定页面跳转或取消通知栏等消息操作

        Notification notification = new Notification(moodId, null,

                System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,

                new Intent(this, NotifyControlActivity.class), 0);

// 在此处设置在nority列表里的该norifycation得显示情况。

//        notification.setLatestEventInfo(this,
//
//                getText(R.string.app_name), text,
//
//                contentIntent);

/**
 * 注意,我们使用出来。incoming_message ID 通知。它可以是任何整数,但我们使用 资源id字符串相关
 * 通知。它将永远是一个独特的号码在你的 应用程序。

 */

        mNM.notify(MOOD_NOTIFICATIONS, notification);

    }

// 这是接收来自客户端的交互的对象. See

    private final IBinder mBinder = new Binder() {


        @Override

        protected boolean onTransact(int code, Parcel data, Parcel reply,

                                     int flags) throws RemoteException {


            return super.onTransact(code, data, reply, flags);

        }

    };

}
