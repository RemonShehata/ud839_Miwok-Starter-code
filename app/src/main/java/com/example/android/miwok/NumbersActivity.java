package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {
    /**
     * Handles playback of all the sound files
     */
    private MediaPlayer mMediaPlayer;

    /**
     * Handles AudioFocus when playing a sound
     */
    private AudioManager mAudioManger;

    /**
     * This listener {@link MediaPlayer} gets triggered when the Media Player has completed the
     * audio file
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

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
    protected void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        //adding the up action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Create and setup the {@link AudioManger} to request AudioFocus
        mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<>();
        words.add(new Word("One", "Lutti",
                R.drawable.number_one, R.raw.number_one));
        words.add(new Word("Two", "otiiko",
                R.drawable.number_two, R.raw.number_two));
        words.add(new Word("Three", "tolookosu",
                R.drawable.number_three, R.raw.number_three));
        words.add(new Word("Four", "oyyisa",
                R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five", "massokka",
                R.drawable.number_five, R.raw.number_five));
        words.add(new Word("Six", "temmokka",
                R.drawable.number_six, R.raw.number_six));
        words.add(new Word("Seven", "kenekaku",
                R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("Eight", "kawinta",
                R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("Nine", "wo'e",
                R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("Ten", "na'aacha",
                R.drawable.number_ten, R.raw.number_ten));

        WordAdapter adapter = new WordAdapter(this, words, R.color.category_numbers);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Word word = words.get(position);
                releaseMediaPlayer();

                //request audio focus in order to play the audio file
                int result = mAudioManger.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // we have audio focus now
                    mMediaPlayer = MediaPlayer.create(NumbersActivity.this,
                            word.getAudioResourceId());
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


}
