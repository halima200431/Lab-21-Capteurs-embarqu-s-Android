package com.example.lab21.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lab21.R;

public class UiKit {

    /**
     * Convertit les dp en pixels.
     * Android travaille en pixels, mais les interfaces sont mieux pensées en dp.
     */
    public static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    /**
     * Crée un ScrollView avec un LinearLayout vertical à l'intérieur.
     * Pratique pour les écrans contenant plusieurs cartes.
     */
    public static LinearLayout createScrollableRoot(Context context, ScrollView scrollView) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 30));

        scrollView.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return root;
    }

    /**
     * Crée une carte arrondie utilisée pour afficher les informations.
     */
    public static LinearLayout createCard(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_glass);
        card.setPadding(dp(context, 18), dp(context, 18), dp(context, 18), dp(context, 18));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, dp(context, 14));
        card.setLayoutParams(params);

        return card;
    }

    public static TextView title(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(24);
        view.setTextColor(Color.parseColor("#3C096C"));
        view.setTypeface(null, android.graphics.Typeface.BOLD);
        return view;
    }

    public static TextView subtitle(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(Color.parseColor("#6C4A7E"));
        view.setPadding(0, dp(context, 6), 0, dp(context, 12));
        return view;
    }

    public static TextView body(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(15);
        view.setTextColor(Color.parseColor("#2F243A"));
        view.setLineSpacing(4, 1.1f);
        return view;
    }

    public static TextView valueChip(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(Color.parseColor("#3C096C"));
        view.setTypeface(null, android.graphics.Typeface.BOLD);
        view.setBackgroundResource(R.drawable.bg_value_chip);
        view.setPadding(dp(context, 18), dp(context, 10), dp(context, 18), dp(context, 10));
        return view;
    }
}