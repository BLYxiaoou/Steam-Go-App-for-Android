package com.miking.steam;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    private LinearLayout list;
    private int themeModeAtCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeModeAtCreate = Ui.getThemeMode(this);
        Ui.applyTheme(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Ui.BG);
        root.addView(buildToolbar(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 64)));

        ScrollView scroll = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, Ui.dp(this, 8), 0, Ui.dp(this, 24));

        addSection("外观");
        addRow(R.drawable.ic_fltbtn, "主题", Ui.themeName(this), new Runnable() {
            public void run() {
                showThemeDialog();
            }
        });

        addSection("数据");
        addRow(R.drawable.ic_refresh, "清除网页缓存", "不会退出 Steam 登录", new Runnable() {
            public void run() {
                clearCache();
            }
        });

        addSection("关于");
        addRow(R.drawable.ic_version, "关于 Steam Go", "版本 " + versionName(), new Runnable() {
            public void run() {
                startActivity(new android.content.Intent(SettingsActivity.this, AboutActivity.class));
            }
        });
        addRow(R.drawable.ic_bubble, "Steam 客服", "账户数据、购买记录与激活产品", new Runnable() {
            public void run() {
                BrowserActivity.openUniversal(SettingsActivity.this,
                        "https://help.steampowered.com/zh-cn");
            }
        });

        scroll.addView(list, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (themeModeAtCreate != Ui.getThemeMode(this)) {
            recreate();
        }
    }

    private LinearLayout buildToolbar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setBackgroundColor(Ui.BG);
        bar.setPadding(Ui.dp(this, 4), 0, Ui.dp(this, 4), 0);

        bar.addView(Ui.actionButton(this, R.drawable.ic_back, Ui.TEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

        TextView title = Ui.text(this, "设置", 20, Ui.TEXT);
        title.setTypeface(Ui.medium());
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.leftMargin = Ui.dp(this, 4);
        bar.addView(title, tp);
        return bar;
    }

    private void addSection(String label) {
        TextView section = Ui.text(this, label, 12, Ui.PRIMARY);
        section.setTypeface(Ui.medium());
        section.setPadding(Ui.dp(this, 24), Ui.dp(this, 20), Ui.dp(this, 24), Ui.dp(this, 8));
        list.addView(section);
    }

    private void addRow(int iconRes, String title, String subtitle, final Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(this, 24), Ui.dp(this, 12), Ui.dp(this, 16), Ui.dp(this, 12));
        row.setClickable(true);
        Ui.applyRipple(row, this, 12);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setColorFilter(Ui.TEXT_DIM, android.graphics.PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(Ui.dp(this, 24), Ui.dp(this, 24));
        ip.rightMargin = Ui.dp(this, 16);
        row.addView(icon, ip);

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = Ui.text(this, title, 16, Ui.TEXT);
        column.addView(titleView);
        TextView subtitleView = Ui.text(this, subtitle, 12, Ui.TEXT_DIM);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sp.topMargin = Ui.dp(this, 2);
        column.addView(subtitleView, sp);
        row.addView(column, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = Ui.text(this, ">", 18, Ui.TEXT_DIM);
        row.addView(arrow);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
        list.addView(row);
    }

    private void showThemeDialog() {
        new Md3Dialog(this)
                .setTitle("主题")
                .setMessage("选择 MD3 外观。跟随系统会随手机的深色模式自动切换。")
                .setNegativeButton("跟随系统", new Md3Dialog.Action() {
                    public void run() {
                        applyThemeMode(Ui.THEME_SYSTEM);
                    }
                })
                .setNeutralButton("深色", new Md3Dialog.Action() {
                    public void run() {
                        applyThemeMode(Ui.THEME_DARK);
                    }
                })
                .setPositiveButton("浅色", new Md3Dialog.Action() {
                    public void run() {
                        applyThemeMode(Ui.THEME_LIGHT);
                    }
                })
                .show();
    }

    private void applyThemeMode(int mode) {
        Ui.setThemeMode(this, mode);
        recreate();
    }

    private void clearCache() {
        try {
            WebView webView = new WebView(this);
            webView.clearCache(true);
            webView.destroy();
            Toast.makeText(this, "网页缓存已清除", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "清除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String versionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "2.6.0";
        }
    }
}
