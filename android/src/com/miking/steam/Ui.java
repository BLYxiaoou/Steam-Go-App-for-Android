package com.miking.steam;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

final class Ui {
    static final int BG = 0xFF101418;
    static final int SURFACE = 0xFF1A1F26;
    static final int SURFACE_HIGH = 0xFF242A33;
    static final int TEXT = 0xFFE3E6EB;
    static final int TEXT_DIM = 0xFFC2C7D0;
    static final int PRIMARY = 0xFF8AB4F8;
    static final int FAB = 0xFF2A4A78;
    static final int ON_FAB = 0xFFD7E3FF;

    static final int MD_SURFACE = 0xFFECE6F0;
    static final int MD_SURFACE_CONTAINER = 0xFFF3EDF7;
    static final int MD_ON_SURFACE = 0xFF1C1B1F;
    static final int MD_ON_VARIANT = 0xFF49454F;
    static final int MD_PRIMARY = 0xFF1A73E8;

    private Ui() {
    }

    static int dp(Context c, float value) {
        return (int) (value * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    static GradientDrawable rounded(Context c, int color, float radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(color);
        d.setCornerRadius(dp(c, radiusDp));
        return d;
    }

    static void applyRipple(View view, Context c, float radiusDp, int rippleColor) {
        GradientDrawable mask = rounded(c, 0xFFFFFFFF, radiusDp);
        view.setBackground(new RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask));
    }

    static ImageView icon(Context c, int resId, int tint, int sizeDp) {
        ImageView iv = new ImageView(c);
        iv.setImageResource(resId);
        iv.setColorFilter(tint, PorterDuff.Mode.SRC_IN);
        iv.setLayoutParams(new FrameLayout.LayoutParams(dp(c, sizeDp), dp(c, sizeDp), Gravity.CENTER));
        return iv;
    }

    static FrameLayout actionButton(Context c, int resId, int tint, View.OnClickListener listener) {
        FrameLayout box = new FrameLayout(c);
        box.setLayoutParams(new LinearLayout.LayoutParams(dp(c, 48), dp(c, 48)));
        box.setClickable(true);
        box.setFocusable(true);
        applyRipple(box, c, 24, 0x33FFFFFF);
        box.addView(icon(c, resId, tint, 24));
        if (listener != null) {
            box.setOnClickListener(listener);
        }
        return box;
    }

    static TextView text(Context c, String value, float sizeSp, int color) {
        TextView t = new TextView(c);
        t.setText(value);
        t.setTextSize(sizeSp);
        t.setTextColor(color);
        return t;
    }

    static View divider(Context c) {
        View v = new View(c);
        v.setBackgroundColor(0x22FFFFFF);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(c, 0.5f)));
        p.leftMargin = dp(c, 16);
        p.rightMargin = dp(c, 16);
        v.setLayoutParams(p);
        return v;
    }

    static void openExternal(Activity activity, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception ignored) {
                Toast.makeText(activity, "未找到可打开此链接的应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ignored) {
        }
    }

    static String encodeUrl(String url) {
        StringBuilder sb = new StringBuilder(url.length() + 32);
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch > 127) {
                try {
                    byte[] bytes = String.valueOf(ch).getBytes("UTF-8");
                    for (byte b : bytes) {
                        sb.append('%');
                        String hex = Integer.toHexString(b & 0xFF).toUpperCase();
                        if (hex.length() == 1) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    }
                } catch (Exception e) {
                    sb.append(ch);
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    static LinearLayout drawerRow(Context c, int iconRes, int tint, String label, int textColor, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(c, 20), 0, dp(c, 16), 0);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(c, 52)));
        row.setClickable(true);
        applyRipple(row, c, 12, 0x33FFFFFF);
        if (iconRes != 0) {
            ImageView iv = new ImageView(c);
            iv.setImageResource(iconRes);
            iv.setColorFilter(tint, PorterDuff.Mode.SRC_IN);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(c, 24), dp(c, 24));
            ip.rightMargin = dp(c, 16);
            row.addView(iv, ip);
        }
        TextView tv = text(c, label, 15, textColor);
        tv.setSingleLine(true);
        row.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row.setOnClickListener(listener);
        return row;
    }

    static Typeface medium() {
        return Typeface.create("sans-serif-medium", Typeface.NORMAL);
    }
}
