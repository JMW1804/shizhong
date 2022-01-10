package com.wzq.jz_app.ui.fragment.chart;



import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;

import java.util.List;

/**
 *图表基类
 * Created by jin
 */

public abstract class BaseChartEntity<T extends Entry> {

    protected BarLineChartBase mChart;

    protected List<T>[] mEntries;//折线图绘制数据
    protected String[] labels;
    protected int []mChartColors;
    protected float mTextSize;
    protected int mValueColor;

    /*为true表示需要设置成虚线*/
    protected boolean[] hasDotted;


    protected BaseChartEntity(BarLineChartBase chart, List<T>[]entries, String[] labels,
                              int []chartColor, int valueColor, float textSize) {
        this.mChart = chart;
        this.mEntries = entries;
        this.labels = labels;
        this.mValueColor = valueColor;
        this.mChartColors = chartColor;
//        this.mTextSize = textSize;
        this.mTextSize = 11f;
        initChart();
    }

    protected BaseChartEntity(BarLineChartBase chart, List<T>[]entries, String[] labels,
                              int []chartColor, int valueColor, float textSize, boolean[] hasDotted) {
        this.mChart = chart;
        this.mEntries = entries;
        this.labels = labels;
        this.mValueColor = valueColor;
        this.mChartColors = chartColor;
        this.mTextSize = textSize;
//        this.mTextSize = 11f;
        this.hasDotted = hasDotted;
        initChart();
    }

    /**
     * <p>初始化chart</p>
     */
    protected void initChart() {
        initProperties();

        setChartData();

        initLegend(Legend.LegendForm.LINE, mTextSize, mValueColor);//图例

        initXAxis(mValueColor, mTextSize);

        initLeftAxis(mValueColor, mTextSize);


    }
    /**
        private XAxis xAxis;                 //X轴
        private YAxis leftYAxis;            //左侧Y轴
        private YAxis rightYAxis;           //右侧Y轴
        private Legend legend;              //图例
        private LimitLine limitLine;        //限制线
     */

    private void initLeftAxis(int color, float textSize) {
        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setTextColor(color);
        leftAxis.setTextSize(textSize);
        float yMax = mChart.getData().getYMax() == 0 ? 100f : mChart.getData().getYMax();
        leftAxis.setAxisMaximum(yMax + yMax * 0.007f);
//        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);//禁止显示网格线
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawZeroLine(false);//启用/禁用绘制零线
        leftAxis.setLabelCount(6);//设置X轴分割数量
        leftAxis.setAxisLineWidth(1f);//设置坐标轴那条线的宽度
        leftAxis.setAxisLineColor(mValueColor);//设置坐标轴那条线的颜色

        mChart.getAxisRight().setEnabled(false);//不显示右侧坐标轴

    }

    private void initXAxis(int color, float textSize) {
        XAxis xAxis = mChart.getXAxis();

        xAxis.setTextSize(textSize);
        xAxis.setAxisMinimum(0);//保证X轴从0开始，不然会上移一点
        xAxis.setTextColor(color);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);////是不是显示轴上的刻度
        xAxis.setAxisLineWidth(1f);
        xAxis.setLabelCount(8);
        //false表示线画在数据图的前面，true表示线画在数据图的后面会被遮挡。
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setAxisLineColor(mValueColor);
        xAxis.setCenterAxisLabels(false);
        xAxis.setAxisMinimum(mChart.getData().getXMin());

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴设置显示位置在底部


    }

    /**
     * <p>初始化属性信息</p>
     */
    private void initProperties() {
        mChart.setNoDataText("");
        // no description text
        mChart.getDescription().setEnabled(false);////设置描述文本

        // enable touch gestures
        mChart.setTouchEnabled(true);//设置支持触控手势
        mChart.setDragDecelerationFrictionCoef(0.9f);////拖动降速速率
        // enable scaling and dragging
        mChart.setDragEnabled(true);//设置可以拖动
        mChart.setScaleXEnabled(true);//启用/禁用缩放在X轴上
        mChart.setPinchZoom(false);//如果设置为true，没缩放功能。如果false，x轴和y轴可分别放大。
        mChart.setVisibleXRangeMaximum(12);
        mChart.setScaleYEnabled(false);
        mChart.setDrawGridBackground(false);////后台绘制
        mChart.setHighlightPerDragEnabled(false);//设置为true，允许每个图表表面拖过，当它完全缩小突出。默认值：true
     // 是否可以拖拽
        mChart.setDragEnabled(false);
        // 是否可以缩放
        mChart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
    }

    /**
     * <p>初始化Legend展示信息</p>
     * @param form 样式
     * @param legendTextSize 文字大小
     * @param legendColor 颜色值
     */
    public void initLegend(Legend.LegendForm form, float legendTextSize, int legendColor) {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(form);

        l.setTextSize(legendTextSize);
        l.setTextColor(legendColor);
        //l.setYOffset(11f);

        updateLegendOrientation(Legend.LegendVerticalAlignment.BOTTOM, Legend.LegendHorizontalAlignment.RIGHT, Legend.LegendOrientation.HORIZONTAL);
    }

    /**
     * <p>图例说明</p>
     * @param vertical 垂直方向位置 默认底部
     * @param horizontal 水平方向位置 默认右边
     * @param orientation 显示方向 默认水平展示
     */

    public void updateLegendOrientation (Legend.LegendVerticalAlignment vertical, Legend.LegendHorizontalAlignment horizontal, Legend.LegendOrientation orientation) {
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(vertical);
        l.setHorizontalAlignment(horizontal);
        l.setOrientation(orientation);
        l.setDrawInside(false);

    }

    /**
     * 图表value显示开关
     */
    public void toggleChartValue () {
        List<BaseDataSet> sets = mChart.getData().getDataSets();
        for (BaseDataSet iSet : sets) {
            iSet.setDrawValues(!iSet.isDrawValuesEnabled());
        }
        mChart.invalidate();
    }
    //标记视图 即点击xy轴交点时弹出展示信息的View 需自定义
    public void setMarkView (MarkerView markView) {
        markView.setChartView(mChart); // For bounds control
        mChart.setMarker(markView); // Set the marker to the chart
        mChart.invalidate();
    }

    /**
     * x/ylabel显示样式
     * @param xvalueFromatter x
     * @param leftValueFromatter y
     */
    public void setAxisFormatter(IAxisValueFormatter xvalueFromatter, IAxisValueFormatter leftValueFromatter) {
        mChart.getXAxis().setValueFormatter(xvalueFromatter);
        mChart.getAxisLeft().setValueFormatter(leftValueFromatter);
        mChart.invalidate();

    }

    protected abstract void setChartData();


    /**
     * value显示格式设置
     * @param valueFormatter IValueFormatter
     */
    public void setDataValueFormatter(IValueFormatter valueFormatter) {
        mChart.getData().setValueFormatter(valueFormatter);
    }
}
