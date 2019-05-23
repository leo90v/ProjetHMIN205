package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentMenu extends Activity {

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

            }
        });
    }

}