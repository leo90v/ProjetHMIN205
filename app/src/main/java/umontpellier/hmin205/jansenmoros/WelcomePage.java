package umontpellier.hmin205.jansenmoros;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;

public class WelcomePage extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button btnCourses, btnProgression, btnProfile, btnStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);

        if(Properties.getInstance().getUserType()==1) setAlarm(7,12);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        // Set the text
        TextView tv = findViewById(R.id.textViewWelcome);
        String username = Properties.getInstance().getUsername();
        tv.setText(getString(R.string.welcome_welcome) + " " + username);

        btnCourses = findViewById(R.id.btn_courses);
        btnProgression = findViewById(R.id.btn_progression);
        btnProfile = findViewById(R.id.btn_profile);
        btnStudents = findViewById(R.id.btn_students);

        btnCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, CourseListPage.class);
                startActivity(intent);
            }
        });

        btnProgression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, ProgressionCurve.class);
                intent.putExtra(ProgressionCurve.STUDENT_ID, Properties.getInstance().getUserId());
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, Profile.class);
                startActivity(intent);
            }
        });

        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, StudentList.class);
                startActivity(intent);
            }
        });

        if (Properties.getInstance().getUserType() == 1) {
            btnStudents.setVisibility(View.GONE);
        }
        else {
            btnCourses.setVisibility(View.GONE);
            btnProgression.setVisibility(View.GONE);
        }
    }

    // TODO : on peut eventuellement enlever le if pour les vues qui sont accessible a partir d'un seul type d'utilisateur
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

    public void setAlarm(int h, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,h);
        calendar.set(Calendar.MINUTE,min);
        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmManager.INTERVAL_DAY,pendingIntent);
    }
}
