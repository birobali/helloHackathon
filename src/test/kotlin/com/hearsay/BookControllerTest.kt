package com.hearsay;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext
import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import io.micronaut.function.aws.proxy.MicronautLambdaHandler

class BookRequestHandlerTest {

    @Test
    fun testBookController() {
        val handler = MicronautLambdaHandler()
        val book = Book()
        book.name = "Building Microservices"
        val objectMapper = handler.applicationContext.getBean(ObjectMapper::class.java)
        val json = objectMapper.writeValueAsString(book)
        val request = AwsProxyRequestBuilder("/", HttpMethod.POST.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(json)
                .build()
        val lambdaContext: Context = MockLambdaContext()
        val response = handler.handleRequest(request, lambdaContext)
        Assertions.assertEquals(response.statusCode, HttpStatus.OK.code)
        val bookSaved: BookSaved = objectMapper.readValue(response.body, BookSaved::class.java)
        Assertions.assertNotNull(bookSaved)
        Assertions.assertEquals(bookSaved.name, book.name)
        Assertions.assertNotNull(bookSaved.isbn)
        handler.applicationContext.close()
    }
}
