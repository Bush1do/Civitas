package com.here.name.website.Civitas.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Charles on 12/27/2017.
 */

public class Heart {
    private static final String TAG = "Heart";

    //final int DURATION_MS=300;
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR=new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR=new AccelerateInterpolator();

    public ImageView heartOutline, heartFill;

    public Heart(ImageView heartOutline, ImageView heartFill) {
        this.heartOutline = heartOutline;
        this.heartFill = heartFill;
    }

  //  public enum HEART_COLOR{HEART_FILL,HEART_OUTLINE};
  /*
    public void setState(HEART_COLOR color) { 
        Log.d(TAG, "setState: to " + color); 
        AnimatorSet animationSet = new AnimatorSet(); 
        if (color == HEART_COLOR.HEART_OUTLINE) { 
            Log.d(TAG, "setState: toggling red heart OFF"); 
            heartFill.setScaleX(1f); 
            heartFill.setScaleY(1f); 
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartFill, "scaleY", 1f, 0.1f); 
            scaleDownY.setDuration(DURATION_MS); 
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR); 
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartFill, "scaleX", 1f, 0.1f); 
            scaleDownX.setDuration(DURATION_MS); 
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR); 
            scaleDownX.addListener(new Animator.AnimatorListener() { 
                @Override public void onAnimationStart(Animator animator) { 
                    
                } @Override public void onAnimationEnd(Animator animator) { 
                    heartFill.setVisibility(View.GONE);
                    heartOutline.setVisibility(View.VISIBLE);
                } @Override public void onAnimationCancel(Animator animator) { 
                    
                } @Override public void onAnimationRepeat(Animator animator) { 
                    
                } 
            }); 
            animationSet.playTogether(scaleDownY, scaleDownX);
        } else if (color == HEART_COLOR.HEART_FILL) { 
            Log.d(TAG, "setState: setting red heart ON");
            heartFill.setScaleX(0.1f); 
            heartFill.setScaleY(0.1f); 
            heartFill.setVisibility(View.VISIBLE);
            heartOutline.setVisibility(View.GONE);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartFill, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(DURATION_MS); 
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartFill, "scaleX", 0.1f, 1f); 
            scaleDownX.setDuration(DURATION_MS); 
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);
            scaleDownX.addListener(new Animator.AnimatorListener() { 
                @Override public void onAnimationStart(Animator animator) {
                    
                } @Override public void onAnimationEnd(Animator animator) {
                    heartFill.setVisibility(View.VISIBLE);
                    heartOutline.setVisibility(View.GONE);
                } @Override public void onAnimationCancel(Animator animator) { 

                } @Override public void onAnimationRepeat(Animator animator) {
                    
                } 
            });
            animationSet.playTogether(scaleDownY, scaleDownX); 
        } 
        animationSet.start(); 
    } 
    
    public void toggleLike() {
        Log.d(TAG, "toggleLike: toggling heart"); 
        if (heartFill.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "toggleLike: toggling red heart OFF"); 
            setState(HEART_COLOR.HEART_OUTLINE);
        } else if (heartFill.getVisibility() == View.GONE) {
            Log.d(TAG, "toggleLike: toggling red heart ON"); 
            setState(HEART_COLOR.HEART_OUTLINE); }
    } */
    

    public void toggleLike(){
        Log.d(TAG, "toggleLike: Toggling heart.");

        AnimatorSet animatorSet= new AnimatorSet();

        if(heartFill.getVisibility()== View.VISIBLE){
            Log.d(TAG, "toggleLike: Toggling red heart off.");
            heartFill.setScaleX(0.1f);
            heartFill.setScaleY(0.1f);

            ObjectAnimator scaleDownY= ObjectAnimator.ofFloat(heartFill,"scaleY",1f,0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX= ObjectAnimator.ofFloat(heartFill,"scaleX",1f,0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            heartFill.setVisibility(View.GONE);
            heartOutline.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownY,scaleDownX);
        }
        else if(heartFill.getVisibility()== View.GONE){
            Log.d(TAG, "toggleLike: Toggling red heart on.");
            heartFill.setScaleX(0.1f);
            heartFill.setScaleY(0.1f);

            ObjectAnimator scaleDownY= ObjectAnimator.ofFloat(heartFill,"scaleY",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX= ObjectAnimator.ofFloat(heartFill,"scaleX",0.1f,1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);

            heartFill.setVisibility(View.VISIBLE);
            heartOutline.setVisibility(View.GONE);

            animatorSet.playTogether(scaleDownY,scaleDownX);
        }

        animatorSet.start();
    }
}
