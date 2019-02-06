package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {
    /**
     * Handles playback of all the sound files
     */
    private MediaPlayer mMediaPlayer;

    /*
    Handles audio focus when playing a sound file
     */
    private AudioManager mAudioManger;

    /**
     * This listener {@link MediaPlayer} gets triggered when the Media Player has completed
     * playing the audio file
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //now that the sound file has finished playing release the media player resources
            releaseMediaPlayer();
        }
    };

    /**
     * This listener gets triggered whenever audio focus changes
     * i.e when we gain or loss focus because of an app or another device
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //The AUDIOFOCUS_LOSS_TRANSIENT case means that we have lost focus for
                        // a short amount of time. The AUDIOFOCUS_LOSS TRNSIENT_CAN_DUCK case
                        // means that our app will continue playing but at a lower volume
                        //we will treat both cases the same way because our app is  playing
                        //short sound files
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //The AUDIOFOCUS_GIAN means that we have re gained audio focus and we
                        //can resume playback
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //The AUDIOFOCUS_LOSS means that we havae lost the audio focus
                        //stop playback and clean  resources
                        releaseMediaPlayer();
                    }

                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        //add the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Create and set up the {@link AudioManger} to request AudioFocus
        mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> family = new ArrayList<>();
        family.add(new Word("father", "әpә",
                R.drawable.family_father, R.raw.family_father));
        family.add(new Word("mother", "әṭa",
                R.drawable.family_mother, R.raw.family_mother));
        family.add(new Word("son", "angsi",
                R.drawable.family_son, R.raw.family_son));
        family.add(new Word("daughter", "tune",
                R.drawable.family_daughter, R.raw.family_daughter));
        family.add(new Word("older brother", "taachi",
                R.drawable.family_older_brother, R.raw.family_older_brother));
        family.add(new Word("younger brother", "chalitti",
                R.drawable.family_younger_brother, R.raw.family_younger_brother));
        family.add(new Word("older sister", "teṭe",
                R.drawable.family_older_sister, R.raw.family_older_sister));
        family.add(new Word("younger sister", "kolliti",
                R.drawable.family_younger_sister, R.raw.family_younger_sister));
        family.add(new Word("grandmother", "ama",
                R.drawable.family_grandmother, R.raw.family_grandmother));
        family.add(new Word("grandfather", "paapa",
                R.drawable.family_grandfather, R.raw.family_grandfather));

        WordAdapter familyAdapter = new WordAdapter(this, family, R.color.category_family);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(familyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // release the media player if it currently exists because we are about to
                //play a different audio file
                releaseMediaPlayer();

                //request audio focus in order to play the audio file
                int result = mAudioManger.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //we have audio focus now

                    mMediaPlayer = MediaPlayer.create(FamilyActivity.this,
                            family.get(position).getAudioResourceId());
                    mMediaPlayer.start();

                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                }
            }
        });
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            //regardless of whether or not we were granted audio focus, abnadon it. This is also
            //unregisters the OnAudioFocusChangeListner so we don't get callbacks anymore.
            mAudioManger.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }
}
