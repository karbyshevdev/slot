package com.karbyshev.slot;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static int TIMEOUT = 1500;

    @BindView(R.id.settingsButton) Button mSettingsButton;
    @BindView(R.id.minusButton) Button mMinusButton;
    @BindView(R.id.plusButton) Button mPlusButton;
    @BindView(R.id.spinButton) Button mSpinButton;

    @BindView(R.id.jackpotTextView) TextView mJackpotTextView;
    @BindView(R.id.myCoinsTextView) TextView mMyCoinsTextView;
    @BindView(R.id.betTextView) TextView mBetTextView;

    @BindView(R.id.spinner1) SpinnerView mSpinner1;
    @BindView(R.id.spinner2) SpinnerView mSpinner2;
    @BindView(R.id.spinner3) SpinnerView mSpinner3;

    private long jackpot = 1000000;
    private long myCoins = 995;
    private long bet = 5;

    private List<SpinnerView> spinnerViews;

    private Dialog winDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        mJackpotTextView.setText(String.valueOf(jackpot));
        mMyCoinsTextView.setText(String.valueOf(myCoins));
        mBetTextView.setText(String.valueOf(bet));

        winDialog = new Dialog(this);

        spinnerViews = Arrays.asList(mSpinner1, mSpinner2, mSpinner3);

        startPosition();
    }

    @OnClick(R.id.minusButton)
    void minusBet() {
        mPlusButton.setClickable(true);
        if (bet > 5) {
            bet -= 5;
            myCoins += 5;
            mMyCoinsTextView.setText(String.valueOf(myCoins));
            mBetTextView.setText(String.valueOf(bet));
        } else {
            mMinusButton.setClickable(false);
        }
    }

    @OnClick(R.id.plusButton)
    void plusBet() {
        mMinusButton.setClickable(true);
        if (bet < 100) {
            mPlusButton.setClickable(true);
            bet += 5;
            myCoins -= 5;
            mMyCoinsTextView.setText(String.valueOf(myCoins));
            mBetTextView.setText(String.valueOf(bet));
        } else {
            mPlusButton.setClickable(false);
        }
    }

    @OnClick(R.id.spinButton)
    void spin() {
        mSpinButton.setClickable(false);
        int maxScroll = 0;

        for (SpinnerView spinnerView : spinnerViews) {
            spinnerView.setSequence(getRandomSequence());
            maxScroll = Math.max(maxScroll, spinnerView.getMaxScrollPosition());
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofInt(mSpinner1, "scrollPosition", 0, maxScroll),
                ObjectAnimator.ofInt(mSpinner2, "scrollPosition", 0, maxScroll),
                ObjectAnimator.ofInt(mSpinner3, "scrollPosition", 0, maxScroll)
        );
        set.setDuration(3000);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                long coefficient = bet / 5;
                int selectedImage1 = mSpinner1.getResultedImageIndex();
                int selectedImage2 = mSpinner2.getResultedImageIndex();
                int selectedImage3 = mSpinner3.getResultedImageIndex();

                myCoins -= bet;

                if (isNoCombinations(selectedImage1, selectedImage2, selectedImage3)) {
                    jackpot += bet;
                }

                if (isTwoHorseshoes(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += 50 * coefficient;
                    jackpot -= bet;
                    showWinDialog(50 * coefficient);
                } else if (isOneHorseshoe(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += 25 * coefficient;
                    jackpot -= bet;
                    showWinDialog(25 * coefficient);
                }

                if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += jackpot;
                    jackpot = 0;
                    showWinJackpot(jackpot);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 5)) {
                    myCoins += 75 * coefficient;
                    showWinDialog(75 *coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 4)) {
                    myCoins += 50 * coefficient;
                    showWinDialog(50 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 3)) {
                    myCoins += 35 * coefficient;
                    showWinDialog(35 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 2)) {
                    myCoins += 25 * coefficient;
                    showWinDialog(25 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 1)) {
                    myCoins += 15 * coefficient;
                    showWinDialog(15 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 0)) {
                    myCoins += 10 * coefficient;
                    showWinDialog(10 * coefficient);
                }

                bet = 5;
                mBetTextView.setText(String.valueOf(bet));
                mJackpotTextView.setText(String.valueOf(jackpot));
                mMyCoinsTextView.setText(String.valueOf(myCoins));
                mPlusButton.setClickable(true);
                mSpinButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private ArrayList<Integer> getRandomSequence() {
        int minLen = 30;
        int maxLen = 50;

        int len = 30 + (int) Math.round(Math.random() * (maxLen - minLen));

        ArrayList<Integer> sequence = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            sequence.add((int) Math.round(Math.random() * 6));
        }
        return sequence;
    }

    private void startPosition() {
        ArrayList<Integer> start = new ArrayList<>();
        start.add(6);
        start.add(5);
        start.add(4);
        for (SpinnerView spinnerView : spinnerViews) {
            spinnerView.setSequence(start);
        }
    }

    private boolean isWinCombinations(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if (selectedImage1 == positionOfImage && selectedImage2 == positionOfImage && selectedImage3 == positionOfImage) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isTwoHorseshoes(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if ((selectedImage1 == positionOfImage && selectedImage2 == positionOfImage) ||
                (selectedImage1 == positionOfImage && selectedImage3 == positionOfImage) ||
                (selectedImage3 == positionOfImage && selectedImage2 == positionOfImage)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOneHorseshoe(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if (selectedImage1 == positionOfImage ||
                selectedImage2 == positionOfImage ||
                selectedImage3 == positionOfImage) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNoCombinations(int selectedImage1, int selectedImage2, int selectedImage3) {
        if (selectedImage1 != selectedImage2 || selectedImage2 != selectedImage3) {
            return true;
        } else {
            return false;
        }
    }

    private void showWinDialog(long coins) {
        winDialog.setContentView(R.layout.popup);
        TextView mPopupTextView = (TextView) winDialog.findViewById(R.id.popupTextView);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupTextView.setText("You win:\n" + coins);
        winDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                winDialog.dismiss();
            }
        }, TIMEOUT);
    }

    private void showWinJackpot(long coins) {
        winDialog.setContentView(R.layout.popup);
        TextView mPopupTextView = (TextView) winDialog.findViewById(R.id.popupTextView);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupTextView.setText("JACKPOT\n" + coins);
        winDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                winDialog.dismiss();
            }
        }, TIMEOUT);
    }
}
