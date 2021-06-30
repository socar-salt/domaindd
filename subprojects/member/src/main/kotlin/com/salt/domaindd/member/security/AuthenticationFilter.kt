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
import org.slf4j.LoggerFactory


class AuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private var memberService: MemberService,
    private var env: Environment
): UsernamePasswordAuthenticationFilter() {

    val log = LoggerFactory.getLogger(javaClass)

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

            // User정보를 SpringSecurity에서 사용할 수 있는 형태의 인증토큰으로 변환
          authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.email,
                    creds.pwd,
                    ArrayList() // 어떤 권한을 가질 것인지...
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 인증에 성공하였을 경우 처리작업을 할 것인지, 어느 권한을 부여할 것인지
     */
    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val userName = (authResult.principal as User).username
        val userDetails = memberService.getMemberDetailsByEmail(userName)

        // jwt Build
        val token: String = Jwts.builder()
            .setSubject(userDetails.memberId)
            .setExpiration(Date(System.currentTimeMillis() + env.getProperty("token.expiration_time").toLong()))
            .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret")) // 키 알고리즘 + 키 조합
            .compact()
        response.addHeader("token", token)
        response.addHeader("userId", userDetails.memberId)
    }
}
