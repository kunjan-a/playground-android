package com.example.testAndroid.com.example.asyncHttp;

import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;
import com.example.asyncHttp.proteus.ProteusRequest;
import com.example.asyncHttp.proteus.error.ProteusCallFailedException;
import com.example.asyncHttp.proteus.error.ProteusInvalidResponseException;
import com.example.testAndroid.com.example.asyncHttp.zeus.Response.CompleteProfile;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;
import com.google.myjson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kunjanagarwal on 6/2/14.
 */
public class AsyncHttpTest extends AndroidTestCase {

    private static final String LOGTAG = "asyncHttpTest";
    private AsyncHttpClient _asyncHttpClient;
    private CountDownLatch latch;
    private Gson _gson;
    private CountDownLatch latch2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _asyncHttpClient = new AsyncHttpClient();
        _gson = new Gson();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDummy() throws Exception {
        Log.d(LOGTAG, "dummy started");
        System.out.println("dummy started");
        assertEquals("Even dummy test failed", 1, 1);
        Log.d(LOGTAG, "dummy finished");
    }

    public void testRequestTimeOut() throws Exception {
        latch2 = new CountDownLatch(1);
        latch = new CountDownLatch(1);
        Log.d(LOGTAG,"Scheduling runnable. thread:"+Thread.currentThread().toString());
        Executors.newSingleThreadScheduledExecutor().submit(postTask());
//        getHandlerThread().post(postTask());
        //getTask().run();
        latch.await();
        Log.d(LOGTAG, "Test finished. thread:" + Thread.currentThread().toString());

    }

    public Handler getHandlerThread(){
        HandlerThread thread = new HandlerThread("anshuman");
        thread.start();

        return new Handler(thread.getLooper());  //Performs a blocking wait till looper has been created
    }

    private Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(LOGTAG,"Making request. thread:"+Thread.currentThread().toString());
                _asyncHttpClient.get(null, "http://172.16.42.20:9999/zeus/1.0/getProfile?token=kgj4wb4l2kc3q9y6", getResponseHandler(new ProteusRequest<CompleteProfile>() {
                    @Override
                    protected String getTenantVersion() {
                        return null;
                    }

                    @Override
                    protected String getRequestSpecificPath() {
                        return null;
                    }

                    @Override
                    protected Class<CompleteProfile> getResponseClass() {
                        return CompleteProfile.class;
                    }
                }));

            /*
                _asyncHttpClient.get("http://172.16.42.20:9999/zeus/1.0/getProfile?token=kgj4wb4l2kc3q9y6",new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(LOGTAG, "Proteus req successful. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                        System.out.println("Proteus req successful. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(LOGTAG, "Proteus req failed. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers+", error:{}"+error);
                        System.out.println("Proteus req failed. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers+", error:{}"+error);
                        latch.countDown();
                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        Log.d(LOGTAG,"Bytes writ:"+bytesWritten+", total:"+totalSize);
                        System.out.println("Bytes writ:"+bytesWritten+", total:"+totalSize);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d(LOGTAG, "Starting");
                        System.out.println("Starting");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.d(LOGTAG, "Finishing");
                        System.out.println("Finishing");
                    }
                });
            */
                try {
                    latch2.await(10, TimeUnit.SECONDS);
                    Log.d(LOGTAG, "wait over. thread:"+Thread.currentThread());
                } catch (InterruptedException e) {
                    Log.d(LOGTAG, "oops");
                }
            }
        };
    }

    private Runnable postTask() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(LOGTAG,"Making request. thread:"+Thread.currentThread().toString());
                StringEntity entity = null;
                try {
                    String s = "[{\"call_identifier\":\"1kzk-bbyhhzy4uhzpbwph-1408381685995\",\"timestamp\":\"2014-08-18T22:38:14+0530\",\"packet_loss_rx\":0,\"totalTxPkt\":148,\"packet_loss_tx\":0,\"totalRxPkt\":152,\"duration\":8,\"round_trip_time\":348000,\"jitter_rx\":1500,\"jitter_tx\":17375},{\"call_identifier\":\"1kzk-bbyhhzy4uhzpbwph-1408381685995\",\"timestamp\":\"2014-08-18T22:38:15+0530\",\"packet_loss_rx\":0,\"totalTxPkt\":196,\"packet_loss_tx\":0,\"totalRxPkt\":202,\"duration\":9,\"round_trip_time\":348000,\"jitter_rx\":4000,\"jitter_tx\":17375},{\"call_identifier\":\"1kzk-bbyhhzy4uhzpbwph-1408381685995\",\"timestamp\":\"2014-08-18T22:38:16+0530\",\"packet_loss_rx\":0,\"totalTxPkt\":246,\"packet_loss_tx\":0,\"totalRxPkt\":250,\"duration\":10,\"round_trip_time\":350000,\"jitter_rx\":8875,\"jitter_tx\":18125}]";
                    Log.d(LOGTAG,"Sending string:"+s);
                    entity = new StringEntity(s);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                _asyncHttpClient.addHeader("Content-Type", "application/json");

                _asyncHttpClient.post(null, "https://clog.handler.domain.to/domain_stats", entity,"application/json",new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(LOGTAG, "Proteus req successful. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                        System.out.println("Proteus req successful. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(LOGTAG, "Proteus req failed. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers+", error:{}"+error);
                        System.out.println("Proteus req failed. statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers+", error:{}"+error);
                        latch.countDown();
                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        Log.d(LOGTAG,"Bytes writ:"+bytesWritten+", total:"+totalSize);
                        System.out.println("Bytes writ:"+bytesWritten+", total:"+totalSize);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d(LOGTAG, "Starting");
                        System.out.println("Starting");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.d(LOGTAG, "Finishing");
                        System.out.println("Finishing");
                    }
                });
                try {
                    latch2.await(10, TimeUnit.SECONDS);
                    Log.d(LOGTAG, "wait over. thread:"+Thread.currentThread());
                } catch (InterruptedException e) {
                    Log.d(LOGTAG, "oops");
                }
            }
        };
    }

    private <T extends CompleteProfile> BaseJsonHttpResponseHandler getResponseHandler(final ProteusRequest<T> request) {
        return new BaseJsonHttpResponseHandler<T>("UTF-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody, T response) {
                Log.d(LOGTAG, "Proteus req successful. thread:"+Thread.currentThread().toString()+", statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                latch2.countDown();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  String responseBody, T errorResponse) {
                Log.d(LOGTAG, "Proteus req failed. thread:"+Thread.currentThread().toString()+",statusCode:" + statusCode + ", responseBody:" + responseBody + ", headers:" + headers);
                Throwable error;
                if (e != null) {
                    if (e instanceof JsonParseException) {
                        error = new ProteusInvalidResponseException(responseBody);
                    } else {
                        if (e instanceof HttpResponseException) {
                            HttpResponseException e1 = (HttpResponseException) e;
                            error = request.httpError(e1.getStatusCode(), "");
                        } else {
                            error = new ProteusCallFailedException(e);
                        }
                    }
                } else {
                    error = request.httpError(statusCode, responseBody);
                }
                Log.d(LOGTAG,"error:"+error);
                latch2.countDown();
            }

            @Override
            protected T parseResponse(String responseBody)
                    throws JsonParseException, JsonSyntaxException {
                Log.d(LOGTAG, "parse thread:"+Thread.currentThread().toString()+", response:" + responseBody);
                return request.parseResponseJson(responseBody);
            }

            @Override
            protected void postRunnable(Runnable r) {
                if (r != null) {
                    r.run();
                }
            }
        };
    }

}
