package com.example.hello.impl

import com.example.hello.api.{AddToCart, CartService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

/**
  * Implementation of the HelloService.
  */
class CartServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends CartService {

  override def addProductToCart() = ServiceCall { r: AddToCart =>
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    ref.ask(HelloSecond(id, second))
  }
}
