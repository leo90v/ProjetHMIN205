package umontpellier.hmin205.jansenmoros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
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

        final LinearLayout layout = (LinearLayout) findViewById(R.id.course_page_layout);

        Button bQuiz = (Button) findViewById(R.id.buttonQuiz);
        bQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListPage.this, QcmPage.class);
                startActivity(intent);
            }
        });

        compositeDisposable.add(myAPI.getCourses(Properties.getInstance().getGrade())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Course>>() {
                            @Override
                            public void accept(List<Course> T) throws Exception {
                                for (final Course c : T)
                                    addCourse(layout,c);
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

    private void addCourse(LinearLayout layout, final Course course){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View courseItemView = inflater.inflate(R.layout.nestedlayout_course_item, null);
        layout.addView(courseItemView, layout.getChildCount());

        TextView tvName = courseItemView.findViewById(R.id.tvCourseName);
        TextView tvDescription = courseItemView.findViewById(R.id.tvCourseDescription);

        tvName.setText(course.getName());
        tvDescription.setText(course.getDescription());

        Button btnVideo = courseItemView.findViewById(R.id.btn_videos);
        Button btnDocuments = courseItemView.findViewById(R.id.btn_documents);

        if (course.getId() != 0) {
            btnVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CourseListPage.this, ContentList.class);
                    intent.putExtra(ContentList.CONTENT_TYPE, ContentList.VIDEO);
                    intent.putExtra(ContentList.COURSE_CODE, course.getId());
                    startActivity(intent);
                }
            });

            btnDocuments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CourseListPage.this, ContentList.class);
                    intent.putExtra(ContentList.CONTENT_TYPE, ContentList.PDF);
                    intent.putExtra(ContentList.COURSE_CODE, course.getId());
                    startActivity(intent);
                }
            });
        }
    }
}
