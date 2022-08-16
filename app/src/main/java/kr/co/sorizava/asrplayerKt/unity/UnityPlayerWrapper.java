package kr.co.sorizava.asrplayerKt.unity;

import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.unity3d.player.UnityPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UnityPlayerWrapper extends UnityPlayer {
    private final static String TAG = "UnityPlayerWrapper";

    private List<IUnityPlayerWrapperObserver> mObservers = new CopyOnWriteArrayList<IUnityPlayerWrapperObserver>();

    public UnityPlayerWrapper(ContextWrapper context) {
        super(context);

    }

    public void addObserver(IUnityPlayerWrapperObserver ob) {
        mObservers.add(ob);
    }

    public void removeObserver(IUnityPlayerWrapperObserver observer) {
        mObservers.remove(observer);
    }

    public void notifyOnUnityQuit() {
        for (IUnityPlayerWrapperObserver observer : mObservers) {
            observer.onUnityQuit();
        }
    }

    public final void onStart() {
        Log.i(TAG, "onStart()");
        this.requestFocus();
        this.windowFocusChanged(true);
        this.resume();
    }
    public final void onResume() {

        Log.i(TAG, "onResume()");
        this.resume();
    }

    public void onPause() {

        Log.i(TAG, "onPause()");
        this.pause();
    }

    public void onStop() {

        Log.i(TAG, "onStop()");
        this.quit();
    }



    @Override
    public void quit() {
        Log.i(TAG, "quit()");
        super.quit();
        notifyOnUnityQuit();
    }

    public final void resetOverlayUnity() {


        SurfaceView sv = findSurfaceView(this.getView());
        if (sv == null) {
            Log.w(TAG, "No SurfaceView found in Unity view hierarchy.");
        } else {
            Log.i(TAG, "Found SurfaceView " + sv.toString() + ".");

            //sv.getHolder().setFormat(PixelFormat.TRANSPARENT);  // transparent
            // mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);  // transparent + fullscreen
            sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
            sv.setZOrderOnTop(true);
            //sv.setZOrderMediaOverlay(true);
        }

        //setOverlaySurfaceView((ViewGroup)this.getView());
    }

    SurfaceView mSurfaceView;

    /** Walk a view hierarchy looking for the first SurfaceView.
     * Search is depth first.
     * @param v View hierarchy root.
     * @return The first SurfaceView in the hierarchy, or null if none could be found.
     */
    private SurfaceView findSurfaceView(View v)
    {
        if (v == null) return null;
        else if (v instanceof SurfaceView) return (SurfaceView)v;
        else if (v instanceof ViewGroup) {
            int childCount = ((ViewGroup)v).getChildCount();
            for (int i = 0; i < childCount; i++) {
                SurfaceView ret = findSurfaceView(((ViewGroup)v).getChildAt(i));
                if (ret != null) return ret;
            }
        }
        return null;
    }

    private void setOverlaySurfaceView(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                setOverlaySurfaceView((ViewGroup) view);
            else if (view instanceof SurfaceView) {
                mSurfaceView = (SurfaceView) view;
                Log.d(TAG, "findSurfaceView SurfaceView: " + mSurfaceView);
               // mSurfaceView.getHolder().setFormat(-3);
                mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);  // transparent -2
                // mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);  // transparent + fullscreen -3
                mSurfaceView.setZOrderOnTop(true);
            }
        }
    }
}
