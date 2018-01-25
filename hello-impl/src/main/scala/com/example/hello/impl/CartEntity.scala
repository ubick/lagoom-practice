package com.example.hello.impl

import java.time.LocalDateTime

import akka.Done
import com.example.hello.api.Product
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

/**
  * This is an event sourced entity. It has a state, [[HelloState]], which
  * stores what the greeting should be (eg, "Hello").
  *
  * Event sourced entities are interacted with by sending them commands. This
  * entity supports two commands, a [[UseGreetingMessage]] command, which is
  * used to change the greeting, and a [[Hello]] command, which is a read
  * only command which returns a greeting to the name specified by the command.
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines one event, the [[GreetingMessageChanged]] event,
  * which is emitted when a [[UseGreetingMessage]] command is received.
  */
class CartEntity extends PersistentEntity {

  override type Event = CartEvent
  override type State = CartState
  override type Command = CartCommand[_]

  override def behavior: Behavior = {
    case CartState(message, _) => Actions().onCommand[AddToCartCommand, Done] {

      // Command handler for the UseGreetingMessage command
      case (AddToCartCommand(cart, product), ctx, state) =>
        // In response to this command, we want to first persist it as a
        // GreetingMessageChanged event
        ctx.thenPersist(
          AddedToCartEvent(cart, product)
        ) { _ =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(Done)
        }
    }.onEvent {

      // Event handler for the GreetingMessageChanged event
      case (AddedToCartEvent(cart, product), state) =>
        CartState(cart, Product(product)::state.products)
    }
  }

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: CartState = CartState("cart", List.empty)

  /**
    * Akka serialization, used by both persistence and remoting, needs to have
    * serializers registered for every type serialized or deserialized. While it's
    * possible to use any serializer you want for Akka messages, out of the box
    * Lagom provides support for JSON, via this registry abstraction.
    *
    * The serializers are registered here, and then provided to Lagom in the
    * application loader.
    */
  object HelloSerializerRegistry extends JsonSerializerRegistry {
    override def serializers: Seq[JsonSerializer[_]] = Seq(
      JsonSerializer[CartState]
    )
  }
}

case class AddToCartCommand(cart: String, product: String) extends CartCommand[Done]
case class AddedToCartEvent(cart: String, product: String) extends CartEvent


sealed trait CartCommand[R] extends ReplyType[R]

/**
  * This interface defines all the events that the HelloEntity supports.
  */
sealed trait CartEvent extends AggregateEvent[CartEvent] {
  def aggregateTag = CartEvent.Tag
}

object CartEvent {
  val Tag: AggregateEventTag[CartEvent] = AggregateEventTag[CartEvent]
}

case class CartState(cart: String, products: List[Product])

object CartState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[CartState] = Json.format
}
