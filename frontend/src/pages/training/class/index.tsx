import { Card, Empty } from 'antd'

function ClassList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>班级管理</div>
      <Card>
        <Empty description="班级管理页面开发中..." />
      </Card>
    </div>
  )
}

export default ClassList
