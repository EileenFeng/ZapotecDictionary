package edu.haverford.cs.zapotecdictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class WordOfDayFragment extends Fragment {
    protected static DBHelper mDB;
    protected static int randomOid = -1;

    public WordOfDayFragment() {
        super();
    }

    public void setDB(DBHelper dbHelper){
        this.mDB = dbHelper;
    }

    public void set_curID(int newID){
        this.randomOid = newID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view;
        int oid = mDB.getOidOfRandomRow();
        if (oid == DBHelper.DICTIONARY_DATABASE_QUERY_ERROR) {
           view = inflater.inflate(R.layout.word_day_empty, container, false);
        } else {
            if(randomOid == -1) {
                randomOid = oid;
            }
            view = inflater.inflate(R.layout.word_view, container, false);
            ImageButton imb = view.findViewById(R.id.searchWords_voiceE);
            imb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = new MediaPlayer();
                    try {
                        String audiofn = mDB.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_AUDIO).toString();
                        String audiofp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                + "/dataFolder/tlacochahuaya_content/aud/" + audiofn;
                        mp.setDataSource(audiofp);
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        SharedPreferences sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                        if(sp.getBoolean("#1", false)) {
                            Toast.makeText(getContext(), "Please select corresponding download option in the setting page to enable audio.", Toast.LENGTH_LONG*2).show();
                        } else {
                            Toast.makeText(getContext(), "The audio file has not been provided. ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            TextView word = view.findViewById(R.id.word_WordView);
            TextView wordEnglishDef = view.findViewById(R.id.word_eng_def);
            TextView wordEsDef = view.findViewById(R.id.word_es_def);
            ImageView image = view.findViewById(R.id.word_pic);
            word.setText(mDB.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_LANG).toString());
            wordEnglishDef.setText(mDB.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_GLOSSARY).insert(0, "English: ").toString());
            wordEsDef.setText(mDB.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_ES_GLOSS).insert(0, "Spanish: ").toString());

            // set picture
            String wordOfDay_pic = mDB.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_IMAGE).toString();
            String wordOfDay_pic_fp = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() + "/dataFolder/tlacochahuaya_content/pix/" + wordOfDay_pic;
            File word_pic = new File(wordOfDay_pic_fp);
            if (wordOfDay_pic.length() != 0 && word_pic.exists()) {
                Bitmap bMap = BitmapFactory.decodeFile(wordOfDay_pic_fp);
                image.setImageBitmap(bMap);
            } else {
                Bitmap noPic = BitmapFactory.decodeResource(getResources(), R.drawable.no_img);
                image.setImageBitmap(noPic);
            }
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }
    }
}