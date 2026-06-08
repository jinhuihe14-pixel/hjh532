import { useState } from 'react'
import { Form, Input, Button, Card, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'
import request from '@/utils/request'
import './login.scss'

interface LoginForm {
  username: string
  password: string
}

function Login() {
  const navigate = useNavigate()
  const { setToken, setUserInfo } = useAuthStore()
  const [loading, setLoading] = useState(false)

  const onFinish = async (values: LoginForm) => {
    setLoading(true)
    try {
      const res = await request.post('/auth/login', values)
      setToken(res.token)
      setUserInfo({
        userId: res.userId,
        username: res.username,
        realName: res.realName,
        avatar: res.avatar,
        roles: res.roles,
        permissions: res.permissions,
      })
      message.success('登录成功')
      navigate('/dashboard')
    } catch (error) {
      console.error('Login failed:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-bg">
        <Card className="login-card" title="游泳馆管理系统">
          <Form
            name="login"
            initialValues={{ username: 'admin', password: 'admin123' }}
            onFinish={onFinish}
            size="large"
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input prefix={<UserOutlined />} placeholder="用户名" />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password prefix={<LockOutlined />} placeholder="密码" />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                block
              >
                登录
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  )
}

export default Login
