package work.yeshu.sidesliplayout;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;


/**
 * Created by yeshu on 16/6/13.
 * 侧滑显示菜单的layout
 */
public class SideSlipLayout extends FrameLayout {
    private static final float OVER_MOVE_SCALE = 0.25f;
    private static final int OVER_SCROLL_DISTANCE = 100;


    private View mContentView;
    private View mMenuView;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;

    private float mLastMotionX;
    private float mLastMotionY;
    private boolean mIsBeingDragged;

    private OnMenuOpenListener mOnMenuOpenListener = null;

    public interface OnMenuOpenListener {
        void onMenuOpen();
    }


    public SideSlipLayout(Context context) {
        this(context, null);
    }

    public SideSlipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideSlipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SideSlipLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SideSlipLayout(View contentView, View menuView) {
        super(contentView.getContext());
        mContentView = contentView;
        mMenuView = menuView;

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.END;
        addView(mMenuView, params);
        addView(mContentView);

        init();
    }

    @Override
    protected void onFinishInflate() {
        mContentView = findViewById(R.id.side_slip_content);
        mMenuView = findViewById(R.id.side_slip_menu);
        super.onFinishInflate();
    }

    private void init() {
        GestureDetector gestureDetector = new GestureDetector(getContext(), new MyGestureListener());

        mScroller = new OverScroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        int minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        int maximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //所有的滑动事件都交给ViewGroup处理,来实现侧滑显示删除按钮的效果
                if (mIsBeingDragged) {
                    //如果当前正在处于滑动状态,则直接拦截
                    return true;
                }
                final float x = ev.getX();
                final float y = ev.getY();

                //向左滑为负,向右滑为正
                int deltaX = (int) (x - mLastMotionX);
                int deltaY = (int) (y - mLastMotionY);

//                if (Math.abs(deltaX) < Math.abs(deltaY) || Math.abs(deltaX) < mTouchSlop) {
//                    break;
//                }
                if (Math.abs(deltaX) < mTouchSlop) {
                    break;
                }

                //确认时滑动事件则拦截,交给ViewGroup处理
                return true;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //处理滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //mGestureDetector.onTouchEvent(event);
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = event.getX();
                final float y = event.getY();

                //向左滑为负,向右滑为正
                int deltaX = (int) (x - mLastMotionX);
                int deltaY = (int) (y - mLastMotionY);

//                if (Math.abs(deltaX) < Math.abs(deltaY) || Math.abs(deltaX) < mTouchSlop) {
//                    break;
//                }

                if (Math.abs(deltaX) < mTouchSlop) {
                    break;
                }

                mIsBeingDragged = true;
                mLastMotionX = x;

                onSideSlip(deltaX);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
//                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                    int velocityX = (int) mVelocityTracker .getXVelocity(mActivePointerId);
                    //处理松手后的UI
                    onSideSlipEnd();
                    mIsBeingDragged = false;
                    recycleVelocityTracker();
                }

                break;
            default:
                break;
        }

        return true;
    }

    private class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    //向左滑为负,向右滑为正
    private void onSideSlip(int distance) {
        //滑到顶端时,让overScroll带点阻力
        if (Math.abs(mContentView.getLeft()) > mMenuView.getWidth()) {
            distance = (int) (distance * OVER_MOVE_SCALE);
        }
        int left = mContentView.getLeft() + distance;

        if (left > 0) {
            left = 0;
        }

        if (Math.abs(left) > mMenuView.getWidth() + OVER_SCROLL_DISTANCE) {
            left = 0 - (mMenuView.getWidth() + OVER_SCROLL_DISTANCE);
        }

        mContentView.layout(left, mContentView.getTop(), left + mContentView.getWidth(), mContentView.getBottom());
    }

    //想加上fling的效果
    private void onSideSlipEnd() {
        int left = mContentView.getLeft();
        int lastLeft;
        if (Math.abs(left) > (mMenuView.getWidth() * 2.0f / 3)) {
            //向左滑动的距离大于删除菜单宽度的2/3,则展开菜单
            lastLeft = 0 - mMenuView.getWidth();
            if (null != mOnMenuOpenListener) {
                mOnMenuOpenListener.onMenuOpen();
            }
        } else {
            lastLeft = 0;
        }

        mScroller.startScroll(mContentView.getLeft(), mContentView.getTop(), lastLeft - mContentView.getLeft(), 0);
        postInvalidate();

        //mContentView.layout(lastLeft, mContentView.getTop(), lastLeft + mContentView.getWidth(), mContentView.getBottom());
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mContentView.layout(mScroller.getCurrX(), mContentView.getTop(), mScroller.getCurrX() + mContentView.getWidth(), mContentView.getBottom());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 判断当前菜单是否出于展开显示状态
     *
     * @return 打开返回true
     */
    public boolean isMenuOpen() {
        return !mIsBeingDragged && mContentView.getLeft() < 0;
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mScroller.startScroll(mContentView.getLeft(), mContentView.getTop(), 0 - mContentView.getLeft(), 0);
        postInvalidate();
    }

    /**
     * 监听菜单滑出事件
     */
    public void setOnMenuOpenListener(OnMenuOpenListener onMenuOpenListener) {
        mOnMenuOpenListener = onMenuOpenListener;
    }
}
