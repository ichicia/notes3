package com.example.notes3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.utils.Easing;
import com.example.notes3.adapter.MyAdapter;
import com.example.notes3.database.NotesDB;
import com.example.notes3.other_activity.AddContent;
import com.example.notes3.other_activity.SelectActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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

    private void init(){
        //view
        listView = (ListView)findViewById(R.id.listView_main);
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
        String flag = ((Button)v).getText().toString().trim();
        addWhat = new Intent(this, AddContent.class);
        switch (flag){
            case "文字":
                addWhat.putExtra("what", "text");
                startActivity(addWhat);
                break;
            case "图文":
                addWhat.putExtra("what", "img");
                startActivity(addWhat);
                break;
            case "视频":
                addWhat.putExtra("what", "video");
                startActivity(addWhat);
                break;
        }
    }

    public void showChart(){

        PopupWindow window = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_chart, null, false);
//        PieChart pieChart = inflate.findViewById(R.id.consume_pie_chart);
//
//        pieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
//        pieChart.setDescription("全年消费情况");//设置描述
//        pieChart.setDescriptionTextSize(20);//设置描述字体大小
////pieChart.setDescriptionColor(); //设置描述颜色
////pieChart.setDescriptionTypeface();//设置描述字体
//
//        pieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量
//
//        pieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难
//
//        pieChart.setDrawCenterText(true);//是否绘制中间的文字
//        pieChart.setCenterTextColor(Color.RED);//中间的文字颜色
//        pieChart.setCenterTextSize(24);//中间的文字字体大小
//
//        pieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
//        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
//        pieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小
//
//        pieChart.setTransparentCircleColor(Color.BLACK);//设置圆环的颜色
//        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
//        mChart.setTransparentCircleRadius(60f);//设置圆环的半径值
//
//// enable rotation of the chart by touch
//        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
//        pieChart.setRotationAngle(10);//设置饼状图旋转的角度
//
//        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)
//
//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
//        l.setXEntrySpace(0f);
//        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
//        l.setYOffset(0f);
//
//// entry label styling
//        pieChart.setDrawEntryLabels(true);//设置是否绘制Label
//        pieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
////pieChart.setEntryLabelTypeface(mTfRegular);
//        pieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小
//
//        pieChart.setOnChartValueSelectedListener(this);//设值点击时候的回调
//        pieChart.animateY(3400, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画
//        ArrayList<PieEntry> pieEntries = new ArrayList<>();
//        for(ConsumeTypeMoneyPo typeMoneyVo : consumeTypeMoneyVoList){
//            PieEntry pieEntry = new PieEntry((float)typeMoneyVo.getTotalMoney(), typeMoneyVo.getConsumeTypeName());
//            pieEntries.add(pieEntry);
//            totalMoney += typeMoneyVo.getTotalMoney();
//        }
//        String centerText = mQueryYear+"年消费\n¥"+totalMoney;
//        pieChart.setCenterText(centerText);//设置中间的文字
//        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
//        pieDataSet.setColors(getPieChartColors());
//        pieDataSet.setSliceSpace(3f);//设置选中的Tab离两边的距离
//        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
//        PieData pieData = new PieData();
//        pieData.setDataSet(pieDataSet);
//
//        pieData.setValueFormatter(new PercentFormatter());
//        pieData.setValueTextSize(12f);
//        pieData.setValueTextColor(Color.BLUE);
//
//        pieChart.setData(pieData);
//// undo all highlights
//        pieChart.highlightValues(null);
//        pieChart.invalidate();
    }

    public void getDataFromDB(){
        cursor = daReader.query(NotesDB.TABLE_NAME, null, null,
                null, null, null, null);
        myAdapter = new MyAdapter(this, cursor);
        listView.setAdapter(myAdapter);
    }
}
