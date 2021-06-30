package com.salt.domaindd.member.dto

import com.salt.domaindd.member.domain.Member
import java.time.LocalDateTime

data class MemberDto (
    val email: String,
    val name: String,
    val pwd: String?,
    val memberId: String?

) {
    val createdAt: LocalDateTime? = LocalDateTime.now()

    companion object {
        fun newOf(member: Member): MemberDto {
            return MemberDto(
                email = member.email,
                name = member.name,
                pwd = member.encryptedPwd,
                memberId = member.memberId
            ).apply {
            }
        }
    }
}
