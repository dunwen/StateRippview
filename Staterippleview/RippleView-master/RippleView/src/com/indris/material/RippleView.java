package com.indris.material;

/*
 * Copyright (C) 2013 Muthuramakrishnan <siriscac@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import com.indris.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

@SuppressLint("ClickableViewAccessibility")
public class RippleView extends Button {

	private float mDownX;
	private float mDownY;
	private float mAlphaFactor;
	private float mDensity;
	private float mRadius = 0;
	private float mMaxRadius;

	private int loadingColor;
	private int errorColor;
	private int idieColor;
	private int finishColor;

	private boolean mIsAnimating = false;
	private boolean mHover = true;

	private RadialGradient mRadialGradient;
	private Paint mPaint;
	private ObjectAnimator mRadiusAnimator;
	private State mState;
	private StateManager mStateManager;
	
	private String StringOnButton="";
	
	private int dp(int dp) {
		return (int) (dp * mDensity + 0.5f);
	}

	public RippleView(Context context) {
		this(context, null);
	}

	public RippleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * idie = Õý³£×´Ì¬
	 * */
	public enum State {
		PROCESSING, FINISH, IDIE, ERROR,CANCEL;
	}
	
	private final int MSG_SET_BTN_STRING = 0x10001;
	private final int MSG_CANCELED_SETTING = 0x10002;
	
	private Handler mHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			
			switch(msg.what){
			case MSG_SET_BTN_STRING:
					RippleView.this.setText(StringOnButton);
				break;
			case MSG_CANCELED_SETTING:
					mState = State.IDIE;
					notifyStateChange();
				break;
			}
			
			super.dispatchMessage(msg);
		}
	};
	
	
	public RippleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RippleView);
		loadingColor = a
				.getColor(R.styleable.RippleView_loadingColor, 0x24f092);
		idieColor = a.getColor(R.styleable.RippleView_idieColor, 0xffffff);
		finishColor = a.getColor(R.styleable.RippleView_finishColor, 0x2484f0);
		errorColor = a.getColor(R.styleable.RippleView_errorColor, 0xf02424);

		mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor,
				mAlphaFactor);
		mHover = a.getBoolean(R.styleable.RippleView_hover, mHover);
		a.recycle();
	}

	public void init() {
		mDensity = getContext().getResources().getDisplayMetrics().density;
		mState = State.IDIE;
		mStateManager = new StateManager(mState);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setAlpha(100);
		setRippleColor(Color.BLACK, 1f);
		StringOnButton = this.getText().toString();

	}

	public void setRippleColor(int rippleColor, float alphaFactor) {
		loadingColor = rippleColor;
		mAlphaFactor = alphaFactor;
	}

	public void setHover(boolean enabled) {
		mHover = enabled;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mMaxRadius = (float) Math.sqrt(w * w + h * h);
	}

	private boolean mAnimationIsCancel;
	private Rect mRect;
	float targetRadius;
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		boolean superResult = super.onTouchEvent(event);
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN
				&& this.isEnabled() && mHover) {
			if(mState==State.IDIE){

				mRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
				mAnimationIsCancel = false;
				mDownX = event.getX();
				mDownY = event.getY();

				mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", 0, dp(50))
						.setDuration(400);
				mRadiusAnimator
						.setInterpolator(new AccelerateDecelerateInterpolator());
				mRadiusAnimator.start();
				if (!superResult) {
					return true;
				}
			}
				
			
		} else if (event.getActionMasked() == MotionEvent.ACTION_MOVE
				&& this.isEnabled() && mHover) {
			mDownX = event.getX();
			mDownY = event.getY();

			// Cancel the ripple animation when moved outside
			if (mAnimationIsCancel = !mRect.contains(
					getLeft() + (int) event.getX(),
					getTop() + (int) event.getY())) {
				setRadius(0);
			} else {
				setRadius(dp(50));
			}
			if (!superResult) {
				return true;
			}
		} else if (event.getActionMasked() == MotionEvent.ACTION_UP
				&& !mAnimationIsCancel && this.isEnabled()) {
			if(mState == State.PROCESSING){
				mState = State.CANCEL;
				notifyStateChange();
			}else if(mState == State.ERROR||mState == State.FINISH){
				mState = State.IDIE;
				notifyStateChange();
			}else if(mState == State.IDIE){
				mState = State.PROCESSING;
				notifyStateChange();
				
				mDownX = event.getX();
				mDownY = event.getY();

				final float tempRadius = (float) Math.sqrt(mDownX * mDownX + mDownY
						* mDownY);
				targetRadius = Math.max(tempRadius, mMaxRadius);

				if (mIsAnimating) {
					mRadiusAnimator.cancel();
				}
				mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", dp(50),
						targetRadius);
				mRadiusAnimator.setDuration(500);
				mRadiusAnimator
						.setInterpolator(new AccelerateDecelerateInterpolator());
				mRadiusAnimator.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {
						mIsAnimating = true;
					}

					@Override
					public void onAnimationEnd(Animator animator) {
						// setRadius(0);
						ViewHelper.setAlpha(RippleView.this, 1);
						mIsAnimating = false;
					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});
				mRadiusAnimator.start();
				if (!superResult) {
					return true;
				}
			}
		}
		return superResult;
	}

	public int adjustAlpha(int color, float factor) {
		int alpha = Math.round(Color.alpha(color) * factor);
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		return Color.argb(alpha, red, green, blue);
	}

	public void setRadius(final float radius) {
		mRadius = radius;
		if (mRadius > 0) {
//			mRadialGradient = new RadialGradient(mDownX, mDownY, mRadius,
//					adjustAlpha(loadingColor, mAlphaFactor), loadingColor,
//					Shader.TileMode.MIRROR);
//			mPaint.setShader(mRadialGradient);
			mPaint.setColor(loadingColor);
		}
		invalidate();
	}

	private Path mPath = new Path();

	
	@Override
	protected void onDraw(final Canvas canvas) {

		if (isInEditMode()) {
			return;
		}

		canvas.save(Canvas.CLIP_SAVE_FLAG);

		mPath.reset();
		mPath.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

		canvas.clipPath(mPath);
		canvas.restore();
		
		canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
		super.onDraw(canvas);
	}
	
	
	public void setState(State mState){
		this.mState = mState;
		notifyStateChange();
	}
	
	private void notifyStateChange() {
		Log.d("rippleview","State changed >>>>> "+mState);
		mStateManager.setCurrentState(mState);
		State preState = mStateManager.getPreState();
		if(preState == State.IDIE && mState==State.PROCESSING){
			Idie2ProcessingAnimation();
		}else if(preState ==State.PROCESSING && mState == State.CANCEL){
			Processing2CancelAnimation();
		}else if((preState == State.CANCEL||preState==State.ERROR) && mState == State.IDIE){
			cancel2IdieAnimation();
		}else if(preState == State.PROCESSING && mState == State.ERROR){
			loading2ErrorAnimation();
		}else if(preState==State.PROCESSING && mState == State.FINISH){
			loading2FinishAnimation();
		}else if(preState == State.FINISH && mState == State.IDIE){
			finish2IdieAnimation();
		}

	}

	private void finish2IdieAnimation() {
		ColorChangeAnimation cca = new ColorChangeAnimation(finishColor, idieColor, this);
		cca.start();
		setText(StringOnButton);		
	}

	private void loading2FinishAnimation() {
		final ColorChangeAnimation cca = new ColorChangeAnimation(loadingColor, finishColor, this);
		ObjectAnimator oa = ObjectAnimator.ofFloat(this, "radius", targetRadius,
				0);
		
		oa.setDuration(600);
		
		
		oa.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				cca.start();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		oa.start();
		setText("finished");
	}

	private void loading2ErrorAnimation() {
		final ColorChangeAnimation cca = new ColorChangeAnimation(loadingColor, errorColor, this);
		ObjectAnimator oa = ObjectAnimator.ofFloat(this, "radius", targetRadius,
				0);
		
		oa.setDuration(600);
		
		
		oa.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				cca.start();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		oa.start();
		setText("Error");
		
	}
	
	/** 1 is error 0 is cancel */
	private void cancel2IdieAnimation() {
		ColorChangeAnimation cca = new ColorChangeAnimation(errorColor, idieColor, this);
		cca.start();
		setText(StringOnButton);
	}

	private void Processing2CancelAnimation() {
		final ColorChangeAnimation cca = new ColorChangeAnimation(loadingColor,errorColor,this);
//		cca.start();
		ObjectAnimator oa = ObjectAnimator.ofFloat(this, "radius", targetRadius,
				0);
		
		oa.setDuration(600);
		
		
		oa.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				cca.start();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		oa.start();
		this.setText("canceled");
		mHandler.sendEmptyMessageDelayed(this.MSG_CANCELED_SETTING,2500);
	}

	private void Idie2ProcessingAnimation() {
		this.setText("Loading..");
	}
	
	
	
	public State getState(){
		return mState;
	}
	
}
