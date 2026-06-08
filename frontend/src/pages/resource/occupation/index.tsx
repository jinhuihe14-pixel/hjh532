import { Card, Empty } from 'antd'

function OccupationView() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>场地占用视图</div>
      <Card>
        <Empty description="场地占用日历视图开发中..." />
      </Card>
    </div>
  )
}

export default OccupationView
