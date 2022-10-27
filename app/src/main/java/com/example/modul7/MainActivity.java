package com.example.modul7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class MainActivity extends AppCompatActivity {
    private ImageView imgslot1,imgslot2,imgslot3;
    private Button play;
    private TextView tvhasil;
    ArrayList<String> arrayUrl= new ArrayList<>();
    SlotTask slotTask1,slotTask2,slotTask3;
    ExecutorService exe1,exe2,exe3;
    boolean myplay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgslot1 = findViewById(R.id.gambar1);
        imgslot2 = findViewById(R.id.gambar2);
        imgslot3 = findViewById(R.id.gambar3);
        play = findViewById(R.id.play);
        tvhasil = findViewById(R.id.tv_hasil);
        ExecutorService exeGetImage = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        slotTask1 = new SlotTask(imgslot1);
        slotTask2 = new SlotTask(imgslot2);
        slotTask3 = new SlotTask(imgslot3);
        exe1 = Executors.newSingleThreadExecutor();
        exe2 = Executors.newSingleThreadExecutor();
        exe3 = Executors.newSingleThreadExecutor();
        exeGetImage.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String txt = loadStringFromNetwork("https://mocki.io/v1/821f1b13-fa9a-43aa-ba9a-9e328df8270e");
                    try {
                        JSONArray jsonArray = new JSONArray(txt);
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            arrayUrl.add(jsonObject.getString("url"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(MainActivity.this).load(arrayUrl.get(0)).into(imgslot1);
                            Glide.with(MainActivity.this).load(arrayUrl.get(1)).into(imgslot2);
                            Glide.with(MainActivity.this).load(arrayUrl.get(2)).into(imgslot3);
                            play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (view.getId()==play.getId()){
                                        if (!myplay==true){
                                            slotTask1._play=true;
                                            slotTask2._play=true;
                                            slotTask3._play=true;
                                            exe1.execute(slotTask1);
                                            exe2.execute(slotTask2);
                                            exe3.execute(slotTask3);
                                            play.setText("STOP");
                                            tvhasil.setText("");
                                            myplay=!myplay;

                                        }
                                        else {
                                            slotTask1._play=false;
                                            slotTask2._play=false;
                                            slotTask3._play=false;
                                            play.setText("PLAY");
                                            if (slotTask1.i==slotTask2.i && slotTask2.i ==slotTask3.i){
                                                tvhasil.setText("Anda Berhasil");
                                            }
                                            else{tvhasil.setText("Coba Lagi");
                                            }
                                            myplay=!myplay;
                                        }}
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String loadStringFromNetwork(String s) throws IOException {
        final URL myUrl = new URL(s);
        final InputStream in = myUrl.openStream();
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1; ) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal mendapatkan text",e);
        }
        final String yourFileAsAString = out.toString();
        return yourFileAsAString;
    }
    class SlotTask implements Runnable{
        ImageView gambar;
        Random _random = new Random();
        public boolean _play = true;
        int i ;
        public SlotTask(ImageView img){
            this.gambar = img;
            i=0;
            _play = true;
        }
        @Override
        public void run() {
            while (_play) {
                i = _random.nextInt(3);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(arrayUrl.get(i)).into(gambar);
                    }
                });
                try {
                    Thread.sleep(_random.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}