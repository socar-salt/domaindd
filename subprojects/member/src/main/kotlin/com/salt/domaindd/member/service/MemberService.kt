package com.salt.domaindd.member.service

import com.salt.domaindd.member.domain.Member
import com.salt.domaindd.member.domain.MemberRepository
import com.salt.domaindd.member.dto.MemberDto
import com.salt.domaindd.member.dto.RequestMember
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    val memberRepository: MemberRepository,
    val passwordEncoder: BCryptPasswordEncoder
): UserDetailsService {

    fun createMember(requestMember: RequestMember): MemberDto {
        requestMember.memberId = UUID.randomUUID().toString()
        val member = Member.newOf(requestMember)
        member.encryptedPwd = passwordEncoder.encode(requestMember.pwd)
        memberRepository.save(member)
        return MemberDto(
            email = requestMember.email,
            name = requestMember.name,
            pwd = requestMember.pwd
        )
    }

    fun getMemberByAll(): MutableIterable<Member> {
        return memberRepository.findAll()
    }

    fun getMemberDetailByMemberId(memberId: String): MemberDto {
        val member: Member = memberRepository.findByMemberId(memberId) ?: throw UsernameNotFoundException("User not found")
        return ModelMapper().map(member, MemberDto::class.java)
    }

    fun getMemberDetailsByEmail(email: String): MemberDto {
        val member: Member = memberRepository.findByEmail(email) ?: throw UsernameNotFoundException(email)
        return ModelMapper().map(member, MemberDto::class.java)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val member: Member = memberRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)
        return User(
            member.email, member.encryptedPwd,
            true, true, true, true,
            ArrayList<GrantedAuthority>()
        )
    }
}
