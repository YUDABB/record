package com.example.project_record;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class AudioAdapter extends RecyclerView.Adapter {

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Uri> dataModels;
    Context context;

    // 리스너 객체 참조를 저장하는 변수
    private OnIconClickListener listener = null;

    /**
     * 커스텀 이벤트 리스너
     * 클릭이벤트를 Adapter에서 구현하기에 제약이 있기 때문에 Activity 에서 실행시키기 위해 커스텀 이벤트 리스너를 생성함.
     * 절차
     * 1.커스텀 리스너 인터페이스 정의
     * 2. 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할변수 추가
     * 3. 아이템 클릭 이벤트 핸들러 메스드에서 리스너 객체 메서드 호출
     * 4. 액티비티에서 커스텀 리스너 객체 생성 및 전달(MainActivity.java 에서 audioAdapter.setOnItemClickListener() )
     */
    // 1.커스텀 리스너 인터페이스 정의


    public interface OnIconClickListener {
        void onItemClick(View view, int position);
    }

    // 2. 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할변수 추가

    public void setOnItemClickListener(OnIconClickListener listener) {
        this.listener = listener;
    }
    public AudioAdapter(Context context, ArrayList<Uri> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }

    //생성자를 통하여 데이터 리스트 context를 받음
    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    //데이터 리스트의 크기를 전달해주어야 함
    //(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }
    //(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;

        String uriName = String.valueOf(dataModels.get(position));
        File file = new File(uriName);

        myViewHolder.audioTitle.setText(file.getName());
    }
    //(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.

   //
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton audioBtn;
        TextView audioTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            audioBtn = itemView.findViewById(R.id.playBtn_itemAudio);
            audioTitle = itemView.findViewById(R.id.audioTitle_itemAudio);

            //3. 아이템 클릭 이벤트 핸들러 메스드에서 리스너 객체 메서드 호출
            //리스너 객체의 메서드 호출.

            audioBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(view, pos) ;
                        }
                    }
                }
            });
        }
    }
}