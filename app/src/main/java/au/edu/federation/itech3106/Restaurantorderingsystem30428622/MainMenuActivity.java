package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainMenuActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 1;
    private MediaPlayer mediaPlayer;
    private boolean isPlayingAudio = false;
    private VideoView videoView;
    private boolean isPlayingVideo = false;
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        getSupportActionBar().setTitle("MainMenu");

        // 添加 CircleView 到布局中
        circleView = new CircleView(this);
        ConstraintLayout mainLayout = findViewById(R.id.main_menu_layout);
        mainLayout.addView(circleView);

        // Select Seat Button
        Button btnSelectSeat = findViewById(R.id.btn_select_seat);
        btnSelectSeat.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SeatSelectionActivity.class);
            startActivity(intent);
            finish();
        });

        // Upload Audio Button
        Button btnUploadAudio = findViewById(R.id.btn_upload_audio);
        btnUploadAudio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, PICK_AUDIO_REQUEST);
        });

        // Play/Pause Audio Button
        Button btnPlayPauseAudio = findViewById(R.id.btn_play_pause_audio);
        btnPlayPauseAudio.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlayingAudio) {
                    mediaPlayer.pause();
                    isPlayingAudio = false;
                } else {
                    mediaPlayer.start();
                    isPlayingAudio = true;
                }
            } else {
                Toast.makeText(MainMenuActivity.this, "Please select an audio file first", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置并加载视频资源，但初始隐藏 VideoView
        videoView = findViewById(R.id.video_view);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sample_video"); // 确保文件名匹配
        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.GONE); // 初始隐藏
        videoView.requestFocus();

        // 添加视频准备和错误监听
        videoView.setOnPreparedListener(mp -> {
            Toast.makeText(MainMenuActivity.this, "Video is ready to play", Toast.LENGTH_SHORT).show();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainMenuActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Play/Pause Video Button
        Button btnPlayPauseVideo = findViewById(R.id.btn_play_pause_video);
        btnPlayPauseVideo.setOnClickListener(v -> {
            if (videoView.getVisibility() != View.VISIBLE) {
                // 显示 VideoView 并播放视频
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
                isPlayingVideo = true;
            } else {
                // 视频可见时切换播放和暂停
                if (isPlayingVideo) {
                    videoView.pause();
                    isPlayingVideo = false;
                } else {
                    videoView.start();
                    isPlayingVideo = true;
                }
            }
        });




        // 设置主布局的触摸监听，更新 CircleView
        mainLayout.setOnTouchListener((view, event) -> {
            circleView.updateCircleArray(event);
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            if (audioUri != null) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(this, audioUri);
                isPlayingAudio = false;
                Toast.makeText(this, "The audio file is loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to load the audio file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            navigateToHome();
            return true;
        } else if (id == R.id.menu_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Returning to Home", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
