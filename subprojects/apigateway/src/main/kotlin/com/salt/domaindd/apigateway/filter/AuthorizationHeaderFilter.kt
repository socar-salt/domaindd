package com.salt.domaindd.apigateway.filter

import com.google.common.net.HttpHeaders
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthorizationHeaderFilter(private val env: Environment): AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>(Config::class.java) {
    private val log = LoggerFactory.getLogger(javaClass)

    class Config {
    }

    // login -> token -> users(with token) -> header(include token)
    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return@GatewayFilter onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED)
            }

            val authorizationHeader = request.headers[HttpHeaders.AUTHORIZATION]!![0]
            val jwt = authorizationHeader.replace("Bearer", "")
            if (!isJwtValid(jwt)) {
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)
            }
            chain.filter(exchange)
        }
    }

    // Mono (단일), Flux(다종)
    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        log.error(err)
        return response.setComplete()
    }

    private fun isJwtValid(jwt: String): Boolean {
        var returnValue = true
        var subject: String? = null
        val secret = env.getProperty("token.secret")
        try {
            subject = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(jwt).body // parse
                .subject  // subject 값 얻음
        } catch (ex: Exception) {
            log.debug(ex.message)
            returnValue = false
        }
        if (subject == null || subject.isEmpty()) {
            returnValue = false
        }
        return returnValue
    }
}
