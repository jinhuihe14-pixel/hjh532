package com.swim.service.group;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.common.PageQuery;
import com.swim.entity.group.GroupClassSchedule;

import java.time.LocalDate;
import java.util.List;

public interface GroupClassScheduleService extends IService<GroupClassSchedule> {

    Page<GroupClassSchedule> getSchedulePage(PageQuery query, Long classId, Long coachId, Long venueId,
                                              LocalDate startDate, LocalDate endDate);

    List<GroupClassSchedule> getByClassId(Long classId);

    GroupClassSchedule createSchedule(GroupClassSchedule schedule);

    boolean cancelSchedule(Long id);

    boolean completeSchedule(Long id, Integer actualStudentCount, String teachingContent);

    List<GroupClassSchedule> generateSchedules(Long classId);
}
