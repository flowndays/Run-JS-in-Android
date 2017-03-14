package com.cantang.jsexp.webview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.cantang.jsexp.Irrelevant;
import com.cantang.jsexp.TestContext;
import com.cantang.jsexp.Tester;

import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

/**
 * Created by cantang on 3/13/17.
 */

public class WebViewTester extends Tester {

    private static final String AUTO_RUN_TEMPLATE = "<script type=\"text/javascript\">%1$s</script>";
    private static final String JS_SCRIPT = "var text = '123456789012345';\n"
            + "var validateBankCardNumber = function(text) {\n"
            + "    if (text == null) { return false; }\n"
            + "    return /\\d{15}/g.test(text);\n"
            + "};\n"
            + "\n"
            + "validateBankCardNumber(text);\n"
            + "%1$s.onResult();";

    private final PublishSubject<Irrelevant> resultSubject = PublishSubject.create();

    private SingleSubject<Long> singleSubject;
    private long jsStartTimeStamp;

    private int test_times = 0;
    private String jsString;
    private WebView webView;

    public WebViewTester(TestContext testContext) {
        super(testContext);
    }

    /**
     * start JS test
     *
     * @param context Context
     * @return Single
     */
    public Flowable<Long> start(@NonNull Context context) {
        singleSubject = SingleSubject.create();

        resultSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Irrelevant>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Irrelevant iRelevant) {
                        doTest();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        singleSubject.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        long timeCost = (System.nanoTime() - jsStartTimeStamp) / 1000000;
                        Log.d("tctest",
                                "repeat:" + (testContext.getExecuteTimes() - 1) + ","
                                        + "js time: " + timeCost);
                        singleSubject.onSuccess(timeCost);
                    }
                });
        webView = createWebView(context);
        jsString = createJSString();
        jsStartTimeStamp = System.nanoTime();
        doTest();
        return singleSubject.toFlowable();
    }

    private void doTest() {
        webView.loadData(jsString, "text/html", "utf-8");
//        webView.evaluateJavascript(jsString, new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });
    }

    /**
     * JS call back, don't call it from Java
     */
    @JavascriptInterface
    public void onResult() throws Exception {
        if (test_times++ < testContext.getExecuteTimes()) {
            resultSubject.onNext(Irrelevant.IRRELEVANT);
        } else {
            resultSubject.onComplete();
        }
    }

    /**
     * Run testContext.getExecuteTimes() times to make the result significant.
     *
     * @return JS test logic
     */
    private String createJSString() {
        return String.format(
                Locale.US,
                AUTO_RUN_TEMPLATE,
                String.format(
                        Locale.US,
                        JS_SCRIPT,
                        "JSBridge"));
    }

    private WebView createWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setVisibility(View.GONE);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(this, "JSBridge");

        return webView;
    }
}
