package com.salt.domaindd.member.dto

import java.time.LocalDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

data class RequestMember (
    @NotNull(message = "Email cannot be null")
    @Email
    val email: String,

    @NotNull(message = "Name cannot be null")
    val name:  String,

    @NotNull(message = "Password cannot be null")
    val pwd:   String
) {
    var memberId: String? = null
    val encryptedPwd: String? = null
    val createdAt: LocalDateTime? = LocalDateTime.now()
}
