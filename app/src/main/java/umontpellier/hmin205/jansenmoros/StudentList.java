package umontpellier.hmin205.jansenmoros;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import umontpellier.hmin205.jansenmoros.POJO.User;

public class StudentList extends Activity {

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
}
