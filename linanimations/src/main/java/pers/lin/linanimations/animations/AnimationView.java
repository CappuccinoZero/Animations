package pers.lin.linanimations.animations;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

abstract public class AnimationView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    protected Context context;
    protected Thread mThread;
    protected SurfaceHolder holder;
    protected boolean pause = false;
    protected boolean stop = false;
    protected Paint clearPaint;
    protected Paint asPaint;
    protected int asBackground;
    public AnimationView(Context context) {
        super(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    abstract protected void onDrawingAnimation();
    public void start() {
        pause = false;
    }

    public void stop() {
        stop = true;
    }

    public void pause() {
        pause = true;
    }

    abstract protected void init(Context context, AttributeSet attrs);

    private void initThis(){
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setAsBackground(int color){
        asBackground = color;
    }
}
