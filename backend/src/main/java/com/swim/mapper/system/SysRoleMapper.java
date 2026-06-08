package com.swim.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
}
