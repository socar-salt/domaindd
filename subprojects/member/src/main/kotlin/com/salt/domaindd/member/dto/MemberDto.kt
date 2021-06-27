package com.salt.domaindd.member.dto

import java.time.LocalDateTime

data class MemberDto (
    val email: String,
    val name: String,
    val pwd: String,

) {
    var memberId: String? = null
    val encryptedPwd: String? = null
    val createdAt: LocalDateTime? = LocalDateTime.now()
}
