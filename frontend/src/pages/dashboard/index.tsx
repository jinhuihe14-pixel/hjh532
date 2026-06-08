import { Row, Col, Card, Statistic } from 'antd'
import {
  UserOutlined,
  TeamOutlined,
  MoneyCollectOutlined,
  ShoppingOutlined,
  ScheduleOutlined,
  BookOutlined,
  TrophyOutlined,
  BuildOutlined,
} from '@ant-design/icons'
import { useEffect, useState } from 'react'
import dayjs from 'dayjs'

function Dashboard() {
  const [stats, setStats] = useState({
    todayTickets: 0,
    todayRevenue: 0,
    totalMembers: 0,
    todayClasses: 0,
    activeCards: 0,
    trainingStudents: 0,
    venueCount: 0,
    todayEvents: 0,
  })

  useEffect(() => {
    setStats({
      todayTickets: 128,
      todayRevenue: 8650,
      totalMembers: 2856,
      todayClasses: 12,
      activeCards: 1892,
      trainingStudents: 356,
      venueCount: 6,
      todayEvents: 2,
    })
  }, [])

  return (
    <div>
      <div style={{ fontSize: 18, fontWeight: 600, marginBottom: 20 }}>
        工作台 - {dayjs().format('YYYY年MM月DD日')}
      </div>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="今日散客票"
              value={stats.todayTickets}
              prefix={<ShoppingOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="今日营收"
              value={stats.todayRevenue}
              precision={2}
              prefix={<MoneyCollectOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="会员总数"
              value={stats.totalMembers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="今日课程"
              value={stats.todayClasses}
              prefix={<ScheduleOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="有效会员卡"
              value={stats.activeCards}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="在训学员"
              value={stats.trainingStudents}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="场地数量"
              value={stats.venueCount}
              prefix={<BuildOutlined />}
              valueStyle={{ color: '#2f54eb' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="今日赛事活动"
              value={stats.todayEvents}
              prefix={<TrophyOutlined />}
              valueStyle={{ color: '#eb2f96' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard
