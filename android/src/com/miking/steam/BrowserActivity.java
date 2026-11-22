package com.miking.steam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BrowserActivity extends Activity {
    private static final String EXTRA_MODE = "mode";
    private static final String EXTRA_URL = "url";

    private static final String MODE_UNIVERSAL = "universal";
    private static final String MODE_STORE = "store";
    private static final String MODE_COMMUNITY = "community";
    private static final String MODE_DISCOUNTED = "discounted";
    private static final String MODE_FRIENDS = "friends";
    private static final String MODE_WORKSHOP = "workshop";

    static void openUniversal(Activity activity, String url) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_UNIVERSAL);
        i.putExtra(EXTRA_URL, url);
        activity.startActivity(i);
    }

    static void openStore(Activity activity) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_STORE);
        activity.startActivity(i);
    }

    static void openCommunity(Activity activity) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_COMMUNITY);
        activity.startActivity(i);
    }

    static void openDiscounted(Activity activity) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_DISCOUNTED);
        activity.startActivity(i);
    }

    static void openFriends(Activity activity) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_FRIENDS);
        activity.startActivity(i);
    }

    static void openWorkshop(Activity activity) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_WORKSHOP);
        activity.startActivity(i);
    }

    static void openWorkshop(Activity activity, String url) {
        Intent i = new Intent(activity, BrowserActivity.class);
        i.putExtra(EXTRA_MODE, MODE_WORKSHOP);
        i.putExtra(EXTRA_URL, url);
        activity.startActivity(i);
    }

    private static final String[][] STORE_TABS = {
            {"精选", "https://store.steampowered.com"},
            {"探索", "https://store.steampowered.com/explore/"},
            {"鉴赏家", "https://store.steampowered.com/curators/recommendedcurators/"},
            {"新闻", "https://store.steampowered.com/news/"},
            {"统计", "https://store.steampowered.com/stats/"},
    };

    private static final String[][] COMMUNITY_TABS = {
            {"主页", "https://steamcommunity.com"},
            {"讨论", "https://steamcommunity.com/discussions"},
            {"市场", "https://steamcommunity.com/market"},
            {"实况直播", "https://steamcommunity.com/?subsection=broadcasts"},
    };

    private static final String[][] DISCOUNT_TABS = {
            {"特惠", "https://store.steampowered.com/specials#tab=TopSellers"},
            {"周期特惠", "https://store.steampowered.com/search/?filter=weeklongdeals"},
            {"低于40\u00A5", "https://store.steampowered.com/search/?filter=ut1&category1=998&os="},
            {"低于20\u00A5", "https://store.steampowered.com/search/?filter=ut2&category1=998&os="},
    };

    private static final String[][] WORKSHOP_TABS = {
            {"热门", "https://steamcommunity.com/workshop/browse/?browsesort=trend&section=readytouseitems"},
            {"最新", "https://steamcommunity.com/workshop/browse/?browsesort=mostrecent&section=readytouseitems"},
            {"我的订阅", "https://steamcommunity.com/my/subscribedworkshopitems"},
            {"我的收藏", "https://steamcommunity.com/my/favorites/"},
            {"我的作品", "https://steamcommunity.com/my/workshopfiles/"},
    };

    private static final String[][] WORKSHOP_CATEGORIES = {
            {"创意工坊首页", "https://steamcommunity.com/workshop/"},
            {"Wallpaper Engine", "https://steamcommunity.com/app/431960/workshop/"},
            {"反恐精英 2", "https://steamcommunity.com/app/730/workshop/"},
            {"Dota 2", "https://steamcommunity.com/app/570/workshop/"},
            {"绝地求生", "https://steamcommunity.com/app/578080/workshop/"},
            {"求生之路 2", "https://steamcommunity.com/app/550/workshop/"},
            {"盖瑞模组", "https://steamcommunity.com/app/4000/workshop/"},
            {"泰拉瑞亚", "https://steamcommunity.com/app/105600/workshop/"},
            {"城市：天际线", "https://steamcommunity.com/app/255710/workshop/"},
            {"环世界", "https://steamcommunity.com/app/294100/workshop/"},
            {"群星", "https://steamcommunity.com/app/281990/workshop/"},
            {"方舟：生存进化", "https://steamcommunity.com/app/346110/workshop/"},
            {"上古卷轴 5：天际", "https://steamcommunity.com/app/489830/workshop/"},
            {"辐射 4", "https://steamcommunity.com/app/377160/workshop/"},
            {"欧洲卡车模拟 2", "https://steamcommunity.com/app/227300/workshop/"},
            {"饥荒联机版", "https://steamcommunity.com/app/322330/workshop/"},
            {"无人深空", "https://steamcommunity.com/app/275850/workshop/"},
    };

    private static final String[][] STORE_CATEGORIES = {
            {"最近更新的产品", "https://store.steampowered.com/updated/myapps/"},
            {"查看热门游戏标签", "https://store.steampowered.com/tag/browse/?snr=1_4_4__12"},
            {"好友中热门", "https://store.steampowered.com/recommended/friendactivity/"},
            {"免费游玩", "https://store.steampowered.com/genre/Free%20to%20Play/"},
            {"抢先体验", "https://store.steampowered.com/genre/Early%20Access/"},
            {"试玩", "https://store.steampowered.com/freestuff/demos/"},
            {"虚拟现实", "https://store.steampowered.com/vr/"},
            {"支持 Steam 控制器", "https://store.steampowered.com/controller/"},
            {"Steam 上的网吧游戏", "https://store.steampowered.com/pccafe/"},
            {"远程畅玩", "https://store.steampowered.com/remoteplay_hub/"},
            {"休闲", "https://store.steampowered.com/tags/zh-cn/\u4f11\u95f2/"},
            {"体育", "https://store.steampowered.com/tags/zh-cn/\u4f53\u80b2/"},
            {"冒险", "https://store.steampowered.com/tags/zh-cn/\u5192\u9669/"},
            {"动作", "https://store.steampowered.com/tags/zh-cn/\u52a8\u4f5c/"},
            {"大型多人在线", "https://store.steampowered.com/tags/zh-cn/\u5927\u578b\u591a\u4eba\u5728\u7ebf/"},
            {"模拟", "https://store.steampowered.com/tags/zh-cn/\u6a21\u62df/"},
            {"独立", "https://store.steampowered.com/tags/zh-cn/\u72ec\u7acb/"},
            {"竞速", "https://store.steampowered.com/tags/zh-cn/\u7ade\u901f/"},
            {"策略", "https://store.steampowered.com/tags/zh-cn/\u7b56\u7565/"},
            {"角色扮演", "https://store.steampowered.com/tags/zh-cn/\u89d2\u8272\u626e\u6f14/"},
            {"羞羞", "https://store.steampowered.com/tags/cn/Sexual+Content"},
            {"Linux 平台的游戏", "https://store.steampowered.com/linux"},
            {"Mac OS 平台的游戏", "https://store.steampowered.com/macos"},
            {"Steam软件中心", "https://store.steampowered.com/software/"},
            {"动画制作与建模", "https://store.steampowered.com/tags/zh-cn/\u52a8\u753b\u5236\u4f5c\u8207\u5efa\u6a21/"},
            {"实用工具", "https://store.steampowered.com/tags/zh-cn/\u5b9e\u7528\u5de5\u5177/"},
            {"教育", "https://store.steampowered.com/tags/zh-cn/\u6559\u80b2/"},
            {"游戏开发", "https://store.steampowered.com/tags/zh-cn/\u6e38\u620f\u5f00\u53d1/"},
            {"照片编辑", "https://store.steampowered.com/tags/zh-cn/\u7167\u7247\u7f16\u8f91/"},
            {"网络出版", "https://store.steampowered.com/tags/zh-cn/\u7f51\u7edc\u51fa\u7248/"},
            {"视频制作", "https://store.steampowered.com/tags/zh-cn/\u89c6\u9891\u5236\u4f5c/"},
            {"设计与插画", "https://store.steampowered.com/tags/zh-cn/\u8bbe\u8ba1\u4e0e\u63d2\u753b/"},
            {"音频制作", "https://store.steampowered.com/tags/zh-cn/\u97f3\u9891\u5236\u4f5c/"},
            {"Valve Index\u00AE", "https://store.steampowered.com/valveindex?snr=1_5_9__12"},
            {"Steam 控制器", "https://store.steampowered.com/app/353370/Steam_Controller"},
            {"Steam 流式盒", "https://store.steampowered.com/app/353380/Steam_Link"},
            {"HTC Vive", "https://store.steampowered.com/app/358040/HTC_Vive/"},
            {"关于 Steam", "https://store.steampowered.com/about"},
    };

    private static final String[] STORE_CATEGORY_ICONS = {
            "upload", "elevation_rise", "fire", "ic_money_off_black_48dp", "ic_healing_black_48dp",
            "ic_disc_full_black_48dp", "hololens", "google_controller", "television", "nfc_tap",
            "ic_extension_black_48dp", "football_helmet", "sword", "run", "ic_map_black_48dp",
            "ic_graphic_eq_black_48dp", "ic_nature_people_black_48dp", "ic_motorcycle_black_48dp", "castle", "ic_face_black_48dp",
            "ic_airline_seat_individual_suite_black_48dp", "linux", "apple", "ic_widgets_black_48dp", "printer_3d",
            "ic_explore_black_48dp", "ic_child_care_black_48dp", "ic_games_black_48dp", "ic_photo_filter_black_48dp", "ic_photo_album_black_48dp",
            "ic_camera_roll_black_48dp", "ic_collections_black_48dp", "ic_audiotrack_black_48dp", "hololens", "google_controller",
            "ic_router_black_48dp", "hololens", "information",
    };

    private String mode = MODE_UNIVERSAL;
    private String startUrl;

    private FrameLayout root;
    private LinearLayout content;
    private WebView webView;
    private ProgressBar progress;
    private TextView titleView;
    private EditText searchView;
    private View errorView;
    private int themeModeAtCreate;

    private View scrim;
    private ScrollView drawer;
    private boolean drawerOpen;

    private String[][] tabs;
    private final java.util.List<View> tabIndicators = new java.util.ArrayList<View>();
    private final java.util.List<TextView> tabLabels = new java.util.ArrayList<TextView>();

    private float edgeDownX;
    private float edgeDownY;
    private boolean edgeTracking;
    private int autoRetries;
    private boolean retryScheduled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeModeAtCreate = Ui.getThemeMode(this);
        Ui.applyTheme(this);
        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) {
            mode = MODE_UNIVERSAL;
        }
        startUrl = getIntent().getStringExtra(EXTRA_URL);

        root = new FrameLayout(this);
        root.setBackgroundColor(Ui.BG);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(buildToolbar(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 64)));

        tabs = tabsForMode();
        if (tabs != null) {
            content.addView(buildTabs(tabs), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 48)));
        }

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Ui.PRIMARY));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0x00000000));
        progress.setVisibility(View.GONE);
        content.addView(progress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 3)));

        webView = new WebView(this);
        FrameLayout webBox = new FrameLayout(this);
        webBox.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        errorView = buildErrorView();
        webBox.addView(errorView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.addView(webBox, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(content, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (MODE_STORE.equals(mode) || MODE_WORKSHOP.equals(mode)) {
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

            drawer = buildDrawer(MODE_STORE.equals(mode) ? STORE_CATEGORIES : WORKSHOP_CATEGORIES);
            root.addView(drawer, drawerParams());
        }

        setContentView(root);
        if (drawer != null) {
            setDrawer(false, false);
        }
        setupWebView();
        loadInitial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (themeModeAtCreate != Ui.getThemeMode(this)) {
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Web.persistCookies();
    }

    private View buildErrorView() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackgroundColor(Ui.BG);
        box.setPadding(Ui.dp(this, 32), Ui.dp(this, 32), Ui.dp(this, 32), Ui.dp(this, 32));

        TextView title = Ui.text(this, "网页无法打开", 22, Ui.TEXT);
        title.setTypeface(Ui.medium());
        box.addView(title);

        TextView message = Ui.text(this, "请检查网络连接后重试\nSteam 商店与社区在中国大陆可能需要网络加速", 14, Ui.TEXT_DIM);
        message.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.topMargin = Ui.dp(this, 12);
        box.addView(message, mp);

        TextView retry = Ui.text(this, "重试", 14, Ui.ON_FAB);
        retry.setTypeface(Ui.medium());
        retry.setGravity(Gravity.CENTER);
        retry.setPadding(Ui.dp(this, 28), Ui.dp(this, 10), Ui.dp(this, 28), Ui.dp(this, 10));
        GradientDrawable retryBg = Ui.rounded(this, Ui.FAB, 20);
        retry.setBackground(new android.graphics.drawable.RippleDrawable(
                android.content.res.ColorStateList.valueOf(0x33FFFFFF), retryBg, retryBg));
        retry.setClickable(true);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideError();
                webView.reload();
            }
        });
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.topMargin = Ui.dp(this, 24);
        box.addView(retry, rp);

        TextView external = Ui.text(this, "用系统浏览器打开", 14, Ui.PRIMARY);
        external.setTypeface(Ui.medium());
        external.setPadding(Ui.dp(this, 16), Ui.dp(this, 12), Ui.dp(this, 16), Ui.dp(this, 12));
        external.setClickable(true);
        Ui.applyRipple(external, this, 20);
        external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = webView.getUrl();
                if (url == null || url.length() == 0) {
                    url = startUrl;
                }
                if (url != null) {
                    Ui.openExternal(BrowserActivity.this, url);
                }
            }
        });
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ep.topMargin = Ui.dp(this, 8);
        box.addView(external, ep);

        box.setVisibility(View.GONE);
        return box;
    }

    private void showError() {
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void hideError() {
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }

    private void handleLoadError(int errorCode, String description) {
        if (errorCode == WebViewClient.ERROR_UNKNOWN
                || (description != null && description.contains("ERR_ABORTED"))) {
            return;
        }
        if (autoRetries < 2) {
            autoRetries++;
            if (!retryScheduled) {
                retryScheduled = true;
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        retryScheduled = false;
                        hideError();
                        webView.reload();
                    }
                }, 800L);
            }
            return;
        }
        showError();
    }

    private String[][] tabsForMode() {
        if (MODE_STORE.equals(mode)) {
            return STORE_TABS;
        }
        if (MODE_COMMUNITY.equals(mode)) {
            return COMMUNITY_TABS;
        }
        if (MODE_DISCOUNTED.equals(mode)) {
            return DISCOUNT_TABS;
        }
        if (MODE_WORKSHOP.equals(mode)) {
            return WORKSHOP_TABS;
        }
        return null;
    }

    private String titleForMode() {
        if (MODE_STORE.equals(mode)) {
            return "\u5546\u5e97";
        }
        if (MODE_COMMUNITY.equals(mode)) {
            return "Steam \u793e\u533a";
        }
        if (MODE_DISCOUNTED.equals(mode)) {
            return "\u6298\u6263\u4f18\u60e0";
        }
        if (MODE_FRIENDS.equals(mode)) {
            return "Steam \u597d\u53cb";
        }
        if (MODE_WORKSHOP.equals(mode)) {
            return "\u521b\u610f\u5de5\u574a";
        }
        return "\u6b63\u5728\u52a0\u8f7d...";
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
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        }));

        if (MODE_WORKSHOP.equals(mode)) {
            bar.addView(Ui.actionButton(this, R.drawable.ic_menu, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDrawer(!drawerOpen);
                }
            }));
        }

        if (MODE_STORE.equals(mode) || MODE_WORKSHOP.equals(mode)) {
            searchView = new EditText(this);
            searchView.setHint(MODE_WORKSHOP.equals(mode) ? "\u641c\u7d22\u521b\u610f\u5de5\u574a" : "\u641c\u7d22\u5546\u5e97");
            searchView.setTextSize(14);
            searchView.setTextColor(Ui.TEXT_DIM);
            searchView.setHintTextColor(0xFF79747E);
            searchView.setSingleLine(true);
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchView.setBackground(Ui.rounded(this, Ui.SURFACE_HIGH, 28));
            searchView.setPadding(Ui.dp(this, 16), 0, Ui.dp(this, 16), 0);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, Ui.dp(this, 40), 1f);
            sp.leftMargin = Ui.dp(this, 4);
            sp.rightMargin = Ui.dp(this, 4);
            bar.addView(searchView, sp);
            searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN)) {
                        submitSearch();
                        return true;
                    }
                    return false;
                }
            });
        } else {
            titleView = Ui.text(this, titleForMode(), 20, Ui.TEXT);
            titleView.setTypeface(Ui.medium());
            titleView.setSingleLine(true);
            titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tp.leftMargin = Ui.dp(this, 4);
            bar.addView(titleView, tp);
        }

        if (MODE_STORE.equals(mode) || MODE_WORKSHOP.equals(mode)) {
            bar.addView(Ui.actionButton(this, R.drawable.ic_refresh, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.reload();
                }
            }));
            bar.addView(Ui.actionButton(this, R.drawable.ic_dots, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog();
                }
            }));
        } else if (MODE_COMMUNITY.equals(mode) || MODE_DISCOUNTED.equals(mode) || MODE_FRIENDS.equals(mode)) {
            bar.addView(Ui.actionButton(this, R.drawable.ic_refresh, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.reload();
                }
            }));
            bar.addView(Ui.actionButton(this, R.drawable.ic_dots, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog();
                }
            }));
        } else {
            bar.addView(Ui.actionButton(this, R.drawable.ic_refresh, Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.reload();
                }
            }));
        }
        return bar;
    }

    private View buildTabs(String[][] items) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setBackgroundColor(Ui.BG);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        for (int i = 0; i < items.length; i++) {
            final int index = i;
            LinearLayout tab = new LinearLayout(this);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(Ui.dp(this, 16), 0, Ui.dp(this, 16), 0);

            TextView label = Ui.text(this, items[i][0], 14, Ui.TEXT);
            tab.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1f));

            View indicator = new View(this);
            indicator.setBackgroundColor(i == 0 ? Ui.PRIMARY : 0x00000000);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 3));
            tab.addView(indicator, ip);

            tabLabels.add(label);
            tabIndicators.add(indicator);
            tab.setClickable(true);
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTab(index, true);
                }
            });
            row.addView(tab, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Ui.dp(this, 48)));
        }
        scroll.addView(row, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return scroll;
    }

    private void selectTab(int index, boolean load) {
        for (int i = 0; i < tabIndicators.size(); i++) {
            tabIndicators.get(i).setBackgroundColor(i == index ? Ui.PRIMARY : 0x00000000);
            tabLabels.get(i).setTextColor(i == index ? Ui.TEXT : Ui.TEXT_DIM);
        }
        if (load && tabs != null && index >= 0 && index < tabs.length) {
            webView.loadUrl(Ui.encodeUrl(tabs[index][1]));
        }
    }

    private void clearTabSelection() {
        for (int i = 0; i < tabIndicators.size(); i++) {
            tabIndicators.get(i).setBackgroundColor(0x00000000);
            tabLabels.get(i).setTextColor(Ui.TEXT_DIM);
        }
    }

    private ScrollView buildDrawer(String[][] items) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackground(Ui.roundedRight(this, Ui.SURFACE, 16));
        scroll.setElevation(Ui.dp(this, 8));
        scroll.setClipToOutline(true);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, Ui.dp(this, 8), 0, Ui.dp(this, 12));

        TextView header = Ui.text(this, MODE_WORKSHOP.equals(mode) ? "热门游戏工坊" : "商店分类", 18, Ui.TEXT);
        header.setTypeface(Ui.medium());
        header.setPadding(Ui.dp(this, 24), Ui.dp(this, 16), Ui.dp(this, 24), Ui.dp(this, 8));
        list.addView(header);
        list.addView(Ui.divider(this));

        for (int i = 0; i < items.length; i++) {
            final String url = items[i][1];
            int iconRes = 0;
            if (MODE_WORKSHOP.equals(mode)) {
                iconRes = R.drawable.ic_extension_black_48dp;
            } else if (i < STORE_CATEGORY_ICONS.length) {
                iconRes = getResources().getIdentifier(STORE_CATEGORY_ICONS[i], "drawable", getPackageName());
            }
            LinearLayout row = Ui.drawerRow(this, iconRes, Ui.TEXT_DIM, items[i][0], Ui.TEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDrawer(false, false);
                    clearTabSelection();
                    webView.loadUrl(Ui.encodeUrl(url));
                }
            });
            list.addView(row);
        }

        scroll.addView(list, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scroll;
    }

    private FrameLayout.LayoutParams drawerParams() {
        return new FrameLayout.LayoutParams(Ui.dp(this, 320), ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void submitSearch() {
        if (searchView == null) {
            return;
        }
        String term = searchView.getText().toString().trim();
        if (term.length() == 0) {
            return;
        }
        clearTabSelection();
        if (MODE_WORKSHOP.equals(mode)) {
            webView.loadUrl("https://steamcommunity.com/workshop/browse/?searchtext=" + Uri.encode(term));
        } else {
            webView.loadUrl("https://store.steampowered.com/search/?term=" + Uri.encode(term));
        }
        searchView.clearFocus();
        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    private void showMoreDialog() {
        Md3Dialog dialog = new Md3Dialog(this);
        if (MODE_STORE.equals(mode)) {
            dialog.setTitle("\u66f4\u591a\u9009\u9879")
                    .setNegativeButton("Steam \u5b9e\u9a8c\u5ba4", open("https://store.steampowered.com/labs/"))
                    .setNeutralButton("Steam \u793c\u54c1\u5361", open("https://store.steampowered.com/digitalgiftcards/"))
                    .setPositiveButton("\u5f00\u53d1\u8005\u4e0e\u53d1\u884c\u5546\u9996\u9875", open("https://store.steampowered.com/publisher/"))
                    .show();
        } else if (MODE_COMMUNITY.equals(mode)) {
            dialog.setTitle("Steam \u793e\u533a\u4e2d\u7684\u66f4\u591a\u9009\u9879")
                    .setNegativeButton("\u521b\u610f\u5de5\u574a", open("https://steamcommunity.com/workshop"))
                    .setNeutralButton("Steam TV", open("https://steam.tv//smite"))
                    .show();
        } else if (MODE_DISCOUNTED.equals(mode)) {
            dialog.setTitle("\u66f4\u591a\u9009\u9879")
                    .setNegativeButton("\u67e5\u770b\u5305\u542bWindows\u5e73\u53f0\u7684\u4f18\u60e0",
                            load("https://store.steampowered.com/search/?specials=1&os=win"))
                    .setNeutralButton("\u67e5\u770b\u5305\u542bLinux\u5e73\u53f0\u7684\u4f18\u60e0",
                            load("https://store.steampowered.com/search/?specials=1&os=linux"))
                    .setPositiveButton("\u67e5\u770b\u5305\u542bmacOS\u5e73\u53f0\u7684\u4f18\u60e0",
                            load("https://store.steampowered.com/search/?specials=1&os=mac"))
                    .show();
        } else if (MODE_WORKSHOP.equals(mode)) {
            dialog.setTitle("\u521b\u610f\u5de5\u574a")
                    .setNegativeButton("\u521b\u610f\u5de5\u574a\u9996\u9875", load("https://steamcommunity.com/workshop/"))
                    .setNeutralButton("\u5728\u6d4f\u89c8\u5668\u4e2d\u6253\u5f00", new Md3Dialog.Action() {
                        @Override
                        public void run() {
                            String current = webView.getUrl();
                            Ui.openExternal(BrowserActivity.this,
                                    current == null ? "https://steamcommunity.com/workshop/" : current);
                        }
                    })
                    .setPositiveButton("\u5237\u65b0", new Md3Dialog.Action() {
                        @Override
                        public void run() {
                            webView.reload();
                        }
                    })
                    .show();
        } else if (MODE_FRIENDS.equals(mode)) {
            dialog.setTitle("\u66f4\u591a\u9009\u9879")
                    .setNegativeButton("\u4e0eSteam\u4e0a\u7684\u597d\u53cb\u804a\u5929", new Md3Dialog.Action() {
                        @Override
                        public void run() {
                            showChatDialog();
                        }
                    })
                    .show();
        } else {
            return;
        }
    }

    private void showChatDialog() {
        new Md3Dialog(this)
                .setTitle("\u4e0e\u597d\u53cb\u804a\u5929")
                .setMessage("\u73b0\u5728\u4f60\u53ef\u4ee5\u900f\u8fc7Steam Chat\u66f4\u52a0\u65b9\u4fbf\u7684\u5728\u79fb\u52a8\u8bbe\u5907\u4e0a\u6536\u53d1\u6d88\u606f\uff0c\u5f53\u7136\u4f60\u4e5f\u53ef\u4ee5\u7ee7\u7eed\u9009\u62e9\u4f7f\u7528Steam Go\u4e0e\u597d\u53cb\u804a\u5929")
                .setNegativeButton("\u901a\u8fc7\u7f51\u9875\u83b7\u53d6Steam Chat", open("https://store.steampowered.com/mobile"))
                .setNeutralButton("\u5728Google Play Store\u83b7\u53d6 Steam Chat", new Md3Dialog.Action() {
                    @Override
                    public void run() {
                        Ui.openExternal(BrowserActivity.this,
                                "https://play.google.com/store/apps/details?id=com.valvesoftware.android.steam.friendsui&hl=en_US");
                    }
                })
                .setPositiveButton("\u7ee7\u7eed\u4f7f\u7528Steam Go\u4e0e\u597d\u53cb\u804a\u5929", load("https://steamcommunity.com/chat/"))
                .show();
    }

    private Md3Dialog.Action open(final String url) {
        return new Md3Dialog.Action() {
            @Override
            public void run() {
                BrowserActivity.openUniversal(BrowserActivity.this, url);
            }
        };
    }

    private Md3Dialog.Action load(final String url) {
        return new Md3Dialog.Action() {
            @Override
            public void run() {
                clearTabSelection();
                webView.loadUrl(Ui.encodeUrl(url));
            }
        };
    }

    private void loadInitial() {
        String url;
        if (MODE_STORE.equals(mode)) {
            selectTab(0, false);
            url = STORE_TABS[0][1];
        } else if (MODE_COMMUNITY.equals(mode)) {
            selectTab(0, false);
            url = COMMUNITY_TABS[0][1];
        } else if (MODE_DISCOUNTED.equals(mode)) {
            selectTab(0, false);
            url = DISCOUNT_TABS[0][1];
        } else if (MODE_WORKSHOP.equals(mode)) {
            selectTab(0, false);
            url = (startUrl != null && startUrl.length() > 0) ? startUrl : WORKSHOP_TABS[0][1];
        } else if (MODE_FRIENDS.equals(mode)) {
            url = "https://steamcommunity.com/friends/";
        } else {
            url = startUrl;
        }
        if (url == null || url.length() == 0) {
            url = Web.START;
        }
        webView.loadUrl(Ui.encodeUrl(url));
    }

    private void setupWebView() {
        Web.setup(webView);
        if (MODE_FRIENDS.equals(mode)) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                webView.getSettings().setUserAgentString(ua + " Steam");
            } catch (Exception ignored) {
            }
        }
        final boolean showProgress = !MODE_FRIENDS.equals(mode);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return Web.route(BrowserActivity.this, url, false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                hideError();
                if (showProgress) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(0);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                if (errorView == null || errorView.getVisibility() != View.VISIBLE) {
                    autoRetries = 0;
                }
                Web.inject(view);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request != null && request.isForMainFrame()) {
                    CharSequence description = error.getDescription();
                    handleLoadError(error.getErrorCode(), description == null ? null : description.toString());
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (failingUrl != null && failingUrl.equals(view.getUrl())) {
                    handleLoadError(errorCode, description);
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (MODE_UNIVERSAL.equals(mode) && titleView != null) {
                    if (title != null && title.trim().length() > 0) {
                        titleView.setText(title);
                    } else {
                        titleView.setText("\u6b63\u5728\u52a0\u8f7d...");
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (showProgress) {
                    progress.setProgress(newProgress);
                    if (newProgress >= 100) {
                        progress.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setDrawer(boolean open) {
        setDrawer(open, true);
    }

    private void setDrawer(boolean open, boolean animate) {
        if (drawer == null || scrim == null) {
            return;
        }
        drawerOpen = open;
        float hidden = -(drawer.getWidth() == 0 ? Ui.dp(this, 320) : drawer.getWidth());
        final float target = open ? 0f : hidden;
        if (!animate) {
            drawer.setTranslationX(target);
            scrim.setVisibility(open ? View.VISIBLE : View.GONE);
            scrim.setAlpha(open ? 1f : 0f);
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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MODE_STORE.equals(mode) && !drawerOpen) {
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
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        finish();
    }
}
