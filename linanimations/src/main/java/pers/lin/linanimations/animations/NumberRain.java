package pers.lin.linanimations.animations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pers.lin.linanimations.R;
import pers.lin.linanimations.util.MyUtil;

public class NumberRain extends AnimationView {
    public NumberRain(Context context) {
        super(context);
    }

    public NumberRain(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    @Override
    protected void onDrawingAnimation() {
        while(!stop){
            if(!pause){
                Random random = new Random();
                Canvas canvas = holder.lockCanvas();
                if(canvas!=null){
                    canvas.drawPaint(clearPaint);
                    if(asBackground!=Color.TRANSPARENT)
                        canvas.drawRect(0,0,viewWidth,viewHeight,asPaint);
                    canvas.drawRect(0,0,viewWidth,viewHeight,bgPaint);
                    for(int i=0;i<textCount;i++){
                        TextBean number = numbers.get(i);
                        if(number.delay>0){
                            number.delay -= 16;
                            continue;
                        }
                        if(number.y>=textMaxLen+viewHeight){
                            number.y = 0;
                            number.text = getString();
                            number.delay = getNumberdelay();
                            number.speed = getRandomSpeed();
                            number.color = getRandomColor();
                            continue;
                        }
                        int height = spaceY+textSizeY;
                        int start = number.y;
                        if(isRandomColor) paint.setColor(number.color);
                        for(int j=0;j<number.text.length();j++){
                            double f = (j+1)/(double)(number.text.length());
                            f = f*f;
                            int alpha =(int)(255-255*f);
                            paint.setAlpha(alpha);
                            String str = number.text.charAt(j)+"";
                            canvas.drawText(str,number.x,start,paint);
                            start -= height;
                        }
                        number.y += number.speed;
                    }
                    holder.unlockCanvasAndPost(canvas);
                }
                SystemClock.sleep(16);
            }
        }
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mThread = new Thread(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NumberRain);
        textSize = (int)array.getDimension(R.styleable.NumberRain_numberRain_textSize, MyUtil.dip_px(24,context));
        textMin = array.getInteger(R.styleable.NumberRain_numberRain_minCount,48);
        textMax = array.getInteger(R.styleable.NumberRain_numberRain_maxCount,60);
        spaceX = (int)array.getDimension(R.styleable.NumberRain_numberRain_spaceX,MyUtil.dip_px(1,context));
        spaceY = (int)array.getDimension(R.styleable.NumberRain_numberRain_spaceX,MyUtil.dip_px(1,context));
        backgroundTint = array.getColor(R.styleable.NumberRain_numberRain_backgroundTint,Color.BLACK);
        color = array.getColor(R.styleable.NumberRain_numberRain_color,Color.GREEN);
        isRandomColor = array.getBoolean(R.styleable.NumberRain_numberRain_randomColor,false);
        minSpeed = array.getInteger(R.styleable.NumberRain_numberRain_minSpeed,40);
        maxSpeed = array.getInteger(R.styleable.NumberRain_numberRain_maxSpeed,60);
        style = array.getInteger(R.styleable.NumberRain_numberRain_style,2);
        delay = array.getInteger(R.styleable.NumberRain_numberRain_delay,2000);
        asBackground = array.getColor(R.styleable.NumberRain_rain_asBackground,Color.TRANSPARENT);
        array.recycle();

        if(asBackground!=Color.TRANSPARENT){
            asPaint = new Paint();
            asPaint.setColor(asBackground);
            setZOrderMediaOverlay(true);
        }

        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(color);

        bgPaint = new Paint();
        bgPaint.setColor(backgroundTint);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        initColors();
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
        onDrawingAnimation();
    }

    class TextBean{
        String text;
        int speed;
        int x;
        int y;
        int delay;
        int color;
        TextBean(String text,int color){
            this.text = text;
            this.color = color;
            delay = 0;
            x = 0;
            y = 0;
        }
    }

    private List<TextBean> numbers = new ArrayList<>();
    private float viewWidth;
    private float viewHeight;

    private int textCount;//文字数量
    private int textSizeX;
    private int textSizeY;
    private int textMaxLen;
    private Paint paint;
    private Paint bgPaint;
    private Paint clearPaint;
    private int[] colors;

    /**属性**/
    private int spaceX;//x方向间距
    private int spaceY;//y方向间距
    private int textSize;//文字大小
    private int textMin;//最短文字数
    private int textMax;//最长文字数目
    private int color;//文字颜色
    private int minSpeed;//最慢速度
    private int maxSpeed;//最快速度
    private int style;//风格
    private int backgroundTint;//背景颜色
    private int delay;//延迟
    private boolean isRandomColor;//是否随机颜色

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MyUtil.dip_px(200,context);
        viewHeight = MyUtil.dip_px(200,context);
        if(MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.EXACTLY)
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.EXACTLY)
            viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        textCount = (int)viewWidth/(textSizeX+spaceX);
        initTextSize();
        initNumbers();
    }

    private void initTextSize(){
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds("0",0,1,rect);
        textSizeX = rect.width();
        textSizeY = rect.height();
        textMaxLen = (spaceY+textSizeY)*textMax;
    }

    private void initNumbers(){
        int start =spaceY/2;
        int space = spaceY+textSizeX;
        for(int i=0;i<textCount;i++){
            TextBean textBean = new TextBean(getString(),getRandomColor());
            textBean.x = start;
            textBean.speed = getRandomSpeed();
            textBean.delay = getNumberdelay();
            start +=space;
            numbers.add(textBean);
        }
    }

    private int getRandomSpeed(){
        Random random = new Random();
        if(maxSpeed>minSpeed)
            return random.nextInt(maxSpeed-minSpeed)+minSpeed;
        else
            return minSpeed;
    }

    private int getNumberdelay(){
        return new Random().nextInt(delay);
    }

    private String getString(){
        StringBuilder number ;
        Random random = new Random();
        if(style==2)
            number = new StringBuilder("1");
        else if(style==10)
            number = new StringBuilder(random.nextInt(10));
        else
            number = new StringBuilder();
        int len = random.nextInt(textMax-textMin)+textMin-1;
        for(int i=0;i<len;i++){
            int x = random.nextInt(style);
            number.append(x);
        }
        return number.toString();
    }

    private void initColors(){
        colors = new int[]{
                getResources().getColor(R.color.MDRed400_1),
                getResources().getColor(R.color.MDRed400_2),
                getResources().getColor(R.color.MDRed400_3),
                getResources().getColor(R.color.MDRed400_4),
                getResources().getColor(R.color.MDBlue400_1),
                getResources().getColor(R.color.MDBlue400_2),
                getResources().getColor(R.color.MDBlue400_3),
                getResources().getColor(R.color.MDBlue400_4),
                getResources().getColor(R.color.MDGreen400_1),
                getResources().getColor(R.color.MDGreen400_2),
                getResources().getColor(R.color.MDGreen400_3),
                getResources().getColor(R.color.MDGreen400_4),
                getResources().getColor(R.color.MDYellow400_1),
                getResources().getColor(R.color.MDYellow400_2),
                getResources().getColor(R.color.MDYellow400_3),
                getResources().getColor(R.color.MDYellow400_4)};
    }

    private int getRandomColor(){
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
}
