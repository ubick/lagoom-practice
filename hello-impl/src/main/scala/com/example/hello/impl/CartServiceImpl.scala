package com.example.hello.impl

import akka.{Done, NotUsed}
import com.example.hello.api.{AddToCartRequest, CartService, Product, RemoveFromCartRequest}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

/**
  * Implementation of the CartService.
  */
class CartServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends CartService {

  def addProductToCart(id: String) = ServiceCall { r: AddToCartRequest =>
    val ref = persistentEntityRegistry.refFor[CartEntity](id)

    ref.ask(AddToCartCommand(r.product))
  }

  def showCart(id: String): ServiceCall[NotUsed, List[Product]] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[CartEntity](id)

    ref.ask(ShowCartCommand)
  }

  override def removeFromCart(id: String): ServiceCall[RemoveFromCartRequest, Done] = ServiceCall { r: RemoveFromCartRequest =>
    val ref = persistentEntityRegistry.refFor[CartEntity](id)

    ref.ask(RemoveFromCartCommand(r.product))
  }
}
