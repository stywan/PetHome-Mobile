package com.example.pethome.data.remote

import retrofit2.http.*

interface PetApiService {

    @GET("/api/pets")
    suspend fun getPets(): List<PetResponse>

    @GET("/api/pets/{id}")
    suspend fun getPetById(@Path("id") petId: String): PetResponse

    @POST("/api/pets")
    suspend fun createPet(@Body pet: PetRequest): PetResponse

    @PUT("/api/pets/{id}")
    suspend fun updatePet(
        @Path("id") petId: String,
        @Body pet: PetRequest
    ): PetResponse

    @DELETE("/api/pets/{id}")
    suspend fun deletePet(@Path("id") petId: String)

    @GET("/api/pets/species/{species}")
    suspend fun getPetsBySpecies(@Path("species") species: String): List<PetResponse>
}
