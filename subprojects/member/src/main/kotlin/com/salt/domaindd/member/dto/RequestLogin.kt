package com.salt.domaindd.member.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class RequestLogin {
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    var email: String = ""

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equals or grater than 8 characters")
    var pwd: String = ""
}
