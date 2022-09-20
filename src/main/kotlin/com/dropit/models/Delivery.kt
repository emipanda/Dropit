package com.dropit.models

data class Delivery(
    val deliveryId: Long,
    val userId: Long,
    var status: DeliveryStatus,
    val timeslot: Timeslot
)

data class DeliveryRequest(
    val user: User,
    val timeslotId: Long
)

enum class DeliveryStatus{
     IN_PROGRESS, DELIVERED, CANCELED
}