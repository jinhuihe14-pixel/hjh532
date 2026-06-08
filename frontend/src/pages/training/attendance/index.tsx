import { Card, Empty } from 'antd'

function AttendancePage() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>考勤管理</div>
      <Card>
        <Empty description="考勤管理页面开发中..." />
      </Card>
    </div>
  )
}

export default AttendancePage
