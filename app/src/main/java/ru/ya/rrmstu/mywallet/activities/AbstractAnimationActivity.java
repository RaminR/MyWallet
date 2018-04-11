package ru.ya.rrmstu.mywallet.activities;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;

public abstract class AbstractAnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAnimation();
    }

    protected void finishWithTransition() {
        ActivityCompat.finishAfterTransition(AbstractAnimationActivity.this);
    }

    private void setupAnimation() {

        // при открытии активити
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(300);
        getWindow().setEnterTransition(slide);

        // при закрытии активити
        Slide slide2 = new Slide(Gravity.TOP);
        slide2.setDuration(500);
        getWindow().setExitTransition(slide2);

    }

}

