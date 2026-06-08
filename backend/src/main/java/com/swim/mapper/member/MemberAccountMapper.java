package com.swim.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swim.entity.member.MemberAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberAccountMapper extends BaseMapper<MemberAccount> {
}
