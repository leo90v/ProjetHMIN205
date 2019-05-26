package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    public static final String STUDENT_ID = "student_id";

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int user;

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

        user = getIntent().getIntExtra(STUDENT_ID,0);

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

        compositeDisposable.add(myAPI.getPdfViews(year, user)
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

        compositeDisposable.add(myAPI.getVideoViews(year, user)
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        if(Properties.getInstance().getUserType()==1){
            MenuItem item = menu.findItem(R.id.students_nav);
            item.setVisible(false);
        }
        else{
            MenuItem item = menu.findItem(R.id.courses_nav);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.progression_nav);
            item2.setVisible(false);
        }

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_nav:
                startActivity(new Intent(this, Profile.class));
                return true;

            case R.id.courses_nav:
                startActivity(new Intent(this, CourseListPage.class));
                return true;

            case R.id.progression_nav:
                Intent intent = new Intent(this, ProgressionCurve.class);
                intent.putExtra(ProgressionCurve.STUDENT_ID, Properties.getInstance().getUserId());
                startActivity(intent);
                return true;

            case R.id.students_nav:
                startActivity(new Intent(this, StudentList.class));
                return true;

            case R.id.logout_nav:
                Intent intent2 = new Intent(this, Login.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
