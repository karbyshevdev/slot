package com.karbyshev.slot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.settingsButton) Button mSettingsButton;
    @BindView(R.id.minusButton) Button mMinusButton;
    @BindView(R.id.plusButton) Button mPlusButton;
    @BindView(R.id.spinButton) Button mSpinButton;

    @BindView(R.id.jackpotTextView) TextView mJackpotTextView;
    @BindView(R.id.myCoinsTextView) TextView mMyCoinsTextView;
    @BindView(R.id.betTextView) TextView mBetTextView;

    private long jackpot = 1000000;
    private long myCoins = 995;
    private long bet = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        mJackpotTextView.setText(String.valueOf(jackpot));
        mMyCoinsTextView.setText(String.valueOf(myCoins));
        mBetTextView.setText(String.valueOf(bet));
    }

    @OnClick(R.id.minusButton)
    void minusBet(){
        mPlusButton.setClickable(true);
        if (bet > 5){
            bet -= 5;
            myCoins += 5;
            mMyCoinsTextView.setText(String.valueOf(myCoins));
            mBetTextView.setText(String.valueOf(bet));
        } else {
            mMinusButton.setClickable(false);
        }
    }

    @OnClick(R.id.plusButton)
    void plusBet(){
        mMinusButton.setClickable(true);
        if (bet < 100){
            mPlusButton.setClickable(true);
            bet += 5;
            myCoins -= 5;
            mMyCoinsTextView.setText(String.valueOf(myCoins));
            mBetTextView.setText(String.valueOf(bet));
        } else {
            mPlusButton.setClickable(false);
        }
    }
}
