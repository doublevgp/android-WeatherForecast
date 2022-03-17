package com.example.frag.utility;

import android.app.Activity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public interface mCall {
        public void onResponse(String s);
    }
    public interface SimpleAsyncCall {
        public void onFailure(String e);
        public void onResponse(String s);
    }
    public static void getOkHttp(final Activity activity, String url, final mCall l) {
        OkHttpClient client = new OkHttpClient();
        Request build = new Request.Builder().url(url).get().build(); // ctrl + alt + v || command + option + v
        Call call = client.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res;
                try {
                    res = response.body().string();
                    l.onResponse(res);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            l.onResponse(res);
//                        }
//                    });
                } catch (final IOException e) {
                    e.printStackTrace();
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            l.onFailure(e.toString());
//                        }
//                    });
                }
            }
        });
    }
    public static void getOkHttpAsync(final Activity activity, String url, final SimpleAsyncCall l) {
        OkHttpClient client = new OkHttpClient();
        Request build = new Request.Builder().url(url).get().build(); // ctrl + alt + v || command + option + v
        System.out.println("URL==" + url);
        Call call = client.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onFailure(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res;
                try {
                    res = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l.onResponse(res);
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l.onFailure(e.toString());
                        }
                    });
                }
            }
        });
    }

    public static String getOkHttpBlock(String url) {
        OkHttpClient client = new OkHttpClient();
        Request build = new Request.Builder().url(url).get().build();
        Call call = client.newCall(build);
        try {
            Response resp = call.execute();
            String s = resp.body().string();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
