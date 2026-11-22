package com.miking.steam;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

final class Ui {
    static final int THEME_SYSTEM = 0;
    static final int THEME_DARK = 1;
    static final int THEME_LIGHT = 2;

    static final int ACCENT_BLUE = 0;
    static final int ACCENT_PURPLE = 1;
    static final int ACCENT_GREEN = 2;
    static final int ACCENT_ORANGE = 3;
    static final int ACCENT_PINK = 4;
    static final int ACCENT_DYNAMIC = 5;

    // Material 3 color roles, resolved from the selected theme.
    static int BG = 0xFF101418;
    static int SURFACE = 0xFF1A1F26;
    static int SURFACE_HIGH = 0xFF242A33;
    static int TEXT = 0xFFE3E6EB;
    static int TEXT_DIM = 0xFFC2C7D0;
    static int PRIMARY = 0xFF8AB4F8;
    static int FAB = 0xFF2A4A78;
    static int ON_FAB = 0xFFD7E3FF;

    static int MD_SURFACE = 0xFF2B2930;
    static int MD_SURFACE_CONTAINER = 0xFF211F26;
    static int MD_ON_SURFACE = 0xFFE6E1E5;
    static int MD_ON_VARIANT = 0xFFCAC4D0;
    static int MD_PRIMARY = 0xFF8AB4F8;

    private static boolean dark = true;

    private Ui() {
    }

