/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Limxing
 * @description XListView's header
 */
package com.ogemray.uilib.xlistview;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogemray.uilib.R;


public class XListViewHeader extends LinearLayout {
    public static final int STATE_FRESH_FAILT = 4;
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private LoadView mProgressBar;
    private TextView mHintTextView;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_SUCCESS = 3;
    private TextView xlistview_header_time;

    public XListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        LayoutParams lp = new LayoutParams(
                LayoutParams.FILL_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.xlistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
        mProgressBar = (LoadView) findViewById(R.id.xlistview_header_progressbar);
        xlistview_header_time=(TextView) findViewById(R.id.xlistview_header_time);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

    }

    public void setState(int state) {
        if (state == mState) return;
        if (state == STATE_REFRESHING) {
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                mArrowImageView.setImageResource(R.drawable.xlist_default_ptr_flip);
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText(R.string.xlistview_header_hint_normal);

                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.xlistview_header_hint_ready);
                }
                break;
            case STATE_REFRESHING:
                mProgressBar.startLoad();
                mHintTextView.setText(R.string.xlistview_header_hint_loading);
                break;
            case STATE_SUCCESS:
                mProgressBar.stopLoad();
                mHintTextView.setText(R.string.xlistview_header_hint_success);
                mArrowImageView.setImageResource(R.drawable.xlistview_success);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(UPDATED_AT + mId, System.currentTimeMillis());
                editor.commit();
                break;
            case STATE_FRESH_FAILT:
                mProgressBar.stopLoad();
                mHintTextView.setText(R.string.xlistview_header_hint_failt);
                mArrowImageView.setImageResource(R.drawable.xlistview_error);
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putLong(UPDATED_AT + mId, System.currentTimeMillis());
                editor1.commit();
                break;
            default:
        }
        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getLayoutParams().height;
    }


    /**
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    private SharedPreferences preferences;

    /**
     */
    private long lastUpdateTime;
    /**
     */
    private int mId = -1;

    /**
     */
    private static final String UPDATED_AT = "xlistview_updated_at";

    /**
     */
    public void refreshUpdatedAtValue() {

        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.xlistview_not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.xlistview_time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.xlistview_updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + getContext().getString(R.string.xlist_minute);
            updateAtValue = String.format(getResources().getString(R.string.xlistview_updated_at),

                    value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + getContext().getString(R.string.xlist_hour);
            updateAtValue = String.format(getResources().getString(R.string.xlistview_updated_at),

                    value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + getContext().getString(R.string.xlist_day);
            updateAtValue = String.format(getResources().getString(R.string.xlistview_updated_at),

                    value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + getContext().getString(R.string.xlist_geyue);
            updateAtValue = String.format(getResources().getString(R.string.xlistview_updated_at),

                    value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + getContext().getString(R.string.xlist_year);
            updateAtValue = String.format(getResources().getString(R.string.xlistview_updated_at),

                    value);
        }
        xlistview_header_time.setText(updateAtValue);
    }

    public TextView getmHintTextView() {
        return mHintTextView;
    }
}
