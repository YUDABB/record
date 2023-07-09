package com.example.project_record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /**
     * xml 변수
     */
    ImageButton audioRecordImageBtn; // 녹음 버튼
    TextView audioRecordText;   //녹음중 텍스트
    ImageButton audioDelete;

    /**
     * 오디오 파일 관련 변수
     */
    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 생성 파일 이름
    private boolean isRecording = false;    // 현재 녹음 상태를 확인하기 위함.
    private Uri audioUri = null; // 오디오 파일 uri

    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageView playIcon;

    /**
     * 리사이클러뷰
     */
    private AudioAdapter audioAdapter;
    private ArrayList<Uri> audioList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("음성녹음기");

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn1:
                Intent intent = new Intent(MainActivity.this, secondActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }


    // xml 변수 초기화
    // 리사이클러뷰 생성 및 클릭 이벤트
    private void init() {
        audioRecordImageBtn = findViewById(R.id.audioRecordImageBtn);
        audioRecordText = findViewById(R.id.audioRecordText);
        audioDelete = findViewById(R.id.playBtn_itemAudio1);

        audioRecordImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording) {
                    isRecording = false;
                    audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record1, null));
                    audioRecordText.setText("녹음 시작");
                    stopRecording();
                } else {
                    if(checkAudioPermission()) {
                        isRecording = true;
                        audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_red, null));
                        audioRecordText.setText("녹음 중");
                        startRecording();
                    }
                }
            }
        });


        RecyclerView audioRecyclerView = findViewById(R.id.recyclerview);

        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);

//액티비티에서 커스텀 리스너 객체 생성 및 전달(MainActivity.java 에서 audioAdapter.setOnItemClickListener() )

        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String uriName = String.valueOf(audioList.get(position));
                File file = new File(uriName);
                if(isPlaying){
                    if(playIcon == (ImageView)view){
                        stopAudio(); }
                    else {
                        stopAudio();
                        playIcon = (ImageView)view;
                        playAudio(file); } }
                else {
                    playIcon = (ImageView)view;
                    playAudio(file);
                }
            }
        });
    }

    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    private void startRecording() {
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = recordPath + "/" +"녹음됨_" + timeStamp + "_"+"audio.mp4";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }


    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        audioUri = Uri.parse(audioFileName);
        audioList.add(audioUri);
        audioAdapter.notifyDataSetChanged();

    }


    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace(); }
        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_pause, null));
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        }); }


    private void stopAudio() {
        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play, null));
        isPlaying = false;
        mediaPlayer.stop();
    }


}