package com.swim.service.training;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swim.entity.training.ClassSchedule;
import java.util.List;

public interface ClassScheduleService extends IService<ClassSchedule> {

    List<ClassSchedule> generateSchedules(Long classId);

    boolean cancelSchedule(Long scheduleId);

    boolean reschedule(Long scheduleId, java.time.LocalDate newDate,
                       java.time.LocalTime newStartTime, java.time.LocalTime newEndTime);
}
