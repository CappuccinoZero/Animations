package pers.lin.linanimations.animations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pers.lin.linanimations.R;
import pers.lin.linanimations.util.MyUtil;

public class WaveView extends AnimationView {
    private Context context;
    private Paint firstPaint;
    private Paint secondPaint;
    private Paint bgPaint;

    private int animationPacent = 0;
    private float oldPacent;
    private float newPacent;
    private List<Wave> waves ;
    private float viewWidth;
    private float viewHeight;

    private float waveWidth;
    private float waveHeight;

    private float waveMoveDistance;
    private WaveAnimation animation;

    private Canvas mCanvas;
    private Bitmap mBitmap;


    private int viewSize;
    private int waveNum;

    private float parcent;//高度百分百
    private int speed;//速度
    private int backgroundTint;//背景颜色
    private int firstColor;//颜色
    private int secondColor;
    private boolean randomWave;//随机高度
    private int container;//容器
    private boolean single;

    public WaveView(Context context) {
        super(context);
        this.context = context;
    }

    public WaveView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context,attrs);
    }

    @Override
    protected void onDrawingAnimation() {
        Canvas canvas = holder.lockCanvas();
        if(canvas!=null) {
            canvas.drawPaint(clearPaint);
            mBitmap = Bitmap.createBitmap((int) viewWidth, (int) viewHeight, Bitmap.Config.ARGB_8888);
            if(asBackground!=Color.TRANSPARENT)
                canvas.drawRect(0,0,viewWidth,viewHeight,asPaint);
            mCanvas = new Canvas(mBitmap);
            switch (container) {
                case 0:
                    mCanvas.drawRect(0, 0, viewWidth, viewHeight, bgPaint);
                    break;
                case 1:
                    mCanvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, bgPaint);
                    break;
                case 2:
                    mCanvas.drawPath(getHeartPath(),bgPaint);
                    break;
            }
            mCanvas.drawPath(getWavePath(true), firstPaint);
            if (!single) mCanvas.drawPath(getWavePath(false), secondPaint);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private Path getHeartPath(){
        Path path = new Path();
        path.reset();
        path.moveTo(viewWidth/2,viewHeight/4);
        path.cubicTo(viewWidth*3/4,0,viewWidth,viewHeight/2,viewWidth/2,viewHeight-viewHeight/5);
        path.moveTo(viewWidth/2,viewHeight/4);
        path.cubicTo(viewWidth/4,0,0,viewHeight/2,viewWidth/2,viewHeight-viewHeight/5);
        path.close();
        return path;
    }

    protected void init(Context context,  AttributeSet attrs){
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        mThread = new Thread(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        speed = array.getInt(R.styleable.WaveView_waveSpeed,400);
        backgroundTint = array.getColor(R.styleable.WaveView_backgroundTint, Color.GREEN);
        firstColor = array.getColor(R.styleable.WaveView_firstColor, Color.GRAY);
        secondColor = array.getColor(R.styleable.WaveView_secondColor, Color.BLUE);
        randomWave = array.getBoolean(R.styleable.WaveView_randomWave,false);
        waveHeight = array.getDimension(R.styleable.WaveView_waveHeight, MyUtil.dip_px(12,context));
        waveWidth = array.getDimension(R.styleable.WaveView_waveWidth,MyUtil.dip_px(90,context));
        container = array.getInt(R.styleable.WaveView_container,0);
        parcent = array.getFloat(R.styleable.WaveView_parcent,0.5f);
        single = array.getBoolean(R.styleable.WaveView_single,false);
        asBackground = array.getColor(R.styleable.WaveView_wave_asBackground,Color.TRANSPARENT);
        array.recycle();

        if(asBackground!=Color.TRANSPARENT){
            setZOrderMediaOverlay(true);
            asPaint = new Paint();
            asPaint.setColor(asBackground);
        }
        waveMoveDistance = 0;
        bgPaint = new Paint();
        bgPaint.setColor(backgroundTint);
        bgPaint.setAntiAlias(true);

        firstPaint = new Paint();
        firstPaint.setColor(firstColor);
        firstPaint.setAntiAlias(true);
        firstPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        secondPaint = new Paint();
        secondPaint.setColor(secondColor);
        secondPaint.setAntiAlias(true);
        secondPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        animation = new WaveAnimation();
        setBackgroundColor(Color.TRANSPARENT);
        if(container==1||container==2){
            setZOrderOnTop(true);
            holder.setFormat(PixelFormat.TRANSLUCENT);
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(View.MeasureSpec.getMode(widthMeasureSpec)== View.MeasureSpec.EXACTLY){
            viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        }else {
            viewWidth = MyUtil.dip_px(300,context);
        }
        if(View.MeasureSpec.getMode(heightMeasureSpec)== View.MeasureSpec.EXACTLY){
            viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        }else{
            viewHeight = MyUtil.dip_px(300,context);
        }
        viewSize = (int)Math.min(viewHeight,viewWidth);
        waveNum = (int)Math.ceil((viewWidth/(double)(waveWidth*2)));
        initWaves();
        updateWaves();
        start();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private Path getWavePath(boolean fh){
        int f = fh?1:-1;
        Path path = new Path();
        path.reset();
        path.moveTo(viewWidth,(1-parcent)*viewHeight);
        path.lineTo(viewWidth,viewHeight);
        path.lineTo(0,viewHeight);
        path.lineTo(-waveMoveDistance,(1-parcent)*viewHeight);
        for(int i=0;i<waveNum*2;i++){
            path.rQuadTo(waveWidth/2,-f*waves.get(2*i).height,waveWidth,0);
            path.rQuadTo(waveWidth/2,f*waves.get(2*i+1).height,waveWidth,0);
        }
        path.close();
        return path;
    }


    private float getRandomHeight(float height){
        if(randomWave){
            Random random = new Random();
            int x = random.nextInt(41)+60;
            float randomF = x/(float)100;
            return height*randomF;
        }
        return height;
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
        while (!stop){
            if(!pause){
                onDrawingAnimation();
                if(waveMoveDistance>waveNum*2*waveWidth)
                    waveMoveDistance = 0;
                waveMoveDistance += speed;
                SystemClock.sleep(16);
            }
        }
    }

    public class WaveAnimation extends Animation{
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if(animationPacent==2){
                parcent =oldPacent + (newPacent-parcent)*interpolatedTime;
            }
        }
    }
    
    class Wave{
        public float width;
        public float height;
        public Wave(float width,float height){
            this.width=width;
            this.height=height;
        }
    }

    public void start(){
        animation.setDuration(400);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(animationPacent==1) animationPacent=2;
                else if(animationPacent==2&&parcent!=newPacent) oldPacent = parcent;
                else if(animationPacent==2&&parcent==newPacent) animationPacent=0;
            }
        });
        this.startAnimation(animation);
    }


    private void initWaves(){
        waves = new ArrayList<>();
        for(int i=0;i<waveNum*2;i++){
            waves.add(new Wave(waveWidth,waveHeight));
            waves.add(new Wave(waveWidth,waveHeight));
        }
    }

    private void updateWaves(){
        for(int i=0;i<waveNum*2;i++){
            float tempHeight = getRandomHeight(waveHeight);
            waves.get(i).height = tempHeight;
            waves.get(i+waveNum*2).height = tempHeight;
        }
    }

    public void setParcent(float parcent){
        if(parcent<=1)
            this.parcent = parcent;
        else
            this.parcent = 1;
    }

    public void addParcent(float num){
        if(parcent+num>=0&&parcent+num<=1){
            if(parcent+num<=1&&parcent+num>=0){
                parcent+=num;
            }
            else if(num>0)
                parcent = 1;
            else
                parcent = 0;
        }
    }

    public void addAnimationParcent(float num){
        if(parcent+num>=0&&parcent+num<=1&&num!=0){
            if(parcent+num<=1&&parcent+num>=0){
                oldPacent = parcent;
                newPacent = parcent+num;
                animationPacent = 1;
            }
            else if(num>0){
                oldPacent = parcent;
                newPacent = 1;
                animationPacent = 1;
            }
            else{
                {
                    oldPacent = parcent;
                    newPacent = 0;
                    animationPacent = 1;
                }
            }
        }
    }

    public void setAnimationParcent(int parcent){
        if(this.parcent!=parcent) {
            if(parcent>=0&&parcent<=1){
                oldPacent = this.parcent;
                newPacent = parcent;
                animationPacent = 1;
            }
        }
    }
}
