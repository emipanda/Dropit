package com.dropit.services

import com.dropit.models.Address
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets


@Service
class AddressService {
    private val mapper = jacksonObjectMapper()

    @Value("\${apiKey}")
    lateinit var apiKey: String

    fun resolveAddress(address: String): Address {
        try {
            return parseAddress(address)
        }
        catch (e: Exception){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "COULD NOT RESOLVE ADDRESS")
        }
    }

    private fun parseAddress(address: String) : Address{
        val response = geoAddressRequest(address)
        val jsonResponse = mapper.readValue(response, LinkedHashMap::class.java)["results"]
        val propertyMap = (jsonResponse as ArrayList<String>)[0] as LinkedHashMap<String,String>

        return Address(
            street = propertyMap["street"],
            addressLine1 = propertyMap["address_line1"],
            addressLine2 = propertyMap["address_line2"],
            postcode = propertyMap["postcode"],
            country = propertyMap["country"]
        )
    }

    private fun geoAddressRequest(address: String): String {
        val client = HttpClient.newBuilder().build();
        val encodedParameters = encodeValue(address)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.geoapify.com/v1/geocode/search?text=$encodedParameters&format=json&apiKey=$apiKey"))
            .header("Content-Type", "application/json")
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    }

    private fun encodeValue(value: String): String? = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
}