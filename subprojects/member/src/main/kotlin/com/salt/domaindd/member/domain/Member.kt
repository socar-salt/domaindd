package com.salt.domaindd.member.domain

import com.salt.domaindd.member.dto.RequestMember
import javax.persistence.*

@Entity
@Table(name = "members")
class Member(
    @Column(nullable = false, length = 50, unique = true)
    val email: String,

    @Column(nullable = false, length = 50)
    val name: String,

    @Transient
    val pwd: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false, unique = true)
    var memberId: String? = null

    @Column(nullable = false, unique = true)
    var encryptedPwd: String? = null

    companion object {
        fun newOf(requestMember: RequestMember): Member {
            return Member(
                email = requestMember.email,
                name = requestMember.name,
                pwd = requestMember.pwd
            )
        }
    }
}
