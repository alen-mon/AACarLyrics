package androidx.auto.car.app.AACarLyrics.common.templates;

import static androidx.car.app.CarToast.LENGTH_LONG;
import static androidx.car.app.model.Action.BACK;
import static androidx.car.app.model.Action.FLAG_PRIMARY;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.LongMessageTemplate;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.ParkedOnlyOnClickListener;
import androidx.car.app.model.Template;
import androidx.auto.car.app.AACarLyrics.common.R;
import androidx.car.app.versioning.CarAppApiLevels;


public class LongMessageScreen extends Screen {
    private final String longMessageText;
    private final String titleText;

    public LongMessageScreen(@NonNull CarContext carContext, String longMessageText, String titleText) {
        super(carContext);
        this.longMessageText = longMessageText;
        this.titleText = titleText;
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        if (getCarContext().getCarAppApiLevel() < CarAppApiLevels.LEVEL_2) {
            return new MessageTemplate.Builder(
                    getCarContext().getString(R.string.long_msg_template_not_supported_text))
                    .setTitle(getCarContext().getString(
                            R.string.long_msg_template_not_supported_title))
                    .setHeaderAction(Action.BACK)
                    .build();
        }

        Action.Builder primaryActionBuilder = new Action.Builder()
                .setOnClickListener(
                        ParkedOnlyOnClickListener.create(() -> {
                            getScreenManager().pop();
                            CarToast.makeText(
                                    getCarContext(),
                                    getCarContext().getString(R.string.primary_action_title),
                                    LENGTH_LONG
                            ).show();
                        }))
                .setTitle(getCarContext().getString(R.string.accept_action_title));
        if (getCarContext().getCarAppApiLevel() >= CarAppApiLevels.LEVEL_4) {
            primaryActionBuilder.setFlags(FLAG_PRIMARY);
        }

        // Use the dynamically set text and title
        return new LongMessageTemplate.Builder(longMessageText)
                .setTitle(titleText)
                .setHeaderAction(BACK)
                .addAction(primaryActionBuilder.build())
                .addAction(new Action.Builder()
                        .setBackgroundColor(CarColor.RED)
                        .setOnClickListener(
                                ParkedOnlyOnClickListener.create(() -> getScreenManager().pop()))
                        .setTitle(getCarContext().getString(R.string.reject_action_title))
                        .build())
                .setActionStrip(new ActionStrip.Builder()
                        .addAction(new Action.Builder()
                                .setTitle(getCarContext().getString(R.string.more_action_title))
                                .setOnClickListener(
                                        () ->
                                                CarToast.makeText(
                                                                getCarContext(),
                                                                getCarContext().getString(
                                                                        R.string.more_toast_msg),
                                                                LENGTH_LONG)
                                                        .show())
                                .build())
                        .build())
                .build();
    }
}