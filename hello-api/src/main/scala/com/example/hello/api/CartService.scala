package com.example.hello.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object HelloService {
  val TOPIC_NAME = "greetings"
}

/**
  * The Hello service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloService.
  */
trait CartService extends Service {

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("cart")
      .withCalls(
        restCall(Method.POST, "/api/add-to-cart/:id", addProductToCart _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }

  def addProductToCart(id: String): ServiceCall[AddToCartRequest, Done]
}

/**
  * The greeting message class.
  */
case class GreetingMessage(message: String)

case class AddToCartRequest(cart: String, product: String)

case class Product(product: String)

object GreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}

object AddToCartRequest {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[AddToCartRequest] = Json.format[AddToCartRequest]
}

object Product {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[Product] = Json.format[Product]
}
