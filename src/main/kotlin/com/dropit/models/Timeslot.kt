package com.dropit.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class Timeslot(
    val id: Long,

    @JsonProperty("start_time")
    val startTime: String,

    @JsonProperty("end_time")
    val endTime: String,

    @JsonFormat(pattern = "dd-MM-yyyy")
    val date: LocalDate,

    @JsonProperty("supported_addresses")
    val supportedAddresses: List<Address>
)