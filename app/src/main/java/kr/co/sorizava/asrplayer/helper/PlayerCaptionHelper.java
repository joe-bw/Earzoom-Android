package kr.co.sorizava.asrplayer.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.TextView;

public class PlayerCaptionHelper {

    /**
     * Custom subtitleView font update
     *
     * @param context Context
     * @param subtitleView SubtitleView
     * @param fontPath font path
     */
    public static void setSubtitleViewFont(
            @NonNull Context context,
            @Nullable TextView subtitleView,
            String fontPath) {
        if (subtitleView == null) {
            return;
        }

        Typeface typeface;

        if(fontPath != null && !fontPath.equals("")) {
            typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        else {
            typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        }

        subtitleView.setTypeface(typeface);
    }

    /**
     * Get color with alpha
     *
     * @param color Color
     * @param ratio alpha ratio
     * @return Color
     */
    public static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }
}
