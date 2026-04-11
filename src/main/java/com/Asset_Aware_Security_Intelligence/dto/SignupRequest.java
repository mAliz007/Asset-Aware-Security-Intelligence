package com.Asset_Aware_Security_Intelligence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
}