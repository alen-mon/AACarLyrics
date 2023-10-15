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
package androidx.auto.car.app.AACarLyrics.common;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.auto.car.app.AACarLyrics.common.templates.LyricsFinderTemplate;

/** The starting screen of the app. */
public final class StartScreen extends Screen {

    public StartScreen(@NonNull CarContext carContext) {
        super(carContext);

    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        ItemList.Builder listBuilder = new ItemList.Builder();

        listBuilder.addItem(
                new Row.Builder()
                        .setTitle(getCarContext().getString(R.string.find_lyrics))
                        .setOnClickListener(
                                () ->
                                        getScreenManager()
                                                .push(new LyricsFinderTemplate(
                                                        getCarContext())))
                        .setBrowsable(true)
                        .build());

        return new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle(getCarContext().getString(R.string.aacarlyics_title) + " ("
                        + getCarContext().getString(R.string.compatibility_prefix,
                        getCarContext().getCarAppApiLevel()) + ")")
                .setHeaderAction(Action.APP_ICON)
                .build();
    }
}