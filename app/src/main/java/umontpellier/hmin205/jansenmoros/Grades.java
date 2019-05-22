package umontpellier.hmin205.jansenmoros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.Result;

public class Grades extends AppCompatActivity {

    public static final String USER_ID = "user_id";
    public static final String COURSE_ID = "course_id";

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
                        mainListView.setAdapter(listAdapter);
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
}
