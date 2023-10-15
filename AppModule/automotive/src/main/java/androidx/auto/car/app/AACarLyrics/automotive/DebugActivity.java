
package androidx.auto.car.app.AACarLyrics.automotive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.car.app.activity.CarAppActivity;
import androidx.auto.car.app.AACarLyrics.R;


public class DebugActivity extends Activity {
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);

        Button mButton = findViewById(R.id.button);
        mTextView = findViewById(R.id.text);

        mButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CarAppActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTextView.setText("Received code: " + resultCode + " action: "
                + (data != null ? data.getAction() : "null"));
    }
}
