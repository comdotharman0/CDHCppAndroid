package com.cdh.cppandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // Exact colors extracted from the screenshot
    private static final int COLOR_NAVY = Color.parseColor("#000642");
    private static final int COLOR_BG = Color.parseColor("#ECECEF");
    private static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
    private static final int COLOR_TEXT_MUTED = Color.parseColor("#6E6E73");
    private static final int COLOR_UNDERLINE = Color.parseColor("#8E8E93");

    private TextView tvTotalPoints;
    private LinearLayout entriesContainer;
    private final List<EditText[]> entryRows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Match status bar color to the dark navy header
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(COLOR_NAVY);

        // Root vertical container
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(COLOR_BG);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // 1. Top Navy App Bar ("Ledger" | "POINTS: 0")
        rootLayout.addView(buildTopBar());

        // 2. Main Body Container (with outer padding)
        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(dp(12), dp(12), dp(12), dp(12));
        contentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // 3. Pill Buttons Row ("MORE", "DELETE ALL", "SEND TO ..")
        contentContainer.addView(buildActionButtonsRow());

        // 4. Column Header Card ("Number" | "Points")
        contentContainer.addView(buildColumnHeaderCard());

        // 5. Scrollable Container for Dynamic Input Cards
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        entriesContainer = new LinearLayout(this);
        entriesContainer.setOrientation(LinearLayout.VERTICAL);
        entriesContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        scrollView.addView(entriesContainer);
        contentContainer.addView(scrollView);
        rootLayout.addView(contentContainer);

        setContentView(rootLayout);

        // Add the initial input card shown in the screenshot
        addEntryRow();
    }

    private LinearLayout buildTopBar() {
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(COLOR_NAVY);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(16), dp(16), dp(16), dp(16));
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("CDH");
        tvTitle.setTextColor(COLOR_WHITE);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvTitle.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        tvTitle.setLayoutParams(titleParams);

        tvTotalPoints = new TextView(this);
        tvTotalPoints.setText("POINTS: 0");
        tvTotalPoints.setTextColor(COLOR_WHITE);
        tvTotalPoints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvTotalPoints.setGravity(Gravity.END);
        tvTotalPoints.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        topBar.addView(tvTitle);
        topBar.addView(tvTotalPoints);
        return topBar;
    }

    private LinearLayout buildActionButtonsRow() {
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.bottomMargin = dp(8);
        buttonRow.setLayoutParams(rowParams);

        Button btnMore = createPillButton("MORE", 1.0f, true);
        Button btnDeleteAll = createPillButton("DELETE ALL", 1.35f, true);
        Button btnSendTo = createPillButton("SEND TO ..", 1.0f, false);

        btnMore.setOnClickListener(v -> addEntryRow());

        btnDeleteAll.setOnClickListener(v -> {
            entriesContainer.removeAllViews();
            entryRows.clear();
            addEntryRow();
            recalculateTotalPoints();
        });

        btnSendTo.setOnClickListener(v -> shareLedgerSummary());

        buttonRow.addView(btnMore);
        buttonRow.addView(btnDeleteAll);
        buttonRow.addView(btnSendTo);
        return buttonRow;
    }

    private Button createPillButton(String label, float weight, boolean hasRightMargin) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setTextColor(COLOR_WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        btn.setAllCaps(true);
        btn.setPadding(dp(8), dp(8), dp(8), dp(8));
        btn.setMinHeight(dp(40));
        btn.setMinimumHeight(dp(40));
        btn.setStateListAnimator(null); // Remove default Material drop-shadow

        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setShape(GradientDrawable.RECTANGLE);
        pillBg.setColor(COLOR_NAVY);
        pillBg.setCornerRadius(dp(24));
        btn.setBackground(pillBg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, dp(42), weight
        );
        if (hasRightMargin) {
            params.rightMargin = dp(4);
        }
        btn.setLayoutParams(params);
        return btn;
    }

    private LinearLayout buildColumnHeaderCard() {
        LinearLayout headerCard = new LinearLayout(this);
        headerCard.setOrientation(LinearLayout.HORIZONTAL);
        headerCard.setBackground(createRoundedCardDrawable(COLOR_WHITE, dp(8)));
        headerCard.setPadding(dp(16), dp(12), dp(16), dp(12));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(10);
        headerCard.setLayoutParams(cardParams);

        TextView tvColNumber = new TextView(this);
        tvColNumber.setText("Number");
        tvColNumber.setTextColor(COLOR_TEXT_MUTED);
        tvColNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvColNumber.setGravity(Gravity.CENTER_HORIZONTAL);
        tvColNumber.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));

        TextView tvColPoints = new TextView(this);
        tvColPoints.setText("Points");
        tvColPoints.setTextColor(COLOR_TEXT_MUTED);
        tvColPoints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvColPoints.setGravity(Gravity.CENTER_HORIZONTAL);
        tvColPoints.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.35f
        ));

        headerCard.addView(tvColNumber);
        headerCard.addView(tvColPoints);
        return headerCard;
    }

    private void addEntryRow() {
        LinearLayout rowCard = new LinearLayout(this);
        rowCard.setOrientation(LinearLayout.HORIZONTAL);
        rowCard.setBackground(createRoundedCardDrawable(COLOR_WHITE, dp(8)));
        rowCard.setPadding(dp(12), dp(12), dp(12), dp(14));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(10);
        rowCard.setLayoutParams(cardParams);

        EditText etNumber = createUnderlinedInput("Number", 1.0f, true);
        EditText etPoints = createUnderlinedInput("Points", 1.35f, false);

        etPoints.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                recalculateTotalPoints();
            }
        });

        rowCard.addView(etNumber);
        rowCard.addView(etPoints);
        entriesContainer.addView(rowCard);
        entryRows.add(new EditText[]{etNumber, etPoints});
    }

    private EditText createUnderlinedInput(String hint, float weight, boolean hasRightMargin) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setHintTextColor(COLOR_TEXT_MUTED);
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setSingleLine(true);
        editText.setPadding(dp(2), dp(6), dp(2), dp(8));

        // Style the bottom underline bar
        if (editText.getBackground() != null) {
            editText.getBackground().mutate().setColorFilter(
                    COLOR_UNDERLINE, PorterDuff.Mode.SRC_ATOP
            );
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight
        );
        if (hasRightMargin) {
            params.rightMargin = dp(8);
        }
        editText.setLayoutParams(params);
        return editText;
    }

    private void recalculateTotalPoints() {
        long total = 0;
        for (EditText[] pair : entryRows) {
            String val = pair[1].getText().toString().trim();
            if (!val.isEmpty()) {
                try {
                    total += Long.parseLong(val);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        tvTotalPoints.setText("POINTS: " + total);
    }

    private void shareLedgerSummary() {
        StringBuilder sb = new StringBuilder("Ledger Summary\n");
        boolean hasData = false;
        for (EditText[] pair : entryRows) {
            String num = pair[0].getText().toString().trim();
            String pts = pair[1].getText().toString().trim();
            if (!num.isEmpty() || !pts.isEmpty()) {
                sb.append("Number: ").append(num.isEmpty() ? "-" : num)
                  .append(" | Points: ").append(pts.isEmpty() ? "0" : pts)
                  .append("\n");
                hasData = true;
            }
        }
        if (!hasData) {
            Toast.makeText(this, "Enter at least one row first", Toast.LENGTH_SHORT).show();
            return;
        }
        sb.append(tvTotalPoints.getText().toString());

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(sendIntent, "Send Ledger via"));
    }

    private GradientDrawable createRoundedCardDrawable(int bgColor, int radiusPx) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(bgColor);
        drawable.setCornerRadius(radiusPx);
        return drawable;
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        ));
    }
            }
