package umontpellier.hmin205.jansenmoros;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import static java.security.AccessController.getContext;

public class VideoPlayer extends AppCompatActivity {

    private AppCompatActivity activity;
    private VideoView vidView;

    public static final String VIDEO_URL = "video_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        activity = this;

        vidView = (VideoView)findViewById(R.id.myVideo);
        String vidAddress = getIntent().getStringExtra(VIDEO_URL);

        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        MediaController vidControl = new MediaController(this) {
            public boolean dispatchKeyEvent(KeyEvent event)
            {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                    ((Activity) getContext()).finish();

                return super.dispatchKeyEvent(event);
            }
        };
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                activity.finish();
            }
        });

        resizeVideo(this.getResources().getConfiguration().orientation);
        vidView.start();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeVideo(newConfig.orientation);
    }

    public void resizeVideo(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, R.id.videoLayout);
            vidView.setLayoutParams(lp);
            vidView.requestLayout();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, R.id.videoLayout);
            vidView.setLayoutParams(lp);
            vidView.requestLayout();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
