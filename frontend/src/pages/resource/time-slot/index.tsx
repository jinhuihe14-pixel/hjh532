import { Card, Empty } from 'antd'

function TimeSlotList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>时段模板</div>
      <Card>
        <Empty description="时段模板管理页面开发中..." />
      </Card>
    </div>
  )
}

export default TimeSlotList
