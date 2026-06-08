import { Card, Empty } from 'antd'

function MemberCardList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>会员卡片</div>
      <Card>
        <Empty description="会员卡片管理页面开发中..." />
      </Card>
    </div>
  )
}

export default MemberCardList
