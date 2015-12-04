package com.sen.test.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Scroller;

/**
 * Created by Senny on 2015/10/12.
 */
public class CustomSwipeView extends RecyclerView {
    private Orientation orientation = Orientation.VERTICAL;
    /**
     * ��ǰ������ListView��position
     */
    private int slidePosition;
    /**
     * ��ָ����X������
     */
    private int downY;
    /**
     * ��ָ����Y������
     */
    private int downX;
    /**
     * ��Ļ���
     */
    private int screenWidth;
    /**
     * ListView��item
     */
    private View itemView;
    /**
     * ������
     */
    private Scroller scroller;
    private static final int SNAP_VELOCITY = 600;
    /**
     * �ٶ�׷�ٶ���
     */
    private VelocityTracker velocityTracker;
    /**
     * �Ƿ���Ӧ������Ĭ��Ϊ����Ӧ
     */
    private boolean isSlide = false;
    /**
     * ��Ϊ���û���������С����
     */
    private int mTouchSlop;
    /**
     * �Ƴ�item��Ļص��ӿ�
     */
    private RemoveListener mRemoveListener;
    /**
     * ����ָʾitem������Ļ�ķ���,�����������,��һ��ö��ֵ�����
     */
    private RemoveDirection removeDirection;

    // ����ɾ�������ö��ֵ
    public enum RemoveDirection {
        RIGHT, LEFT;
    }

