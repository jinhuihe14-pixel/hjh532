import { useState, useEffect } from 'react'
import { Card, Button, Space, Tag, message, Empty } from 'antd'
import { LeftOutlined, RightOutlined, ReloadOutlined } from '@ant-design/icons'
import { getScheduleList, getCoursePage } from '@/api'
import dayjs from 'dayjs'
import isoWeek from 'dayjs/plugin/isoWeek'
import './index.scss'

dayjs.extend(isoWeek)

interface ScheduleItem {
  id: number
  scheduleNo: string
  classId: number
  courseId: number
  coachId: number
  venueId: number
  classDate: string
  startTime: string
  endTime: string
  classHours: number
  scheduleStatus: number
  teachingContent: string
}

interface CourseItem {
  id: number
  courseName: string
  courseType: string
  skillLevel: string
}

function ScheduleList() {
  const [weekStart, setWeekStart] = useState(dayjs().startOf('isoWeek'))
  const [scheduleList, setScheduleList] = useState<ScheduleItem[]>([])
  const [courseList, setCourseList] = useState<CourseItem[]>([])
  const [loading, setLoading] = useState(false)

  const weekDays = Array.from({ length: 7 }, (_, i) => weekStart.add(i, 'day'))
  const weekDayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

  const courseMap = courseList.reduce((map, course) => {
    map[course.id] = course
    return map
  }, {} as Record<number, CourseItem>)

  const fetchData = async () => {
    setLoading(true)
    try {
      const startDate = weekStart.format('YYYY-MM-DD')
      const endDate = weekStart.endOf('isoWeek').format('YYYY-MM-DD')
      
      const [scheduleRes, courseRes] = await Promise.all([
        getScheduleList({ startDate, endDate }),
        getCoursePage({ pageNum: 1, pageSize: 100 }),
      ])
      
      setScheduleList(scheduleRes || [])
      setCourseList(courseRes?.records || [])
    } catch (error) {
      console.error('Failed to fetch schedule:', error)
      message.error('获取排课数据失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [weekStart])

  const getSchedulesByDate = (date: dayjs.Dayjs) => {
    const dateStr = date.format('YYYY-MM-DD')
    return scheduleList
      .filter(s => s.classDate === dateStr)
      .sort((a, b) => a.startTime.localeCompare(b.startTime))
  }

  const getStatusTag = (status: number) => {
    const statusMap: Record<number, { text: string; color: string }> = {
      0: { text: '待排课', color: 'default' },
      1: { text: '已排课', color: 'blue' },
      2: { text: '已上课', color: 'green' },
      3: { text: '已取消', color: 'red' },
    }
    const info = statusMap[status] || { text: '未知', color: 'default' }
    return <Tag color={info.color}>{info.text}</Tag>
  }

  const goToPrevWeek = () => {
    setWeekStart(weekStart.subtract(1, 'week'))
  }

  const goToNextWeek = () => {
    setWeekStart(weekStart.add(1, 'week'))
  }

  const goToThisWeek = () => {
    setWeekStart(dayjs().startOf('isoWeek'))
  }

  const isToday = (date: dayjs.Dayjs) => {
    return date.isSame(dayjs(), 'day')
  }

  return (
    <div className="page-container schedule-page">
      <div className="page-header">
        <div className="page-title">排课管理</div>
        <Space>
          <Button icon={<LeftOutlined />} onClick={goToPrevWeek}>上一周</Button>
          <Button onClick={goToThisWeek}>本周</Button>
          <Button icon={<RightOutlined />} onClick={goToNextWeek}>下一周</Button>
          <Button icon={<ReloadOutlined />} onClick={fetchData}>刷新</Button>
        </Space>
      </div>

      <Card>
        <div className="schedule-header">
          <span className="schedule-title">
            本周排班（{weekStart.format('YYYY年MM月DD日')} - {weekStart.endOf('isoWeek').format('MM月DD日')}）
          </span>
        </div>

        <div className="schedule-grid">
          {weekDays.map((day, index) => (
            <div
              key={day.format('YYYY-MM-DD')}
              className={`schedule-day ${isToday(day) ? 'today' : ''}`}
            >
              <div className="day-header">
                <div className="day-name">{weekDayNames[index]}</div>
                <div className="day-date">{day.format('MM/DD')}</div>
              </div>
              <div className="day-content">
                {loading ? (
                  <div className="loading-placeholder">加载中...</div>
                ) : getSchedulesByDate(day).length === 0 ? (
                  <Empty description="暂无课程" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                ) : (
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    {getSchedulesByDate(day).map(schedule => (
                      <Card
                        key={schedule.id}
                        size="small"
                        className="schedule-card"
                        bodyStyle={{ padding: 10 }}
                      >
                        <div className="schedule-title-text">
                          {courseMap[schedule.courseId]?.courseName || `课程${schedule.courseId}`}
                        </div>
                        <div className="schedule-time">
                          {schedule.startTime?.substring(0, 5)} - {schedule.endTime?.substring(0, 5)}
                        </div>
                        <div className="schedule-hours">
                          课时：{schedule.classHours}h
                        </div>
                        {getStatusTag(schedule.scheduleStatus)}
                      </Card>
                    ))}
                  </Space>
                )}
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}

export default ScheduleList
