import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, DatePicker, InputNumber, message, Card, Tag, Drawer, List, Descriptions } from 'antd'
import { SearchOutlined, EyeOutlined, CheckOutlined, BellOutlined, PlusOutlined } from '@ant-design/icons'
import { getReceivableBillPage, getCustomerList, createReceivableBill, remindReceivableBill, createPayment, getPaymentPage, confirmPayment } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'

interface BillItem {
  id: number
  billNo: string
  customerType: string
  customerId: number
  customerName: string
  billType: string
  billAmount: number
  paidAmount: number
  unpaidAmount: number
  billStatus: number
  billDate: string
  dueDate: string
  settlementPeriod: string
  remark: string
}

interface PaymentItem {
  id: number
  billId: number
  billNo: string
  paymentNo: string
  paymentAmount: number
  paymentType: string
  payMethod: string
  paymentStatus: number
  paymentDate: string
  operatorName: string
  remark: string
}

const billStatusMap: Record<number, { text: string; color: string }> = {
  0: { text: '未支付', color: 'red' },
  1: { text: '部分支付', color: 'orange' },
  2: { text: '已结清', color: 'green' },
  3: { text: '已逾期', color: 'red' },
  4: { text: '已作废', color: 'default' },
}

const billTypeMap: Record<string, string> = {
  GROUP_ORDER: '团体订单',
  MEMBER_RECHARGE: '会员充值',
  OTHER: '其他',
}

const customerTypeMap: Record<string, string> = {
  GROUP: '团体客户',
  MEMBER: '会员',
  OTHER: '其他',
}

