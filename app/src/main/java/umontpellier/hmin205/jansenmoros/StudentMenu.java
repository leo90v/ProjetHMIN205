package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentMenu extends AppCompatActivity {

    Button btnGrades, btnProgression, btnContent;
    public static final String STUDENT_NAME = "student_name";
    public static final String STUDENT_ID = "student_id";

    private int student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_menu);

        btnGrades = findViewById(R.id.btn_grades);
        btnProgression = findViewById(R.id.btn_progress);
        btnContent = findViewById(R.id.btn_completedcontent);

        TextView tv = findViewById(R.id.textViewStudent);
        String studentName = getIntent().getStringExtra(STUDENT_NAME);
        tv.setText(studentName);

        student_id = getIntent().getIntExtra(STUDENT_ID,0);

        btnGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentMenu.this, Grades.class);
                intent.putExtra(Grades.USER_ID, student_id);
                startActivity(intent);
            }
        });

        btnProgression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentMenu.this, ProgressionCurve.class);
                intent.putExtra(ProgressionCurve.STUDENT_ID, student_id);
                startActivity(intent);
            }
        });

        btnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentMenu.this, CompletedContent.class);
                intent.putExtra(CompletedContent.USER_ID, student_id);
                startActivity(intent);
            }
        });
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
