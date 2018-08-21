package ru.supernacho.overtime.view.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import ru.supernacho.overtime.R;

public class SoftKeyboardCoordinatorLayout extends CoordinatorLayout {
    private boolean isKeyboardShown;
    private Rect rect;
    private Activity activity;
    private KeyboardStateListener listener;

    public SoftKeyboardCoordinatorLayout(Context context) {
        super(context);
        init(context);
    }

    public SoftKeyboardCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SoftKeyboardCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        activity = (Activity) context;
        rect = new Rect();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.activity_tabs_with_drawer, this);
        }
    }

    public void setKeyboardStateListener(KeyboardStateListener listener){
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int proposeHeight = MeasureSpec.getSize(heightMeasureSpec);
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        final int actualHeight = (rect.top + rect.height());

        if (actualHeight < proposeHeight){
            if (!isKeyboardShown) {
                isKeyboardShown = true;
                if (listener != null){
                    listener.onKeyboardShown();
                }
            }
        } else {
            if (isKeyboardShown) {
                isKeyboardShown = false;
                if (listener != null){
                    listener.onKeyboardHidden();
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
