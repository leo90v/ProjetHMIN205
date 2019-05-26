package umontpellier.hmin205.jansenmoros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.INodeJS;
import umontpellier.hmin205.jansenmoros.ConnectionNodeJS.RESTClient;
import umontpellier.hmin205.jansenmoros.POJO.User;

// TODO : Set the location permission in the emulator in order to see the location

public class Profile extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private String provider;

    TextView tvName, tvLocation, tvYear, tvType;
    ImageView profilePicture;
    ImageButton btnEditPicture;

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Retrofit retrofit = RESTClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        this.tvName = (TextView) findViewById(R.id.nameProfile);
        this.tvYear = (TextView) findViewById(R.id.gradeProfile);
        this.tvType = (TextView) findViewById(R.id.typeProfile);
        this.tvLocation = (TextView) findViewById(R.id.locationProfile);
        this.profilePicture = (ImageView) findViewById(R.id.pictureProfile);
        this.btnEditPicture = (ImageButton) findViewById(R.id.editButton);

        this.tvName.setText(Properties.getInstance().getUsername());
        int type = Properties.getInstance().getUserType();
        String typeText;
        if(type==1) typeText = getResources().getString(R.string.student_type_profile);
        else typeText = getResources().getString(R.string.parent_type_profile);
        this.tvType.setText(getResources().getString(R.string.type_profile) + " " + typeText);

        // TODO : Add the all the children
        if(type==2){
           final RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutProfile);

            compositeDisposable.add(myAPI.getStudents(Properties.getInstance().getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<User>>() {
                        @Override
                        public void accept(final List<User> results) throws Exception {
                            int i = 0;
                            for (User child : results) {
                                final TextView textView = new TextView(Profile.this);
                                textView.setText(child.getName() + " " + child.getLastName());
                                textView.setTextSize(14);

                                int curTextViewId = i + 1;
                                textView.setId(curTextViewId);

                                final RelativeLayout.LayoutParams params =
                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                                if(i!=0){
                                    params.addRule(RelativeLayout.BELOW, i);
                                }
                                else{
                                    params.addRule(RelativeLayout.BELOW, R.id.locationProfile);
                                }

                                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                                params.setMargins(0,20,0,0);
                                textView.setLayoutParams(params);

                                layout.addView(textView, params);
                                i++;
                            }
                        }
                    }));
        }
        else{
            String gradeArray[] = getResources().getStringArray(R.array.currentYear_array);
            this.tvYear.setText(getResources().getString(R.string.grade_profile) + " " + gradeArray[Properties.getInstance().getGrade()-1]);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                onLocationChanged(location);
            }
        }

        this.btnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(Profile.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            tvLocation.setText( address.get(0).getLocality() + ", " + address.get(0).getCountryName());
        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { getResources().getString(R.string.camera_dialog_profile), getResources().getString(R.string.gallery_dialog_profile),getResources().getString(R.string.cancel_dialog_profile) };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.title_dialog_profile));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getResources().getString(R.string.camera_dialog_profile))) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals(getResources().getString(R.string.gallery_dialog_profile))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals(getResources().getString(R.string.cancel_dialog_profile))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        profilePicture.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try{
                            final android.net.Uri imageUri = data.getData();
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                            profilePicture.setImageBitmap(selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
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

            case R.id.logout_nav:
                Intent intent2 = new Intent(this, Login.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
