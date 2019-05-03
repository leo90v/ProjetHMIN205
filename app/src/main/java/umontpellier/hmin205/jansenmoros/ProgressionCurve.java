package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.MonthProgress;

public class ProgressionCurve extends AppCompatActivity {

    private LineChart lineChart;

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression_curve);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        lineChart = findViewById(R.id.linechart);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisLeft().setTextSize(14f);
        lineChart.getAxisLeft().setGranularityEnabled(true);
        lineChart.getAxisLeft().setGranularity(1f);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setTextSize(14f);
        lineChart.getXAxis().setLabelRotationAngle(-70f);

        final String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value - 1];
            }
        });

        lineChart.getXAxis().setLabelCount(11,false);

        final ArrayList<Entry> videosCount = new ArrayList<>();
        final ArrayList<Entry> pdfsCount = new ArrayList<>();

        //Create datasets
        final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        final LineData data = new LineData(dataSets);
        data.setDrawValues(false);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextSize(12f);
        lineChart.setMarker(new CustomMarkerView(ProgressionCurve.this,R.layout.custom_markerview_layout));

        compositeDisposable.add(myAPI.getPdfViews(2019,2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MonthProgress>>() {
                    @Override
                    public void accept(List<MonthProgress> progress) throws Exception {
                        if (progress.get(0).getMonth() != 0) {
                            int prevMonth = 1;

                            for (MonthProgress p : progress) {
                                if ((p.getMonth() - prevMonth) > 1)
                                    for (int i = prevMonth + 1; i < p.getMonth(); i++)
                                        pdfsCount.add(new Entry(i,0f));
                                prevMonth = p.getMonth();
                                pdfsCount.add(new Entry(p.getMonth(),p.getCount()));
                            }

                            if (prevMonth < 12)
                                for (int i = prevMonth + 1; i < 13; i++)
                                    pdfsCount.add(new Entry(i,0f));

                            LineDataSet lineDataSet = new LineDataSet(pdfsCount,"Documents");
                            lineDataSet.setValueTextSize(12f);
                            lineDataSet.setColor(Color.GREEN);
                            lineDataSet.setCircleColor(Color.GREEN);
                            lineDataSet.setDrawValues(false);
                            data.addDataSet(lineDataSet);

                            data.notifyDataChanged();
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                    }
                }));

        compositeDisposable.add(myAPI.getVideoViews(2019,2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MonthProgress>>() {
                    @Override
                    public void accept(List<MonthProgress> progress) throws Exception {
                        if (progress.get(0).getMonth() != 0) {
                            int prevMonth = 1;

                            for (MonthProgress p : progress) {
                                if ((p.getMonth() - prevMonth) > 1)
                                    for (int i = prevMonth + 1; i < p.getMonth(); i++)
                                        videosCount.add(new Entry(i,0f));
                                prevMonth = p.getMonth();
                                videosCount.add(new Entry(p.getMonth(),p.getCount()));
                            }

                            if (prevMonth < 12)
                                for (int i = prevMonth + 1; i < 13; i++)
                                    videosCount.add(new Entry(i,0f));

                            LineDataSet lineDataSet = new LineDataSet(videosCount,"Videos");
                            lineDataSet.setValueTextSize(12f);
                            lineDataSet.setColor(Color.BLUE);
                            lineDataSet.setCircleColor(Color.BLUE);
                            lineDataSet.setDrawValues(false);
                            data.addDataSet(lineDataSet);

                            data.notifyDataChanged();
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                    }
                }));
    }
}
