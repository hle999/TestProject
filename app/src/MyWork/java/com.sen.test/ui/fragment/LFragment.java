package com.sen.test.ui.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sen.test.R;

/**
 * Created by Sen on 2015/5/16.
 */
public class LFragment extends BaseFragment implements SurfaceHolder.Callback {

    MediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    Button play,pause,stop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_l, null);
        init(view);
        return view;
    }

    private void init(View root) {
        play = (Button)root.findViewById(R.id.button1);
        pause = (Button)root.findViewById(R.id.button2);
        stop = (Button)root.findViewById(R.id.button3);
        surface = (SurfaceView)root.findViewById(R.id.surface);

        surfaceHolder=surface.getHolder(); //SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); //因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        surfaceHolder.setFixedSize(320, 220); //显示的分辨率,不设置为视频默认
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //Surface类型

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                player.start();
            }});

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                player.pause();
            }});

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                player.stop();
            }});
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        player=new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        //设置显示视频显示在SurfaceView上
        try {
            player.setDataSource("/mnt/sdcard/video.avi");
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
        //Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
    }
}
