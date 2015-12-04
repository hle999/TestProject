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

        surfaceHolder=surface.getHolder(); //SurfaceHolder��SurfaceView�Ŀ��ƽӿ�
        surfaceHolder.addCallback(this); //��Ϊ�����ʵ����SurfaceHolder.Callback�ӿڣ����Իص�����ֱ��this
        surfaceHolder.setFixedSize(320, 220); //��ʾ�ķֱ���,������Ϊ��ƵĬ��
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //Surface����

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
        //������surface��������ܳ�ʼ��MediaPlayer,���򲻻���ʾͼ��
        player=new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        //������ʾ��Ƶ��ʾ��SurfaceView��
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
        //Activity����ʱֹͣ���ţ��ͷ���Դ�����������������ʹ�˳�������������Ƶ���ŵ�����
    }
}
