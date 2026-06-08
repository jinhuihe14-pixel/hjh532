package com.swim.service.resource.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swim.common.BusinessException;
import com.swim.entity.resource.VenueOccupation;
import com.swim.mapper.resource.VenueOccupationMapper;
import com.swim.service.resource.VenueOccupationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueOccupationServiceImpl extends ServiceImpl<VenueOccupationMapper, VenueOccupation>
        implements VenueOccupationService {

    @Override
    public List<VenueOccupation> getOccupationsByDateRange(Long venueId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectOccupationsByDateRange(venueId, startDate, endDate, 1);
    }

    @Override
    public boolean checkConflict(Long venueId, LocalDate date, LocalTime startTime, LocalTime endTime,
                                 Long excludeBusinessId) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new BusinessException("开始时间必须早于结束时间");
        }
        List<VenueOccupation> conflicts = baseMapper.checkConflict(venueId, date, startTime, endTime,
                excludeBusinessId, 1);
        return !conflicts.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOccupation(VenueOccupation occupation) {
        boolean conflict = checkConflict(occupation.getVenueId(), occupation.getOccupationDate(),
                occupation.getStartTime(), occupation.getEndTime(), null);
        if (conflict) {
            throw new BusinessException("该时段场地已被占用，请选择其他时段");
        }
        if (occupation.getStatus() == null) {
            occupation.setStatus(1);
        }
        if (occupation.getLockStatus() == null) {
            occupation.setLockStatus(0);
        }
        return save(occupation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOccupation(String businessType, Long businessId) {
        LambdaQueryWrapper<VenueOccupation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueOccupation::getBusinessType, businessType)
                .eq(VenueOccupation::getBusinessId, businessId)
                .eq(VenueOccupation::getStatus, 1);
        List<VenueOccupation> occupations = list(wrapper);
        if (occupations.isEmpty()) {
            return true;
        }
        for (VenueOccupation occ : occupations) {
            occ.setStatus(0);
        }
        return updateBatchById(occupations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOccupation(String businessType, Long businessId, LocalDate newDate,
                                    LocalTime newStartTime, LocalTime newEndTime) {
        LambdaQueryWrapper<VenueOccupation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueOccupation::getBusinessType, businessType)
                .eq(VenueOccupation::getBusinessId, businessId)
                .eq(VenueOccupation::getStatus, 1);
        List<VenueOccupation> occupations = list(wrapper);
        if (occupations.isEmpty()) {
            throw new BusinessException("未找到有效的场地占用记录");
        }

        VenueOccupation occupation = occupations.get(0);
        boolean conflict = checkConflict(occupation.getVenueId(), newDate, newStartTime, newEndTime, businessId);
        if (conflict) {
            throw new BusinessException("新时段场地已被占用，改期失败");
        }

        occupation.setOccupationDate(newDate);
        occupation.setStartTime(newStartTime);
        occupation.setEndTime(newEndTime);

        return updateById(occupation);
    }
}
