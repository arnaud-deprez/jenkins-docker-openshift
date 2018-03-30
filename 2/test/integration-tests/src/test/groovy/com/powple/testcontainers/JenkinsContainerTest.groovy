package com.powple.testcontainers

import org.apache.http.HttpStatus
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JenkinsContainerTest {

    private static def jenkinsInstance

    @BeforeAll
    static void beforeAll() {
        String service = System.properties['service'] ?: "localhost"
        String port = System.properties['port'] ?: "8080"
        String password = System.properties['password'] ?: "password"
        jenkinsInstance = new JenkinsInstance(service, Integer.valueOf(port), "admin", password)
    }

    @Test
    void "It should have an Openshift Sample job"() {
        jenkinsInstance.getJob("OpenShift Sample").withCloseable {
            assert it.getStatusLine().statusCode == HttpStatus.SC_OK
        }
    }

    @Test
    void "It should have plugins installed"() {
        [
            'blueocean',
            'pipeline-utility-steps',
            'job-dsl',
            'jobConfigHistory',
            'throttle-concurrents',
            'build-monitor-plugin',
            'timestamper'
        ].each {
            jenkinsInstance.getPlugin(it).withCloseable {
                assert it.getStatusLine().statusCode == HttpStatus.SC_OK
            }
        }
    }
}
