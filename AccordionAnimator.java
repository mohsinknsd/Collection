import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * A class to handle expand and collapse animation of the view
 * @author Mohsin Khan
 * @date 2/12/2016
 */
public class AccordionAnimator {

    private boolean expanded = true;

    public void setAnimationOn(final View parent, final View title) {
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded) {
                    collapse(parent, 400, title.getHeight() + 70);
                } else {
                    expand(parent, 300, parent.getHeight());
                }
            }
        });
    }

    public void expand(final View v, final int duration, final int limit) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = limit;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int h = (int)(targetHeight * interpolatedTime);
                if (h > limit) {
                    v.getLayoutParams().height = h;
                }
                //v.getLayoutParams().height = interpolatedTime == 150 ? RelativeLayout.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((duration == 0)
                ? (int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density)
                : duration);
        v.startAnimation(a);
        expanded = true;
    }

    public void collapse(final View v, int duration, final int limit) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                } else{
                    int h = initialHeight - (int)(initialHeight * interpolatedTime);
                    if (h >= limit) {
                        v.getLayoutParams().height = h;
                        v.requestLayout();
                    } else this.cancel();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((duration == 0)
                ? (int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)
                : duration);
        v.startAnimation(a);
        expanded = false;
    }
}
