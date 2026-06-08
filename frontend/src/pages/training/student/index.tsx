import { Card, Empty } from 'antd'

function StudentList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>学员管理</div>
      <Card>
        <Empty description="学员管理页面开发中..." />
      </Card>
    </div>
  )
}

export default StudentList
