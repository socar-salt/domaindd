package com.salt.domaindd.member.security

import com.salt.domaindd.member.service.MemberService
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class WebSecurity(
    private val env: Environment,
    private val memberService: MemberService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
//        http.authorizeRequests().antMatchers("/members/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll()
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll()
        http.authorizeRequests().antMatchers("/**")
            .hasIpAddress(env.getProperty("gateway.ip"))
            .and()
            .addFilter(authenticationFilter())

        http.headers().frameOptions().disable()  // h2
    }

    private fun authenticationFilter(): AuthenticationFilter {
        return AuthenticationFilter(
            authenticationManager(),
            memberService,
            env
        )
    }

    // select pwd from member where email = ?
    // db_pwd(encrypted) == input_pwd(encrypted)
    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(memberService).passwordEncoder(bCryptPasswordEncoder)
    }
}
