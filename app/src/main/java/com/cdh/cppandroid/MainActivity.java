package com.cdh.cppandroid;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("native_cpp");
    }

    private static native String applyAdd(long delta);
    private static native String applyMultiply(long factor);
    private static native String resetEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad24 = dp(24);
        int gap12 = dp(12);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(pad24, pad24, pad24, pad24);
        root.setBackgroundColor(Color.parseColor("#121212"));

        TextView statusText = new TextView(this);
        statusText.setText("State: 0 (Ready)");
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        statusText.setTextColor(Color.WHITE);
        statusText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.bottomMargin = pad24;
        root.addView(statusText, textParams);

        Button btnAdd = createButton("Add +5 (Polymorphic Op)", gap12);
        Button btnMul = createButton("Multiply x2 (Polymorphic Op)", gap12);
        Button btnReset = createButton("Reset Engine (RAII Clear)", 0);

        root.addView(btnAdd);
        root.addView(btnMul);
        root.addView(btnReset);

        btnAdd.setOnClickListener(v -> statusText.setText(applyAdd(5L)));
        btnMul.setOnClickListener(v -> statusText.setText(applyMultiply(2L)));
        btnReset.setOnClickListener(v -> statusText.setText(resetEngine()));

        setContentView(root);
    }

    private Button createButton(String label, int bottomMarginPx) {
        Button btn = new Button(this);
        btn.setText(label);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = bottomMarginPx;
        btn.setLayoutParams(params);
        return btn;
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        ));
    }
}