    public CustomSwipeView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public CustomSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public CustomSwipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public void init(Context context) {

        if (orientation == Orientation.VERTICAL) {
            screenWidth = ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth();
        } else {
            screenWidth = ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
        }
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * ���û���ɾ���Ļص��ӿ�
     *
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * �ַ��¼�����Ҫ�������жϵ�������Ǹ�item, �Լ�ͨ��postDelayed��������Ӧ���һ����¼�
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                // ����scroller������û�н���������ֱ�ӷ���
                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();

                itemView = findChildViewUnder(downX, downY);
                if (itemView == null) {
                    return super.dispatchTouchEvent(event);
                }

                slidePosition = getChildPosition(itemView);
                // ��Ч��position, �����κδ���
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (orientation == Orientation.VERTICAL) {// ���
                    // ���һ����ľ���������Ļ������룬����û�����»������ʹ���
                    // �һ�ɾ��������ɾ��
                    if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                            || (Math.abs(event.getX() - downX) > mTouchSlop && Math
                            .abs(event.getY() - downY) < mTouchSlop)) {
                        isSlide = true;

                    }
                } else {
                    // ��� ���»����ľ���������Ļ������룬����û�����һ������ʹ����ϻ�ɾ�������»�ɾ��
                    if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                            || (Math.abs(event.getY() - downY) > mTouchSlop && Math
                            .abs(event.getX() - downX) < mTouchSlop)) {
                        isSlide = true;

                    }

                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * ���һ�����getScrollX()���ص������Ե�ľ��룬������View���ԵΪԭ�㵽��ʼ�����ľ��룬�������ұ߻���Ϊ��ֵ
     */
    private void scrollRight() {
        if (orientation == Orientation.VERTICAL) {// ���һ���
            removeDirection = RemoveDirection.RIGHT;
            final int delta = (screenWidth + itemView.getScrollX());
            // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item
            scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                    Math.abs(delta));
            postInvalidate(); // ˢ��itemView
        } else {// ���ϻ���
            removeDirection = RemoveDirection.RIGHT;
            final int delta = (screenWidth + itemView.getScrollY());
            // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item
            scroller.startScroll(0, itemView.getScrollY(), -delta, 0,
                    Math.abs(delta));
            postInvalidate(); // ˢ��itemView
        }
    }

    /**
     * ���󻬶���������������֪�����󻬶�Ϊ��ֵ
     */
    private void scrollLeft() {
        if (orientation == Orientation.VERTICAL) {// ���󻬶�
            removeDirection = RemoveDirection.LEFT;
            final int delta = (screenWidth - itemView.getScrollX());
            // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item
            scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                    Math.abs(delta));
            postInvalidate(); // ˢ��itemView
        } else {
            removeDirection = RemoveDirection.LEFT;
            final int delta = (screenWidth - itemView.getScrollY());
            // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item
            scroller.startScroll(0, itemView.getScrollY(), delta, 0,
                    Math.abs(delta));
            postInvalidate(); // ˢ��itemView
        }
    }

    /**
     * ������ָ����itemView�ľ������ж��ǹ�������ʼλ�û�������������ҹ���
     */
    private void scrollByDistanceX() {
        // �����������ľ��������Ļ�Ķ���֮һ��������ɾ��

        if (orientation == Orientation.VERTICAL) {
            if (itemView.getScrollX() >= screenWidth / 2) {
                scrollLeft();
            } else if (itemView.getScrollX() <= -screenWidth / 2) {
                scrollRight();
            } else {
                // ���ص�ԭʼλ��,Ϊ��͵����������ֱ�ӵ���scrollTo����
                itemView.scrollTo(0, 0);
            }
        } else {
            if (itemView.getScrollY() >= screenWidth / 2) {
                scrollLeft();
            } else if (itemView.getScrollY() <= -screenWidth / 2) {
                scrollRight();
            } else {
                // ���ص�ԭʼλ��,Ϊ��͵����������ֱ�ӵ���scrollTo����
                itemView.scrollTo(0, 0);
            }
        }

    }

    /**
     * ���������϶�ListView item���߼�
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION
                && itemView != null) {
            requestDisallowInterceptTouchEvent(true);
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:

                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent
                            .setAction(MotionEvent.ACTION_CANCEL
                                    | (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);

                    if (orientation == Orientation.VERTICAL) {
                        int deltaX = downX - x;
                        downX = x;
                        // ��ָ�϶�itemView����, deltaX����0���������С��0���ҹ�
                        itemView.scrollBy(deltaX, 0);
                    } else {
                        int deltaY = downY - y;
                        downY = y;
                        itemView.scrollBy(0, deltaY);
                    }

                    return true; // �϶���ʱ��ListView������
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();
                    // ��ָ�뿪��ʱ��Ͳ���Ӧ���ҹ���
                    isSlide = false;
                    break;
            }
        }

        // ����ֱ�ӽ���ListView������onTouchEvent�¼�
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // ����startScroll��ʱ��scroller.computeScrollOffset()����true��
        if (scroller.computeScrollOffset()) {
            // ��ListView item���ݵ�ǰ�Ĺ���ƫ�������й���
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();

            // ��������������ʱ����ûص��ӿ�
            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException(
                            "RemoveListener is null, we should called setRemoveListener()");
                }

                itemView.scrollTo(0, 0);
                mRemoveListener.removeItem(removeDirection, slidePosition);
            }
        }
    }

    /**
     * ����û����ٶȸ�����
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * �Ƴ��û��ٶȸ�����
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * ��ȡX����Ļ����ٶ�,����0���һ�������֮����
     *
     * @return
     */
    private int getScrollVelocity() {
        if (orientation == Orientation.VERTICAL) {
            velocityTracker.computeCurrentVelocity(1000);
            int velocity = (int) velocityTracker.getXVelocity();
            return velocity;
        }else{
            velocityTracker.computeCurrentVelocity(1000);
            int velocity = (int) velocityTracker.getYVelocity();
            return velocity;
        }

    }

    /**
     *
     * ��ListView item������Ļ���ص�����ӿ� ������Ҫ�ڻص�����removeItem()���Ƴ���Item,Ȼ��ˢ��ListView
     *
     *
     */
    public interface RemoveListener {
        public void removeItem(RemoveDirection direction, int position);
    }

    public static enum Orientation {
        HORIZONTAL(0), VERTICAL(1);

        private int value;

        private Orientation(int i) {
            value = i;
        }

        public int value() {
            return value;
        }

        public static Orientation valueOf(int i) {
            switch (i) {
                case 0:
                    return HORIZONTAL;
                case 1:
                    return VERTICAL;
                default:
                    throw new RuntimeException("[0->HORIZONTAL, 1->VERTICAL]");
            }
        }
    }
}
