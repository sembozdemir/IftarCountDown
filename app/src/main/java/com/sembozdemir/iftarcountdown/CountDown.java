package com.sembozdemir.iftarcountdown;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by semih on 28.06.2015.
 */
public class CountDown {

    private Context context;
    private TextView timerTextView;
    private MyCountDownTimer timer;
    private MyTime iftarTime;
    private MyTime remainingTime;
    private String timeStamp;

    public CountDown(Context context, TextView timerTextView, MyTime iftarTime) {
        this.timerTextView = timerTextView;
        this.iftarTime = iftarTime;
        this.context = context;
    }

    public void setTimer(MyTime theTime){
        // TODO: try - catch block to check if iftar time > remaining time
        remainingTime = iftarTime.minus(theTime);
        timeStamp = remainingTime.getTimeStamp();
        timer = new MyCountDownTimer(remainingTime.getMilliseconds(), 1000);
        timerTextView.setText(timeStamp);
    }

    public void start(){
        // TODO: try-catch block to check timer is initiliazed
        timer.start();
    }

    private class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO : set textview one more second
            remainingTime.count();
            timerTextView.setText(remainingTime.getTimeStamp());
        }

        @Override
        public void onFinish() {
            timerTextView.setText(context.getString(R.string.iftar_finish_text));
        }
    }
}
