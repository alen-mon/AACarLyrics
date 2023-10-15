
package androidx.auto.car.app.AACarLyrics.common.renderer;

import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** A renderer for use on templates with a surface. */
public interface Renderer {
    /**
     * Informs the renderer that it will receive {@link #renderFrame} calls.
     *
     * @param onChangeListener a runnable that will initiate a render pass in the controller
     */
    void enable(@NonNull Runnable onChangeListener);

    /** Informs the renderer that it will no longer receive {@link #renderFrame} calls. */
    void disable();

    /** Request that a frame should be drawn to the supplied canvas. */
    void renderFrame(@NonNull Canvas canvas, @Nullable Rect visibleArea, @Nullable Rect stableArea);
}
