package com.swim.entity.member;

import com.baomidou.mybatisplus.annotation.TableName;
import com.swim.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("card_type")
public class CardType extends BaseEntity {

    private String cardName;

    private String cardType;

    private String cardCode;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer validDays;

    private Integer totalCount;

    private BigDecimal giftPrincipal;

    private BigDecimal giftAmount;

    private BigDecimal giftHours;

    private String venueScope;

    private String timeScope;

    private String description;

    private Integer status;

    private Integer sort;
}
