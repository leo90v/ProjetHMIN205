package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.Course;

public class CourseListPage extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_page);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        final LinearLayout layout = findViewById(R.id.course_page_layout);

        compositeDisposable.add(myAPI.getCourses(Properties.getInstance().getGrade())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Course>>() {
                            @Override
                            public void accept(List<Course> T) throws Exception {
                                for (final Course c : T)
                                {
                                    MaterialButton btnCourse = new MaterialButton(CourseListPage.this);
                                    btnCourse.setText(c.getName());
                                    layout.addView(btnCourse);
                                    btnCourse.setOnClickListener(new View.OnClickListener() {
                                        private Course course = c;

                                        @Override
                                        public void onClick(View v) {
                                            /*Intent intent = new Intent(CourseListPage.this, CoursePage.class);
                                            intent.putExtra(CoursePage.COURSE_ID,course.getId());
                                            startActivity(intent);*/
                                        }
                                    });
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
}
