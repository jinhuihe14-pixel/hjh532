import { Card, Empty } from 'antd'

function TicketOrder() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>散客票订单</div>
      <Card>
        <Empty description="散客票订单管理页面开发中..." />
      </Card>
    </div>
  )
}

export default TicketOrder
