
package androidx.auto.car.app.AACarLyrics.common;

import static androidx.auto.car.app.AACarLyrics.common.ShowcaseService.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.auto.car.app.AACarLyrics.common.misc.RequestPermissionScreen;
import androidx.auto.car.app.AACarLyrics.common.renderer.SurfaceController;
import androidx.auto.car.app.AACarLyrics.common.templates.LyricsFinderTemplate;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.Session;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


public class ShowcaseSession extends Session implements DefaultLifecycleObserver {
    static final String URI_SCHEME = "app";
    static final String URI_HOST = "AACarLyrics";

    @Nullable
    private SurfaceController mSurfaceController;

    @NonNull
    @Override
    public Screen onCreateScreen(@NonNull Intent intent) {
        Lifecycle lifecycle = getLifecycle();
        lifecycle.addObserver(this);
        mSurfaceController = new SurfaceController(getCarContext(), lifecycle);

        if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
            // Handle the navigation Intent by pushing first the "home" screen onto the stack, then
            // returning the screen that we want to show a template for.
            // Doing this allows the app to go back to the previous screen when the user clicks on a
            // back action.
            getCarContext()
                    .getCarService(ScreenManager.class)
                    .push(new StartScreen(getCarContext()));
            return new LyricsFinderTemplate(getCarContext());
        }

        if (getCarContext().getCallingComponent() != null) {
            // Similarly, if the application has been called "for result", we push a "home"
            // screen onto the stack and return the results demo screen.
            getCarContext()
                    .getCarService(ScreenManager.class)
                    .push(new StartScreen(getCarContext()));
            return new LyricsFinderTemplate(getCarContext());
        }

        // For demo purposes this uses a shared preference setting to store whether we should
        // pre-seed the screen back stack. This allows the app to have a way to go back to the
        // home/start screen making the home/start screen the 0th position.
        // For a real application, it would probably check if it has all the needed system
        // permissions, and if any are missing, it would pre-seed the start screen and return a
        // screen that will send the user to the phone to grant the needed permissions.
        boolean shouldPreSeedBackStack =
                getCarContext()
                        .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                        .getBoolean(ShowcaseService.PRE_SEED_KEY, false);
        if (shouldPreSeedBackStack) {
            // Reset so that we don't require it next time
            getCarContext()
                    .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(ShowcaseService.PRE_SEED_KEY, false)
                    .apply();

            getCarContext()
                    .getCarService(ScreenManager.class)
                    .push(new StartScreen(getCarContext()));
            return new RequestPermissionScreen(getCarContext(), /*preSeedMode*/ true);
        }
        return new StartScreen(getCarContext());
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.i("SHOWCASE AACARLYRIC", "onDestroy");

        // Stop navigation notification service if it is running.
        CarContext context = getCarContext();
        context.stopService(new Intent(context, LyricsFinderTemplate.class));
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        // Process various deeplink intents.

        ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);

        if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
            // If the Intent is to navigate, and we aren't already, push the navigation screen.
            if (screenManager.getTop() instanceof LyricsFinderTemplate) {
                return;
            }
            screenManager.push(new LyricsFinderTemplate(getCarContext()));
            return;
        }

        if (getCarContext().getCallingComponent() != null) {
            // Remove any other instances of the results screen.
            screenManager.popToRoot();
            screenManager.push(new LyricsFinderTemplate(getCarContext()));
            return;
        }

        Uri uri = intent.getData();
        if (uri != null
                && URI_SCHEME.equals(uri.getScheme())
                && URI_HOST.equals(uri.getSchemeSpecificPart())) {

            Screen top = screenManager.getTop();
            // No-op
            if (INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP.equals(uri.getFragment())) {
                if (!(top instanceof LyricsFinderTemplate)) {
                    screenManager.push(new LyricsFinderTemplate(getCarContext()));
                }
            }
        }
    }

    @Override
    public void onCarConfigurationChanged(@NonNull Configuration configuration) {
        if (mSurfaceController != null) {
            mSurfaceController.onCarConfigurationChanged();
        }
    }

}
