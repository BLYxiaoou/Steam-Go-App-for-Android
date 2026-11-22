package com.miking.steam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

final class Web {
    static final String START = "https://store.steampowered.com/account";

    private static final String INJECT_JS =
            "(function(){try{" +
            "var sels=['.responsive_header','.responsive_page_menu_ctn mainmenu','#store_nav_area','.continue_shopping_ctn'];" +
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
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportMultipleWindows(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        // Present as a current mobile Chrome instead of the "wv" WebView UA,
        // which Steam sometimes serves with a degraded layout.
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Pixel 7) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        webView.setBackgroundColor(Ui.BG);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.addJavascriptInterface(new Object(), "JsInterface");
    }

    static void inject(WebView webView) {
        try {
            webView.evaluateJavascript(INJECT_JS, null);
        } catch (Exception ignored) {
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
            if (url.startsWith("intent:") || url.contains("://")) {
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

        if (url.contains("store.steampowered.com/search")) {
            BrowserActivity.openUniversal(activity, url);
            return true;
        }

        if (url.contains("store.steampowered.com/login")) {
            showLoginHint(activity);
            return false;
        }

        return false;
    }

    static void showLoginHint(final Activity activity) {
        new Md3Dialog(activity)
                .setTitle("Steam账户未登录")
                .setMessage("Steam Go相关服务依赖Steam账户，登录以获得最佳体验")
                .setPositiveButton("知道了", null, Ui.MD_PRIMARY)
                .show();
    }

    static void openUrl(Activity activity, String url) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
