package com.example.hello.impl

import com.example.hello.api.{AddToCartRequest, CartService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

/**
  * Implementation of the HelloService.
  */
class CartServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends CartService {

  override def addProductToCart(id: String) = ServiceCall { r: AddToCartRequest =>
    val ref = persistentEntityRegistry.refFor[CartEntity](id)

    ref.ask(AddToCartCommand(r.cart, r.product))
  }
}
