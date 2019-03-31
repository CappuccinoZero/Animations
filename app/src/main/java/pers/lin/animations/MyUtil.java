package pers.lin.animations;

import android.content.Context;

public class MyUtil {

    public static int dip_px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
