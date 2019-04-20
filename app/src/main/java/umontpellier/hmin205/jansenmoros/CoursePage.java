package umontpellier.hmin205.jansenmoros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CoursePage extends AppCompatActivity {

    public static final String COURSE_ID = "course_id";
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        courseId = getIntent().getIntExtra(COURSE_ID,0);
        Toast.makeText(CoursePage.this,Integer.toString(courseId),Toast.LENGTH_LONG).show();
    }
}
