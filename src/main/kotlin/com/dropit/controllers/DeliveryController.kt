package com.dropit.controllers

import com.dropit.models.Address
import com.dropit.models.Delivery
import com.dropit.models.DeliveryRequest
import com.dropit.models.Timeslot
import com.dropit.services.AddressService
import com.dropit.services.DeliveryService
import com.dropit.services.TimeslotService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveryController(
    private val addressService: AddressService,
    private val timeslotService: TimeslotService,
    private val deliveryService: DeliveryService
) {
    @PostMapping("/resolve-address")
    fun resolveAddress(@RequestBody searchTerm: String) : Address {
        return addressService.resolveAddress(searchTerm)
    }

    @PostMapping("/timeslots")
    fun timeslots(@RequestBody formattedAddress: Address) : List<Timeslot>{
        return timeslotService.timeslots(formattedAddress)
    }

    @PostMapping("/deliveries")
    fun createDelivery(@RequestBody deliveryRequest: DeliveryRequest) : Delivery{
        return deliveryService.createDelivery(deliveryRequest)
    }

    @PostMapping("/deliveries/{deliveryId}/complete")
    fun completeDelivery(@PathVariable deliveryId: Long) : Delivery?{
        return deliveryService.completeDelivery(deliveryId)
    }

    @DeleteMapping("/deliveries/{deliveryId}")
    fun cancelDelivery(@PathVariable deliveryId: Long) : Delivery?{
        return deliveryService.cancelDelivery(deliveryId)
    }

    @GetMapping("/deliveries/daily")
    fun dailyDeliveries() : List<Delivery>{
        return deliveryService.dailyDeliveries()
    }

    @GetMapping("/deliveries/weekly")
    fun weeklyDeliveries() : List<Delivery>{
        return deliveryService.weeklyDeliveries()
    }
}