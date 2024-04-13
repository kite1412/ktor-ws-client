package models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(val username: String, val password: String)
