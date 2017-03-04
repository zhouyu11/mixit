package mixit.integration

import mixit.Application
import org.junit.After
import org.junit.Before
import org.springframework.util.SocketUtils.*
import org.springframework.web.reactive.function.client.WebClient


abstract class AbstractIntegrationTests {

    lateinit var application: Application
    lateinit var client: WebClient

    @Before
    fun setup() {
        application = Application(findAvailableTcpPort())
        application.start()
        client = WebClient.create("http://localhost:${application.port}")
    }

    @After
    fun tearDown() {
        application.stop()
    }

}