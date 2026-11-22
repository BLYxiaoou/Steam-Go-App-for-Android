package com.miking.steam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private FrameLayout root;
    private LinearLayout content;
    private FrameLayout fab;
    private View scrim;
    private ScrollView drawer;
    private WebView webView;
    private ProgressBar progress;
    private boolean drawerOpen;
    private SharedPreferences prefs;

    private float edgeDownX;
    private float edgeDownY;
    private boolean edgeTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("steamgo", Context.MODE_PRIVATE);
        getWindow().setNavigationBarColor(Ui.BG);
        getWindow().setStatusBarColor(Ui.BG);

        root = new FrameLayout(this);
        root.setBackgroundColor(Ui.BG);

        content = buildContent();
        root.addView(content, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        fab = buildFab();
        root.addView(fab, fabParams());

        scrim = new View(this);
        scrim.setBackgroundColor(0x99000000);
        scrim.setVisibility(View.GONE);
        scrim.setAlpha(0f);
        scrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawer(false);
            }
        });
        root.addView(scrim, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        drawer = buildDrawer();
        root.addView(drawer, drawerParams());

        setContentView(root);
        root.post(new Runnable() {
            @Override
            public void run() {
                placeFab();
            }
        });

        setupWebView();
        webView.loadUrl(Web.START);
    }

    private LinearLayout buildContent() {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);

        column.addView(buildToolbar(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 64)));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Ui.PRIMARY));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0x00000000));
        progress.setVisibility(View.GONE);
        column.addView(progress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 3)));

        webView = new WebView(this);
        column.addView(webView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return column;
    }

    private LinearLayout buildToolbar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setBackgroundColor(Ui.BG);
        bar.setPadding(Ui.dp(this, 4), 0, Ui.dp(this, 4), 0);

        bar.addView(Ui.actionButton(this, R.drawable.ic_menu, Ui.TEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawer(!drawerOpen);
            }
        }));

        TextView title = Ui.text(this, "Steam Go", 20, Ui.TEXT);
        title.setTypeface(Ui.medium());
        title.setSingleLine(true);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.leftMargin = Ui.dp(this, 4);
        bar.addView(title, tp);

        bar.addView(Ui.actionButton(this, R.drawable.ic_notifications, Ui.TEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.openUniversal(MainActivity.this,
                        "https://steamcommunity.com/profiles/76561198408205511/home/invites");
            }
        }));

        bar.addView(Ui.actionButton(this, R.drawable.ic_dots, Ui.TEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        }));
        return bar;
    }

    private void showMoreDialog() {
        new Md3Dialog(this)
                .setTitle("更多选项")
                .setNegativeButton("Steam客服", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(MainActivity.this, "https://help.steampowered.com/zh-cn");
                    }
                })
                .setNeutralButton("近期购买记录", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(MainActivity.this,
                                "https://help.steampowered.com/zh-cn/wizard/HelpWithPurchase");
                    }
                })
                .setPositiveButton("在Steam上激活产品", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        BrowserActivity.openUniversal(MainActivity.this,
                                "https://store.steampowered.com/account/registerkey");
                    }
                })
                .show();
    }

    private ScrollView buildDrawer() {
        ScrollView scroll = new ScrollView(this);
        GradientDrawable bg = Ui.rounded(this, Ui.SURFACE, 16);
        scroll.setBackground(bg);
        scroll.setElevation(Ui.dp(this, 8));
        scroll.setClipToOutline(false);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, Ui.dp(this, 12), 0, Ui.dp(this, 12));

        addDrawerItem(list, R.drawable.ic_sale, "折扣优惠", new Runnable() {
            public void run() {
                BrowserActivity.openDiscounted(MainActivity.this);
            }
        });
        addDrawerItem(list, R.drawable.ic_store, "浏览商店", new Runnable() {
            public void run() {
                BrowserActivity.openStore(MainActivity.this);
            }
        });
        addDrawerItem(list, R.drawable.ic_community, "浏览社区", new Runnable() {
            public void run() {
                communityToast();
                BrowserActivity.openCommunity(MainActivity.this);
            }
        });
        addDrawerItem(list, R.drawable.ic_face, "我的资料", new Runnable() {
            public void run() {
                communityToast();
                BrowserActivity.openUniversal(MainActivity.this, "https://steamcommunity.com/login");
            }
        });
        addDrawerItem(list, R.drawable.ic_friends, "我的好友", new Runnable() {
            public void run() {
                communityToast();
                BrowserActivity.openFriends(MainActivity.this);
            }
        });
        addDrawerItem(list, R.drawable.ic_activity, "好友动态", new Runnable() {
            public void run() {
                communityToast();
                BrowserActivity.openUniversal(MainActivity.this,
                        "https://steamcommunity.com/id/username/home/");
            }
        });
        addDrawerItem(list, R.drawable.ic_wishlist, "愿望单", new Runnable() {
            public void run() {
                BrowserActivity.openUniversal(MainActivity.this, "https://store.steampowered.com/wishlist");
            }
        });
        addDrawerItem(list, R.drawable.ic_cart, "购物车", new Runnable() {
            public void run() {
                BrowserActivity.openUniversal(MainActivity.this, "https://store.steampowered.com/cart");
            }
        });
        addDrawerItem(list, R.drawable.ic_info, "关于", new Runnable() {
            public void run() {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        scroll.addView(list, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scroll;
    }

    private void addDrawerItem(LinearLayout list, int icon, String label, final Runnable action) {
        LinearLayout row = Ui.drawerRow(this, icon, Ui.TEXT_DIM, label, Ui.TEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawer(false, false);
                action.run();
            }
        });
        list.addView(row);
    }

    private void communityToast() {
        Toast.makeText(this,
                "该页面访问的域名位于Steam社区\n(steamcommunity.com)下\n中国大陆地区可能需要科学上网才能访问",
                Toast.LENGTH_LONG).show();
    }

    private FrameLayout.LayoutParams drawerParams() {
        int screenH = getResources().getDisplayMetrics().heightPixels;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Ui.dp(this, 280), screenH - Ui.dp(this, 96));
        lp.leftMargin = Ui.dp(this, 8);
        lp.topMargin = Ui.dp(this, 48);
        return lp;
    }

    private FrameLayout buildFab() {
        FrameLayout box = new FrameLayout(this);
        GradientDrawable bg = Ui.rounded(this, Ui.FAB, 16);
        box.setBackground(bg);
        box.setElevation(Ui.dp(this, 6));
        box.setClickable(true);
        ImageView icon = Ui.icon(this, R.drawable.ic_fltbtn, Ui.ON_FAB, 24);
        box.addView(icon);
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.openUniversal(MainActivity.this,
                        "https://help.steampowered.com/zh-cn/accountdata");
            }
        });
        box.setOnTouchListener(new FabTouch());
        return box;
    }

    private FrameLayout.LayoutParams fabParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Ui.dp(this, 56), Ui.dp(this, 56));
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.leftMargin = 0;
        lp.topMargin = 0;
        return lp;
    }

    private final class FabTouch implements View.OnTouchListener {
        private float downRawX;
        private float downRawY;
        private float startX;
        private float startY;
        private boolean moved;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downRawX = event.getRawX();
                    downRawY = event.getRawY();
                    startX = v.getX();
                    startY = v.getY();
                    moved = false;
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    float dx = event.getRawX() - downRawX;
                    float dy = event.getRawY() - downRawY;
                    if (Math.abs(dx) > Ui.dp(MainActivity.this, 6) || Math.abs(dy) > Ui.dp(MainActivity.this, 6)) {
                        moved = true;
                    }
                    int maxX = root.getWidth() - v.getWidth();
                    int maxY = root.getHeight() - v.getHeight();
                    if (maxX < 0) {
                        maxX = 0;
                    }
                    if (maxY < 0) {
                        maxY = 0;
                    }
                    float nx = Math.max(0, Math.min(maxX, startX + dx));
                    float ny = Math.max(0, Math.min(maxY, startY + dy));
                    v.setX(nx);
                    v.setY(ny);
                    return true;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!moved) {
                        v.performClick();
                    } else {
                        prefs.edit().putFloat("fab_x", v.getX()).putFloat("fab_y", v.getY()).apply();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    private void setupWebView() {
        Web.setup(webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return Web.route(MainActivity.this, url, true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                Web.inject(view);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                if (newProgress >= 100) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && fab != null) {
            fab.post(new Runnable() {
                @Override
                public void run() {
                    placeFab();
                }
            });
        }
    }

    private void placeFab() {
        if (fab == null || root.getWidth() <= 0 || fab.getWidth() <= 0) {
            return;
        }
        int maxX = Math.max(0, root.getWidth() - fab.getWidth());
        int maxY = Math.max(0, root.getHeight() - fab.getHeight());
        float x = prefs.getFloat("fab_x", -1f);
        float y = prefs.getFloat("fab_y", -1f);
        if (!prefs.getBoolean("fab_placed", false) || x < 0 || y < 0) {
            x = maxX - Ui.dp(this, 16);
            y = maxY - Ui.dp(this, 96);
        }
        x = Math.max(0, Math.min(maxX, x));
        y = Math.max(0, Math.min(maxY, y));
        fab.setX(x);
        fab.setY(y);
        prefs.edit().putFloat("fab_x", x).putFloat("fab_y", y).putBoolean("fab_placed", true).apply();
    }

    private void setDrawer(boolean open) {
        setDrawer(open, true);
    }

    private void setDrawer(boolean open, boolean animate) {
        drawerOpen = open;
        float hidden = -(drawer.getWidth() + Ui.dp(this, 8));
        if (drawer.getWidth() == 0) {
            hidden = -Ui.dp(this, 288);
        }
        final float target = open ? 0f : hidden;
        if (!animate) {
            drawer.setTranslationX(target);
            scrim.setVisibility(open ? View.VISIBLE : View.GONE);
            scrim.setAlpha(open ? 1f : 0f);
            content.setScaleX(open ? 0.92f : 1f);
            content.setScaleY(open ? 0.92f : 1f);
            content.setTranslationX(open ? Ui.dp(this, 8) : 0);
            return;
        }
        drawer.animate().translationX(target).setDuration(220).start();
        if (open) {
            scrim.setVisibility(View.VISIBLE);
        }
        scrim.animate().alpha(open ? 1f : 0f).setDuration(220).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (!drawerOpen) {
                    scrim.setVisibility(View.GONE);
                }
            }
        }).start();
        content.setPivotX(content.getWidth() * 0.5f);
        content.setPivotY(content.getHeight() * 0.5f);
        content.animate()
                .scaleX(open ? 0.92f : 1f)
                .scaleY(open ? 0.92f : 1f)
                .translationX(open ? Ui.dp(this, 8) : 0)
                .setDuration(220)
                .start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!drawerOpen) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    edgeTracking = ev.getX() < Ui.dp(this, 24);
                    edgeDownX = ev.getX();
                    edgeDownY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (edgeTracking) {
                        float dx = ev.getX() - edgeDownX;
                        float dy = Math.abs(ev.getY() - edgeDownY);
                        if (dx > Ui.dp(this, 48) && Math.abs(dx) > dy) {
                            setDrawer(true);
                            edgeTracking = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    edgeTracking = false;
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (drawerOpen) {
            setDrawer(false);
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        new Md3Dialog(this)
                .setTitle("确定要退出Steam Go吗？")
                .setPositiveButton("退出", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 0xFFFF0000)
                .show();
    }
}
