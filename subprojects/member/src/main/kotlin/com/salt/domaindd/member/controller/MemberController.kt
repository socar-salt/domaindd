package com.salt.domaindd.member.controller

import com.salt.domaindd.member.domain.Member
import com.salt.domaindd.member.dto.RequestMember
import com.salt.domaindd.member.dto.ResponseMember
import com.salt.domaindd.member.service.MemberService
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello!"
    }

    @PostMapping("/members")
    fun createMember(@RequestBody requestMember: RequestMember): ResponseEntity<ResponseMember>? {
        val memberDto = memberService.createMember(requestMember)
        val responseMember = ResponseMember(
            email = memberDto.email,
            name = memberDto.name,
            memberId = memberDto.memberId ?: ""
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMember)
    }

    @GetMapping("/members")
    fun getMembers(): ResponseEntity<List<ResponseMember>>? {
        val memberList: Iterable<Member> = memberService.getMemberByAll()
        val result = ArrayList<ResponseMember>()

        memberList.forEach {
            result.add(ModelMapper().map(
                it,
                ResponseMember::class.java
            ))
        }
        return ResponseEntity.status(HttpStatus.OK).body(result)
    }
}
