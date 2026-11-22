package com.miking.steam;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
    private static final int GREEN = 0xFF0F9D58;
    private static final int BLUE = 0xFF1A73E8;
    private static final int QQ_BLUE = 0xFF1EB9F2;
    private static final int STEAM_DARK = 0xFF171A21;
    private static final int TWITTER = 0xFF1DA1F2;
    private static final int ZHIHU = 0xFF0F88EB;
    private static final int BILI = 0xFFFB7299;
    private static final int GROUP_BLUE = 0xFF2C7BF2;
    private static final int WECHAT_GREEN = 0xFF22BB2D;

    private String version = "";
    private int themeModeAtCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeModeAtCreate = Ui.getThemeMode(this);
        Ui.applyTheme(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            version = "2.6.2";
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Ui.BG);

        root.addView(buildToolbar(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 56)));

        ScrollView scroll = new ScrollView(this);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(0, 0, 0, Ui.dp(this, 24));

        body.addView(buildHeader());
        body.addView(buildRows(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scroll.addView(body, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

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

        FrameLayout back = new FrameLayout(this);
        back.setLayoutParams(new LinearLayout.LayoutParams(Ui.dp(this, 48), Ui.dp(this, 48)));
        back.setClickable(true);
        Ui.applyRipple(back, this, 24);
        ImageView icon = Ui.icon(this, R.drawable.ic_back, Ui.MD_ON_SURFACE, 24);
        icon.setColorFilter(Ui.MD_ON_SURFACE);
        back.addView(icon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bar.addView(back);

        TextView title = Ui.text(this, "\u5173\u4e8e", 20, Ui.MD_ON_SURFACE);
        title.setTypeface(Ui.medium());
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.leftMargin = Ui.dp(this, 4);
        bar.addView(title, tp);
        return bar;
    }

    private View buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackground(Ui.rounded(this, Ui.MD_SURFACE_CONTAINER, 24));
        header.setPadding(Ui.dp(this, 24), 0, Ui.dp(this, 16), 0);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 150));
        hp.topMargin = Ui.dp(this, 8);
        hp.leftMargin = Ui.dp(this, 16);
        hp.rightMargin = Ui.dp(this, 16);
        header.setLayoutParams(hp);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_launcher);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Ui.dp(this, 75), Ui.dp(this, 75));
        lp.rightMargin = Ui.dp(this, 16);
        header.addView(logo, lp);

        TextView text = new TextView(this);
        text.setText("Steam Go\n\n\u6c22\u91cf\u7ea7\u7b2c\u4e09\u65b9Steam App");
        text.setTextSize(16);
        text.setTextColor(Ui.MD_ON_SURFACE);
        text.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        text.setLineSpacing(0, 1.15f);
        header.addView(text, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return header;
    }

    private LinearLayout buildRows() {
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Ui.dp(this, 16);
        lp.leftMargin = Ui.dp(this, 8);
        lp.rightMargin = Ui.dp(this, 8);
        list.setLayoutParams(lp);

        addRow(list, R.drawable.ic_version, "\u7a0b\u5e8f\u7248\u672c", version, new Runnable() {
            public void run() {
                showVersionDialog();
            }
        });
        addRow(list, R.drawable.ic_face, "\u5f00\u53d1\u8005", "MiKing233", new Runnable() {
            public void run() {
                showDeveloperDialog();
            }
        });
        addRow(list, R.drawable.ic_cat, "\u5173\u6ce8\u5f00\u53d1\u8005", "\u4e86\u89e3\u5f00\u53d1\u8005\u7684\u6700\u65b0\u52a8\u6001", new Runnable() {
            public void run() {
                showFollowDialog();
            }
        });
        addRow(list, R.drawable.ic_bubble, "\u52a0\u5165\u6211\u4eec", "\u63d0\u4f9b\u53cd\u9988\u5e76\u53c2\u4e0e\u5f00\u53d1", new Runnable() {
            public void run() {
                showJoinDialog();
            }
        });
        addRow(list, R.drawable.ic_wallet, "\u6350\u8d60", "\u5982\u679c\u4f60\u559c\u6b22\u8bf7\u968f\u610f\u6350\u8d60", new Runnable() {
            public void run() {
                showDonateDialog();
            }
        });
        addRow(list, R.drawable.ic_more, "\u5173\u4e8e\u66f4\u591a", "\u5f00\u6e90\u4ee3\u7801 \u5f00\u6e90\u8bb8\u53ef \u7528\u6237\u534f\u8bae", new Runnable() {
            public void run() {
                showAboutMoreDialog();
            }
        });
        return list;
    }

    private void addRow(LinearLayout parent, int iconRes, String title, String content, final Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(Ui.dp(this, 16), Ui.dp(this, 12), Ui.dp(this, 16), Ui.dp(this, 12));
        row.setClickable(true);
        Ui.applyRipple(row, this, 16);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setColorFilter(Ui.MD_ON_VARIANT);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(Ui.dp(this, 28), Ui.dp(this, 28));
        ip.rightMargin = Ui.dp(this, 16);
        row.addView(icon, ip);

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = Ui.text(this, title, 16, Ui.MD_ON_SURFACE);
        TextView contentView = Ui.text(this, content, 14, Ui.MD_ON_VARIANT);
        column.addView(titleView);
        column.addView(contentView);
        row.addView(column, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
        parent.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showVersionDialog() {
        new Md3Dialog(this)
                .setTitle("Steam Go Lua Edition")
                .setMessage(version)
                .setNegativeButton("\u5728\u9177\u5b89\u67e5\u770b", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://www.coolapk.com/apk/com.miking.steam");
                        toast("\u8bb0\u5f97\u6253\u52065\u661f+\u5173\u6ce8\u5440\uff01");
                    }
                }, GREEN)
                .setNeutralButton("\u5728GitHub\u67e5\u770b", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://github.com/MiKing233/Steam-Go-App-for-Android/releases");
                        toast("\u522b\u5fd8\u4e86\u70b9\u4e2aStar\uff01");
                    }
                }, BLUE)
                .setPositiveButton("\u53d6\u6d88", null)
                .show();
    }

    private void showDeveloperDialog() {
        new Md3Dialog(this)
                .setTitle("\u8054\u7cfb\u5f00\u53d1\u8005")
                .setMessage("\u6709\u4ec0\u4e48\u60f3\u8bf4\u7684\uff0c\u8bd5\u8bd5\u4e0b\u9762\u7684\u8054\u7cfb\u65b9\u5f0f")
                .setNeutralButton("\u9177\u5b89", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://www.coolapk.com/u/1214878");
                        toast("\u5411\u5f00\u53d1\u8005\u63cf\u8ff0\u4e00\u4e0b\u4f60\u7684\u95ee\u9898\u5427");
                    }
                }, GREEN)
                .setNegativeButton("QQ", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        openPackageUrl("mqqwpa://im/chat?chat_type=wpa&uin=975680379&version=1");
                        toast("\u5411\u5f00\u53d1\u8005\u63cf\u8ff0\u4e00\u4e0b\u4f60\u7684\u95ee\u9898\u5427");
                    }
                }, QQ_BLUE)
                .setPositiveButton("Steam", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(AboutActivity.this, "https://steamcommunity.com/id/975680379/");
                        toast("\u5411\u5f00\u53d1\u8005\u63cf\u8ff0\u4e00\u4e0b\u4f60\u7684\u95ee\u9898\u5427");
                    }
                }, steamAccent())
                .show();
    }

    private int steamAccent() {
        return Ui.isDark() ? 0xFF8AB4F8 : STEAM_DARK;
    }

    private void showFollowDialog() {
        new Md3Dialog(this)
                .setTitle("\u5173\u6ce8\u5f00\u53d1\u8005")
                .setMessage("\u4e86\u89e3\u5f00\u53d1\u8005\u7684\u6700\u65b0\u52a8\u6001")
                .setPositiveButton("\u5173\u6ce8\u5f00\u53d1\u8005\u7684 Twitter", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://mobile.twitter.com/MiKing233");
                        toast("\u8c22\u8c22\u8001\u677f");
                    }
                }, TWITTER)
                .setNegativeButton("\u5173\u6ce8\u5f00\u53d1\u8005\u7684 \u77e5\u4e4e", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://www.zhihu.com/people/dyking-96/activities");
                        toast("\u8c22\u8c22\u8001\u677f");
                    }
                }, ZHIHU)
                .setNeutralButton("\u5173\u6ce8\u5f00\u53d1\u8005\u7684 BiliBili", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(AboutActivity.this, "https://m.bilibili.com/space/22939458");
                        toast("\u8c22\u8c22\u8001\u677f");
                    }
                }, BILI)
                .show();
    }

    private void showJoinDialog() {
        new Md3Dialog(this)
                .setTitle("\u4f53\u9a8c\u6539\u8fdb\u8ba1\u5212")
                .setMessage("\u6211\u4eec\u8bda\u9080\u60a8\u52a0\u5165\u5e76\u63d0\u4f9b\u53cd\u9988\uff0c\u60a8\u63d0\u4f9b\u7684\u60f3\u6cd5\u548c\u5efa\u8bae\u53ef\u4ee5\u95f4\u63a5\u5f71\u54cdSteam Go\u5e76\u5c06\u5176\u53d8\u5f97\u66f4\u597d")
                .setNegativeButton("\u52a0\u5165Steam Go\u53cd\u9988\u4ea4\u6d41\u7fa4", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        openPackageUrl("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=723659873&card_type=group&source=qrcode");
                    }
                }, GROUP_BLUE)
                .show();
    }

    private void showDonateDialog() {
        new Md3Dialog(this)
                .setTitle("\u652f\u6301\u5f00\u53d1\u8005")
                .setMessage("\u6350\u8d60\u662fSteam Go\u552f\u4e00\u7684\u6536\u5165\u6765\u6e90")
                .setNeutralButton("\u652f\u4ed8\u5b9d", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        showAlipayDialog();
                    }
                }, Ui.MD_PRIMARY)
                .setNegativeButton("\u5fae\u4fe1\u652f\u4ed8", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        showWechatDialog();
                    }
                }, WECHAT_GREEN)
                .setPositiveButton("\u6350\u8d60\u540d\u5355", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(AboutActivity.this,
                                "https://github.com/MiKing233/Steam-Go-App-for-Android/blob/master/Steam%20Go%20Donation%20list");
                    }
                }, steamAccent())
                .show();
    }

    private void showAlipayDialog() {
        new Md3Dialog(this)
                .setTitle("\u652f\u4ed8\u5b9d\u652f\u4ed8")
                .setMessage("Steam Go\u5c06\u5524\u8d77\u652f\u4ed8\u5b9d\u5e76\u8df3\u8f6c\u5230\u8f6c\u8d26\u9875\u9762")
                .setNegativeButton("\u786e\u8ba4\u652f\u4ed8", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        toast("\u8c22\u8c22\u8001\u677f \u6b63\u5728\u5524\u9192\u652f\u4ed8\u5b9d\uff0c\u8bf7\u8010\u5fc3\u7b49\u5f85");
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "alipayqr://platformapi/startapp?saId=10000007&qrcode=HTTPS://QR.ALIPAY.COM/FKX000571TLV18SVKT1O89"));
                            startActivity(i);
                        } catch (Exception e) {
                            Ui.openExternal(AboutActivity.this, "HTTPS://QR.ALIPAY.COM/FKX000571TLV18SVKT1O89");
                        }
                    }
                })
                .show();
    }

    private void showWechatDialog() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView qr = new ImageView(this);
        qr.setImageResource(R.drawable.qrcode);
        qr.setAdjustViewBounds(true);
        qr.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams qp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 320));
        qp.topMargin = Ui.dp(this, 8);
        box.addView(qr, qp);

        new Md3Dialog(this)
                .setTitle("\u5fae\u4fe1\u652f\u4ed8")
                .setMessage("\n\u5982\u679c\u4f60\u559c\u6b22\uff0c\u8bf7\u968f\u610f\u6350\u8d60\n")
                .setView(box)
                .setPositiveButton("\u8df3\u8f6c\u5230 \u5fae\u4fe1 ", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        toast("\u8c22\u8c22\u8001\u677f\uff01");
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        if (intent != null) {
                            startActivity(intent);
                        } else {
                            toast("\u672a\u5b89\u88c5\u5fae\u4fe1");
                        }
                    }
                }, WECHAT_GREEN)
                .show();
    }

    private void showAboutMoreDialog() {
        new Md3Dialog(this)
                .setTitle("\u5173\u4e8e\u66f4\u591a")
                .setMessage("\u66f4\u591a\u6709\u5173Steam Go\u7684\u4fe1\u606f")
                .setPositiveButton("\u7528\u6237\u534f\u8bae", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(AboutActivity.this,
                                "https://github.com/MiKing233/Steam-Go-App-for-Android/blob/master/Steam%20Go%20User%20Agreement");
                    }
                }, BLUE)
                .setNegativeButton("\u5728GitHub\u4e0a\u67e5\u770b\u6211\u4eec\u7684\u5f00\u6e90\u4ee3\u7801", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(AboutActivity.this,
                                "https://github.com/MiKing233/Steam-Go-App-for-Android");
                    }
                }, BLUE)
                .setNeutralButton("\u5f00\u653e\u6e90\u4ee3\u7801\u8bb8\u53ef", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(AboutActivity.this,
                                "https://github.com/MiKing233/Steam-Go-App-for-Android/blob/master/Steam%20Go%20Open%20source%20license");
                    }
                }, BLUE)
                .show();
    }

    private void openPackageUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            toast("\u672a\u5b89\u88c5QQ");
        }
    }
}
