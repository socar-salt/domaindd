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
        val member = Member.newOf(requestMember)
        member.encryptedPwd = passwordEncoder.encode(requestMember.pwd)
        member.memberId = UUID.randomUUID().toString()
        memberRepository.save(member)
        return MemberDto.newOf(member)
    }

    fun getMemberByAll(): MutableIterable<Member> {
        return memberRepository.findAll()
    }

    fun getMemberDetailByMemberId(memberId: String): MemberDto {
        val member: Member = memberRepository.findByMemberId(memberId) ?: throw UsernameNotFoundException("Member not found")
        return MemberDto.newOf(member)
    }

    fun getMemberDetailsByEmail(email: String): MemberDto {
        val member: Member = memberRepository.findByEmail(email) ?: throw UsernameNotFoundException(email)
        return MemberDto.newOf(member)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val member: Member = memberRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)
        return User(
            member.email, member.encryptedPwd,
            true, true, true, true,
            ArrayList<GrantedAuthority>() // 로그인 되었을 때 해당 계정에 부여된 권한들
        )
    }
}
