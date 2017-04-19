package nizomjon.rxconnection;

/**
 * Created by Nizomjon on 03/04/2017.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Nizomjon on 03/04/2017.
 */

public class StatusView extends RelativeLayout {

    private static final int DISMISS_ON_COMPLETE_DELAY = 1000;

    private Status currentStatus;


    private boolean hideOnComplete;


    private View completeView;
    private View errorView;
    private View loadingview;
    private TextView timerView;
    private TextView retryView;
    private int mStartCount;
    private int mCurrentCount;
    private int mSavedCount;

    private Animation slideOut;
    private Animation slideIn;


    private LayoutInflater inflater;


    private Handler handler;
    private Handler mHandler = new Handler();
    private TimerListener timerListener;

    private Runnable autoDismissOnComplete = new Runnable() {
        @Override
        public void run() {
            exitAnimation(getCurrentView(currentStatus));
            handler.removeCallbacks(autoDismissOnComplete);
            timerListener.connectedToNetwork();
        }
    };

    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentCount > 0) {
                timerView.setText(mCurrentCount + "");
                mCurrentCount--;
            } else {
                timerListener.timeOnChanged(true);
            }
        }
    };

    public void start() {
        mHandler.removeCallbacks(mCountDownRunnable);

        timerView.setText(mStartCount + "");
        timerView.setVisibility(View.VISIBLE);

        mCurrentCount = mStartCount;
        mSavedCount = mStartCount;

        mHandler.post(mCountDownRunnable);
        for (int i = 1; i <= mStartCount; i++) {
            mHandler.postDelayed(mCountDownRunnable, i * 1000);
        }
    }

    public void continueTimer() {
        mHandler.removeCallbacks(mCountDownRunnable);
        timerView.setText(mSavedCount + "");
        timerView.setVisibility(VISIBLE);
        mCurrentCount = mSavedCount * 2;
        mSavedCount = mCurrentCount;
        mHandler.post(mCountDownRunnable);
        for (int i = 1; i <= mSavedCount; i++) {
            mHandler.postDelayed(mCountDownRunnable, i * 1000);
        }
    }

    public void cancel() {
        mHandler.removeCallbacks(mCountDownRunnable);

        timerView.setText("");
        timerView.setVisibility(View.GONE);
    }

    public void setStartCount(int startCount) {
        this.mStartCount = startCount;
    }


    public int getStartCount() {
        return mStartCount;
    }

    public StatusView(Context context) {
        super(context);
        init(context, null, 0, 0, 0);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StatusView(Context context, int completeLayout, int errorLayout, int loadingLayout) {
        super(context);
        init(context, null, completeLayout, errorLayout, loadingLayout);
    }

    public StatusView(Context context, AttributeSet attrs, int completeLayout, int errorLayout, int loadingLayout) {
        super(context, attrs);
        init(context, attrs, completeLayout, errorLayout, loadingLayout);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int completeLayout, int errorLayout, int loadingLayout) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, completeLayout, errorLayout, loadingLayout);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int completeLayout, int errorLayout, int loadingLayout) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, completeLayout, errorLayout, loadingLayout);
    }

    private void init(Context context, AttributeSet attrs, int completeLayout, int errorLayout, int loadingLayout) {

        /**
         * Load initial values
         */
        currentStatus = Status.IDLE;
        hideOnComplete = true;
        slideIn = AnimationUtils.loadAnimation(context, R.anim.sv_slide_in);
        slideOut = AnimationUtils.loadAnimation(context, R.anim.sv_slide_out);
        inflater = LayoutInflater.from(context);
        handler = new Handler();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.statusview);

        /**
         * get status layout ids
         */
        int completeLayoutId = a.getResourceId(R.styleable.statusview_complete, 0);
        int errorLayoutId = a.getResourceId(R.styleable.statusview_error, 0);
        int loadingLayoutId = a.getResourceId(R.styleable.statusview_loading, 0);

        hideOnComplete = a.getBoolean(R.styleable.statusview_dismissOnComplete, true);

        /**
         * inflate layouts
         */
        if (completeLayout == 0) {
            completeView = inflater.inflate(completeLayoutId, null);
            errorView = inflater.inflate(errorLayoutId, null);
            loadingview = inflater.inflate(loadingLayoutId, null);
        } else {
            completeView = inflater.inflate(completeLayout, null);
            errorView = inflater.inflate(errorLayout, null);
            loadingview = inflater.inflate(loadingLayout, null);
        }

        retryView = (TextView) loadingview.findViewById(R.id.retryText);
        timerView = (TextView) loadingview.findViewById(R.id.timerView);
        /**
         * Default layout params
         */
        completeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        errorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loadingview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        /**
         * Add layout to root
         */
        addView(completeView);
        addView(errorView);
        addView(loadingview);

        /**
         * set visibilities of childs
         */
        completeView.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.INVISIBLE);
        loadingview.setVisibility(View.INVISIBLE);

        a.recycle();
    }

    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        retryView.setOnClickListener(onRetryClickListener);
    }

    public void setOnErrorClickListener(OnClickListener onErrorClickListener) {
        errorView.setOnClickListener(onErrorClickListener);
    }

    public void setOnLoadingClickListener(OnClickListener onLoadingClickListener) {
        loadingview.setOnClickListener(onLoadingClickListener);
    }

    public void setOnTimeChangeListener(TimerListener timeChangeListener) {
        timerListener = timeChangeListener;
    }

    public View getErrorView() {
        return errorView;
    }

    public View getCompleteView() {
        return completeView;
    }

    public View getLoadingView() {
        return loadingview;
    }

    public void setStatus(final Status status) {
        if (currentStatus == Status.IDLE) {
            currentStatus = status;
            enterAnimation(getCurrentView(currentStatus));
        } else if (status != Status.IDLE) {
            switchAnimation(getCurrentView(currentStatus), getCurrentView(status));
            currentStatus = status;
        } else {
            exitAnimation(getCurrentView(currentStatus));
        }

        handler.removeCallbacksAndMessages(null);
        if (status == Status.COMPLETE)
            handler.postDelayed(autoDismissOnComplete, DISMISS_ON_COMPLETE_DELAY);
    }

    private View getCurrentView(Status status) {
        if (status == Status.IDLE)
            return null;
        else if (status == Status.COMPLETE)
            return completeView;
        else if (status == Status.ERROR)
            return errorView;
        else if (status == Status.LOADING)
            return loadingview;
        return null;
    }

    private void switchAnimation(final View exitView, final View enterView) {
        clearAnimation();
        exitView.setVisibility(View.VISIBLE);
        exitView.startAnimation(slideOut);
        slideOut.setAnimationListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                slideOut.setAnimationListener(null);
                exitView.setVisibility(View.INVISIBLE);
                enterView.setVisibility(View.VISIBLE);
                enterView.startAnimation(slideIn);
            }
        });
    }

    private void enterAnimation(View enterView) {
        if (enterView == null)
            return;

        enterView.setVisibility(VISIBLE);
        enterView.startAnimation(slideIn);
    }

    private void exitAnimation(final View exitView) {
        if (exitView == null)
            return;

        exitView.startAnimation(slideOut);
        slideOut.setAnimationListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                currentStatus = Status.IDLE;
                exitView.setVisibility(INVISIBLE);
                slideOut.setAnimationListener(null);
            }
        });
    }

    public interface TimerListener {
        void timeOnChanged(boolean isFinished);

        void connectedToNetwork();
    }
}
