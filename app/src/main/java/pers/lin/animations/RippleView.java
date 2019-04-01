package pers.lin.animations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class RippleView extends AnimationView {

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {
        onDrawingAnimation();
    }


    class Circle{
        int width;
        int color;
        int alpha;
        boolean isRateOfChange;
        boolean islife;
        Circle(int w,int a){
            width = w;
            isRateOfChange = true;
            color = a;
            alpha = 200;
            islife = true;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int asBackground;//背景颜色
    private int color ;//颜色
    private int speed;//扩散速度
    private int density;//两个圈的间隔
    private boolean isFill;//填充
    private boolean isGradient;//是否渐变
    private int gradient;//渐变深浅
    private int locate;//圆心位置
    private int maxRadio;//最大距离
    private int rateOfChange;
    private int style;
    private boolean firstChange = false;

    private Paint paint;
    private Paint asPaint;

    private float viewHeight;
    private float viewWidth;

    private float circleX;
    private float circleY;

    private List<Circle> ripples;


    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    @Override
    protected void onDrawingAnimation() {
        while(!stop){
            if(!pause){
                Canvas canvas = holder.lockCanvas();
                if(canvas!=null){
                    canvas.drawPaint(clearPaint);
                    if(asBackground!=Color.TRANSPARENT) {
                        Log.d(TAG, "onDrawingAnimation: 测试进入了");
                        canvas.drawRect(0,0,viewWidth,viewHeight,asPaint);
                    }
                    switch (style){
                        case 1:
                            drawInCircle(canvas);
                            break;
                        case 2:
                            drawCircle(canvas);
                            break;
                    }
                    holder.unlockCanvasAndPost(canvas);
                }

            }
        }
    }

    public void setTop(){
        setZOrderMediaOverlay(true);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        this.context =context;
        holder = getHolder();
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
        mThread = new Thread(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        color = array.getColor(R.styleable.RippleView_color, Color.GREEN);
        speed = array.getInt(R.styleable.RippleView_speed,1);
        density = (int)array.getDimension(R.styleable.RippleView_density, MyUtil.dip_px(10,context));
        isFill = array.getBoolean(R.styleable.RippleView_isFill,false);
        isGradient  = array.getBoolean(R.styleable.RippleView_isGradient,true);
        gradient = array.getInt(R.styleable.RippleView_gradient,0);
        locate = array.getInt(R.styleable.RippleView_locate,5);
        rateOfChange = array.getInt(R.styleable.RippleView_rateOfChange,1);
        asBackground = array.getColor(R.styleable.RippleView_asBackground,Color.TRANSPARENT);
        style = array.getInt(R.styleable.RippleView_style,2);
        array.recycle();

        if(asBackground!=Color.TRANSPARENT){
            setZOrderMediaOverlay(true);
            asPaint = new Paint();
            asPaint.setColor(asBackground);
        }
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(MyUtil.dip_px(1,context));
        paint.setStyle(isFill?Paint.Style.FILL:Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);


        ripples = new ArrayList<>();
        Circle circle = new Circle(0,color);
        circle.alpha = 100;
        circle.isRateOfChange = false;
        ripples.add(circle);

    }


    private void drawCircle(Canvas canvas){
        canvas.save();
        for(int i=0;i<ripples.size();i++){
            Circle circle = ripples.get(i);
            paint.setColor(circle.color);
            if(rateOfChange>0&&circle.isRateOfChange){
                float f = circle.width/(float)(maxRadio-maxRadio/3);
                for(int j=1;j<rateOfChange;j++) f*=f;
                if(f>=1) f =1;
                paint.setAlpha((int)(200*f));
            }else paint.setAlpha(circle.alpha);
            canvas.drawCircle(circleX,circleY, circle.width-paint.getStrokeWidth(),paint);
            if(i==0)
                Log.d(TAG, "drawCircle: 数据"+circle.width+"  "+maxRadio+"  "+ripples.size());
            if(circle.width - density>=maxRadio){
                ripples.remove(i);
            }else {
                circle.width += speed;
                if(isGradient){
                    float f = circle.width/(float)(maxRadio-maxRadio/5);
                    if(f>=1)
                        f=1f;
                    int degree = getDarkerColor(f,0.6f,0.2f);
                    circle.color = degree;
                }
            }
        }

        if(ripples.size() > 0 ){
            Circle c = ripples.get(ripples.size()-1);
            if(c.width  > density){
                ripples.add(new Circle(0,color));
            }
        }

        canvas.restore();
    }

    private static final String TAG = "RippleView";
    private void drawInCircle(Canvas canvas){
        canvas.save();
        int radius = (int)Math.max(viewWidth/2,viewHeight/2);
        for(int i=0;i<ripples.size();i++){
            Circle circle = ripples.get(i);
            paint.setColor(circle.color);
            paint.setAlpha(circle.alpha);
            canvas.drawCircle(viewWidth/2,viewHeight/2, circle.width-paint.getStrokeWidth(),paint);
            if(circle.width>radius){
                ripples.remove(i);
            }else {
                if(isGradient){
                    float f = circle.width/(float)radius;
                    if(f>=1)
                        f=1f;
                    circle.alpha=(int)(200-200*f);
                }
                circle.width += speed;
            }
        }


        if(ripples.size() > 0 ){
            Circle c = ripples.get(ripples.size()-1);
            if(c.width  > density){
                ripples.add(new Circle(0,color));
            }
        }
        canvas.restore();
    }


    private int getDarkerColor(float var,float init,float k){
        float[] hsv = new float[3];
        // 0:0-360 色调 1:饱和度 2:明度
        Color.colorToHSV(color, hsv);
        if(gradient==0){//加深
            hsv[1] =init + var*k;
            hsv[2] =init + k - var*k;
        }else {//变浅
            hsv[1] =init + k - var*k;
            hsv[2] =init + var*k;
        }

        return Color.HSVToColor(hsv);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        viewWidth = widthMode == MeasureSpec.EXACTLY ? width:MyUtil.dip_px(120,context);
        viewHeight = heightMode == MeasureSpec.EXACTLY ? height:MyUtil.dip_px(120,context);

        setMeasuredDimension((int)viewWidth,(int)viewHeight);

        switch (locate){
            case 1:
                circleX = viewWidth/4;
                circleY = viewHeight/4;
                maxRadio = getMaxRadio(viewWidth-circleX,viewHeight-circleY);
                break;
            case 2:
                circleX = viewWidth/2;
                circleY = viewHeight/4;
                maxRadio = getMaxRadio(viewWidth-circleX,viewHeight-circleY);
                break;
            case 3:
                circleX = viewWidth/4*3;
                circleY = viewHeight/4;
                maxRadio = getMaxRadio(circleX-viewWidth,viewHeight-circleY);
                break;
            case 4:
                circleX = viewWidth/4;
                circleY = viewHeight/2;
                maxRadio = getMaxRadio(viewWidth-circleX,viewHeight-circleY);
                break;
            case 5:
                circleX = viewWidth/2;
                circleY = viewHeight/2;
                maxRadio = getMaxRadio(viewWidth-circleX,viewHeight-circleY);
                break;
            case 6:
                circleX = viewWidth/4*3;
                circleY = viewHeight/2;
                maxRadio = getMaxRadio(circleX,viewHeight-circleY);
                break;
            case 7:
                circleX = viewWidth/4;
                circleY = viewHeight/4*3;
                maxRadio = getMaxRadio(viewWidth-circleX,circleY);
                break;
            case 8:
                circleX = viewWidth/2;
                circleY = viewHeight/4*3;
                maxRadio = getMaxRadio(viewWidth-circleX,circleY);
                break;
            case 9:
                circleX = viewWidth/4*3;
                circleY = viewHeight/4*3;
                maxRadio = getMaxRadio(circleX,circleY);
                break;
        }

    }

    private int getMaxRadio(float x,float y){
        return (int)Math.sqrt(x*x+y*y);
    }

    public int getBgColor(){
        return getDarkerColor(1f,0.6f,0.2f);
    }
}
