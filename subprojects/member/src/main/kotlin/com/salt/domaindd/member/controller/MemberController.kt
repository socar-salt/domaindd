package com.salt.domaindd.member.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MemberController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello!"
    }
}
