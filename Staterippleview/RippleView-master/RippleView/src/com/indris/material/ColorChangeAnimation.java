package com.indris.material;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class ColorChangeAnimation {
	private int fromColor;
	private int toColor;
	private RippleView mView;
	public ColorChangeAnimation(int fromColor, int toColor, RippleView mView) {
		super();
		this.fromColor = fromColor;
		this.toColor = toColor;
		this.mView = mView;
	}
	
	public ColorChangeAnimation() {
	}

	public int getFromColor() {
		return fromColor;
	}

	public void setFromColor(int fromColor) {
		this.fromColor = fromColor;
	}

	public int getToColor() {
		return toColor;
	}

	public void setToColor(int toColor) {
		this.toColor = toColor;
	}

	public RippleView getmView() {
		return mView;
	}

	public void setmView(RippleView mView) {
		this.mView = mView;
	}
	
	
	public void start(){
		ObjectAnimator oa = ObjectAnimator.ofInt(mView, "backgroundColor",fromColor,toColor);
		oa.setEvaluator(new ArgbEvaluator());
		oa.setDuration(500);
		oa.start();
	}

	
	
}
