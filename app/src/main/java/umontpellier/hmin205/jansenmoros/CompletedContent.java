package umontpellier.hmin205.jansenmoros;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import umontpellier.hmin205.jansenmoros.POJO.AllGrades;
import umontpellier.hmin205.jansenmoros.POJO.Completed;

public class CompletedContent extends Activity {

    public static final String USER_ID = "user_id";
    private int user_id;

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    LinearLayout videosLayout, documentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_content);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        user_id = getIntent().getIntExtra(USER_ID,0);
        videosLayout = findViewById(R.id.videosLayout);
        documentsLayout = findViewById(R.id.documentsLayout);

        compositeDisposable.add(myAPI.getCompletedVideos(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Completed>>() {
                    @Override
                    public void accept(List<Completed> cc) throws Exception {

                        for (final Completed subject : cc) {
                            TextView tv = new TextView(CompletedContent.this);
                            tv.setText(subject.getName());
                            tv.setTextAppearance(android.R.style.TextAppearance_Medium);
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);

                            videosLayout.addView(tv);

                            ListView lv = new ListView(CompletedContent.this);

                            if (subject.getViews() != null) {
                                ListAdapter listAdapter = new ArrayAdapter(CompletedContent.this, android.R.layout.simple_list_item_2, android.R.id.text1, subject.getViews()) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                        text1.setText(subject.getViews().get(position).getName());
                                        text2.setText(subject.getViews().get(position).getCompletionTime());
                                        return view;
                                    }
                                };
                                lv.setAdapter(listAdapter);
                                videosLayout.addView(lv);
                            }
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
