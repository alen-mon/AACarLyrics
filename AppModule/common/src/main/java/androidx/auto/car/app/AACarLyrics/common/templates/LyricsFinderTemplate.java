package androidx.auto.car.app.AACarLyrics.common.templates;

import static androidx.car.app.CarToast.LENGTH_LONG;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.model.*;
import androidx.car.app.model.Action;
import androidx.car.app.model.signin.InputSignInMethod;
import androidx.car.app.model.signin.SignInTemplate;
import androidx.auto.car.app.AACarLyrics.common.R;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class LyricsFinderTemplate extends Screen {
    private final InputCallback inputCallback;
    private String InputText = "";

    public LyricsFinderTemplate(@NonNull CarContext carContext) {
        super(carContext);

        inputCallback = new InputCallback() {
            @Override
            public void onInputSubmitted(@NonNull String text) {
                // Handle input submission if needed
            }

            @Override
            public void onInputTextChanged(@NonNull String text) {
                // Update the input fields as the user types

                    InputText = text;
                // Invalidate the template to show the updated inputs
                invalidate();
            }
        };
    }
    public interface StringListener {
        void onStringEntered(String Title, String Artist, String lyrics);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        // Create two input fields

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getCarContext()));

        }
        InputSignInMethod InputTextMethod = new InputSignInMethod.Builder(inputCallback)
                .setHint("Your Input")
                .setDefaultValue(InputText)
                .build();

        Action formatAndDisplayAction = new Action.Builder()
                .setTitle(getCarContext().getString(
                                R.string.find_lyrics))
                .setOnClickListener(ParkedOnlyOnClickListener.create(this::onClickNextScreen))
                .build();

        return new SignInTemplate.Builder(InputTextMethod)
                .addAction(formatAndDisplayAction)
                .setTitle(getCarContext().getString(
                                R.string.search_lyric_Page))
                .setHeaderAction(Action.BACK)
                .setActionStrip(
                        new ActionStrip.Builder()
                                .addAction(
                                        new Action.Builder()
                                                .setTitle(getCarContext().getString(
                                                        R.string.list_page))
                                                .setOnClickListener(this::onClick)
                                                .build())
                                .build())

                .build();
    }

    private void onClick() {
        MyListScreen myNewScreen = new MyListScreen(getCarContext());
        getScreenManager().push(myNewScreen);
    }

    private void onClickNextScreen() {
        // Format the input strings (you can customize the formatting logic)
        String formattedString = InputText;


        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("MyPythonscript");

        PyObject obj = pyobj.callAttr("main",formattedString);

                String title = obj.asList().get(0).toString();
                String artist = obj.asList().get(1).toString();
                String lyrics = obj.asList().get(2).toString();

        if(!lyrics.equals("0")) {
            CarToast.makeText(getCarContext(), formattedString + " Lyrics Found", LENGTH_LONG).show();
        }else{
            CarToast.makeText(getCarContext(), formattedString+" Not Found", LENGTH_LONG).show();
        }
        // Pass the string to MyListScreen
        MyListScreen myListScreen = new MyListScreen(getCarContext());
        ((StringListener) myListScreen).onStringEntered(title, artist, lyrics);

        // Navigate to MyListScreen
        getScreenManager().push(myListScreen);
    }


}
