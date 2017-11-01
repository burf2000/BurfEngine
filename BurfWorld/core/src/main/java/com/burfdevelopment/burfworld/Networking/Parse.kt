package com.burfdevelopment.burfworld.Networking

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net

/**
 * Created by burfies1 on 21/10/2017.
 */


class Parse : Net.HttpResponseListener {

    fun add_net_score() {
        // LibGDX NET CLASS
        val httpPost = Net.HttpRequest(Net.HttpMethods.POST)
        httpPost.url = "https://api.parse.com/1/classes/score/"
        httpPost.setHeader("Content-Type", "application/json")
        httpPost.setHeader("X-Parse-Application-Id", app_id)
        httpPost.setHeader("X-Parse-REST-API-Key", app_key)
        httpPost.content = "{\"score\": 1337, \"user\": \"CarelessLabs Java\"}"
        Gdx.net.sendHttpRequest(httpPost, this@Parse)
    }

    fun get_net_score() {
        // LibGDX NET CLASS
        val httpGet = Net.HttpRequest(Net.HttpMethods.GET)
        httpGet.url = "https://api.parse.com/1/classes/score/"
        httpGet.setHeader("Content-Type", "application/json")
        httpGet.setHeader("X-Parse-Application-Id", app_id)
        httpGet.setHeader("X-Parse-REST-API-Key", app_key)
        Gdx.net.sendHttpRequest(httpGet, this@Parse)
    }

    override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
        val statusCode = httpResponse.status.statusCode
        println(statusCode.toString() + " " + httpResponse.resultAsString)
    }

    override fun failed(t: Throwable) {
        println(t.message)
    }

    override fun cancelled() {

    }

    companion object {
        const val app_id = ""
        const val app_key = ""
    }
}