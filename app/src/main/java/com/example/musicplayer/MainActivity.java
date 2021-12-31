package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "External storage permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> files=fetchsongs(Environment.getExternalStorageDirectory());
                        String[] names=new String[files.size()];
                        for(int i=0;i<names.length;i++){
                            names[i]=files.get(i).getName().replace(".mp3","");
                        }
                        System.out.println(Arrays.toString(names));
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,names);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent=new Intent(MainActivity.this,PlaySong.class);
                                String current_song=listView.getItemAtPosition(i).toString();
                                intent.putExtra("SongList",files);
                                intent.putExtra("CurrentSong",current_song);
                                intent.putExtra("Position",i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }
    public ArrayList<File> fetchsongs(File file){
        ArrayList<File> arr=new ArrayList<>();
        File[] songs=file.listFiles();
        if(songs!=null){
            for(File curr:songs){
//                System.out.println(curr.getName()+" out of the loop");
                if(!curr.isHidden() && curr.isDirectory()){
                    arr.addAll(fetchsongs(curr));
                }
                else if(curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".")){
                    arr.add(curr);
//                    System.out.println(curr.getName()+" inside of the loop");
                }
            }
        }
        return arr;
    }
}