package com.karbyshev.slot;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.karbyshev.slot.R;

import java.util.ArrayList;

public class SpinnerView extends android.support.v7.widget.AppCompatImageView {

    private static final int[] bitmapResources = new int[] {
            R.mipmap.combination_1,
            R.mipmap.combination_2,
            R.mipmap.combination_3,
            R.mipmap.combination_4,
            R.mipmap.combination_5,
            R.mipmap.combination_6,
            R.mipmap.combination_7
    };

    public Drawable images[];

    public ArrayList<Integer> sequenceToShow = new ArrayList<>();

    private int scrollPosition = 0;

    private int padding = 10;

    public SpinnerView(Context context) {
        super(context);
        init();
    }

    public SpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        images = new Drawable[bitmapResources.length];
        for (int i = 0; i < bitmapResources.length; i++) {
            images[i] = getContext().getResources().getDrawable(bitmapResources[i]);
        }
    }

    public void setSequence(ArrayList<Integer> sequence) {
        sequenceToShow.clear();
        sequenceToShow.addAll(sequence);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);        for (int i = 0; i < sequenceToShow.size(); i++) {
            Rect viewPortRect = getViewPortRect();
            Rect elementRect = getPositionRect(i);
            if (viewPortRect.intersect(elementRect)) {
                elementRect.bottom -= scrollPosition;
                elementRect.top -= scrollPosition;
                Drawable img = images[sequenceToShow.get(i)];
                img.setBounds(elementRect);
                img.draw(canvas);
            }
        }
    }

    private Rect getPositionRect(int position) {
        int cellHeight = getHeight() / 3;
        return new Rect(padding, position * cellHeight + padding, getWidth() - padding, (position + 1) * cellHeight - padding);
    }

    private Rect getViewPortRect() {
        return new Rect(0, scrollPosition, getWidth(), scrollPosition + getHeight());
    }

    public int getMaxScrollPosition() {
        int cellHeight = getHeight() / 3;
        return cellHeight * sequenceToShow.size() - getHeight();
    }

    private void setScrollPosition(int scrollPosition) {
        this.scrollPosition = Math.min(scrollPosition, getMaxScrollPosition());
        invalidate();
    }

    public int getResultedImageIndex() {
        if (sequenceToShow.size() < 2) {
            return -1;
        }
        return sequenceToShow.get(sequenceToShow.size() - 2);
    }
}