package com.example.testAndroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.testAndroid.ConnectionMetric.State.*;


public class MyActivity extends Activity {
    private CheckBox _cbSecure;
    private EditText _etHost;
    private EditText _etPort;
    private EditText _etRequestString;
    private Button _btnConnect;
    private Spinner countrySpinner;
    private EditText _etTimeout;
    private TextView _etResult;
    private long _startTime;
    private Spinner proxySpinner;
    private EditText _proxyHost;
    private EditText _proxyPort;
    private Button _btnForward;
    private CheckBox _cbInvalidate;
    private CheckBox _cbDoor;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.w("kunj",String.valueOf(Thread.currentThread().getPriority())+" "+Thread.currentThread().getName());
        Log.w("kunj",String.valueOf(Process.getThreadPriority(Process.myTid()))+" "+Thread.currentThread().getName());

        Thread threadFromUI = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w("kunj",String.valueOf(Thread.currentThread().getPriority())+" "+Thread.currentThread().getName());
                Log.w("kunj",String.valueOf(Process.getThreadPriority(Process.myTid()))+" "+Thread.currentThread().getName());
            }
        },"threadFromUI");
        threadFromUI.start();

        Thread threadFromUIWithPriority = new Thread(new Runnable() {
            @Override
            public void run() {
                logme(Process.THREAD_PRIORITY_URGENT_AUDIO);
            }
        },"threadFromUIWithPriorityUrgentAudio");
        threadFromUIWithPriority.start();

        Thread threadFromUIWithNormal = new Thread(new Runnable() {
            @Override
            public void run() {
                logme(Process.THREAD_PRIORITY_DEFAULT);
            }
        },"threadFromUIWithPriorityNormal");
        threadFromUIWithNormal.start();

        countrySpinner = (Spinner) findViewById(R.id.spEndpoints);
        _cbInvalidate = (CheckBox) findViewById(R.id.cbInvalidate);
        _cbDoor = (CheckBox) findViewById(R.id.cbDoor);
        _cbSecure = (CheckBox) findViewById(R.id.cbSecure);
        _cbSecure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    _cbInvalidate.setEnabled(true);
                } else {
                    _cbInvalidate.setEnabled(false);
                    _cbInvalidate.setChecked(false);
                }
            }
        });
        _etHost = (EditText) findViewById(R.id.etHost);
        _etPort = (EditText) findViewById(R.id.etPort);
        _etTimeout = (EditText) findViewById(R.id.etTimeout);
        proxySpinner = (Spinner) findViewById(R.id.spProxy);
        _proxyHost = (EditText) findViewById(R.id.etProxyHost);
        _proxyPort = (EditText) findViewById(R.id.etProxyPort);
        _etRequestString = (EditText) findViewById(R.id.etRequest);
        _etResult = (TextView) findViewById(R.id.etResult);
        _btnForward = (Button) findViewById(R.id.btnForward);
        _btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share_intent = new Intent(android.content.Intent.ACTION_SEND);

                share_intent.setType("text/plain");
                share_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Connectivity Result");
                share_intent.putExtra(android.content.Intent.EXTRA_TEXT, _etResult.getText().toString());
                startActivity(share_intent);
            }
        });
        _btnConnect = (Button) findViewById(R.id.btnConnect);
        _btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectButtonClick();
            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                endpointSelectionListener(adapter, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countrySpinner.setSelection(0);
            }
        });

        proxySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
                proxySelectionListener(adapter, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                proxySpinner.setSelection(0);
            }
        });
        countrySpinner.setSelection(0);
        proxySpinner.setSelection(0);

    }

    private void logme(int threadPriority) {

        Log.w("kunj before",String.valueOf(Thread.currentThread().getPriority())+" "+Thread.currentThread().getName());
        Log.w("kunj before",String.valueOf(Process.getThreadPriority(Process.myTid()))+" "+Thread.currentThread().getName());

        Process.setThreadPriority(threadPriority);

        Log.w("kunj after",String.valueOf(Thread.currentThread().getPriority())+" "+Thread.currentThread().getName());
        Log.w("kunj after",String.valueOf(Process.getThreadPriority(Process.myTid()))+" "+Thread.currentThread().getName());
    }

    private void proxySelectionListener(AdapterView<?> adapter, int position) {
        final String proxy = adapter.getItemAtPosition(position).toString();

        if (proxy.equals("Default")) {
            _proxyHost.setText(NetworkUtil.getProxyHost());
            _proxyPort.setText(NetworkUtil.getProxyPort());
        } else {
            _proxyHost.setText(null);
            _proxyPort.setText(null);
        }
    }

    private void displayResult(final ConnectionMetric connectionMetric, final Throwable throwable) {
        runOnUI(new Runnable() {
            @Override
            public void run() {
                long totalTime = TimeUnit.MILLISECONDS.convert(System.nanoTime() - _startTime, TimeUnit.NANOSECONDS);
                final ConnectionMetric.State testState = connectionMetric.getState();
                final StringBuilder sb = new StringBuilder("Testing - ").append(_etHost.getText().toString()).append(
                        ':').append(_etPort.getText().toString())
                        .append('\n').append("Request:").append(_etRequestString.getText().toString()).append(
                                '\n').append(" ***** ").
                                append(testState == RESP_RECEIVED ? "Test Succeeded in " : "Test Failed in ").append(
                                totalTime).append(" millisec").append(" ***** ")
                        .append('\n');

                if (testState.ordinal() >= DNS_REQUESTED.ordinal()) {
                    sb.append("* DNS").append('\n');
                    if (testState.ordinal() >= DNS_RESOLVED.ordinal()) {
                        sb.append("\t\tTime (milliSec):").append(
                                connectionMetric.getDnsResolutionTime(TimeUnit.MILLISECONDS))
                                .append("\n\t\t").append(connectionMetric.getResolvedAddress());
                        if (connectionMetric.getResolvedAddress() != null && connectionMetric.getResolvedAddress().getAddress() == null) {
                            sb.append("\n\t\t").append("Host is unresolved. No address found.");
                        }
                    } else {
                        sb.append("\t\tDNS Resolution failed.");
                    }
                    sb.append('\n').append('\n');
                }

                if (testState.ordinal() >= TCP_HANDSHAKE_REQUESTED.ordinal()) {
                    sb.append("* TCP Handshake").append('\n');
                    if (testState.ordinal() >= TCP_HANDSHAKE_DONE.ordinal()) {
                        sb.append("\t\tTime (milliSec):").append(
                                connectionMetric.getTcpHandshakeTime(TimeUnit.MILLISECONDS));
                    } else {
                        sb.append("\t\tTCP Handshake failed.");
                    }
                    sb.append('\n').append('\n');
                }

                if (connectionMetric.isSecure() && testState.ordinal() >= SSL_HANDSHAKE_RQUESTED.ordinal()) {
                    sb.append("* SSL Handshake").append('\n');
                    if (testState.ordinal() >= SSL_HANDSHAKE_DONE.ordinal()) {
                        sb.append("\t\tTime (milliSec):").append(
                                connectionMetric.getSslHandshakeTime(TimeUnit.MILLISECONDS));
                    } else {
                        sb.append("\t\tSSL Handshake failed.");
                    }
                    sb.append('\n').append('\n');
                }

                if (testState.ordinal() >= REQ_SENT.ordinal()) {
                    sb.append("* Request/Response").append('\n')
                            .append("\t\tReq Size: ").append(connectionMetric.getRequest().length()).append(
                            " bytes").append('\n');
                    if (testState.ordinal() >= RESP_RECEIVED.ordinal()) {
                        final String response = connectionMetric.getResponse();
                        sb.append("\t\tResponse Size: ").append(response != null ? response.length() : 0).append(
                                " bytes").append('\n')
                                .append("\t\tTime (milliSec):").append(
                                connectionMetric.getResponseTime(TimeUnit.MILLISECONDS)).append('\n')
                                .append('\n').append("Received Response:\n").append(response);
                    } else {
                        sb.append("\t\tNo response received.");
                    }
                    sb.append('\n').append('\n');
                }

                if (throwable != null) {
                    sb.append("Received exception:").append('\n').append(throwable.toString());
                    final StackTraceElement[] stackTrace = throwable.getStackTrace();
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        sb.append('\n').append(stackTraceElement);
                    }
                    sb.append('\n').append('\n');
                }

                sb.append("DNS servers:").append(NetworkUtil.getDNSServers());
                if (connectionMetric.getProxy() == null) {
                    sb.append("\n\n").append("Proxy type:default, host:").append(NetworkUtil.getProxyHost()).append(
                            ", port:").append(NetworkUtil.getProxyPort());
                } else {
                    final Proxy proxy = connectionMetric.getProxy();
                    sb.append("\n\n").append("Proxy:").append(proxy.toString());
                }
                sb.append('\n').append('\n');

                final CustomNetworkInfo networkInfo = NetworkUtil.getNetworkInfo(getApplicationContext());
                if (networkInfo != null) {
                    sb.append(networkInfo);
                }

                _etResult.setText(sb.toString());
                _btnConnect.setEnabled(true);
            }
        });
    }

    private void onConnectButtonClick() {
        final ProgressDialog processingRequestDialog = ProgressDialog
                .show(this, null, "Test in progress ..", true, false);

        _startTime = System.nanoTime();
        _etResult.setText(null);
        _btnConnect.setEnabled(false);
        final String endpoint = countrySpinner.getItemAtPosition(countrySpinner.getSelectedItemPosition()).toString();
        final String proxyType = proxySpinner.getItemAtPosition(proxySpinner.getSelectedItemPosition()).toString();
        Proxy proxy = null;
        final String proxyHost = _proxyHost.getText().toString();
        if (!proxyType.equals("Default") && !Strings.isNullOrEmpty(proxyHost) && !Strings.isNullOrEmpty(
                _proxyPort.getText().toString())) {
            final int proxyPort = Integer.parseInt(_proxyPort.getText().toString());
            proxy = new Proxy(proxyType.equals("SOCKS") ? Proxy.Type.SOCKS : Proxy.Type.HTTP,
                              new InetSocketAddress(proxyHost, proxyPort));
        }
        final ListenableFuture<ConnectionMetric> metricListenableFuture;
        boolean isDoorProtocol = false;
        if (endpoint.equals("doorstaging.handler.talk.to:5222") || endpoint.equals("doormobile.handler.talk.to:443")) {
            isDoorProtocol = true;
        }
        final ConnectionMetric connectionMetric = new ConnectionMetric();
        metricListenableFuture = testConnectivity(_etHost.getText().toString(),
                                                  Integer.parseInt(_etPort.getText().toString()), _cbSecure.isChecked(),
                                                  Integer.parseInt(_etTimeout.getText().toString()),
                                                  _etRequestString.getText().toString(), _cbDoor.isChecked(),
                                                  connectionMetric, proxy, _cbInvalidate.isChecked());

        Futures.addCallback(metricListenableFuture, new FutureCallback<ConnectionMetric>() {
            @Override
            public void onSuccess(ConnectionMetric result) {
                processingRequestDialog.dismiss();
                displayResult(result, null);
            }

            @Override
            public void onFailure(Throwable t) {
                processingRequestDialog.dismiss();
                displayResult(connectionMetric, t);
            }
        });
    }

    private void endpointSelectionListener(AdapterView<?> adapter, int position) {
        final String endpoint = adapter.getItemAtPosition(position).toString();

        if (endpoint.equals("Custom")) {
            _cbSecure.setChecked(false);
            _cbDoor.setChecked(false);
            _etRequestString.setText(null);
            _etHost.setText(null);
            _etPort.setText(null);
            _etTimeout.setText("60");
        } else {

            final String[] strings = endpoint.split(":");
            String host = strings[0];
            int port = Integer.parseInt(strings[1]);
            boolean isSecure = false;
            boolean isDoor = false;
            if (port == 443 && !host.startsWith("uecho")) {
                isSecure = true;
            }
            String reqString = "GET / HTTP/1.1\r\n\r\n";

            if (host.equals("doormobile.handler.talk.to") || host.equals("doorstaging.handler.talk.to")) {
                isSecure = true;
                isDoor = true;
                reqString = "{\"type\":\"ping\"}";
            }

            _cbSecure.setChecked(isSecure);
            _cbDoor.setChecked(isDoor);
            _etRequestString.setText(reqString);
            _etHost.setText(host);
            _etPort.setText(String.valueOf(port));
            _etTimeout.setText("60");
        }
    }

    protected void runOnUI(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    private ListenableFuture<ConnectionMetric> testConnectivity(final String host, final int port, final boolean useSecure,
                                                                int timeoutInSeconds, String requestString, boolean isDoorProtocol, ConnectionMetric connectionMetric, Proxy proxy, boolean invalidateSession) {
        return NetworkClient.connect(host, port, useSecure, timeoutInSeconds, requestString, isDoorProtocol,
                                     connectionMetric, proxy, invalidateSession);
    }

    private String getIncompleteTrace() {
        final StringBuilder stringBuilder = new StringBuilder(
                "------------------------StackDump-------------------");
        final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Thread thread : allStackTraces.keySet()) {
            final StackTraceElement[] stackTraceElements = allStackTraces.get(thread);
            stringBuilder.append('\n').append('\n').append(thread.toString()).append("state:").append(
                    thread.getState()).append(" tid:").append(thread.getId());
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                stringBuilder.append('\n').append(' ').append(stackTraceElement.toString());
            }
        }
        stringBuilder.append("---------------------------------------");
        return stringBuilder.toString();
    }

    private String getStackTraceForEverything() {
        final StringBuilder stringBuilder = new StringBuilder(
                "\n\n------------------------ThreadTrace-------------------");
        final int pid = android.os.Process.myPid();

        String path = "/proc/" + pid + "/stat";
        String info = "";
        FileReader fr = null;
        try {
            fr = new FileReader(path);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((info = localBufferedReader.readLine()) != null) {
                //analyse
                stringBuilder.append("ThreadInfo:" + info);
            }
            // close...
        } catch (IOException e) {
            Log.e("phata", "received exception " + e);
        } finally {
            if (fr != null) {
                try {
                    Log.d("bye", "closing");
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }
}
