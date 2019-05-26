package umontpellier.hmin205.jansenmoros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import umontpellier.hmin205.jansenmoros.POJO.User;

public class StudentList extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        mainListView = findViewById( R.id.students_listview);

        compositeDisposable.add(myAPI.getStudents(Properties.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(final List<User> results) throws Exception {
                        listAdapter = new ArrayAdapter(StudentList.this, android.R.layout.simple_list_item_2, android.R.id.text1, results) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                text1.setText(results.get(position).getName() + " " + results.get(position).getLastName());
                                text2.setText("Last login : " + results.get(position).getLastLogin());
                                return view;
                            }
                        };
                        mainListView.setAdapter(listAdapter);

                        //ItemClick
                        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(StudentList.this, StudentMenu.class);
                                intent.putExtra(StudentMenu.STUDENT_NAME, results.get(position).getName() + " " + results.get(position).getLastName());
                                intent.putExtra(StudentMenu.STUDENT_ID,results.get(position).getId());
                                startActivity(intent);
                            }
                        });
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
