package com.cantang.jsexp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cantang.jsexp.javatest.JavaTester;
import com.cantang.jsexp.webview.WebViewTester;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static final String GAP = "-----------\n";
    private TextView resultView;
    private TestContext DEFAULT_CONTEXT = new TestContext.Builder()
            .setExecuteTimes(1000)
            .setExperimentTimes(100)
            .build();
    private TestContext testContext = DEFAULT_CONTEXT;
    private StringBuilder testResultBuilder = new StringBuilder();
    private static final String RECORD_STRING_LINE = "%3$s\n %1$s x %2$s\n";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressBar processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner targetSpinner = (Spinner) findViewById(R.id.target_algarithms);
        setUpTargetSpinner(targetSpinner);

        Spinner executeTimeSpinner = (Spinner) findViewById(R.id.execute_times);
        setUpExecuteTimeSpanner(executeTimeSpinner);

        findViewById(R.id.test_java).setOnClickListener(
                view -> testWithJava());
        findViewById(R.id.test_webview_solution).setOnClickListener(
                view -> testWithWebView());
        findViewById(R.id.test_duktape).setOnClickListener(view -> testDuktape());
        findViewById(R.id.test_v8).setOnClickListener(view -> testV8());
        findViewById(R.id.test_rhino).setOnClickListener(view -> testRhino());
        processing = (ProgressBar) findViewById(R.id.processing_progressbar);
        resultView = (TextView) findViewById(R.id.test_records);
    }

    private void updateResult(String type, long result) {
        testResultBuilder.append(GAP)
                .append(
                        String.format(RECORD_STRING_LINE,
                                testContext.getExperimentTimes(),
                                testContext.getExecuteTimes(),
                                type))
                .append(result).append("ms\n");
        resultView.setText(testResultBuilder.toString());
        scrollToBottom();
    }

    private void updateError(String type) {
        testResultBuilder.append(GAP)
                .append(
                        String.format(RECORD_STRING_LINE,
                                testContext.getExperimentTimes(),
                                testContext.getExecuteTimes(),
                                type))
                .append("error\n");
        resultView.setText(testResultBuilder.toString());
        scrollToBottom();
    }

    private void setUpExecuteTimeSpanner(Spinner executeTimeSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.execute_times_array, android.R.layout.simple_spinner_item);
        executeTimeSpinner.setAdapter(adapter);
        executeTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemAtPosition = (String) parent.getItemAtPosition(position);
                updateTarget(itemAtPosition);
                testContext = new TestContext.Builder()
                        .setExperimentTimes(MainActivity.this.testContext.getExperimentTimes())
                        .setExecuteTimes(Integer.valueOf(itemAtPosition)).build();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                testContext = DEFAULT_CONTEXT;
            }
        });
        executeTimeSpinner.setSelection(2);
    }

    private void setUpTargetSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.target_logic_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemAtPosition = (String) parent.getItemAtPosition(position);
                updateTarget(itemAtPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
    }

    private void updateTarget(String target) {

    }

    private void testWithWebView() {
        processing.setVisibility(View.VISIBLE);
        compositeDisposable.add(new WebViewTester(testContext).start(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeCost -> {
                    processing.setVisibility(View.GONE);
                    updateResultForWebViewApproach(timeCost);
                }, throwable -> {
                    updateError("WebView test");
                    processing.setVisibility(View.GONE);
                }));
    }

    private void updateResultForWebViewApproach(long timeCost) {
        testResultBuilder.append(GAP)
                .append(
                        String.format("WebView\n 1 x %1$s\n",
                                testContext.getExecuteTimes()))
                .append(timeCost).append("ms\n");
        resultView.setText(testResultBuilder.toString());
        scrollToBottom();
    }

    private void scrollToBottom() {
        ScrollView parent = (ScrollView) resultView.getParent().getParent();
        parent.fullScroll(View.FOCUS_DOWN);
    }

    private void testWithJava() {
        processing.setVisibility(View.VISIBLE);
        compositeDisposable.add(new JavaTester(testContext).start()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeCost -> {
                    processing.setVisibility(View.GONE);
                    updateResult("Java test", timeCost);
                }, throwable -> {
                    updateError("Java test");
                    processing.setVisibility(View.GONE);
                }));
    }

    private void testDuktape() {

    }


    private void testRhino() {

    }

    private void testV8() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