    static void applyTheme(Activity activity) {
        int mode = getThemeMode(activity);
        boolean isDark;
        if (mode == THEME_LIGHT) {
            isDark = false;
        } else if (mode == THEME_DARK) {
            isDark = true;
        } else {
            isDark = (activity.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
        dark = isDark;
        if (isDark) {
            BG = 0xFF101418;
            SURFACE = 0xFF1A1F26;
            SURFACE_HIGH = 0xFF242A33;
            TEXT = 0xFFE3E6EB;
            TEXT_DIM = 0xFFC2C7D0;

            MD_SURFACE = 0xFF2B2930;
            MD_SURFACE_CONTAINER = 0xFF211F26;
            MD_ON_SURFACE = 0xFFE6E1E5;
            MD_ON_VARIANT = 0xFFCAC4D0;
        } else {
            BG = 0xFFFFFBFE;
            SURFACE = 0xFFF3EDF7;
            SURFACE_HIGH = 0xFFECE6F0;
            TEXT = 0xFF1C1B1F;
            TEXT_DIM = 0xFF49454F;

            MD_SURFACE = 0xFFECE6F0;
            MD_SURFACE_CONTAINER = 0xFFF3EDF7;
            MD_ON_SURFACE = 0xFF1C1B1F;
            MD_ON_VARIANT = 0xFF49454F;
        }

        int[] accent = accentColors(activity, getAccent(activity), isDark);
        PRIMARY = accent[0];
        FAB = accent[1];
        ON_FAB = accent[2];
        MD_PRIMARY = PRIMARY;

        activity.getWindow().setStatusBarColor(BG);
        activity.getWindow().setNavigationBarColor(BG);
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
        if (isDark) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isDark) {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    static boolean isDark() {
        return dark;
    }

    static int getThemeMode(Context context) {
        return context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .getInt("theme_mode", THEME_DARK);
    }

    static void setThemeMode(Context context, int mode) {
        context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .edit().putInt("theme_mode", mode).apply();
    }

    static String themeName(Context context) {
        int mode = getThemeMode(context);
        if (mode == THEME_LIGHT) {
            return "浅色";
        }
        if (mode == THEME_DARK) {
            return "深色";
        }
        return "跟随系统";
    }

    static int getAccent(Context context) {
        return context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .getInt("theme_accent", ACCENT_BLUE);
    }

    static void setAccent(Context context, int accent) {
        context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .edit().putInt("theme_accent", accent).apply();
    }

    static int getWebZoom(Context context) {
        return context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .getInt("web_zoom", 100);
    }

    static void setWebZoom(Context context, int zoom) {
        context.getSharedPreferences("steamgo", Context.MODE_PRIVATE)
                .edit().putInt("web_zoom", zoom).apply();
    }

    static String accentName(Context context) {
        switch (getAccent(context)) {
            case ACCENT_PURPLE:
                return "紫色";
            case ACCENT_GREEN:
                return "绿色";
            case ACCENT_ORANGE:
                return "橙色";
            case ACCENT_PINK:
                return "粉色";
            case ACCENT_DYNAMIC:
                return Build.VERSION.SDK_INT >= 31 ? "动态取色" : "动态取色（不可用）";
            default:
                return "蓝色";
        }
    }

    static int accentSwatch(Context context, int accent) {
        return accentColors(context, accent, dark)[0];
    }

    private static int systemColor(Context context, int resId, int fallback) {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                return context.getColor(resId);
            }
        } catch (Throwable ignored) {
        }
        return fallback;
    }

    /** @return {primary, fab container, on fab} */
    private static int[] accentColors(Context context, int accent, boolean isDark) {
        if (accent == ACCENT_DYNAMIC && Build.VERSION.SDK_INT >= 31) {
            if (isDark) {
                return new int[]{
                        systemColor(context, android.R.color.system_accent1_200, 0xFF8AB4F8),
                        systemColor(context, android.R.color.system_accent1_700, 0xFF2A4A78),
                        systemColor(context, android.R.color.system_accent1_100, 0xFFD7E3FF)
                };
            }
            return new int[]{
                    systemColor(context, android.R.color.system_accent1_600, 0xFF1A73E8),
                    systemColor(context, android.R.color.system_accent1_600, 0xFF1A73E8),
                    systemColor(context, android.R.color.system_accent1_50, 0xFFFFFFFF)
            };
        }
        switch (accent) {
            case ACCENT_PURPLE:
                return isDark
                        ? new int[]{0xFFD0BCFF, 0xFF4F378B, 0xFFEADDFF}
                        : new int[]{0xFF6750A4, 0xFF6750A4, 0xFFFFFFFF};
            case ACCENT_GREEN:
                return isDark
                        ? new int[]{0xFFA8DAB5, 0xFF2E5B1C, 0xFFC8F0C8}
                        : new int[]{0xFF386A20, 0xFF386A20, 0xFFFFFFFF};
            case ACCENT_ORANGE:
                return isDark
                        ? new int[]{0xFFFFB77C, 0xFF7A3A00, 0xFFFFDCC2}
                        : new int[]{0xFFB4510A, 0xFFB4510A, 0xFFFFFFFF};
            case ACCENT_PINK:
                return isDark
                        ? new int[]{0xFFFFB1C8, 0xFF8B0050, 0xFFFFD9E2}
                        : new int[]{0xFFB90063, 0xFFB90063, 0xFFFFFFFF};
            default:
                return isDark
                        ? new int[]{0xFF8AB4F8, 0xFF2A4A78, 0xFFD7E3FF}
                        : new int[]{0xFF1A73E8, 0xFF1A73E8, 0xFFFFFFFF};
        }
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

    /** Rounded only on the trailing (right) edge, for a modal navigation drawer. */
    static GradientDrawable roundedRight(Context c, int color, float radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(color);
        float r = dp(c, radiusDp);
        d.setCornerRadii(new float[]{0, 0, r, r, r, r, 0, 0});
        return d;
    }

    static void applyRipple(View view, Context c, float radiusDp) {
        applyRipple(view, c, radiusDp, dark ? 0x33FFFFFF : 0x1F000000);
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
        applyRipple(box, c, 24);
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
        v.setBackgroundColor(dark ? 0x22FFFFFF : 0x1F000000);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(c, 0.5f)));
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

    static LinearLayout drawerRow(Context c, int iconRes, int tint, String label, int textColor,
                                  View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(c);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(c, 24), 0, dp(c, 16), 0);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(c, 52)));
        row.setClickable(true);
        applyRipple(row, c, 26);
        if (iconRes != 0) {
            ImageView iv = new ImageView(c);
            iv.setImageResource(iconRes);
            iv.setColorFilter(tint, PorterDuff.Mode.SRC_IN);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(c, 24), dp(c, 24));
            ip.rightMargin = dp(c, 16);
            row.addView(iv, ip);
        }
        TextView tv = text(c, label, 14, textColor);
        tv.setSingleLine(true);
        row.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row.setOnClickListener(listener);
        return row;
    }

    static Typeface medium() {
        return Typeface.create("sans-serif-medium", Typeface.NORMAL);
    }
}
