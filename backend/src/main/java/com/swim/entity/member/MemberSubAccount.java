package com.swim.entity.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_sub_account")
public class MemberSubAccount extends BaseEntity {

    private Long memberId;

    private String subName;

    private String subPhone;

    private String idCard;

    private String relation;

    private LocalDate birthday;

    private Integer gender;

    private String avatar;

    private Integer status;
}
