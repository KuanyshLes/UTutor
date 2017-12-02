package com.support.robigroup.ututor.orazbai;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.root.blablachat.MainActivity;
import com.example.root.blablachat.R;
import com.example.root.blablachat.SocketIO.constants.MessageStates;
import com.example.root.blablachat.custom.holders.message.dialogs.audio.AudioPlayerCallback;
import com.example.root.blablachat.fragments.dialogs.DialogFragment;
import com.example.root.blablachat.models.dialog.DialogMessageNew;
import com.example.root.blablachat.utils.Functions;
import com.stfalcon.chatkit.messages.MessageHolders;


public class CustomOutcomingDialogAudioMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<DialogMessageNew> {
    MainActivity context;
    DialogFragment dialogFragment;

    View itemView;
    LinearLayout bubble;
    ImageView messageState;

    ImageButton playPause;
    SeekBar seekBar;
    TextView timeTv;



    Handler handler=new Handler();


    int duration,stepToUpdate,progress;


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (!(stepToUpdate * seekBar.getProgress() >= duration)) {
                progress+=1;
                seekBar.setProgress(progress);
                timeTv.setText(""+ Functions.milliSecondsToTimer(dialogFragment.mediaPlayer.getCurrentPosition()));
            }
            handler.postDelayed(this,stepToUpdate);

        }
    };


    public CustomOutcomingDialogAudioMessageViewHolder(View itemView) {
        super(itemView);
        context=(MainActivity)itemView.getContext();
        dialogFragment=(DialogFragment)context.getSupportFragmentManager().findFragmentById(R.id.container);
        this.itemView=itemView;
        bubble=(LinearLayout)itemView.findViewById(R.id.bubble);
        playPause=(ImageButton)itemView.findViewById(R.id.btn);
        seekBar=(SeekBar)itemView.findViewById(R.id.progress);
        timeTv=(TextView)itemView.findViewById(R.id.timeTv);
        messageState=(ImageView)itemView.findViewById(R.id.isRead);


    }

    @Override
    public void onBind(final DialogMessageNew message) {
        super.onBind(message);
        switch (message.getState()){
            case MessageStates.WAITING:
                messageState.setImageResource(R.drawable.message_states_waiting);
                break;
            case MessageStates.SENT:
                messageState.setImageResource(R.drawable.message_state_sent);
                break;
            case MessageStates.DELIVERED:
                messageState.setImageResource(R.drawable.message_state_delivered);
                break;
            case MessageStates.READ:
                messageState.setImageResource(R.drawable.message_state_read);
                break;
        }

        time.setText(time.getText());
        time.setTextColor(Color.parseColor("#cccccc"));


        seekBar.setProgress(0);
        seekBar.setMax(100);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playPause.getTag().toString().equals(context.getString(R.string.pause))) {
                    dialogFragment.stopPrevious();
                    download();
                    Log.e("Audio","onStopSoPlay");
                    dialogFragment.setCurrentsCallback(new AudioPlayerCallback() {
                        @Override
                        public void onNewPlay() {
                            Log.e("Audio","onNewPlay");
                            stop();
                        }

                        @Override
                        public void onProgressChanged(long tDur, long cDur) {

                            timeTv.setText("" + Functions.milliSecondsToTimer(cDur));

                            // Updating progress bar
                            int progress = (int) (Functions.getProgressPercentage(cDur, tDur));
                            Log.e("Progress", "" + progress);
                            seekBar.setProgress(progress);
                        }

                        @Override
                        public void onComplete() {
                            stop();

                        }

                        @Override
                        public void onReady(int wholeDuration) {
                            progress = 0;
                            duration = wholeDuration;
                            stepToUpdate = duration / 100;
                            play();
                        }

                    });
                    dialogFragment.playAudio(message.getBody());

                }else if(playPause.getTag().toString().equals(context.getString(R.string.play))) {
                    Log.e("Audio","onPlaySoStop");
                    dialogFragment.stopAudio();
                    stop();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void play(){
        playPause.clearAnimation();
        playPause.setImageResource(R.drawable.player_button_stop);
        playPause.setTag(context.getString(R.string.play));
        handler.postDelayed(mUpdateTimeTask, stepToUpdate);
    }
    private  void stop(){
        playPause.clearAnimation();
        timeTv.setText("");
        playPause.setImageResource(R.drawable.player_button_play);
        seekBar.setProgress(0);
        playPause.setTag(context.getString(R.string.pause));
        handler.removeCallbacks(mUpdateTimeTask);

    }
    private void download(){
        playPause.setTag("");
        playPause.setImageResource(R.drawable.spinner);
        playPause.startAnimation(AnimationUtils.loadAnimation(context,R.anim.rotate));

    }
}
