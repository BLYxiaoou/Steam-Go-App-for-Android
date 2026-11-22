package com.miking.steam;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

final class Web {
    static final String START = "https://store.steampowered.com/";

    private static final int[] ZOOM_STEPS = {90, 100, 110, 125, 150};

    private static final String INJECT_JS =
            "(function(){try{" +
            "var sels=['.responsive_header','.responsive_page_menu_ctn mainmenu','#store_nav_area'," +
            "'.continue_shopping_ctn','#global_header','.responsive_page_header','.community_header'," +
            "'.responsive_page_content_overlay'];" +
            "for(var i=0;i<sels.length;i++){var n=document.querySelectorAll(sels[i]);" +
            "for(var j=0;j<n.length;j++){n[j].style.display='none';}}" +
            "var c=document.getElementsByClassName('responsive_page_content');" +
            "if(c&&c.length){c[0].style.paddingTop='0px';}" +
            "}catch(e){}})();";

    static final String CHAT_JS =
            "(function(){try{var p=document.getElementById('chat_box');if(!p)return;" +
            "}catch(e){}})();";

    private Web() {
    }

    static void setup(WebView webView) {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);
        s.setTextZoom(Ui.getWebZoom(webView.getContext()));
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportMultipleWindows(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        // Present as a current mobile Chrome instead of the "wv" WebView UA,
        // which Steam sometimes serves with a degraded layout.
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Pixel 7) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        // Steam login posts to login.steampowered.com and reads cookies from
        // both that host and store.steampowered.com. Without third-party
        // cookies the sign-in silently fails and loops back to the login page.
        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookies.setAcceptThirdPartyCookies(webView, true);
        }
        // Steam stores a "use desktop site" preference in a cookie. A stale
        // value makes the community/workshop pages render their 980px desktop
        // layout inside the WebView even with a mobile user agent.
        forceMobileLayout(cookies);

        webView.setBackgroundColor(Ui.BG);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.addJavascriptInterface(new Object(), "JsInterface");
    }

    static void persistCookies() {
        try {
            CookieManager.getInstance().flush();
        } catch (Exception ignored) {
        }
    }

    private static final String[][] MOBILE_HOSTS = {
            {"https://steamcommunity.com/", ".steamcommunity.com"},
            {"https://store.steampowered.com/", ".steampowered.com"},
    };

    /**
     * Clears any leftover "desktop site" preference and asks Steam for the
     * mobile templates. The preference may live in a host-only cookie or in a
     * domain cookie, so both variants are expired before being written.
     */
    private static void forceMobileLayout(CookieManager cookies) {
        for (String[] host : MOBILE_HOSTS) {
            String url = host[0];
            String domain = host[1];
            try {
                String all = cookies.getCookie(url);
                if (all != null) {
                    for (String part : all.split(";")) {
                        int eq = part.indexOf('=');
                        if (eq <= 0) {
                            continue;
                        }
                        String name = part.substring(0, eq).trim();
                        String value = part.substring(eq + 1).trim();
                        if (name.isEmpty()) {
                            continue;
                        }
                        if (!"desktop".equalsIgnoreCase(value) && !"full".equalsIgnoreCase(value)) {
                            continue;
                        }
                        cookies.setCookie(url, name + "=; Max-Age=0; path=/");
                        cookies.setCookie(url, name + "=; Max-Age=0; path=/; domain=" + domain);
                    }
                }
                cookies.setCookie(url, "mobileClient=mobile; Max-Age=31536000; path=/; domain=" + domain);
                cookies.setCookie(url, "mobileClient=mobile; Max-Age=31536000; path=/");
            } catch (Exception ignored) {
            }
        }
        persistCookies();
    }

    static void showPageInfo(final Activity activity, final WebView webView) {
        final String head = "URL\n" + webView.getUrl()
                + "\n\nUA\n" + webView.getSettings().getUserAgentString()
                + "\n\nCOOKIE\n" + CookieManager.getInstance().getCookie(webView.getUrl())
                + "\n\n";
        try {
            webView.evaluateJavascript(
                    "(function(){try{var m=document.querySelector('meta[name=viewport]');" +
                    "return 'innerWidth='+window.innerWidth+'\\nclientWidth='+document.documentElement.clientWidth" +
                    "+'\\nmetaViewport='+(m?m.content:'none')" +
                    "+'\\nuaDataMobile='+(navigator.userAgentData?navigator.userAgentData.mobile:'n/a');" +
                    "}catch(e){return 'err '+e;}})()",
                    new android.webkit.ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String info = value == null ? "" : value;
                            if (info.length() > 1 && info.startsWith("\"") && info.endsWith("\"")) {
                                info = info.substring(1, info.length() - 1);
                            }
                            info = info.replace("\\n", "\n").replace("\\\"", "\"");
                            new Md3Dialog(activity)
                                    .setTitle("\u7f51\u9875\u4fe1\u606f")
                                    .setMessage(head + info)
                                    .setPositiveButton("\u5173\u95ed", null)
                                    .show();
                        }
                    });
        } catch (Exception e) {
            new Md3Dialog(activity)
                    .setTitle("\u7f51\u9875\u4fe1\u606f")
                    .setMessage(head)
                    .setPositiveButton("\u5173\u95ed", null)
                    .show();
        }
    }

    static void inject(WebView webView) {
        try {
            webView.evaluateJavascript(INJECT_JS, null);
        } catch (Exception ignored) {
        }
    }

    static void showZoomDialog(final Activity activity, final WebView webView) {
        showZoomDialog(activity, webView, null);
    }

    static void showZoomDialog(final Activity activity, final WebView webView, final Runnable onChanged) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackground(Ui.rounded(activity, Ui.MD_SURFACE, 28));
        box.setPadding(0, Ui.dp(activity, 12), 0, Ui.dp(activity, 12));

        TextView title = Ui.text(activity, "网页缩放", 20, Ui.MD_ON_SURFACE);
        title.setTypeface(Ui.medium());
        title.setPadding(Ui.dp(activity, 24), Ui.dp(activity, 8),
                Ui.dp(activity, 24), Ui.dp(activity, 4));
        box.addView(title);

        TextView hint = Ui.text(activity, "也可以直接在网页上双指捏合缩放", 12, Ui.MD_ON_VARIANT);
        hint.setPadding(Ui.dp(activity, 24), 0, Ui.dp(activity, 24), Ui.dp(activity, 8));
        box.addView(hint);

        int current = Ui.getWebZoom(activity);
        for (final int step : ZOOM_STEPS) {
            boolean on = step == current;
            TextView row = Ui.text(activity, step + "%", 15, on ? Ui.MD_PRIMARY : Ui.MD_ON_SURFACE);
            if (on) {
                row.setTypeface(Ui.medium());
            }
            row.setPadding(Ui.dp(activity, 32), Ui.dp(activity, 13),
                    Ui.dp(activity, 24), Ui.dp(activity, 13));
            row.setClickable(true);
            Ui.applyRipple(row, activity, 12);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ui.setWebZoom(activity, step);
                    if (webView != null) {
                        webView.getSettings().setTextZoom(step);
                    }
                    dialog.dismiss();
                    if (onChanged != null) {
                        onChanged.run();
                    }
                }
            });
            box.addView(row);
        }

        dialog.setContentView(box);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    static void applyZoom(WebView webView) {
        if (webView != null) {
            webView.getSettings().setTextZoom(Ui.getWebZoom(webView.getContext()));
        }
    }

    static boolean isHttp(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * @param root true for the main activity, which routes store pages into sub pages.
     * @return true when the navigation was handled and must not load in this WebView.
     */
    static boolean route(Activity activity, String url, boolean root) {
        if (url == null) {
            return true;
        }
        if (!isHttp(url)) {
            if (url.startsWith("javascript:")) {
                return true;
            }
            String lower = url.toLowerCase(java.util.Locale.US);
            // With a mobile user agent Steam tries to hand off to the official
            // Steam app through steam:// links and intent:// URIs. Inside this
            // client that hand-off throws the user out of the app as soon as a
            // page opens, so swallow it and keep browsing here.
            if (lower.startsWith("steam://") || lower.startsWith("intent:")
                    || lower.contains("scheme=steam") || lower.contains("package=com.valvesoftware")) {
                return true;
            }
            if (url.contains("://")) {
                Ui.openExternal(activity, url);
                return true;
            }
            return true;
        }

        if (!root) {
            // Inside a browser page, let the WebView handle normal navigation.
            // Otherwise every store app link would spawn another activity stack.
            return false;
        }

        if (url.contains("steamcommunity.com/chat")) {
            BrowserActivity.openFriends(activity);
            return true;
        }

        if (url.contains("steamcommunity.com/app/") && url.contains("/workshop")) {
            BrowserActivity.openWorkshop(activity, url);
            return true;
        }

        if (url.contains("steamcommunity.com/workshop")
                || url.contains("steamcommunity.com/my/subscribedworkshopitems")
                || url.contains("steamcommunity.com/my/workshopfiles")) {
            BrowserActivity.openWorkshop(activity, url);
            return true;
        }

        if (url.contains("steamcommunity.com") || url.contains("help.steampowered.com")) {
            BrowserActivity.openUniversal(activity, url);
            return true;
        }

        if (url.contains("store.steampowered.com/app")
                || url.contains("store.steampowered.com/account/registerkey")
                || url.contains("store.steampowered.com/account/wishlist")
                || url.contains("store.steampowered.com/account/cart")) {
            BrowserActivity.openUniversal(activity, url);
            return true;
        }

        // Steam moved the wishlist and cart to top level paths. Keep the home
        // WebView on the store front page instead of letting it drift away.
        if (url.contains("store.steampowered.com/wishlist")
                || url.contains("store.steampowered.com/cart")
                || url.contains("store.steampowered.com/points")) {
            BrowserActivity.openUniversal(activity, url);
            return true;
        }

        if (url.contains("store.steampowered.com/search")) {
            BrowserActivity.openUniversal(activity, url);
            return true;
        }

        return false;
    }

    static void openUrl(Activity activity, String url) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
