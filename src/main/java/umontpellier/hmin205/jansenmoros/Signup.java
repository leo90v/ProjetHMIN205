package umontpellier.hmin205.jansenmoros;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btn_parent, btn_child, btn_teacher;

        btn_parent = (Button) findViewById(R.id.btn_parent);
        btn_child = (Button) findViewById(R.id.btn_child);
        btn_teacher = (Button) findViewById(R.id.btn_teacher);

        btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the intent to change the screen
            }
        });

        btn_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the intent to change the screen
            }
        });

        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the intent to change the screen
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
