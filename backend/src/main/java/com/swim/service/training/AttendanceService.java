package com.swim.service.training;

import com.swim.entity.training.StudentAttendance;
import java.util.List;

public interface AttendanceService {

    boolean recordAttendance(Long scheduleId, Long studentId, Integer status, String remark);

    List<StudentAttendance> getAttendanceBySchedule(Long scheduleId);

    boolean batchRecordAttendance(Long scheduleId, List<StudentAttendance> attendanceList);
}
