# Basic Redis Caching Demo Spring (Java) 

This app returns the number of repositories in a given GitHub account. When you first search for an account, the server calls GitHub's API to return the response. This can take 100s of milliseconds. The server then caches this response in Redis for future requests. When you search again, the next response comes directly from a Redis cache instead of calling GitHub. The responses usually take around a millisecond.

![Screenshot](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/screenshot001.png)

# Overview video

Here's a short video that explains the project and how it uses Redis: 

[![Watch the video on YouTube](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/YTThumbnail.png)](https://youtube.com/watch?v=Ov18gLo0Da8)

## Try it out
<p>
    <a href="https://heroku.com/deploy" target="_blank">
        <img src="https://www.herokucdn.com/deploy/button.svg" alt="Deploy to Heorku" width="200px"/>
    <a>
</p>

<p>
    <a href="https://deploy.cloud.run?dir=server" target="_blank">
        <img src="https://deploy.cloud.run/button.svg" alt="Run on Google Cloud" width="200px"/>
    </a>
    (See notes: How to run on Google Cloud)
</p>


## How to run on Google Cloud

<p>
    If you don't have redis yet, plug it in  (https://spring-gcp.saturnism.me/app-dev/cloud-services/cache/memorystore-redis).
    After successful deployment, you need to manually enable the vpc connector as shown in the pictures:
</p>

1. Open link google cloud console.

![1 step](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/1.png)

2. Click "Edit and deploy new revision" button.

![2 step](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/2.png)

3. Add environment.

![3 step](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/3.png)

4.  Select vpc-connector and deploy application.

![4  step](https://github.com/redis-developer/basic-caching-demo-java/raw/master/docs/4.png)

<a href="https://github.com/GoogleCloudPlatform/cloud-run-button/issues/108#issuecomment-554572173">
Problem with unsupported flags when deploying google cloud run button
</a>

---
# How it works?

## 1. How the data is stored:
<ol>
     <li>New repos are added:<pre>SETEX github_username timeout amount_of_repositories
Example: SETEX redis 3600 14</pre> 
<a href="https://redis.io/commands/setex">
more information</a>
</li>
</ol>

## 2. How the data is accessed:
<ol>
    <li> Get cache (Don't think about cache's timeout): <pre>GET github_username
Example: GET redis</pre>
<a href="https://redis.io/commands/get">
more information</a>
</li>

</ol>
  
---

## How to run it locally?

#### Open the file `server/src/main/resources/application.properties`, and provide the Redis configuration:
   	- REDIS_URL: Redis server url
    - REDIS_HOST: Redis server host
	- REDIS_PORT: Redis server port
	- REDIS_PASSWORD: Redis server password

#### Run backend

1. Install gradle (Use Gradle 6.3 or later) (on mac: https://gradle.org/install/) 

2. Install JDK (use 8 or later version) (on mac: https://docs.oracle.com/javase/10/install/installation-jdk-and-jre-macos.htm)

3. From the root directory of the project, run the following commands:
``` sh
cd server
./gradlew build
./gradlew run
```

4. Point your browser to `localhost:5000`.

#### Run frontend

Static сontent runs automatically with the backend part. In case you need to run it separately, please see README in the [client](client) folder

