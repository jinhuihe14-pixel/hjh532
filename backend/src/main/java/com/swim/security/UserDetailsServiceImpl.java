package com.swim.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swim.entity.system.SysMenu;
import com.swim.entity.system.SysRole;
import com.swim.entity.system.SysUser;
import com.swim.mapper.system.SysMenuMapper;
import com.swim.mapper.system.SysRoleMapper;
import com.swim.mapper.system.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用");
        }

        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<SysMenu> menus = sysMenuMapper.selectMenusByUserId(user.getId());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .collect(Collectors.toList()));
        authorities.addAll(menus.stream()
                .filter(menu -> menu.getPermission() != null && !menu.getPermission().isEmpty())
                .map(menu -> new SimpleGrantedAuthority(menu.getPermission()))
                .collect(Collectors.toList()));

        return new LoginUser(user.getId(), user.getUsername(), user.getPassword(),
                user.getRealName(), authorities);
    }
}
