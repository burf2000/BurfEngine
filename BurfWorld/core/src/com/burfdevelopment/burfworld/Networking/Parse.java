package com.burfdevelopment.burfworld.Networking;

/**
 * Created by burfies1 on 28/07/15.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class Parse implements HttpResponseListener {

    private String app_id = "Dy95q3VpJ3z5Ppbl7dZhj4xmqY8vrj4Sck5VZWbA";
    private String app_key = "1EleDLFzhQRRGChnoEhQ39QMWk03yB7O6Qu9lsZv";

    public Parse() {
        super(); // may not need
    }

    public void add_net_score(){
        // LibGDX NET CLASS
        HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
        httpPost.setUrl("https://api.parse.com/1/classes/score/");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("X-Parse-Application-Id", app_id);
        httpPost.setHeader("X-Parse-REST-API-Key", app_key);
        httpPost.setContent("{\"score\": 1337, \"user\": \"CarelessLabs Java\"}");
        Gdx.net.sendHttpRequest(httpPost,Parse.this);
    }

    public void get_net_score(){
        // LibGDX NET CLASS
        HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
        httpGet.setUrl("https://api.parse.com/1/classes/score/");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("X-Parse-Application-Id", app_id);
        httpGet.setHeader("X-Parse-REST-API-Key", app_key);
        Gdx.net.sendHttpRequest(httpGet,Parse.this);
    }

    @Override
    public void handleHttpResponse(HttpResponse httpResponse) {
        final int statusCode = httpResponse.getStatus().getStatusCode();
        System.out.println(statusCode + " " + httpResponse.getResultAsString());
    }

    @Override
    public void failed(Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void cancelled() {

    }
}