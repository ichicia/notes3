package com.example.notes3.tools;

import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;


import java.util.List;

public class CandleStickChartUtils {
    private CandleStickChart csc;
    public CandleStickChartUtils(CandleStickChart csc){
        this.csc = csc;
        initSetting();
    }

    /**
     * 常用设置
     */
    private void initSetting() {
        csc.getDescription().setText("");
        csc.getDescription().setTextColor(Color.RED);
        csc.getDescription().setTextSize(16);//设置描述的文字 ,颜色 大小
        csc.setNoDataText("无数据噢"); //没数据的时候显示
        csc.setDrawBorders(false);//是否显示边框
        csc.animateX(500);//x轴动画
        csc.setTouchEnabled(true); // 设置是否可以触摸
        csc.setDragEnabled(true);// 是否可以拖拽
        csc.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
        csc.setScaleXEnabled(true); //是否可以缩放 仅x轴
        csc.setScaleYEnabled(true); //是否可以缩放 仅y轴
        csc.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        csc.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        csc.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        csc.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        csc.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止

        //x轴的设置
        XAxis xAxis = csc.getXAxis();//获取x轴
        xAxis.setAxisMinimum(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴位置
        xAxis.setTextColor(Color.GRAY);//设置x轴字体颜色
        xAxis.setTextSize(14);//设置x轴文字字体大小
        xAxis.setDrawGridLines(false);//设置竖向线  网格线

        //y轴的设置
        YAxis yAxisLeft = csc.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);//设置左侧y轴
        yAxisLeft.setTextSize(14);//左侧y轴文字字体大小
        YAxis yAxisRight = csc.getAxisRight(); //设置右侧y轴
        yAxisRight.setEnabled(false);//设置右侧y轴是否可用

        /**
         * 设置图例
         */
        Legend legend = csc.getLegend();
        legend.setEnabled(true);//
        legend.setForm(Legend.LegendForm.SQUARE); //设置比例图样式 方
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//设置横向
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//设置位置
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        String[] lables = new String[]{"涨","跌"};
        int[] colors = new int[]{Color.RED,Color.GREEN};
        LegendEntry incre = new LegendEntry("涨", Legend.LegendForm.SQUARE, 10f, 1f, null, colors[0]);
        LegendEntry decre = new LegendEntry("跌", Legend.LegendForm.SQUARE, 10f, 1f, null, colors[1]);
        legend.setCustom(new LegendEntry[]{incre,decre});
    }

    /**
     * 设置数据
     * @param yVals
     */
    public void setCandleStickData(List<CandleEntry> yVals){
        CandleDataSet candleDataSet = new CandleDataSet(yVals,"");
        candleDataSet.setValueTextColor(Color.BLACK);
        candleDataSet.setValueTextSize(14);
        candleDataSet.setShadowColor(Color.DKGRAY);//设置影线的颜色
        candleDataSet.setShadowWidth(0.5f);//设置影线的宽度
        candleDataSet.setShadowColorSameAsCandle(true);//设置影线和蜡烛图的颜色一样
        candleDataSet.setDecreasingColor(Color.GREEN);//设置减少色
        candleDataSet.setDecreasingPaintStyle(Paint.Style.STROKE);//绿跌，空心描边
        candleDataSet.setIncreasingColor(Color.RED);//设置增长色
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);//设置增长红 实心
        candleDataSet.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
        candleDataSet.setHighlightEnabled(true);//设置定位线是否可用
        candleDataSet.setHighLightColor(Color.BLACK); //设置定位线的颜色
        candleDataSet.setHighlightLineWidth(0.5f);//设置定位线的线宽
        candleDataSet.setBarSpace(0.9f);//0 至1 之间,越小蜡烛图的宽度越宽
        candleDataSet.setDrawValues(false);//设置是否显示蜡烛图上的文字
        CandleData data = new CandleData(candleDataSet);
        csc.setData(data);
    }
}

