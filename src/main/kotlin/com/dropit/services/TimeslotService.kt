package com.dropit.services

import com.dropit.models.Address
import com.dropit.models.Timeslot
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.io.File

@Service
class TimeslotService {
    private val mapper = ObjectMapper().registerModules(KotlinModule(),JavaTimeModule())

    private val courierFilePath = "src/main/resources/courier-timeslots.json"

    fun timeslots(formattedAddress: Address) : List<Timeslot>{
            val timeslots = allCourierTimeslots()
            val matchByAddressTimeslots = mutableListOf<Timeslot>()
            for (timeslot in timeslots){
                for (address in timeslot.supportedAddresses){
                    if(address == formattedAddress){
                        matchByAddressTimeslots.add(timeslot)
                    }
                }
            }

            return matchByAddressTimeslots
    }

    fun timeslot(timeslotId: Long) : Timeslot? {
        val timeslots = allCourierTimeslots()
        return timeslots.find { it.id == timeslotId }
    }

     private fun allCourierTimeslots(): List<Timeslot> {
        try {
            val json = File(courierFilePath).readText()
            return mapper.readValue(json)
        }
        catch (e: Exception){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.localizedMessage, e)
        }
    }


}