package com.dropit.services

import com.dropit.models.Delivery
import com.dropit.models.DeliveryRequest
import com.dropit.models.DeliveryStatus
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

@Service
class DeliveryService(
    private val timeslotService: TimeslotService
) {
    private val deliveries = mutableListOf<Delivery>()
    private val deliveryId : AtomicLong = AtomicLong(1)

    companion object{
        const val MAX_DELIVERIES_PER_DAY = 10
        const val MAX_DELIVERIES_PER_TIMESLOT = 2
    }

    fun createDelivery(deliveryRequest: DeliveryRequest) : Delivery{
        val timeslot = timeslotService.timeslot(deliveryRequest.timeslotId)
        timeslot ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "TIMESLOT DOESN'T EXIST")

        val deliveriesCountByTSId = allDeliveries().count { it.timeslot.id == deliveryRequest.timeslotId}

        if (allDeliveries().size >= MAX_DELIVERIES_PER_DAY)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "MAX DELIVERIES PER DAY")

        if (deliveriesCountByTSId >= MAX_DELIVERIES_PER_TIMESLOT)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "MAX DELIVERIES PER TIMESLOT")

        val newDelivery = Delivery(
            deliveryId = deliveryId.getAndIncrement(),
            userId = deliveryRequest.user.userId,
            status = DeliveryStatus.IN_PROGRESS,
            timeslot = timeslot
        )
        deliveries.add(newDelivery)

        return newDelivery
    }

    fun completeDelivery(deliveryId: Long): Delivery {
        val delivery = deliveryById(deliveryId)
        delivery ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "DELIVERY ID NOT FOUND")
        delivery.status = DeliveryStatus.DELIVERED

        return delivery
    }

    fun cancelDelivery(deliveryId: Long): Delivery? {
        val delivery = deliveryById(deliveryId)
        delivery ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST,"DELIVERY_ID NO FOUND")

        if (delivery.status == DeliveryStatus.DELIVERED)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "DELIVERY ALREADY DELIVERED, CANNOT CANCEL!")

        deliveries.remove(delivery)

        return delivery.copy(status = DeliveryStatus.CANCELED)
    }

    fun dailyDeliveries(): List<Delivery> {
        return deliveries.filter { it.timeslot.date == LocalDate.now() }
    }

    fun weeklyDeliveries(): List<Delivery> {
        return deliveries.filter { it.timeslot.date.isAfter(LocalDate.now().minusDays(7)) }
    }

    private fun deliveryById(deliveryId : Long) : Delivery?{
        return deliveries.find { it.deliveryId == deliveryId }
    }

    private fun allDeliveries() : List<Delivery> = deliveries

}