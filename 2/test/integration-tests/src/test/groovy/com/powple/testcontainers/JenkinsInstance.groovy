package com.powple.testcontainers

import org.apache.http.HttpException
import org.apache.http.HttpHeaders
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.AuthCache
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.BasicAuthCache
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.HttpContext
import org.slf4j.LoggerFactory

class JenkinsInstance implements AutoCloseable {
    static def LOGGER = LoggerFactory.getLogger(JenkinsInstance.class)

    private String hostname
    private int port
    private String username
    private String password
    private CloseableHttpClient httpclient
    private HttpClientContext context

    JenkinsInstance(String hostname, int port = 8080, String username = "admin", String password = "password") {
        this.hostname = hostname
        this.port = port
        this.username = username
        this.password = password

        // setup preemptive basic-auth context
        this.context = HttpClientContext.create()
        File kubernetesToken = new File('/var/run/secrets/kubernetes.io/serviceaccount/token')
        if (!kubernetesToken.exists()) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider()
            credsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(this.username, this.password))
            AuthCache authCache = new BasicAuthCache()
            authCache.put(new HttpHost(this.hostname, this.port, "http"), new BasicScheme())
            context.setCredentialsProvider(credsProvider)
            context.setAuthCache(authCache)
        }

        this.httpclient = HttpClients.custom()
            .addInterceptorFirst({ httpRequest, httpContext ->
                if (kubernetesToken.exists()) {
                    def token = kubernetesToken.text
                    httpRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $token")
                }
            } as HttpRequestInterceptor)
            .build()
    }

    private CloseableHttpResponse executeRequest(HttpUriRequest request) {
        LOGGER.info("prepare to execute: {}", request)
        return httpclient.execute(request, this.context)
    }

    CloseableHttpResponse getJob(String name) {
        def uri = new URIBuilder()
            .setScheme("http")
            .setHost(this.hostname)
            .setPort(this.port)
            .setPath("/job/$name/config.xml")
            .build()
        def request = new HttpGet(uri)

        return executeRequest(request)
    }

    CloseableHttpResponse getPlugin(String id) {
        def uri = new URIBuilder()
            .setScheme("http")
            .setHost(this.hostname)
            .setPort(this.port)
            .setPath("/pluginManager/plugin/$id/thirdPartyLicenses")
            .build()
        def request = new HttpGet(uri)

        return executeRequest(request)
    }

    @Override
    void close() throws Exception {
        this.httpclient.close()
    }
}
