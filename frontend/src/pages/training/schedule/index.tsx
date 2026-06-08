import { Card, Empty } from 'antd'

function ScheduleList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>排课管理</div>
      <Card>
        <Empty description="排课管理页面开发中..." />
      </Card>
    </div>
  )
}

export default ScheduleList
