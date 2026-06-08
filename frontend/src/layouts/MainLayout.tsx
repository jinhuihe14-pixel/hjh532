import { Layout, Menu, Avatar, Dropdown, Space, theme } from 'antd'
import {
  DashboardOutlined,
  ShopOutlined,
  UserOutlined,
  BookOutlined,
  BuildOutlined,
  TrophyOutlined,
  MoneyCollectOutlined,
  TeamOutlined,
  LogoutOutlined,
  SettingOutlined,
  ScheduleOutlined,
  UsergroupAddOutlined,
  FileTextOutlined,
  WalletOutlined,
} from '@ant-design/icons'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'
import { useState } from 'react'

const { Header, Sider, Content } = Layout

const menuItems = [
  {
    key: '/dashboard',
    icon: <DashboardOutlined />,
    label: '工作台',
  },
  {
    key: '/resource',
    icon: <BuildOutlined />,
    label: '资源管理',
    children: [
      { key: '/resource/venue', label: '场地管理' },
      { key: '/resource/time-slot', label: '时段模板' },
      { key: '/resource/occupation', label: '场地占用' },
    ],
  },
  {
    key: '/ticket',
    icon: <ShopOutlined />,
    label: '票务管理',
    children: [
      { key: '/ticket/order', label: '散客票订单' },
    ],
  },
  {
    key: '/member',
    icon: <UserOutlined />,
    label: '会员管理',
    children: [
      { key: '/member/list', label: '会员列表' },
      { key: '/member/card-type', label: '卡种管理' },
      { key: '/member/card', label: '会员卡片' },
    ],
  },
  {
    key: '/training',
    icon: <BookOutlined />,
    label: '培训管理',
    children: [
      { key: '/training/course', label: '课程管理' },
      { key: '/training/class', label: '班级管理' },
      { key: '/training/student', label: '学员管理' },
      { key: '/training/schedule', label: '排课管理' },
      { key: '/training/attendance', label: '考勤管理' },
    ],
  },
  {
    key: '/schedule',
    icon: <ScheduleOutlined />,
    label: '智能排班',
    children: [
      { key: '/schedule/shift', label: '班次模板' },
      { key: '/schedule/plan', label: '排班表' },
    ],
  },
  {
    key: '/group',
    icon: <UsergroupAddOutlined />,
    label: '团单管理',
    children: [
      { key: '/group/customer', label: '团体客户' },
      { key: '/group/order', label: '团单订单' },
    ],
  },
  {
    key: '/event',
    icon: <TrophyOutlined />,
    label: '赛事管理',
    children: [
      { key: '/event/order', label: '赛事订单' },
    ],
  },
  {
    key: '/finance',
    icon: <MoneyCollectOutlined />,
    label: '财务管理',
    children: [
      { key: '/finance/order', label: '订单查询' },
      { key: '/finance/receivable', label: '应收账单' },
      { key: '/finance/prepayment', label: '预存账户' },
      { key: '/finance/daily-close', label: '日结管理' },
      { key: '/finance/cost', label: '成本核算' },
    ],
  },
  {
    key: '/hr',
    icon: <TeamOutlined />,
    label: '人事管理',
    children: [
      { key: '/hr/employee', label: '员工管理' },
      { key: '/hr/salary', label: '薪酬管理' },
      { key: '/hr/commission', label: '佣金规则' },
    ],
  },
]

function MainLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { userInfo, logout } = useAuthStore()
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
  ]

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
  }

  const handleUserMenuClick = ({ key }: { key: string }) => {
    if (key === 'logout') {
      logout()
      navigate('/login')
    }
  }

  const getSelectedKeys = () => {
    const path = location.pathname
    if (path === '/dashboard') return ['/dashboard']
    const parts = path.split('/').filter(Boolean)
    if (parts.length >= 2) {
      return [`/${parts[0]}/${parts[1]}`]
    }
    return [path]
  }

  const getOpenKeys = () => {
    const path = location.pathname
    const parts = path.split('/').filter(Boolean)
    if (parts.length > 0) {
      return [`/${parts[0]}`]
    }
    return []
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        width={220}
      >
        <div style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? 14 : 18,
          fontWeight: 'bold',
          background: 'rgba(255, 255, 255, 0.1)',
        }}>
          {collapsed ? '游泳馆' : '游泳馆管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          items={menuItems}
          selectedKeys={getSelectedKeys()}
          defaultOpenKeys={getOpenKeys()}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{
          padding: '0 24px',
          background: colorBgContainer,
          display: 'flex',
          justifyContent: 'flex-end',
          alignItems: 'center',
          borderBottom: '1px solid #f0f0f0',
        }}>
          <Dropdown menu={{ items: userMenuItems, onClick: handleUserMenuClick }} placement="bottomRight">
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} />
              <span>{userInfo?.realName || userInfo?.username}</span>
            </Space>
          </Dropdown>
        </Header>
        <Content style={{ margin: '16px', padding: 0, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout
