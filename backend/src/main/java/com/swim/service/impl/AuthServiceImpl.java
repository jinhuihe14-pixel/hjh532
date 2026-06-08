package com.swim.service.impl;

import com.swim.dto.LoginDTO;
import com.swim.entity.system.SysMenu;
import com.swim.entity.system.SysRole;
import com.swim.mapper.system.SysMenuMapper;
import com.swim.mapper.system.SysRoleMapper;
import com.swim.security.LoginUser;
import com.swim.service.AuthService;
import com.swim.util.JwtUtil;
import com.swim.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(loginUser.getUserId(), loginUser.getUsername());

        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(loginUser.getUserId());
        List<SysMenu> menus = sysMenuMapper.selectMenusByUserId(loginUser.getUserId());

        List<String> roleList = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        List<String> permissionList = menus.stream()
                .filter(m -> m.getPermission() != null && !m.getPermission().isEmpty())
                .map(SysMenu::getPermission)
                .collect(Collectors.toList());

        return new LoginVO(
                token,
                loginUser.getUserId(),
                loginUser.getUsername(),
                loginUser.getRealName(),
                null,
                roleList,
                permissionList
        );
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
