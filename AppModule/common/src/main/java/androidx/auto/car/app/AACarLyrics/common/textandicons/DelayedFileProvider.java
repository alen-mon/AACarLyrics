/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.auto.car.app.AACarLyrics.common.textandicons;

import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/** A simple file provider that returns files after a random delay. */
public class DelayedFileProvider extends FileProvider {
    private static final long MIN_DELAY_MILLIS = 1000;
    private static final long MAX_DELAY_MILLIS = 3000;

    @Override
    @NonNull
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode)
            throws FileNotFoundException {
        try {
            // Wait for a random period between the minimum and maximum delay.
            Thread.sleep(ThreadLocalRandom.current().nextLong(MIN_DELAY_MILLIS, MAX_DELAY_MILLIS));
        } catch (InterruptedException e) {
            throw new FileNotFoundException(e.getMessage());
        }

        return Objects.requireNonNull(super.openFile(uri, mode));
    }
}
