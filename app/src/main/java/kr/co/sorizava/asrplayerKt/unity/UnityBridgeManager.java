package kr.co.sorizava.asrplayerKt.unity;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unity3d.player.UnityPlayer;

import java.util.HashMap;


/***
 * UnityBridgeManager Class
 * @author Choi
 * @DATE 2020-02-14
 ***/
public class UnityBridgeManager {
    private static final String TAG = "UnityBrManager";

    private static final String AVATAR_NAME = "LyricsManager"; // "ChracterJi_neutral_full2nd";  //"AGIA_anime_girl"; //"Female"; //"AGIAFree_sample";//"girl";

    private volatile static UnityBridgeManager mInstance;

    private UnityBridgeManager(){}

    /**
     * UnityBridgeManager Instance
     * @return
     */
    public static UnityBridgeManager getInstance(){
        if (null == mInstance){
            synchronized (UnityBridgeManager.class){
                if (null == mInstance) {
                    mInstance = new UnityBridgeManager();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    public Activity getActivity() {
        return UnityPlayer.currentActivity;
    }

    public void runSafelyOnUiThread(@NonNull final Runnable runnable) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception running command on UI thread: " + e.getMessage());
                        //LogUtils.e( "Exception running command on UI thread: " + e.getMessage());
                    }
                }
            });
        }
    }

    /**************************** Android에서 Unity로 메시지 *****************************/

    public void UnitySendMessage(@NonNull String method) {
        UnityPlayer.UnitySendMessage(AVATAR_NAME, method, "");
    }

    public void UnitySendMessage(@NonNull String method, @Nullable HashMap paramHashMap) {

        String strParam = "";
        /*
        if (paramHashMap != null) {
            strParam = new Gson().toJson(paramHashMap);
        }
        */
/*
        Log.d("UnityBridgeManager"," AVATAR_NAME: " + AVATAR_NAME
                + " method: " + method
                + " strParam: " + strParam);

 */
        UnityPlayer.UnitySendMessage(AVATAR_NAME, method, strParam);
    }

    public void newUnitySendMessage(@NonNull String method, @Nullable String parameter) {
        if (parameter == null) {
            parameter = "";
        }

        try {
            Log.i(TAG,"newUnitySendMessage 첫번째 유니티전송");
            //UnityPlayer.UnitySendMessage(AVATAR_NAME, method, parameter);
            UnityPlayer.UnitySendMessage("LyricsManager", method, parameter);

        } catch (Exception e) {
            Log.i(TAG, "error newUnitySendMessage ", e);
        }


    }

    public void UnitySendMessage(@NonNull String method, @Nullable String parameter) {
        if (parameter == null) {
            parameter = "";
        }
        /*
        Log.d("UnityBridgeManager"," AVATAR_NAME: " + AVATAR_NAME
                + " method: " + method
                + " parameter: " + parameter);

         */
        UnityPlayer.UnitySendMessage(AVATAR_NAME, method, parameter);
    }

    public void UnitySendMessage(@NonNull String avatar, @NonNull String method, @Nullable String parameter) {
        if (parameter == null) {
            parameter = "";
        }
        /*
        Log.d("UnityBridgeManager"," AVATAR_NAME: " + avatar
                + " method: " + method
                + " parameter: " + parameter);

         */
        UnityPlayer.UnitySendMessage(avatar, method, parameter);

    }

    /**************************** Unity 에서 Android로 보내는 메시지*****************************/
    /**
     * 애니메이션 완료 후 콜백 알림
     */
    public void onPlayFinishCallback() {
        Log.i(TAG, "onPlayFinishCallback Callback");
        Log.d(TAG, "onPlayFinishCallback Callback");
        //LogUtils.d( "onPlayFinishCallback Callback");
        //CompanionControl.getInstance().avatarPlayEnd();
    }
}
