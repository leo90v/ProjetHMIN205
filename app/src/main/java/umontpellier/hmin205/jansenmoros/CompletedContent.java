package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.AllGrades;
import umontpellier.hmin205.jansenmoros.POJO.Completed;

public class CompletedContent extends Activity {

    public static final String USER_ID = "user_id";
    private int user_id;

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    LinearLayout ccLayout;
    TextView tvVideoView, tvPdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_content);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        user_id = getIntent().getIntExtra(USER_ID,0);
        ccLayout = findViewById(R.id.ccLayout);
        tvVideoView = findViewById(R.id.tvVideoView);
        tvPdfView = findViewById(R.id.tvPdfView);

        compositeDisposable.add(myAPI.getCompletedVideos(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Completed>>() {
                    @Override
                    public void accept(List<Completed> cc) throws Exception {

                        int index = ccLayout.indexOfChild(tvVideoView)+1;
                        for (final Completed subject : cc) {
                            TextView tv = new TextView(CompletedContent.this);
                            tv.setText(subject.getName());
                            tv.setTextAppearance(android.R.style.TextAppearance_Medium);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.topMargin = 40;
                            params.gravity = Gravity.CENTER_HORIZONTAL;
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            tv.setLayoutParams(params);
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);

                            ccLayout.addView(tv,index);
                            index = ccLayout.indexOfChild(tv)+1;

                            ListView lv = new ListView(CompletedContent.this);

                            if (subject.getViews() != null) {
                                ListAdapter listAdapter = new ArrayAdapter(CompletedContent.this, android.R.layout.simple_list_item_2, android.R.id.text1, subject.getViews()) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                        text1.setText(subject.getViews().get(position).getName());
                                        text2.setText(subject.getViews().get(position).getCompletionTime());
                                        return view;
                                    }
                                };
                                lv.setAdapter(listAdapter);
                                ccLayout.addView(lv,index);
                                setListViewHeightBasedOnChildren(lv);
                                index = ccLayout.indexOfChild(lv)+1;
                            }
                        }
                    }
                }));

        compositeDisposable.add(myAPI.getCompletedPdfs(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Completed>>() {
                    @Override
                    public void accept(List<Completed> cc) throws Exception {

                        int index = ccLayout.indexOfChild(tvPdfView)+1;
                        for (final Completed subject : cc) {
                            TextView tv = new TextView(CompletedContent.this);
                            tv.setText(subject.getName());
                            tv.setTextAppearance(android.R.style.TextAppearance_Medium);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.topMargin = 40;
                            params.gravity = Gravity.CENTER_HORIZONTAL;
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            tv.setLayoutParams(params);
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);

                            ccLayout.addView(tv,index);
                            index = ccLayout.indexOfChild(tv)+1;

                            ListView lv = new ListView(CompletedContent.this);

                            if (subject.getViews() != null) {
                                ListAdapter listAdapter = new ArrayAdapter(CompletedContent.this, android.R.layout.simple_list_item_2, android.R.id.text1, subject.getViews()) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                        text1.setText(subject.getViews().get(position).getName());
                                        text2.setText(subject.getViews().get(position).getCompletionTime());
                                        return view;
                                    }
                                };
                                lv.setAdapter(listAdapter);
                                ccLayout.addView(lv,index);
                                setListViewHeightBasedOnChildren(lv);
                                index = ccLayout.indexOfChild(lv)+1;
                            }
                        }
                    }
                }));
    }

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
