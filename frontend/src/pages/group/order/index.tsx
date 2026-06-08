import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, InputNumber, message, Card, Tag, Drawer, List, Descriptions } from 'antd'
import { PlusOutlined, EyeOutlined, CheckOutlined, CloseOutlined, SearchOutlined, QrcodeOutlined } from '@ant-design/icons'
import { getGroupOrderPage, getCustomerList, createGroupOrder, confirmGroupOrder, cancelGroupOrder, verifyOrder, getVerificationPage } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs, { Dayjs } from 'dayjs'

interface OrderItem {
  id: number
  orderNo: string
  customerId: number
  customerName: string
  orderType: string
  totalPeople: number
  usedCount: number
  totalCount: number
  totalAmount: number
  payStatus: number
  orderStatus: number
  validStartDate: string
  validEndDate: string
  contactPerson: string
  contactPhone: string
  remark: string
}

interface VerificationItem {
  id: number
  orderId: number
  venueId: number
  venueName: string
  verifyDate: string
  startTime: string
  endTime: string
  actualPeople: number
  usedCount: number
  verifyStatus: number
  operatorName: string
  createTime: string
}

const orderTypeMap: Record<string, string> = {
  TEAM_BUILDING: '企业团建',
  SCHOOL_CLASS: '学校上课',
  SWIM_TEAM: '游泳队训练',
  OTHER: '其他',
}

const orderStatusMap: Record<number, { text: string; color: string }> = {
  0: { text: '待确认', color: 'orange' },
  1: { text: '已确认', color: 'blue' },
  2: { text: '进行中', color: 'green' },
  3: { text: '已完成', color: 'default' },
  4: { text: '已取消', color: 'red' },
}

const payStatusMap: Record<number, { text: string; color: string }> = {
  0: { text: '未支付', color: 'red' },
  1: { text: '部分支付', color: 'orange' },
  2: { text: '已支付', color: 'green' },
}

