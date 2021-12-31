package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    TextView textview;
    ImageView prev, play, next;
    ArrayList<File> songlist;
    MediaPlayer mediaPlayer;
    SeekBar seekbar;
    Thread updateseek;
    int position;
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateseek.interrupt();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textview=findViewById(R.id.textview2);
        ImageView prev=findViewById(R.id.prev);
        ImageView play=findViewById(R.id.play);
        ImageView next=findViewById(R.id.next);
        Intent intend=getIntent();
        Bundle bundle=intend.getExtras();
        songlist=(ArrayList)bundle.getParcelableArrayList("SongList");
        String currentsong= getIntent().getStringExtra("CurrentSong");
        textview.setText(currentsong);
        position= intend.getIntExtra("Position",0);
        System.out.println(songlist);
        Uri uri=Uri.parse(songlist.get(position).toString());
        mediaPlayer= MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekbar=findViewById(R.id.seekBar);
        seekbar.setMax(mediaPlayer.getDuration());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateseek=new Thread(){
            public void run(){
                int currpos=0;
                try{
                    while(currpos<mediaPlayer.getDuration()){
                        currpos=mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currpos);
                        sleep(800);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateseek.start();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==0){
                    position=songlist.size()-1;
                }
                else{
                    position--;
                }
                textview.setText(songlist.get(position).getName().replace(".mp3",""));
                Uri uri=Uri.parse(songlist.get(position).toString());
                mediaPlayer= MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                seekbar=findViewById(R.id.seekBar);
                seekbar.setMax(mediaPlayer.getDuration());
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==songlist.size()-1){
                    position=0;
                }
                else{
                    position++;
                }
                textview.setText(songlist.get(position).getName().replace(".mp3",""));
                Uri uri=Uri.parse(songlist.get(position).toString());
                mediaPlayer= MediaPlayer.create(PlaySong.this,uri);
                mediaPlayer.start();
                seekbar=findViewById(R.id.seekBar);
                seekbar.setMax(mediaPlayer.getDuration());
            }
        });
    }
}