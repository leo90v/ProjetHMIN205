package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import umontpellier.hmin205.jansenmoros.POJO.Result;

public class Grades extends AppCompatActivity {

    public static final String USER_ID = "user_id";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";

    int user_id, course_id;
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        mainListView = findViewById( R.id.grades_listview);

        user_id = getIntent().getIntExtra(USER_ID,0);
        course_id = getIntent().getIntExtra(COURSE_ID,0);

        if (course_id > 0) {
            compositeDisposable.add(myAPI.getGrades(course_id, user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Result>>() {
                        @Override
                        public void accept(final List<Result> results) throws Exception {
                            listAdapter = new ArrayAdapter(Grades.this, android.R.layout.simple_list_item_2, android.R.id.text1, results) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                    text1.setText(results.get(position).getName());

                                    if (results.get(position).getGrades() != null)
                                        text2.setText(results.get(position).calculateGrade());
                                    return view;
                                }
                            };

                            //Add title
                            TextView tv = findViewById(R.id.tvCourseName);
                            String courseName = getIntent().getStringExtra(COURSE_NAME);
                            tv.setText(courseName);
                            tv.setTextAppearance(android.R.style.TextAppearance_Large);

                            mainListView.setAdapter(listAdapter);
                        }
                    }));
        }
        else
        {
            compositeDisposable.add(myAPI.getAllGrades(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<AllGrades>>() {
                        @Override
                        public void accept(List<AllGrades> g) throws Exception {

                            LinearLayout layout = findViewById(R.id.linLayout);

                            for (final AllGrades subject : g) {
                                TextView tv = new TextView(Grades.this);
                                tv.setText(subject.getCourseName());
                                tv.setTextAppearance(android.R.style.TextAppearance_Medium);

                                tv.setGravity(Gravity.CENTER_HORIZONTAL);

                                layout.addView(tv);

                                ListView lv = new ListView(Grades.this);

                                if (subject.getResults() != null) {
                                    listAdapter = new ArrayAdapter(Grades.this, android.R.layout.simple_list_item_2, android.R.id.text1, subject.getResults()) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                        text1.setText(subject.getResults().get(position).getName());

                                        if (subject.getResults().get(position).getGrades() != null)
                                            text2.setText(subject.getResults().get(position).calculateGrade());
                                        return view;
                                    }
                                };
                                lv.setAdapter(listAdapter);
                                layout.addView(lv);
                                }
                            }
                        }
                    }));
        }
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
