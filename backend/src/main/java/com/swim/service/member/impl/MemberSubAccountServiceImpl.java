package com.swim.service.member.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.member.MemberSubAccount;
import com.swim.mapper.member.MemberSubAccountMapper;
import com.swim.service.member.MemberSubAccountService;
import org.springframework.stereotype.Service;

@Service
public class MemberSubAccountServiceImpl extends ServiceImpl<MemberSubAccountMapper, MemberSubAccount>
        implements MemberSubAccountService {
}