function GroupOrder() {
  const [list, setList] = useState<OrderItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [customerId, setCustomerId] = useState<number | undefined>()
  const [orderStatus, setOrderStatus] = useState<number | undefined>()
  const [customerList, setCustomerList] = useState<any[]>([])
  const [modalVisible, setModalVisible] = useState(false)
  const [verifyModalVisible, setVerifyModalVisible] = useState(false)
  const [detailVisible, setDetailVisible] = useState(false)
  const [selectedOrder, setSelectedOrder] = useState<OrderItem | null>(null)
  const [verificationList, setVerificationList] = useState<VerificationItem[]>([])
  const [form] = Form.useForm()
  const [verifyForm] = Form.useForm()

  const fetchData = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res: any = await getGroupOrderPage({ pageNum: page, pageSize, customerId, orderStatus })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch order list:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchCustomers = async () => {
    try {
      const res: any = await getCustomerList()
      setCustomerList(res || [])
    } catch (error) {
      console.error('Failed to fetch customers:', error)
    }
  }

  useEffect(() => {
    fetchData()
    fetchCustomers()
  }, [customerId, orderStatus])

  const handleSearch = () => {
    fetchData(1, pagination.pageSize)
  }

  const handleAdd = () => {
    form.resetFields()
    setModalVisible(true)
  }

  const handleConfirm = (record: OrderItem) => {
    Modal.confirm({
      title: '确认订单',
      content: `确定要确认订单"${record.orderNo}"吗？`,
      onOk: async () => {
        try {
          await confirmGroupOrder(record.id)
          message.success('确认成功')
          fetchData(pagination.current, pagination.pageSize)
        } catch (error) {
          console.error('Failed to confirm order:', error)
        }
      },
    })
  }

  const handleCancel = (record: OrderItem) => {
    Modal.confirm({
      title: '取消订单',
      content: `确定要取消订单"${record.orderNo}"吗？`,
      onOk: async () => {
        try {
          await cancelGroupOrder(record.id)
          message.success('取消成功')
          fetchData(pagination.current, pagination.pageSize)
        } catch (error) {
          console.error('Failed to cancel order:', error)
        }
      },
    })
  }

  const handleVerify = (record: OrderItem) => {
    setSelectedOrder(record)
    verifyForm.resetFields()
    verifyForm.setFieldsValue({
      verifyDate: dayjs(),
    })
    setVerifyModalVisible(true)
  }

  const handleVerifySubmit = async () => {
    if (!selectedOrder) return
    try {
      const values = await verifyForm.validateFields()
      await verifyOrder({
        orderId: selectedOrder.id,
        venueId: values.venueId,
        verifyDate: values.verifyDate.format('YYYY-MM-DD'),
        startTime: values.startTime.format('HH:mm:ss'),
        endTime: values.endTime.format('HH:mm:ss'),
        actualPeople: values.actualPeople,
        usedCount: values.usedCount || 1,
      })
      message.success('核销成功')
      setVerifyModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Failed to verify order:', error)
    }
  }

  const handleViewDetail = async (record: OrderItem) => {
    setSelectedOrder(record)
    try {
      const res: any = await getVerificationPage({ pageNum: 1, pageSize: 100, orderId: record.id })
      setVerificationList(res.records || [])
    } catch {
      setVerificationList([])
    }
    setDetailVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const data = {
        ...values,
        validStartDate: values.validStartDate?.format('YYYY-MM-DD'),
        validEndDate: values.validEndDate?.format('YYYY-MM-DD'),
      }
      await createGroupOrder(data)
      message.success('创建成功')
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<OrderItem> = [
    { title: '订单编号', dataIndex: 'orderNo', key: 'orderNo', width: 160 },
    { title: '团体客户', dataIndex: 'customerName', key: 'customerName' },
    {
      title: '订单类型',
      dataIndex: 'orderType',
      key: 'orderType',
      render: (type) => orderTypeMap[type] || type,
    },
    { title: '总人次', dataIndex: 'totalCount', key: 'totalCount' },
    { title: '已使用', dataIndex: 'usedCount', key: 'usedCount' },
    { title: '总金额(元)', dataIndex: 'totalAmount', key: 'totalAmount' },
    {
      title: '支付状态',
      dataIndex: 'payStatus',
      key: 'payStatus',
      render: (status) => {
        const info = payStatusMap[status] || { text: '未知', color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      key: 'orderStatus',
      render: (status) => {
        const info = orderStatusMap[status] || { text: '未知', color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    { title: '有效期', dataIndex: 'validStartDate', key: 'validStartDate',
      render: (_, record) => `${record.validStartDate} 至 ${record.validEndDate}`
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          <Button type="link" size="small" icon={<QrcodeOutlined />} onClick={() => handleVerify(record)} disabled={record.orderStatus === 4 || record.orderStatus === 3}>
            核销
          </Button>
          {record.orderStatus === 0 && (
            <Button type="link" size="small" icon={<CheckOutlined />} onClick={() => handleConfirm(record)}>
              确认
            </Button>
          )}
          {record.orderStatus !== 3 && record.orderStatus !== 4 && (
            <Button type="link" size="small" danger icon={<CloseOutlined />} onClick={() => handleCancel(record)}>
              取消
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">团单订单</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增订单
        </Button>
      </div>

      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Select
            placeholder="选择团体客户"
            style={{ width: 200 }}
            value={customerId}
            onChange={(val) => setCustomerId(val)}
            allowClear
            showSearch
            optionFilterProp="children"
          >
            {customerList.map((c) => (
              <Select.Option key={c.id} value={c.id}>{c.customerName}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="订单状态"
            style={{ width: 140 }}
            value={orderStatus}
            onChange={(val) => setOrderStatus(val)}
            allowClear
          >
            <Select.Option value={0}>待确认</Select.Option>
            <Select.Option value={1}>已确认</Select.Option>
            <Select.Option value={2}>进行中</Select.Option>
            <Select.Option value={3}>已完成</Select.Option>
            <Select.Option value={4}>已取消</Select.Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
        </Space>
      </Card>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={{
          ...pagination,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => fetchData(page, pageSize),
        }}
      />

      <Modal
        title="新增团单订单"
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="customerId" label="团体客户" rules={[{ required: true, message: '请选择团体客户' }]}>
            <Select placeholder="请选择团体客户" showSearch optionFilterProp="children">
              {customerList.map((c) => (
                <Select.Option key={c.id} value={c.id}>{c.customerName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="orderType" label="订单类型" rules={[{ required: true, message: '请选择订单类型' }]}>
            <Select placeholder="请选择订单类型">
              <Select.Option value="TEAM_BUILDING">企业团建</Select.Option>
              <Select.Option value="SCHOOL_CLASS">学校上课</Select.Option>
              <Select.Option value="SWIM_TEAM">游泳队训练</Select.Option>
              <Select.Option value="OTHER">其他</Select.Option>
            </Select>
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="totalCount" label="总次数" rules={[{ required: true, message: '请输入总次数' }]} style={{ flex: 1 }}>
              <InputNumber style={{ width: '100%' }} min={1} placeholder="请输入总次数" />
            </Form.Item>
            <Form.Item name="totalPeople" label="每次人数" style={{ flex: 1 }}>
              <InputNumber style={{ width: '100%' }} min={1} placeholder="每次使用人数" />
            </Form.Item>
          </div>
          <Form.Item name="totalAmount" label="订单金额(元)" rules={[{ required: true, message: '请输入订单金额' }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={0.01} placeholder="请输入订单金额" />
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="validStartDate" label="开始日期" rules={[{ required: true, message: '请选择开始日期' }]} style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} placeholder="请选择开始日期" />
            </Form.Item>
            <Form.Item name="validEndDate" label="结束日期" rules={[{ required: true, message: '请选择结束日期' }]} style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} placeholder="请选择结束日期" />
            </Form.Item>
          </div>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="contactPerson" label="联系人" style={{ flex: 1 }}>
              <Input placeholder="请输入联系人" />
            </Form.Item>
            <Form.Item name="contactPhone" label="联系电话" style={{ flex: 1 }}>
              <Input placeholder="请输入联系电话" />
            </Form.Item>
          </div>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="核销订单"
        open={verifyModalVisible}
        onOk={handleVerifySubmit}
        onCancel={() => setVerifyModalVisible(false)}
        width={500}
      >
        {selectedOrder && (
          <div style={{ marginBottom: 16, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
            <div>订单号：{selectedOrder.orderNo}</div>
            <div>剩余次数：{selectedOrder.totalCount - selectedOrder.usedCount}</div>
          </div>
        )}
        <Form form={verifyForm} layout="vertical">
          <Form.Item name="verifyDate" label="核销日期" rules={[{ required: true, message: '请选择核销日期' }]}>
            <DatePicker style={{ width: '100%' }} placeholder="请选择核销日期" />
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="startTime" label="开始时间" rules={[{ required: true, message: '请选择开始时间' }]} style={{ flex: 1 }}>
              <DatePicker.TimePicker style={{ width: '100%' }} format="HH:mm" placeholder="开始时间" />
            </Form.Item>
            <Form.Item name="endTime" label="结束时间" rules={[{ required: true, message: '请选择结束时间' }]} style={{ flex: 1 }}>
              <DatePicker.TimePicker style={{ width: '100%' }} format="HH:mm" placeholder="结束时间" />
            </Form.Item>
          </div>
          <Form.Item name="venueId" label="场地">
            <Select placeholder="请选择场地">
              <Select.Option value={1}>主泳池</Select.Option>
              <Select.Option value={2}>训练池</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="actualPeople" label="实际人数">
            <InputNumber style={{ width: '100%' }} min={1} placeholder="请输入实际人数" />
          </Form.Item>
          <Form.Item name="usedCount" label="使用次数" initialValue={1}>
            <InputNumber style={{ width: '100%' }} min={1} placeholder="使用次数" />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="订单详情"
        width={700}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {selectedOrder && (
          <>
            <Descriptions column={2} bordered size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="订单编号">{selectedOrder.orderNo}</Descriptions.Item>
              <Descriptions.Item label="团体客户">{selectedOrder.customerName}</Descriptions.Item>
              <Descriptions.Item label="订单类型">{orderTypeMap[selectedOrder.orderType] || selectedOrder.orderType}</Descriptions.Item>
              <Descriptions.Item label="订单金额">{selectedOrder.totalAmount} 元</Descriptions.Item>
              <Descriptions.Item label="总次数">{selectedOrder.totalCount}</Descriptions.Item>
              <Descriptions.Item label="已使用">{selectedOrder.usedCount}</Descriptions.Item>
              <Descriptions.Item label="支付状态">{payStatusMap[selectedOrder.payStatus]?.text || '-'}</Descriptions.Item>
              <Descriptions.Item label="订单状态">{orderStatusMap[selectedOrder.orderStatus]?.text || '-'}</Descriptions.Item>
              <Descriptions.Item label="有效期" span={2}>
                {selectedOrder.validStartDate} 至 {selectedOrder.validEndDate}
              </Descriptions.Item>
              <Descriptions.Item label="联系人">{selectedOrder.contactPerson || '-'}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{selectedOrder.contactPhone || '-'}</Descriptions.Item>
            </Descriptions>

            <h4>核销记录</h4>
            {verificationList.length === 0 ? (
              <div style={{ textAlign: 'center', color: '#999', padding: 20 }}>暂无核销记录</div>
            ) : (
              <List
                size="small"
                bordered
                dataSource={verificationList}
                renderItem={(item) => (
                  <List.Item>
                    <List.Item.Meta
                      title={`${item.verifyDate} ${item.startTime?.slice(0, 5)}-${item.endTime?.slice(0, 5)}`}
                      description={`实际人数：${item.actualPeople || '-'}人，核销人：${item.operatorName || '-'}`}
                    />
                    <Tag color="green">已核销</Tag>
                  </List.Item>
                )}
              />
            )}
          </>
        )}
      </Drawer>
    </div>
  )
}

export default GroupOrder
