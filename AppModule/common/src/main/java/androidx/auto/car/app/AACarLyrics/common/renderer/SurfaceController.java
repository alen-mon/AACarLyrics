
package androidx.auto.car.app.AACarLyrics.common.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.AppManager;
import androidx.car.app.CarContext;
import androidx.car.app.SurfaceCallback;
import androidx.car.app.SurfaceContainer;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/** A very simple implementation of a renderer for the app's background surface. */
public final class SurfaceController implements DefaultLifecycleObserver {
    private static final String TAG = "AACarLyrics";

    private final DefaultRenderer mDefaultRenderer;
    @Nullable private Renderer mOverrideRenderer;

    private final CarContext mCarContext;
    @Nullable
    Surface mSurface;
    @Nullable
    Rect mVisibleArea;
    @Nullable
    Rect mStableArea;
    private final SurfaceCallback mSurfaceCallback =
            new SurfaceCallback() {
                @Override
                public void onSurfaceAvailable(@NonNull SurfaceContainer surfaceContainer) {
                    synchronized (SurfaceController.this) {
                        Log.i(TAG, "Surface available " + surfaceContainer);
                        mSurface = surfaceContainer.getSurface();
                        renderFrame();
                    }
                }

                @Override
                public void onVisibleAreaChanged(@NonNull Rect visibleArea) {
                    synchronized (SurfaceController.this) {
                        Log.i(TAG, "Visible area changed " + mSurface + ". stableArea: "
                                + mStableArea + " visibleArea:" + visibleArea);
                        mVisibleArea = visibleArea;
                        renderFrame();
                    }
                }

                @Override
                public void onStableAreaChanged(@NonNull Rect stableArea) {
                    synchronized (SurfaceController.this) {
                        Log.i(TAG, "Stable area changed " + mSurface + ". stableArea: "
                                + mStableArea + " visibleArea:" + mVisibleArea);
                        mStableArea = stableArea;
                        renderFrame();
                    }
                }

                @Override
                public void onSurfaceDestroyed(@NonNull SurfaceContainer surfaceContainer) {
                    synchronized (SurfaceController.this) {
                        mSurface = null;
                    }
                }
            };

    public SurfaceController(@NonNull CarContext carContext, @NonNull Lifecycle lifecycle) {
        mCarContext = carContext;
        mDefaultRenderer = new DefaultRenderer();
        lifecycle.addObserver(this);
    }

    /** Callback called when the car configuration changes. */
    public void onCarConfigurationChanged() {
        renderFrame();
    }

    /** Tells the controller whether to override the default renderer. */
    public void overrideRenderer(@Nullable Renderer renderer) {

        if (mOverrideRenderer == renderer) {
            return;
        }

        if (mOverrideRenderer != null) {
            mOverrideRenderer.disable();
        } else {
            mDefaultRenderer.disable();
        }

        mOverrideRenderer = renderer;

        if (mOverrideRenderer != null) {
            mOverrideRenderer.enable(this::renderFrame);
        } else {
            mDefaultRenderer.enable(this::renderFrame);
        }
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "SurfaceController created");
        mCarContext.getCarService(AppManager.class).setSurfaceCallback(mSurfaceCallback);
    }

    void renderFrame() {
        if (mSurface == null || !mSurface.isValid()) {
            // Surface is not available, or has been destroyed, skip this frame.
            return;
        }
        Canvas canvas = mSurface.lockCanvas(null);

        // Clear the background.
        canvas.drawColor(mCarContext.isDarkMode() ? Color.DKGRAY : Color.LTGRAY);

        if (mOverrideRenderer != null) {
            mOverrideRenderer.renderFrame(canvas, mVisibleArea, mStableArea);
        } else {
            mDefaultRenderer.renderFrame(canvas, mVisibleArea, mStableArea);
        }
        mSurface.unlockCanvasAndPost(canvas);

    }
}
