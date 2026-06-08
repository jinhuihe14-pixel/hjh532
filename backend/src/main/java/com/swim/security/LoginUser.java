package com.swim.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Data
public class LoginUser implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private String realName;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public LoginUser(Long userId, String username, String password, String realName,
                    Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.authorities = authorities;
    }
}
