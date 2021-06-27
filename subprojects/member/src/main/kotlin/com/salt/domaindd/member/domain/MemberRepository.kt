package com.salt.domaindd.member.domain

import org.springframework.data.repository.CrudRepository

interface MemberRepository: CrudRepository<Member, Long> {
    fun findByMemberId(memberId: String): Member?
    fun findByEmail(username: String): Member?
}
