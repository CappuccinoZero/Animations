package pers.lin.linanimations.animations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import pers.lin.linanimations.R;
import pers.lin.linanimations.util.MyUtil;

public class FlowRect extends AnimationView
{
    private Context context;
    private float viewWidth;
    private float viewHeight;

    private Paint bgPaint;
    private Paint rectPaint;

    private float rectWidth;
    private float rectCount;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int moveTemp;
    private int moveY;
    private float moveDistance;
    private LinearGradient gradient;

    private float rectHeight;//矩形高度
    private float spaceX;//x方向的间距
    private float spaceY;//y方向的间距
    private float translationX;//y方向的间距
    private int backgroundTint;//背景颜色
    private int startColor;//开始颜色
    private int centerCOlor;//中间颜色
    private int endColor;//结束颜色
    private int speed;//速度
    private boolean isGradient;//是否渐变
    private int radius;//圆角
    public FlowRect(Context context) {
        super(context);
    }

    public FlowRect(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MyUtil.dip_px(300,context);
        viewHeight = MyUtil.dip_px(300,context);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.EXACTLY)
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.EXACTLY)
            viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        rectWidth = (int)(viewWidth*3/4);

        rectCount = (int)Math.ceil(viewHeight/(double)(rectHeight+spaceY))+1;
        moveDistance = rectHeight+spaceY;
        int colors[] = new int[]{startColor,centerCOlor,endColor};
        if (isGradient){
            gradient = new LinearGradient(0,0,rectWidth,0,colors,null, Shader.TileMode.MIRROR);
            rectPaint.setShader(gradient);
        }else
            rectPaint.setColor(startColor);

    }



    private Path onDrawLeftRect(){
        Path path = new Path();
        int startY = (int)-moveY;
        int startX = (int)(-spaceX-translationX);
        for(int i=0;i<rectCount;i++){
            RectF rectF = new RectF(startX,startY,startX+rectWidth,startY+rectHeight);
            path.addRoundRect(rectF,radius,radius,Path.Direction.CW);
            startY+=(spaceY+rectHeight);
        }
        return path;
    }

    private Path onDrawRightRect(){
        Path path = new Path();
        int startY = (int)(-moveY - rectHeight/3);
        int startX = (int)(rectWidth-translationX);
        for(int i=0;i<=rectCount;i++){
            RectF rectF = new RectF(startX,startY,startX+rectWidth,startY+rectHeight);
            path.addRoundRect(rectF,100f,100f,Path.Direction.CW);
            startY+=(spaceY+rectHeight);
        }
        return path;
    }

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
        while(!stop){
            if(!pause){
                onDrawingAnimation();
                moveY += speed;
                if(moveY>=moveDistance){
                    moveY = 0;
                }
            }
            SystemClock.sleep(16);
        }
    }


    @Override
    protected void onDrawingAnimation() {
        Canvas canvas = holder.lockCanvas();
        if(canvas!=null){
            canvas.drawPaint(clearPaint);
            if(asBackground!=Color.TRANSPARENT)
                canvas.drawRect(0,0,viewWidth,viewHeight,asPaint);
            mBitmap = Bitmap.createBitmap((int)viewWidth,(int)viewHeight,Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawRect(0,0,viewWidth,viewHeight,bgPaint);
            mCanvas.drawPath(onDrawLeftRect(),rectPaint);
            mCanvas.drawPath(onDrawRightRect(),rectPaint);
            canvas.drawBitmap(mBitmap,0,0,null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
        mThread = new Thread(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowRect);
        rectHeight = array.getDimension(R.styleable.FlowRect_rectHeight,MyUtil.dip_px(300,context));
        translationX = array.getDimension(R.styleable.FlowRect_translationX,MyUtil.dip_px(0,context));
        spaceX = array.getDimension(R.styleable.FlowRect_spaceX,MyUtil.dip_px(15,context));
        spaceY = array.getDimension(R.styleable.FlowRect_spaceY,MyUtil.dip_px(15,context));
        backgroundTint = array.getColor(R.styleable.FlowRect_backgroundColor, Color.BLUE);
        startColor = array.getColor(R.styleable.FlowRect_startColor,Color.GREEN);
        centerCOlor = array.getColor(R.styleable.FlowRect_centerColor,Color.GREEN);
        endColor = array.getColor(R.styleable.FlowRect_endColor,Color.GREEN);
        speed = array.getInt(R.styleable.FlowRect_FlowSpeed,10);
        isGradient = array.getBoolean(R.styleable.FlowRect_colorGradient,false);
        radius = array.getInt(R.styleable.FlowRect_flowRadius,100);
        asBackground = array.getColor(R.styleable.FlowRect_flow_asBackground,Color.TRANSPARENT);
        array.recycle();
        moveY = 0;

        if(asBackground!=Color.TRANSPARENT){
            asPaint = new Paint();
            asPaint.setColor(asBackground);
            setZOrderMediaOverlay(true);
        }
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(backgroundTint);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }


}
