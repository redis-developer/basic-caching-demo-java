package com.redis.rediscachingjava.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletResponse;

@RestController
@Service
public class Repository {
    @Value("${REDIS_URL}")
    private String properies_uri;

    Jedis jedis;

    private void getConnection() {
        if (jedis == null) {
            String REDIS_URL = System.getenv("REDIS_URL");

            if (REDIS_URL == null) {
                REDIS_URL = properies_uri;
            }
            jedis = new Jedis(REDIS_URL);
        }
    }

    @RequestMapping(value = "/repos/{gitName}", produces = { "text/html; charset=utf-8" })
    @ResponseBody
    public String getGitData(HttpServletResponse response,
                             @PathVariable("gitName") String gitName) {
        getConnection();
        long startTime = System.nanoTime();
        String gitData = jedis.get(gitName);
        boolean isCached = true;
        if (gitData == null) {
            gitData = getGitReposData(gitName);
            isCached = false;
        }

        response.addHeader("X-Response-Time", getResponseTime(System.nanoTime() - startTime, 1_000_000) );
        response.addHeader("Access-Control-Expose-Headers", "X-Response-Time");
        return String.format("{\"username\":\"%s\",\"repos\":\"%s\",\"cached\":%s}", gitName, gitData, isCached);
    }

    public static String getResponseTime(long num, double divisor) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(num / divisor) + "ms";
    }


    private String getGitReposData(String gitName) {
        try {
            String sURL = String.format("https://api.github.com/users/%s", gitName);
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            try {
                String gitData = jsonObject.get("public_repos").getAsString();
                jedis.setex(gitName, 3600, gitData);
                return gitData;
            } catch (Exception e){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
