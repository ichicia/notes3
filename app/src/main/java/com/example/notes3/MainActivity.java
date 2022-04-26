package com.example.notes3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.utils.Easing;
import com.example.notes3.adapter.MyAdapter;
import com.example.notes3.database.NotesDB;
import com.example.notes3.other_activity.AddContent;
import com.example.notes3.other_activity.NotifyControlActivity;
import com.example.notes3.other_activity.SelectActivity;
import com.example.notes3.other_activity.TransactionActivity;
import com.example.notes3.tools.CandleStickChartUtils;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button textButton, imgButton, videoButton;
    private ListView listView;
    private Intent addWhat;

    private NotesDB notesDB;
    private Cursor cursor;
    private SQLiteDatabase daReader;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

    private void init() {
        //view
        listView = (ListView) findViewById(R.id.listView_main);
        textButton = (Button) findViewById(R.id.text_button_main);
        imgButton = (Button) findViewById(R.id.img_button_main);
        videoButton = (Button) findViewById(R.id.video_button_main);

        //other
        notesDB = new NotesDB(this);
        daReader = notesDB.getReadableDatabase();


        //event
        textButton.setOnClickListener(this);
        imgButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent goSelect = new Intent(MainActivity.this, SelectActivity.class);
                goSelect.putExtra(NotesDB.ID, cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                goSelect.putExtra(NotesDB.CONTENT, cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                goSelect.putExtra(NotesDB.TIME, cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                goSelect.putExtra(NotesDB.PATH, cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                goSelect.putExtra(NotesDB.VIDEO, cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                startActivity(goSelect);
            }
        });
    }

    /**
     * 按钮点击监听器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        String flag = ((Button) v).getText().toString().trim();
        addWhat = new Intent(this, AddContent.class);
        switch (flag) {
            case "文字":
                addWhat.putExtra("what", "text");
                startActivity(addWhat);
                break;
            case "事务":
                Intent intent = new Intent(this, TransactionActivity.class);
                startActivity(intent);
                break;
            case "统计":
                showChart(getPieData());
//                Intent intent1 = new Intent(this, NotifyControlActivity.class);
//                startActivity(intent1);
                break;
        }
    }

    public void showChart() {

        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_chart, null, false);
        CandleStickChart candleStickChart = inflate.findViewById(R.id.candleStickChart);
        CandleStickChartUtils chartUtilsc = new CandleStickChartUtils(candleStickChart);
        List<CandleEntry> candleEntry = new ArrayList<>();
        /**
         * shadowH 当天的最高价
         * shadowL 当天的最低价
         * open 开盘价
         * close 收盘价
         */
        candleEntry.add(new CandleEntry(2, 8, 1, 8f, 2f));
        candleEntry.add(new CandleEntry(3, 5, 1, 4f, 4f));
        candleEntry.add(new CandleEntry(4, 8, 2, 4f, 6f));
        chartUtilsc.setCandleStickData(candleEntry);
        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
        popupWindow.setBackgroundDrawable(new ColorDrawable());//设置背景色
        popupWindow.setOutsideTouchable(true);//设置popwindow  外部可点击

        popupWindow.setFocusable(true);//聚焦

        popupWindow.setAnimationStyle(R.style.Widget_AppCompat_Light_PopupMenu);//设置动画
        setBackGroundAlpha(0.5f);//设置背景透明度

        popupWindow.showAtLocation(inflate, Gravity.CENTER, 0, 0);//局内显示位置

        //设置pop消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackGroundAlpha(1f);//消失背景色恢复正常
            }
        });
    }

    private void setBackGroundAlpha(float alpha) {
        //window管理器
        WindowManager.LayoutParams attributes = getWindow().getAttributes();//得到popwindow 属性

        attributes.alpha = alpha;//透明度
        getWindow().setAttributes(attributes);//设置属性
    }

    public void getDataFromDB() {
        cursor = daReader.query(NotesDB.TABLE_NAME, null, null,
                null, null, null, null);
        myAdapter = new MyAdapter(this, cursor);
        listView.setAdapter(myAdapter);
    }

    private void showChart(PieData pieData) {

        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_chart, null, false);
        PieChart pieChart = inflate.findViewById(R.id.candleStickChart);
        //设置中间是够透明
//        pieChart.setHoleColorTransparent(true);
//        设置饼状图的半径
        pieChart.setHoleRadius(60f);
        //饼状图中间添加文字
        pieChart.setDrawCenterText(true);
        //中间是空的
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText("完成比例");
        //设置文字颜色和字号
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.BLUE);
        //初始选择角度
        pieChart.setRotationAngle(90);
        //设置可旋转
        pieChart.setRotationEnabled(true);
        //设置成百分比可显示
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        //设置比例图
        Legend legend = pieChart.getLegend();//获取比例图
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(15f);
        //设置初始动画
        pieChart.animateXY(1000, 1000);

        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
        popupWindow.setBackgroundDrawable(new ColorDrawable());//设置背景色
        popupWindow.setOutsideTouchable(true);//设置popwindow  外部可点击

        popupWindow.setFocusable(true);//聚焦

        popupWindow.setAnimationStyle(R.style.Widget_AppCompat_Light_PopupMenu);//设置动画
        setBackGroundAlpha(0.5f);//设置背景透明度

        popupWindow.showAtLocation(inflate, Gravity.CENTER, 0, 0);//局内显示位置

        //设置pop消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackGroundAlpha(1f);//消失背景色恢复正常
            }
        });
    }

    private PieData getPieData() {

        float i1 = 0;
        float i2 = 0;

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String string = cursor.getString(cursor.getColumnIndex(NotesDB.PLAN_TIME));
            if (!"".equals(string)) {
                String string1 = cursor.getString(cursor.getColumnIndex(NotesDB.OK));
                Log.e("getPieData: " + i, string1);
                if ("777".equals(string1)) {
                    i1++;
                } else if ("1".equals(string1)) {
                    i2++;
                }
            }
        }

//        衣食住行
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("已完成");
        xValues.add("未完成");
        //衣食住行占得比例
        String s = String.valueOf(i1 / (i1 + i2));
        String s1 = String.valueOf(i2 / (i1 + i2));
        ArrayList<PieEntry> yrrayList = new ArrayList();
        yrrayList.add(new PieEntry(i1, "已完成" + s.substring(0,4) + "%"));
        yrrayList.add(new PieEntry(i2, "未完成" + s1.substring(0,4) + "%"));
        PieDataSet pieDataSet = new PieDataSet(yrrayList, "完成比例");
        pieDataSet.setSliceSpace(1f);
        //饼状图的颜色
        ArrayList<Integer> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.BLUE);
        colorList.add(Color.YELLOW);
        colorList.add(Color.GREEN);
        colorList.add(Color.GRAY);
        pieDataSet.setColors(colorList);
        //设置圆盘的文字颜色
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(15f);
//        DisplayMetrics 分辨率
        DisplayMetrics mdisplayMetrics = getResources().getDisplayMetrics();
        float px = 5 * (mdisplayMetrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);
        PieData pie = new PieData(pieDataSet);
        return pie;
    }


}
