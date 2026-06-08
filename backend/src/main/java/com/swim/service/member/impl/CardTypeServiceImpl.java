package com.swim.service.member.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.entity.member.CardType;
import com.swim.mapper.member.CardTypeMapper;
import com.swim.service.member.CardTypeService;
import org.springframework.stereotype.Service;

@Service
public class CardTypeServiceImpl extends ServiceImpl<CardTypeMapper, CardType> implements CardTypeService {
}
