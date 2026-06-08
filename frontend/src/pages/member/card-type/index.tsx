import { Card, Empty } from 'antd'

function CardTypeList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>卡种管理</div>
      <Card>
        <Empty description="卡种管理页面开发中..." />
      </Card>
    </div>
  )
}

export default CardTypeList
