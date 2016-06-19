package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;

import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.fragments.WebViewFragment;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by filles-dator on 2015-06-21.
 */
public class NewsWebViewFragment extends WebViewFragment {

    public static NewsWebViewFragment newInstance(String url){
        NewsWebViewFragment fragment = new NewsWebViewFragment();

        Bundle args = new Bundle();
        args.putString("URL", url);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebView webView = getWebView();
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(getArguments().getString("URL"));

        String url = getArguments().getString("url");
        webView.loadUrl(url);
    }

}
