package com.swim.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
