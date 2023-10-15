package androidx.auto.car.app.AACarLyrics.common.templates;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.auto.car.app.AACarLyrics.common.templates.LyricsFinderTemplate.StringListener;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

public class MyListScreen extends Screen implements StringListener {
    private ItemList.Builder listBuilder;
    private SharedPreferences sharedPreferences;

    public MyListScreen(@NonNull CarContext carContext) {
        super(carContext);

        listBuilder = new ItemList.Builder();

        // Retrieve added items from SharedPreferences and add them to the listBuilder
        sharedPreferences = carContext.getSharedPreferences("MyListScreen", Context.MODE_PRIVATE);

        // Load the last two long messages
        loadLastTwoLongMessages();


    }

    private void loadLastTwoLongMessages() {
        // Load and display the last two long messages
        for (int i = 0; i < 2; i++) {
            String key = "longMessage_" + i;
            String longMessage = sharedPreferences.getString(key, null);

            if (longMessage != null) {
                // Also load the associated title and artist
                String titleKey = "title_" + i;
                String artistKey = "artist_" + i;
                String title = sharedPreferences.getString(titleKey, "");
                String artist = sharedPreferences.getString(artistKey, "");

                addLongMessageToList(title, artist, longMessage);
            }
        }
    }

    private void addLongMessageToList(String title, String artist, String longMessage) {
        String trimmedMessage = longMessage.substring(0, Math.min(longMessage.length(), 100));

        listBuilder.addItem(
                new Row.Builder()
                        .setTitle(title  + " || " + artist)
                        .addText(trimmedMessage)
                        .setOnClickListener(() -> showLongMessage(title, artist, longMessage))
                        .build()
        );
    }
    private void showLongMessage(String title, String artist, String longMessage) {
        // Create a LongMessageScreen to display the long message
        LongMessageScreen longMessageScreen = new LongMessageScreen(getCarContext(), longMessage, title + " || " + artist);

        // Push the LongMessageScreen to display the long message
        getScreenManager().push(longMessageScreen);
    }


    private void saveLongMessage(String title, String artist, String longMessage) {
        // Save the long message, title, and artist in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Shift the data for each song in a circular manner
        for (int i = 1; i >= 0; i--) {
            String currentKey = "longMessage_" + i;
            String currentTitleKey = "title_" + i;
            String currentArtistKey = "artist_" + i;

            int nextIndex = (i + 1) % 2; // Circular shift between 0 and 1

            String nextKey = "longMessage_" + nextIndex;
            String nextTitleKey = "title_" + nextIndex;
            String nextArtistKey = "artist_" + nextIndex;

            String prevLongMessage = sharedPreferences.getString(currentKey, null);
            String prevTitle = sharedPreferences.getString(currentTitleKey, "");
            String prevArtist = sharedPreferences.getString(currentArtistKey, "");

            if (prevLongMessage != null) {
                // Move the data for the current song to the next index
                editor.putString(nextKey, prevLongMessage);
                editor.putString(nextTitleKey, prevTitle);
                editor.putString(nextArtistKey, prevArtist);
            }
        }

        // Save the new long message, title, and artist as the most recent
        editor.putString("longMessage_0", longMessage);
        editor.putString("title_0", title);
        editor.putString("artist_0", artist);
        editor.apply();
    }



    @Override
    public void onStringEntered(String title, String artist, String lyrics) {
        // Add the new item to the listBuilder
        addLongMessageToList(title, artist, "Lyrics: " + lyrics);

        // Save the long message, title, and artist
        saveLongMessage(title, artist, "Lyrics: " + lyrics);

        // Invalidate the template to show the updated list
        invalidate();
    }
    @NonNull
    @Override
    public Template onGetTemplate() {
        // Your existing code to create and return the list template

        return new ListTemplate.Builder()
                .setSingleList(listBuilder.build())
                .setTitle("List of Results")
                .setActionStrip(new ActionStrip.Builder()
                        .addAction(
                                new Action.Builder()
                                        .setTitle("<- Go Back")
                                        .setOnClickListener(this::onClick)
                                        .build())
                        .build())
                .build();
    }


    private void onClick() {
        getScreenManager().pop();
    }

}
