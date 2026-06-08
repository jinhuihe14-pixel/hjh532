import { Card, Empty } from 'antd'

function CourseList() {
  return (
    <div className="page-container">
      <div className="page-title" style={{ marginBottom: 20 }}>课程管理</div>
      <Card>
        <Empty description="课程管理页面开发中..." />
      </Card>
    </div>
  )
}

export default CourseList
