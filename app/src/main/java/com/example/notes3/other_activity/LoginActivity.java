package com.example.notes3.other_activity;

import android.animation.*;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notes3.MainActivity;
import com.example.notes3.R;
import com.example.notes3.database.NotesDB;
import com.example.notes3.tools.JellyInterpolator;
import com.example.notes3.tools.TimeManage;

import java.util.List;
import java.util.Map;

import static com.example.notes3.database.NotesDB.currUser;

/**
 * Created by flyan on 18-6-22.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mBtnLogin;
    private EditText userName;
    private EditText password;

    private View progress;

    private View mInputLayout;

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw;

    private NotesDB notesDB;
    private SQLiteDatabase daReader;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {

        //other
        notesDB = new NotesDB(this);
        daReader = notesDB.getReadableDatabase();

        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        password = (EditText) findViewById(R.id.password);
        userName = (EditText) findViewById(R.id.user_name);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String string1 = userName.getText().toString();
        String string2 = password.getText().toString();
        if (string1 == null) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (string2 == null) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 计算出控件的高与宽
        mWidth = mBtnLogin.getMeasuredWidth();
        mHeight = mBtnLogin.getMeasuredHeight();
        // 隐藏输入框
        mName.setVisibility(View.INVISIBLE);
        mPsw.setVisibility(View.INVISIBLE);

        inputAnimator(mInputLayout, mWidth, mHeight,1);

        login(string1, string2);

    }

    private void login(String string1, String string2) {
        cursor = daReader.query(NotesDB.TABLE_USER, null, NotesDB.ACCOUNT + "=?",
                new String[]{string1}, null, null, null);
        if (cursor.moveToNext()) {
            //存在用户，校验
            String string = cursor.getString(cursor.getColumnIndex(NotesDB.PS));
            if (string2.equals(string)) {
                startActivity(new Intent(this, MainActivity.class));
                currUser = string1;
                finish();
            } else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                refreView();
            }
        } else {
            //不存在用户，新增用户
            ContentValues values = new ContentValues();
            values.put(NotesDB.ACCOUNT, string1);
            values.put(NotesDB.PS, string2);
            daReader.insert(NotesDB.TABLE_USER, null, values);

            currUser = string1;
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "用户新增成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void refreView() {
        // 显示输入框
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        inputAnimator(mInputLayout, mWidth, mHeight,7);
    }


    /**
     * 输入框的动画效果
     *
     * @param view 控件
     * @param w    宽
     * @param h    高
     * @param type 显示
     */
    private void inputAnimator(final View view, float w, float h, int type) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        if (type == 7) {
            animator2 = ObjectAnimator.ofFloat(mInputLayout,
                    "scaleX", 0.5f, 1f);
        }
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (type == 7) {
                    progress.setVisibility(View.INVISIBLE);
//                    progressAnimator(progress);
                    mInputLayout.setVisibility(View.VISIBLE);
                } else {
                    /**
                     * 动画结束后，先显示加载的动画，然后再隐藏输入框
                     */
                    progress.setVisibility(View.VISIBLE);
                    progressAnimator(progress);
                    mInputLayout.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }


}
