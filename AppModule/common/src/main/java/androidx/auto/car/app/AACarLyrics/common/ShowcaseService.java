

package androidx.auto.car.app.AACarLyrics.common;

import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.SessionInfo;
import androidx.car.app.validation.HostValidator;


public final class ShowcaseService extends CarAppService {
    public static final String SHARED_PREF_KEY = "AACarLyricPrefs";
    public static final String PRE_SEED_KEY = "PreSeed";

    public static final String INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP =
            "androidx.auto.car.app.AACarLyrics.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP";

    @NonNull
    @Override
    public Session onCreateSession(@NonNull SessionInfo sessionInfo) {
        return new ShowcaseSession();
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        } else {
            return new HostValidator.Builder(getApplicationContext())
                    .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                    .build();
        }
    }
}
