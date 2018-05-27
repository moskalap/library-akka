package model

case class Book(title: String, price: BigDecimal)

case class Order(title: String)

case class Price(price: BigDecimal)

case class PriceRequest(title: String)

case class StreamRequest(title :String)

case class BookContent(content: String)

case class OrderStatus(confirmed:Boolean)

case class BookNotFound()


