package kr.co.sorizava.asrplayerKt.unity;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unity3d.player.IUnityPlayerLifecycleEvents;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.focus.R;
//import kr.co.sorizava.utils.ScreenUtils;


public class UnityView extends FrameLayout implements IUnityPlayerWrapperObserver, IUnityPlayerLifecycleEvents {
    private final static String TAG = "UnityView";

    private UnityPlayerWrapper unityPlayerWrapper;
    private ViewGroup unityContainer;
    private Context context;

    public UnityView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG,"UnityView() start");
        this.context = context;
        Log.i(TAG,this.context.toString());
        Log.i(TAG, this.toString());

        View v = inflate(context, R.layout.unity_view, this);
        unityContainer = (ViewGroup) v.findViewById(R.id.unity_view);
        Log.i(TAG, "UnityView() end");
    }

    public void start(){
        Log.i(TAG,"start()시작");
        Log.i(TAG,this.unityContainer.toString());
        Log.i(TAG,this.getContext().toString());
        Log.i(TAG,this.getContext().getPackageName());
        Log.i(TAG,this.context.toString());

        Log.i(TAG,context.toString());
        Log.i(TAG,context.getPackageName());


        unityPlayerWrapper = new UnityPlayerWrapper((ContextWrapper) context.getApplicationContext());

        unityContainer.addView(unityPlayerWrapper.getView());
        unityPlayerWrapper.addObserver(this);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                unityPlayerWrapper.onStart();
            }
        });
        Log.i(TAG,"start()끝");
        if( true) return;
        //UnityBridgeManager.getInstance().setActivityUnityPlayer(unityPlayerWrapper);
    }

    public void stop(){
        unityContainer.removeView(unityPlayerWrapper.getView());
        unityPlayerWrapper.onStop();
        unityPlayerWrapper.removeObserver(this);
    }

    public void pause(){
        unityPlayerWrapper.onPause();

    }

    public void resume(){
        Log.i(TAG,"resume()시작");
        unityPlayerWrapper.onResume();
        Log.i(TAG,"resume()끝");
    }

    // Low Memor1080y Unity
    public void onLowMemory()
    {
        unityPlayerWrapper.lowMemory();
    }

    // Trim Memory Unity
    public void onTrimMemory(int level)
    {
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            unityPlayerWrapper.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    public void onConfigurationChanged(Configuration newConfig)
    {
        unityPlayerWrapper.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if( unityPlayerWrapper != null) unityPlayerWrapper.windowFocusChanged(hasFocus);
    }

    @Override
    public void onUnityQuit() {

    }

    public void resetOverlay() {
        if(unityPlayerWrapper!=null) {
            Log.d(TAG,"resetOverlay+");
          //  pause();
          //  unityContainer.removeView(unityPlayerWrapper.getView());
            unityPlayerWrapper.resetOverlayUnity();
          //  unityContainer.addView(unityPlayerWrapper.getView());
          //  resume();
            Log.d(TAG,"resetOverlay-");
        }
    }

    /**
     * 새로운 unityMessage
     * jhong
     */

    public void newSendAnimationMessage() {
        Log.i(TAG,"newSendAnimationMessage start()");
        JSONObject object = new JSONObject();

        try {
            object.put("id", "signlanguage_final");
            //object.put("time", 0.2);
            object.put("time", 20);
            object.put("text", "kt+위즈");
            object.put("origin_text", "kt위즈");
            //object.put("text", "김치+볶음밥");
            //object.put("origin_text", "김치볶음밥을");
        } catch (JSONException e) {
            Log.e(TAG, "error parse json", e);
        }
        finally {


        }

        try {
            Log.i(TAG,"newSendAnimationMessage 첫번째 유니티전송");
            kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().newUnitySendMessage("SetRecieveLyrics", object.toString());
        } catch (Exception e) {
            Log.i(TAG, "error newSendAnimationMessage ", e);
        }

    }

    public void sendAnimationSequenceMessage(float animSpeed, float blending, @Nullable String sequence) {
        JSONObject object = new JSONObject();

        try {
            object.put("animSpeed", animSpeed);
            object.put("blendingTime", blending);
            object.put("startDelay", 0);
            object.put("interval", 0);
            object.put("sequence", sequence);
        } catch (JSONException e) {
            Log.e(TAG, "error parse json", e);
        }
        finally {
            kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("SetAnimationSequence", object.toString());
        }
    }

    public void sendAnimationSequenceMessage(float animSpeed, float blending, float startDelay, float interval, @Nullable String sequence) {
        JSONObject object = new JSONObject();

        try {
            object.put("animSpeed", animSpeed);
            object.put("blendingTime", blending);
            object.put("startDelay", startDelay);
            object.put("interval", interval);
            object.put("sequence", sequence);
        } catch (JSONException e) {
            Log.e(TAG, "error parse json", e);
        }
        finally {
            kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("SetAnimationSequence", object.toString());
        }
    }

    public void sendAnimationShowInfoMessage(boolean isShow) {
        if(isShow) {
            kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("ShowInfoText", "Show");
        }
        else {
            kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("ShowInfoText", "Hide");
        }
    }

    public void sendAnimationPlayMessage() {
        kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("PlayAnimation", "Play");
    }

    public void sendAnimationPauseMessage() {
        kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("PlayAnimation", "Pause");
    }

    public void sendAnimationStopMessage() {
        kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage("PlayAnimation", "Stop");
    }

    public void sendMessage(@NonNull String method, @Nullable String parameter) {
        kr.co.sorizava.asrplayerKt.unity.UnityBridgeManager.getInstance().UnitySendMessage(method, parameter);
    }

    public static final int POSITION_TOP_CENTER = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    public static final int POSITION_RIGHT_CENTER = 3;
    public static final int POSITION_BOTTOM_RIGHT = 4;
    public static final int POSITION_BOTTOM_CENTER = 5;
    public static final int POSITION_BOTTOM_LEFT = 6;
    public static final int POSITION_LEFT_CENTER = 7;
    public static final int POSITION_TOP_LEFT = 8;
    public static final int POSITION_CENTER_CENTER = 9;

    /*
    public void setPosition(int position, int resId) {
        Log.d(TAG,"setPosition+: " + position);

        //ConstraintLayout.LayoutParams params = new ConstraintLayout
        //        .LayoutParams(dp2px(context, 200), dp2px(context, 200));



        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout
                .LayoutParams((int)(ScreenUtils.getScreenWidth(context)*0.4f), ScreenUtils.getScreenHeight(context)-dp2px(context, 50));

        layoutParams.topToTop = resId;
        layoutParams.bottomToBottom = resId;
        layoutParams.leftToLeft = resId;
        layoutParams.rightToRight = resId;
        layoutParams.leftMargin = dp2px(context, 10);
        layoutParams.rightMargin = dp2px(context, 10);
        layoutParams.bottomMargin = dp2px(context, 50);
        layoutParams.verticalBias = (float) 1;

        switch (position) {
            case POSITION_RIGHT_CENTER:
                layoutParams.horizontalBias = (float) 1;
                break;
            case POSITION_LEFT_CENTER:
                layoutParams.horizontalBias = (float) 0;
                break;
            case POSITION_CENTER_CENTER:
            default:
                layoutParams.horizontalBias = (float) 0.5;
                break;
        }

        this.setLayoutParams(layoutParams);

        Log.d(TAG,"setPosition-: " + position);
    }
*/

    /*
    public void setPositionOrig(int position, ConstraintLayout layout) {
        Log.d(TAG,"setPosition+: " + position);

        //ConstraintLayout.LayoutParams params = new ConstraintLayout
        //        .LayoutParams(dp2px(context, 200), dp2px(context, 200));



        ConstraintLayout.LayoutParams params = new ConstraintLayout
                .LayoutParams((int)(ScreenUtils.getScreenWidth(context)*0.4f), ScreenUtils.getScreenHeight(context)-dp2px(context, 40));
        switch (position) {
            case POSITION_TOP_CENTER:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_TOP_RIGHT:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_RIGHT_CENTER:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_BOTTOM_CENTER:
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_BOTTOM_LEFT:
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_LEFT_CENTER:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_TOP_LEFT:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_BOTTOM_RIGHT:
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case POSITION_CENTER_CENTER:
            default:
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
        }

        layout.removeView(this);
        this.setLayoutParams(params);
        //params.setMargins(leftMargin, 0, 0,bottomMargin);
        layout.addView(this);

        Log.d(TAG,"setPosition-: " + position);
    }
*/
    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onUnityPlayerUnloaded() {

    }

    @Override
    public void onUnityPlayerQuitted() {

    }
}