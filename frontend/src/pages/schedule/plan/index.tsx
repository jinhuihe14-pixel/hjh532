import { useState, useEffect } from 'react'
import { Table, Button, Space, Select, DatePicker, Card, Row, Col, Tag, message, Modal, Form, Input } from 'antd'
import { ReloadOutlined, PlusOutlined, EyeOutlined } from '@ant-design/icons'
import { getSchedulePlanPage, generateSchedule, publishSchedulePlan, getScheduleDetailByPosition } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs, { Dayjs } from 'dayjs'

interface SchedulePlanItem {
  id: number
  planName: string
  positionType: string
  planType: string
  startDate: string
  endDate: string
  planStatus: number
  totalEmployees: number
  totalShifts: number
}

interface ScheduleDetailItem {
  id: number
  planId: number
  scheduleDate: string
  shiftId: number
  shiftName: string
  employeeId: number
  employeeName: string
  positionType: string
  startTime: string
  endTime: string
}

const positionTypeMap: Record<string, string> = {
  LIFEGUARD: '救生员',
  COACH: '教练',
  FRONT_DESK: '前台',
  MAINTENANCE: '运维',
  MANAGER: '管理人员',
}

const planStatusMap: Record<number, { text: string; color: string }> = {
  0: { text: '草稿', color: 'default' },
  1: { text: '已发布', color: 'green' },
  2: { text: '已取消', color: 'red' },
}

