package com.salt.domaindd.member.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.salt.domaindd.member.dto.RequestLogin
import com.salt.domaindd.member.service.MemberService
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


class AuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private var memberService: MemberService,
    private var env: Environment
): UsernamePasswordAuthenticationFilter() {

    init {
        super.setAuthenticationManager(authenticationManager)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        return try {
            val creds: RequestLogin = ObjectMapper().readValue(request.inputStream, RequestLogin::class.java)
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.email,
                    creds.password,
                    ArrayList()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val userName = (authResult.principal as User).username
        val userDetails = memberService.getMemberDetailsByEmail(userName)
        val token: String = Jwts.builder()
            .setSubject(userDetails.memberId)
            .setExpiration(Date(System.currentTimeMillis() + env.getProperty("token.expiration_time").toLong()))
            .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
            .compact()
        response.addHeader("token", token)
        response.addHeader("userId", userDetails.memberId)
    }
}
