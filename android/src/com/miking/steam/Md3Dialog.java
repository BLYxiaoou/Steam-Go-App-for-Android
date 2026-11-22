package com.miking.steam;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Material 3 dialog. Keeps the same builder style as the original EMUI5Dialog.
 */
class Md3Dialog {
    interface Action {
        void run();
    }

    private static class Button {
        final String text;
        final Action action;
        final int color;

        Button(String text, Action action, int color) {
            this.text = text;
            this.action = action;
            this.color = color;
        }
    }

    private final Activity activity;
    private final Dialog dialog;
    private final List<Button> buttons = new ArrayList<>();

    private String title;
    private String message;
    private int messageColor = Ui.MD_ON_VARIANT;
    private View customView;
    private boolean cancelable = true;

    Md3Dialog(Activity activity) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    Md3Dialog setTitle(String value) {
        this.title = value;
        return this;
    }

    Md3Dialog setMessage(String value) {
        this.message = value;
        this.messageColor = Ui.MD_ON_VARIANT;
        return this;
    }

    Md3Dialog setMessage(String value, int color) {
        this.message = value;
        this.messageColor = color;
        return this;
    }

    Md3Dialog setView(View view) {
        this.customView = view;
        return this;
    }

    Md3Dialog setCancelable(boolean value) {
        this.cancelable = value;
        return this;
    }

    Md3Dialog setNegativeButton(String text, Action action, int color) {
        buttons.add(new Button(text, action, color));
        return this;
    }

    Md3Dialog setNegativeButton(String text, Action action) {
        return setNegativeButton(text, action, Ui.MD_PRIMARY);
    }

    Md3Dialog setNeutralButton(String text, Action action) {
        return setNegativeButton(text, action, Ui.MD_PRIMARY);
    }

    Md3Dialog setNeutralButton(String text, Action action, int color) {
        return setNegativeButton(text, action, color);
    }

    Md3Dialog setPositiveButton(String text, Action action) {
        return setNegativeButton(text, action, Ui.MD_PRIMARY);
    }

    Md3Dialog setPositiveButton(String text, Action action, int color) {
        return setNegativeButton(text, action, color);
    }

    Md3Dialog setPositiveButton(String text) {
        return setNegativeButton(text, null, Ui.MD_PRIMARY);
    }

    private void buildContent() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Ui.dp(activity, 24), Ui.dp(activity, 24), Ui.dp(activity, 24), Ui.dp(activity, 16));

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(Ui.MD_SURFACE);
        background.setCornerRadius(Ui.dp(activity, 28));
        root.setBackground(background);
        root.setElevation(Ui.dp(activity, 6));

        if (title != null && title.length() > 0) {
            TextView titleView = Ui.text(activity, title, 24, Ui.MD_ON_SURFACE);
            titleView.setTypeface(Ui.medium());
            root.addView(titleView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (message != null && message.length() > 0) {
            TextView messageView = Ui.text(activity, message, 14, messageColor);
            messageView.setLineSpacing(0, 1.3f);
            LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mp.topMargin = Ui.dp(activity, 16);
            if (message.length() > 260) {
                ScrollView scroll = new ScrollView(activity);
                scroll.addView(messageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(activity, 280));
                sp.topMargin = Ui.dp(activity, 16);
                root.addView(scroll, sp);
            } else {
                root.addView(messageView, mp);
            }
        }

        if (customView != null) {
            LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            vp.topMargin = Ui.dp(activity, 16);
            root.addView(customView, vp);
        }

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ap.topMargin = Ui.dp(activity, 20);
        root.addView(actions, ap);

        for (int i = 0; i < buttons.size(); i++) {
            final Button spec = buttons.get(i);
            TextView action = Ui.text(activity, spec.text, 14, spec.color);
            action.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            action.setPadding(Ui.dp(activity, 16), Ui.dp(activity, 10), Ui.dp(activity, 16), Ui.dp(activity, 10));
            action.setMinHeight(Ui.dp(activity, 44));
            action.setClickable(true);
            GradientDrawable mask = Ui.rounded(activity, 0xFFFFFFFF, 22);
            action.setBackground(new RippleDrawable(
                    ColorStateList.valueOf(Ui.isDark() ? 0x33FFFFFF : 0x1F1C1B1F), null, mask));
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                bp.topMargin = Ui.dp(activity, 2);
            }
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (spec.action != null) {
                        spec.action.run();
                    }
                }
            });
            actions.addView(action, bp);
        }

        dialog.setContentView(root);
    }

    void show() {
        buildContent();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.32f);
        }
        dialog.show();
        if (window != null) {
            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.92f);
            int maxWidth = Ui.dp(activity, 560);
            if (width > maxWidth) {
                width = maxWidth;
            }
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    void dismiss() {
        dialog.dismiss();
    }
}
