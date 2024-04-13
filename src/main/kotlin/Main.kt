import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.RegisterUser
import java.net.HttpCookie
import java.time.Duration

private const val baseUrl = "localhost:8080"
private const val httpBaseUrl = "http://$baseUrl"
private const val wsBaseUrl = "ws://$baseUrl"

lateinit var client: HttpClient

fun main() = runBlocking {
    client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json()
        }
    }
    val info = registrationPrompt()
    val res = register(info.first, info.second)
    if (res.status == HttpStatusCode.Created) {
        val cookies = HttpCookie.parse(res.headers[HttpHeaders.SetCookie])
        if (cookies.isEmpty()) {
            println("fail to connect to websocket: no cookies found")
            return@runBlocking
        }
        beginWebsocketConnection(cookies.first().toString())
    }
}

fun registrationPrompt(): Pair<String, String> {
    try {
        println("Registration...")
        print("Username: ")
        val name = readln()

        print("Password: ")
        val pass = readln()

        return Pair(name, pass)
    } catch (e: Exception) {
        println("Please input the right type.")
        return registrationPrompt()
    }
}

suspend fun register(username: String, password: String): HttpResponse {
     return client.post("$httpBaseUrl/user") {
         contentType(ContentType.Application.Json)
         setBody(RegisterUser(username = username, password = password))
    }
}

suspend fun beginWebsocketConnection(cookie: String) {
    client.webSocket("$wsBaseUrl/messaging", request = {
        header("Cookie", cookie)
    }) {
        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println(frame.readText())
                }
            }
        }
        // wait for initial message from server
        delay(Duration.ofSeconds(1).toMillis())
        while(true) {
            val message = readln()
            if (message == "x" || message == "X") {
                break
            }
            send(message)
            // wait for server return the newly sent message
            delay(500)
        }
    }
}