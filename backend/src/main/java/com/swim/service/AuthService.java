package com.swim.service;

import com.swim.dto.LoginDTO;
import com.swim.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    void logout();
}