function SchedulePlan() {
  const [list, setList] = useState<SchedulePlanItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [positionType, setPositionType] = useState('LIFEGUARD')
  const [weekDate, setWeekDate] = useState<Dayjs>(dayjs())
  const [weekSchedule, setWeekSchedule] = useState<any[]>([])
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedPlan, setSelectedPlan] = useState<SchedulePlanItem | null>(null)
  const [generateModalVisible, setGenerateModalVisible] = useState(false)
  const [form] = Form.useForm()

  const fetchPlanList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res: any = await getSchedulePlanPage({ pageNum: page, pageSize, positionType, planStatus: 1 })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch plan list:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchWeekSchedule = async () => {
    try {
      const startOfWeek = weekDate.startOf('week')
      const days = []
      for (let i = 0; i < 7; i++) {
        const date = startOfWeek.add(i, 'day')
        try {
          const details: any = await getScheduleDetailByPosition({
            positionType,
            date: date.format('YYYY-MM-DD'),
          })
          days.push({
            date: date.format('YYYY-MM-DD'),
            weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.day()],
            details: details || [],
          })
        } catch {
          days.push({
            date: date.format('YYYY-MM-DD'),
            weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.day()],
            details: [],
          })
        }
      }
      setWeekSchedule(days)
    } catch (error) {
      console.error('Failed to fetch week schedule:', error)
    }
  }

  useEffect(() => {
    fetchPlanList()
    fetchWeekSchedule()
  }, [positionType])

  const handleGenerate = async () => {
    try {
      const values = await form.validateFields()
      const startDate = weekDate.startOf('week').format('YYYY-MM-DD')
      const endDate = weekDate.endOf('week').format('YYYY-MM-DD')
      const res: any = await generateSchedule({
        positionType: values.positionType,
        startDate,
        endDate,
        scheduleType: 'WEEKLY',
      })
      message.success('排班生成成功')
      setGenerateModalVisible(false)
      fetchPlanList()
      fetchWeekSchedule()
    } catch (error) {
      console.error('Failed to generate schedule:', error)
    }
  }

  const handlePublish = (record: SchedulePlanItem) => {
    Modal.confirm({
      title: '确认发布',
      content: `确定要发布排班计划"${record.planName}"吗？发布后员工即可查看。`,
      onOk: async () => {
        try {
          await publishSchedulePlan(record.id)
          message.success('发布成功')
          fetchPlanList(pagination.current, pagination.pageSize)
          fetchWeekSchedule()
        } catch (error) {
          console.error('Failed to publish plan:', error)
        }
      },
    })
  }

  const handleViewDetail = (record: SchedulePlanItem) => {
    setSelectedPlan(record)
    setDetailModalVisible(true)
  }

  const handleWeekChange = (date: Dayjs | null) => {
    if (date) {
      setWeekDate(date)
      fetchWeekSchedule()
    }
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">排班表</div>
        <Space>
          <Select
            value={positionType}
            onChange={(val) => setPositionType(val)}
            style={{ width: 140 }}
          >
            <Select.Option value="LIFEGUARD">救生员</Select.Option>
            <Select.Option value="COACH">教练</Select.Option>
            <Select.Option value="FRONT_DESK">前台</Select.Option>
            <Select.Option value="MAINTENANCE">运维</Select.Option>
          </Select>
          <DatePicker
            picker="week"
            value={weekDate}
            onChange={handleWeekChange}
            style={{ width: 180 }}
          />
          <Button icon={<ReloadOutlined />} onClick={fetchWeekSchedule}>刷新</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => {
            form.resetFields()
            form.setFieldsValue({ positionType })
            setGenerateModalVisible(true)
          }}>
            生成排班
          </Button>
        </Space>
      </div>

      <Card title="本周排班视图" style={{ marginBottom: 16 }}>
        <Row gutter={8}>
          {weekSchedule.map((day, index) => (
            <Col span={3} key={index}>
              <Card
                size="small"
                title={
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ fontWeight: 'bold' }}>{day.weekday}</div>
                    <div style={{ fontSize: 12, color: '#999' }}>{day.date}</div>
                  </div>
                }
                style={{ minHeight: 300 }}
              >
                {day.details.length === 0 ? (
                  <div style={{ textAlign: 'center', color: '#ccc', padding: '20px 0' }}>暂无排班</div>
                ) : (
                  day.details.map((d: ScheduleDetailItem, idx: number) => (
                    <Tag key={idx} color="blue" style={{ display: 'block', marginBottom: 8, padding: '4px 8px' }}>
                      <div style={{ fontWeight: 'bold' }}>{d.shiftName || '班次'}</div>
                      <div style={{ fontSize: 11 }}>{d.employeeName || '未安排'}</div>
                      <div style={{ fontSize: 11, opacity: 0.8 }}>
                        {d.startTime?.slice(0, 5) || '--'} - {d.endTime?.slice(0, 5) || '--'}
                      </div>
                    </Tag>
                  ))
                )}
              </Card>
            </Col>
          ))}
        </Row>
      </Card>

      <Card title="排班计划列表" size="small">
        <Table
          rowKey="id"
          size="small"
          columns={[
            { title: '计划名称', dataIndex: 'planName', key: 'planName' },
            {
              title: '岗位类型',
              dataIndex: 'positionType',
              key: 'positionType',
              render: (type) => positionTypeMap[type] || type,
            },
            { title: '开始日期', dataIndex: 'startDate', key: 'startDate' },
            { title: '结束日期', dataIndex: 'endDate', key: 'endDate' },
            { title: '总班次', dataIndex: 'totalShifts', key: 'totalShifts' },
            {
              title: '状态',
              dataIndex: 'planStatus',
              key: 'planStatus',
              render: (status) => {
                const info = planStatusMap[status] || { text: '未知', color: 'default' }
                return <Tag color={info.color}>{info.text}</Tag>
              },
            },
            {
              title: '操作',
              key: 'action',
              width: 180,
              render: (_, record) => (
                <Space>
                  <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
                    查看
                  </Button>
                  {record.planStatus === 0 && (
                    <Button type="link" size="small" onClick={() => handlePublish(record)}>
                      发布
                    </Button>
                  )}
                </Space>
              ),
            },
          ] as ColumnsType<SchedulePlanItem>}
          dataSource={list}
          loading={loading}
          pagination={{
            ...pagination,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => fetchPlanList(page, pageSize),
          }}
        />
      </Card>

      <Modal
        title="生成排班"
        open={generateModalVisible}
        onOk={handleGenerate}
        onCancel={() => setGenerateModalVisible(false)}
        width={400}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="positionType" label="岗位类型" rules={[{ required: true, message: '请选择岗位类型' }]}>
            <Select placeholder="请选择岗位类型">
              <Select.Option value="LIFEGUARD">救生员</Select.Option>
              <Select.Option value="COACH">教练</Select.Option>
              <Select.Option value="FRONT_DESK">前台</Select.Option>
              <Select.Option value="MAINTENANCE">运维</Select.Option>
            </Select>
          </Form.Item>
          <div style={{ color: '#666', fontSize: 13 }}>
            将生成 {weekDate.startOf('week').format('YYYY-MM-DD')} 至 {weekDate.endOf('week').format('YYYY-MM-DD')} 的排班
          </div>
        </Form>
      </Modal>

      <Modal
        title="排班详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>关闭</Button>,
        ]}
        width={800}
      >
        {selectedPlan && (
          <div>
            <p><strong>计划名称：</strong>{selectedPlan.planName}</p>
            <p><strong>岗位类型：</strong>{positionTypeMap[selectedPlan.positionType] || selectedPlan.positionType}</p>
            <p><strong>日期范围：</strong>{selectedPlan.startDate} 至 {selectedPlan.endDate}</p>
            <p><strong>总班次：</strong>{selectedPlan.totalShifts}</p>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default SchedulePlan