function ReceivableBill() {
  const [list, setList] = useState<BillItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [customerId, setCustomerId] = useState<number | undefined>()
  const [billStatus, setBillStatus] = useState<number | undefined>()
  const [customerType, setCustomerType] = useState<string | undefined>()
  const [customerList, setCustomerList] = useState<any[]>([])
  const [modalVisible, setModalVisible] = useState(false)
  const [detailVisible, setDetailVisible] = useState(false)
  const [payModalVisible, setPayModalVisible] = useState(false)
  const [selectedBill, setSelectedBill] = useState<BillItem | null>(null)
  const [paymentList, setPaymentList] = useState<PaymentItem[]>([])
  const [form] = Form.useForm()
  const [payForm] = Form.useForm()

  const fetchData = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res: any = await getReceivableBillPage({ pageNum: page, pageSize, customerId, billStatus, customerType })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch bill list:', error)
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
  }, [customerId, billStatus, customerType])

  const handleSearch = () => {
    fetchData(1, pagination.pageSize)
  }

  const handleAdd = () => {
    form.resetFields()
    setModalVisible(true)
  }

  const handleRemind = (record: BillItem) => {
    Modal.confirm({
      title: '发送催款提醒',
      content: `确定要向"${record.customerName}"发送应收账单"${record.billNo}"的催款提醒吗？`,
      onOk: async () => {
        try {
          await remindReceivableBill(record.id)
          message.success('提醒发送成功')
        } catch (error) {
          console.error('Failed to remind:', error)
        }
      },
    })
  }

  const handleViewDetail = async (record: BillItem) => {
    setSelectedBill(record)
    try {
      const res: any = await getPaymentPage({ pageNum: 1, pageSize: 100, billId: record.id })
      setPaymentList(res.records || [])
    } catch {
      setPaymentList([])
    }
    setDetailVisible(true)
  }

  const handlePay = (record: BillItem) => {
    setSelectedBill(record)
    payForm.resetFields()
    payForm.setFieldsValue({
      paymentDate: dayjs(),
      paymentAmount: record.unpaidAmount,
    })
    setPayModalVisible(true)
  }

  const handlePaySubmit = async () => {
    if (!selectedBill) return
    try {
      const values = await payForm.validateFields()
      const data = {
        billId: selectedBill.id,
        paymentAmount: values.paymentAmount,
        payMethod: values.payMethod,
        paymentDate: values.paymentDate?.format('YYYY-MM-DD'),
        remark: values.remark,
      }
      const res: any = await createPayment(data)
      if (res && res.id) {
        await confirmPayment(res.id)
      }
      message.success('收款成功')
      setPayModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Failed to pay:', error)
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const data = {
        ...values,
        billDate: values.billDate?.format('YYYY-MM-DD'),
        dueDate: values.dueDate?.format('YYYY-MM-DD'),
      }
      await createReceivableBill(data)
      message.success('创建成功')
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<BillItem> = [
    { title: '账单编号', dataIndex: 'billNo', key: 'billNo', width: 160 },
    { title: '客户名称', dataIndex: 'customerName', key: 'customerName' },
    {
      title: '客户类型',
      dataIndex: 'customerType',
      key: 'customerType',
      render: (type) => customerTypeMap[type] || type,
    },
    {
      title: '账单类型',
      dataIndex: 'billType',
      key: 'billType',
      render: (type) => billTypeMap[type] || type,
    },
    { title: '账单金额(元)', dataIndex: 'billAmount', key: 'billAmount', width: 120 },
    { title: '已收(元)', dataIndex: 'paidAmount', key: 'paidAmount', width: 100 },
    { title: '未收(元)', dataIndex: 'unpaidAmount', key: 'unpaidAmount', width: 100 },
    {
      title: '状态',
      dataIndex: 'billStatus',
      key: 'billStatus',
      render: (status) => {
        const info = billStatusMap[status] || { text: '未知', color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    { title: '账单日期', dataIndex: 'billDate', key: 'billDate', width: 110 },
    { title: '到期日期', dataIndex: 'dueDate', key: 'dueDate', width: 110 },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {(record.billStatus === 0 || record.billStatus === 1 || record.billStatus === 3) && (
            <Button type="link" size="small" icon={<CheckOutlined />} onClick={() => handlePay(record)}>
              收款
            </Button>
          )}
          {(record.billStatus === 0 || record.billStatus === 3) && (
            <Button type="link" size="small" icon={<BellOutlined />} onClick={() => handleRemind(record)}>
              催款
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">应收账单</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增账单
        </Button>
      </div>

      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Select
            placeholder="客户类型"
            style={{ width: 140 }}
            value={customerType}
            onChange={(val) => setCustomerType(val)}
            allowClear
          >
            <Select.Option value="GROUP">团体客户</Select.Option>
            <Select.Option value="MEMBER">会员</Select.Option>
            <Select.Option value="OTHER">其他</Select.Option>
          </Select>
          <Select
            placeholder="选择客户"
            style={{ width: 200 }}
            value={customerId}
            onChange={(val) => setCustomerId(val)}
            allowClear
            showSearch
            optionFilterProp="children"
            disabled={!customerType || customerType !== 'GROUP'}
          >
            {customerList.map((c) => (
              <Select.Option key={c.id} value={c.id}>{c.customerName}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="账单状态"
            style={{ width: 140 }}
            value={billStatus}
            onChange={(val) => setBillStatus(val)}
            allowClear
          >
            <Select.Option value={0}>未支付</Select.Option>
            <Select.Option value={1}>部分支付</Select.Option>
            <Select.Option value={2}>已结清</Select.Option>
            <Select.Option value={3}>已逾期</Select.Option>
            <Select.Option value={4}>已作废</Select.Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
        </Space>
      </Card>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={list}
        loading={loading}
        scroll={{ x: 1200 }}
        pagination={{
          ...pagination,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => fetchData(page, pageSize),
        }}
      />

      <Modal
        title="新增应收账单"
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={550}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="customerType" label="客户类型" rules={[{ required: true, message: '请选择客户类型' }]}>
            <Select placeholder="请选择客户类型">
              <Select.Option value="GROUP">团体客户</Select.Option>
              <Select.Option value="MEMBER">会员</Select.Option>
              <Select.Option value="OTHER">其他</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="customerId" label="客户" rules={[{ required: true, message: '请选择客户' }]}>
            <Select placeholder="请选择客户" showSearch optionFilterProp="children">
              {customerList.map((c) => (
                <Select.Option key={c.id} value={c.id}>{c.customerName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="billType" label="账单类型" rules={[{ required: true, message: '请选择账单类型' }]}>
            <Select placeholder="请选择账单类型">
              <Select.Option value="GROUP_ORDER">团体订单</Select.Option>
              <Select.Option value="MEMBER_RECHARGE">会员充值</Select.Option>
              <Select.Option value="OTHER">其他</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="billAmount" label="账单金额(元)" rules={[{ required: true, message: '请输入账单金额' }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={0.01} placeholder="请输入账单金额" />
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="billDate" label="账单日期" rules={[{ required: true, message: '请选择账单日期' }]} style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} placeholder="请选择账单日期" />
            </Form.Item>
            <Form.Item name="dueDate" label="到期日期" rules={[{ required: true, message: '请选择到期日期' }]} style={{ flex: 1 }}>
              <DatePicker style={{ width: '100%' }} placeholder="请选择到期日期" />
            </Form.Item>
          </div>
          <Form.Item name="settlementPeriod" label="结算周期">
            <Input placeholder="如：2024年6月" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="收款核销"
        open={payModalVisible}
        onOk={handlePaySubmit}
        onCancel={() => setPayModalVisible(false)}
        width={450}
      >
        {selectedBill && (
          <div style={{ marginBottom: 16, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
            <div>账单号：{selectedBill.billNo}</div>
            <div>未收金额：<span style={{ color: '#f5222d', fontWeight: 'bold' }}>{selectedBill.unpaidAmount} 元</span></div>
          </div>
        )}
        <Form form={payForm} layout="vertical">
          <Form.Item name="paymentAmount" label="收款金额(元)" rules={[{ required: true, message: '请输入收款金额' }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={0.01} placeholder="请输入收款金额" />
          </Form.Item>
          <Form.Item name="payMethod" label="收款方式" rules={[{ required: true, message: '请选择收款方式' }]}>
            <Select placeholder="请选择收款方式">
              <Select.Option value="BANK_TRANSFER">银行转账</Select.Option>
              <Select.Option value="ALIPAY">支付宝</Select.Option>
              <Select.Option value="WECHAT">微信</Select.Option>
              <Select.Option value="CASH">现金</Select.Option>
              <Select.Option value="PREPAYMENT">预存抵扣</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="paymentDate" label="收款日期" rules={[{ required: true, message: '请选择收款日期' }]}>
            <DatePicker style={{ width: '100%' }} placeholder="请选择收款日期" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="账单详情"
        width={600}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {selectedBill && (
          <>
            <Descriptions column={2} bordered size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="账单编号">{selectedBill.billNo}</Descriptions.Item>
              <Descriptions.Item label="客户名称">{selectedBill.customerName}</Descriptions.Item>
              <Descriptions.Item label="客户类型">{customerTypeMap[selectedBill.customerType] || selectedBill.customerType}</Descriptions.Item>
              <Descriptions.Item label="账单类型">{billTypeMap[selectedBill.billType] || selectedBill.billType}</Descriptions.Item>
              <Descriptions.Item label="账单金额">{selectedBill.billAmount} 元</Descriptions.Item>
              <Descriptions.Item label="已收金额">{selectedBill.paidAmount} 元</Descriptions.Item>
              <Descriptions.Item label="未收金额" span={2}>
                <span style={{ color: '#f5222d', fontWeight: 'bold' }}>{selectedBill.unpaidAmount} 元</span>
              </Descriptions.Item>
              <Descriptions.Item label="账单日期">{selectedBill.billDate}</Descriptions.Item>
              <Descriptions.Item label="到期日期">{selectedBill.dueDate}</Descriptions.Item>
              <Descriptions.Item label="结算周期">{selectedBill.settlementPeriod || '-'}</Descriptions.Item>
              <Descriptions.Item label="状态">
                {billStatusMap[selectedBill.billStatus]?.text || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="备注" span={2}>{selectedBill.remark || '-'}</Descriptions.Item>
            </Descriptions>

            <h4>收款记录</h4>
            {paymentList.length === 0 ? (
              <div style={{ textAlign: 'center', color: '#999', padding: 20 }}>暂无收款记录</div>
            ) : (
              <List
                size="small"
                bordered
                dataSource={paymentList}
                renderItem={(item) => (
                  <List.Item>
                    <List.Item.Meta
                      title={`${item.paymentDate} 收款 ${item.paymentAmount} 元`}
                      description={`收款方式：${item.payMethod || '-'}，操作人：${item.operatorName || '-'}`}
                    />
                    <Tag color="green">已确认</Tag>
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

export default ReceivableBill
