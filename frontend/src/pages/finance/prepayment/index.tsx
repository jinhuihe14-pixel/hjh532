import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, message, Card, Tag, Drawer, List, Descriptions, Avatar } from 'antd'
import { SearchOutlined, EyeOutlined, PlusOutlined, RedoOutlined, MinusOutlined, UserOutlined } from '@ant-design/icons'
import { getPrepaymentAccountPage, createPrepaymentAccount, rechargePrepayment, consumePrepayment, getPrepaymentLogPage, getCustomerList } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'

interface AccountItem {
  id: number
  accountNo: string
  accountType: string
  customerType: string
  customerId: number
  customerName: string
  principalBalance: number
  giftBalance: number
  frozenBalance: number
  totalBalance: number
  status: number
  createTime: string
}

interface LogItem {
  id: number
  accountId: number
  changeType: string
  balanceType: string
  changeAmount: number
  balanceAfter: number
  businessType: string
  businessId: number
  businessNo: string
  operatorName: string
  remark: string
  createTime: string
}

const accountTypeMap: Record<string, string> = {
  GROUP: '团体账户',
  MEMBER: '个人账户',
  COMPANY: '企业账户',
}

const customerTypeMap: Record<string, string> = {
  GROUP: '团体客户',
  MEMBER: '会员',
  ENTERPRISE: '企业',
}

const statusMap: Record<number, { text: string; color: string }> = {
  1: { text: '正常', color: 'green' },
  0: { text: '冻结', color: 'orange' },
  2: { text: '注销', color: 'default' },
}

const changeTypeMap: Record<string, string> = {
  RECHARGE: '充值',
  CONSUME: '消费',
  RECHARGE_GIFT: '充值赠送',
  REFUND: '退款',
  FREEZE: '冻结',
  UNFREEZE: '解冻',
  ADJUST: '调整',
}

const balanceTypeMap: Record<string, string> = {
  PRINCIPAL: '本金',
  GIFT: '赠金',
  FROZEN: '冻结金',
}

function PrepaymentAccount() {
  const [list, setList] = useState<AccountItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [keyword, setKeyword] = useState('')
  const [accountType, setAccountType] = useState<string | undefined>()
  const [status, setStatus] = useState<number | undefined>()
  const [customerList, setCustomerList] = useState<any[]>([])
  const [modalVisible, setModalVisible] = useState(false)
  const [rechargeVisible, setRechargeVisible] = useState(false)
  const [consumeVisible, setConsumeVisible] = useState(false)
  const [logVisible, setLogVisible] = useState(false)
  const [selectedAccount, setSelectedAccount] = useState<AccountItem | null>(null)
  const [logList, setLogList] = useState<LogItem[]>([])
  const [logLoading, setLogLoading] = useState(false)
  const [form] = Form.useForm()
  const [rechargeForm] = Form.useForm()
  const [consumeForm] = Form.useForm()

  const fetchData = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res: any = await getPrepaymentAccountPage({ pageNum: page, pageSize, keyword, accountType, status })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch account list:', error)
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
  }, [keyword, accountType, status])

  const handleSearch = () => {
    fetchData(1, pagination.pageSize)
  }

  const handleAdd = () => {
    form.resetFields()
    setModalVisible(true)
  }

  const handleRecharge = (record: AccountItem) => {
    setSelectedAccount(record)
    rechargeForm.resetFields()
    setRechargeVisible(true)
  }

  const handleConsume = (record: AccountItem) => {
    setSelectedAccount(record)
    consumeForm.resetFields()
    setConsumeVisible(true)
  }

  const handleViewLog = async (record: AccountItem) => {
    setSelectedAccount(record)
    setLogVisible(true)
    setLogLoading(true)
    try {
      const res: any = await getPrepaymentLogPage({ pageNum: 1, pageSize: 50, accountId: record.id })
      setLogList(res.records || [])
    } catch {
      setLogList([])
    } finally {
      setLogLoading(false)
    }
  }

  const handleRechargeSubmit = async () => {
    if (!selectedAccount) return
    try {
      const values = await rechargeForm.validateFields()
      await rechargePrepayment(selectedAccount.id, {
        amount: values.amount,
        giftAmount: values.giftAmount,
        remark: values.remark,
      })
      message.success('充值成功')
      setRechargeVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Failed to recharge:', error)
    }
  }

  const handleConsumeSubmit = async () => {
    if (!selectedAccount) return
    try {
      const values = await consumeForm.validateFields()
      await consumePrepayment(selectedAccount.id, {
        amount: values.amount,
        balanceType: values.balanceType || 'PRINCIPAL',
        remark: values.remark,
      })
      message.success('扣款成功')
      setConsumeVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Failed to consume:', error)
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      await createPrepaymentAccount(values)
      message.success('创建成功')
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<AccountItem> = [
    {
      title: '账户信息',
      dataIndex: 'accountNo',
      key: 'accountNo',
      width: 220,
      render: (_, record) => (
        <Space>
          <Avatar size="small" icon={<UserOutlined />} />
          <div>
            <div style={{ fontWeight: 500 }}>{record.customerName}</div>
            <div style={{ fontSize: 12, color: '#999' }}>{record.accountNo}</div>
          </div>
        </Space>
      ),
    },
    {
      title: '账户类型',
      dataIndex: 'accountType',
      key: 'accountType',
      render: (type) => accountTypeMap[type] || type,
    },
    {
      title: '客户类型',
      dataIndex: 'customerType',
      key: 'customerType',
      render: (type) => customerTypeMap[type] || type,
    },
    { title: '本金余额(元)', dataIndex: 'principalBalance', key: 'principalBalance', width: 120 },
    { title: '赠金余额(元)', dataIndex: 'giftBalance', key: 'giftBalance', width: 120 },
    { title: '冻结金额(元)', dataIndex: 'frozenBalance', key: 'frozenBalance', width: 120 },
    {
      title: '总余额(元)',
      dataIndex: 'totalBalance',
      key: 'totalBalance',
      width: 120,
      render: (val) => <span style={{ fontWeight: 'bold', color: '#1890ff' }}>{val}</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (s) => {
        const info = statusMap[s] || { text: '未知', color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewLog(record)}>
            流水
          </Button>
          <Button type="link" size="small" icon={<RedoOutlined />} onClick={() => handleRecharge(record)}>
            充值
          </Button>
          <Button type="link" size="small" danger icon={<MinusOutlined />} onClick={() => handleConsume(record)}>
            扣款
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">预存账户</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新建账户
        </Button>
      </div>

      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="账户名称/账号搜索"
            style={{ width: 220 }}
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
            allowClear
            prefix={<SearchOutlined style={{ color: '#bbb' }} />}
          />
          <Select
            placeholder="账户类型"
            style={{ width: 140 }}
            value={accountType}
            onChange={(val) => setAccountType(val)}
            allowClear
          >
            <Select.Option value="GROUP">团体账户</Select.Option>
            <Select.Option value="MEMBER">个人账户</Select.Option>
            <Select.Option value="COMPANY">企业账户</Select.Option>
          </Select>
          <Select
            placeholder="状态"
            style={{ width: 120 }}
            value={status}
            onChange={(val) => setStatus(val)}
            allowClear
          >
            <Select.Option value={1}>正常</Select.Option>
            <Select.Option value={0}>冻结</Select.Option>
            <Select.Option value={2}>注销</Select.Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
        </Space>
      </Card>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={list}
        loading={loading}
        scroll={{ x: 1100 }}
        pagination={{
          ...pagination,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => fetchData(page, pageSize),
        }}
      />

      <Modal
        title="新建预存账户"
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={500}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="accountType" label="账户类型" rules={[{ required: true, message: '请选择账户类型' }]}>
            <Select placeholder="请选择账户类型">
              <Select.Option value="GROUP">团体账户</Select.Option>
              <Select.Option value="MEMBER">个人账户</Select.Option>
              <Select.Option value="COMPANY">企业账户</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="customerType" label="客户类型" rules={[{ required: true, message: '请选择客户类型' }]}>
            <Select placeholder="请选择客户类型">
              <Select.Option value="GROUP">团体客户</Select.Option>
              <Select.Option value="MEMBER">会员</Select.Option>
              <Select.Option value="ENTERPRISE">企业</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="customerId" label="所属客户" rules={[{ required: true, message: '请选择客户' }]}>
            <Select placeholder="请选择客户" showSearch optionFilterProp="children">
              {customerList.map((c) => (
                <Select.Option key={c.id} value={c.id}>{c.customerName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="customerName" label="客户名称" rules={[{ required: true, message: '请输入客户名称' }]}>
            <Input placeholder="请输入客户名称" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>冻结</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="账户充值"
        open={rechargeVisible}
        onOk={handleRechargeSubmit}
        onCancel={() => setRechargeVisible(false)}
        width={420}
      >
        {selectedAccount && (
          <div style={{ marginBottom: 16, padding: 12, background: '#f0f9ff', borderRadius: 4 }}>
            <div>账户：{selectedAccount.customerName}</div>
            <div>当前总余额：<span style={{ color: '#1890ff', fontWeight: 'bold' }}>{selectedAccount.totalBalance} 元</span></div>
          </div>
        )}
        <Form form={rechargeForm} layout="vertical">
          <Form.Item name="amount" label="充值金额(元)" rules={[{ required: true, message: '请输入充值金额' }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={100} placeholder="请输入充值金额" />
          </Form.Item>
          <Form.Item name="giftAmount" label="赠送金额(元)">
            <InputNumber style={{ width: '100%' }} min={0} step={10} placeholder="请输入赠送金额" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="账户扣款"
        open={consumeVisible}
        onOk={handleConsumeSubmit}
        onCancel={() => setConsumeVisible(false)}
        width={420}
      >
        {selectedAccount && (
          <div style={{ marginBottom: 16, padding: 12, background: '#fff7e6', borderRadius: 4 }}>
            <div>账户：{selectedAccount.customerName}</div>
            <div>本金余额：{selectedAccount.principalBalance} 元</div>
            <div>赠金余额：{selectedAccount.giftBalance} 元</div>
          </div>
        )}
        <Form form={consumeForm} layout="vertical">
          <Form.Item name="amount" label="扣款金额(元)" rules={[{ required: true, message: '请输入扣款金额' }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={10} placeholder="请输入扣款金额" />
          </Form.Item>
          <Form.Item name="balanceType" label="扣除类型" initialValue="PRINCIPAL">
            <Select placeholder="请选择扣除类型">
              <Select.Option value="PRINCIPAL">本金</Select.Option>
              <Select.Option value="GIFT">赠金</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="账户流水"
        width={600}
        open={logVisible}
        onClose={() => setLogVisible(false)}
      >
        {selectedAccount && (
          <>
            <Descriptions column={3} size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="账户">{selectedAccount.customerName}</Descriptions.Item>
              <Descriptions.Item label="总余额">{selectedAccount.totalBalance} 元</Descriptions.Item>
              <Descriptions.Item label="状态">{statusMap[selectedAccount.status]?.text}</Descriptions.Item>
            </Descriptions>

            <h4>账户流水记录</h4>
            <List
              size="small"
              bordered
              loading={logLoading}
              dataSource={logList}
              locale={{ emptyText: '暂无流水记录' }}
              renderItem={(item) => {
                const isAdd = item.changeType === 'RECHARGE' || item.changeType === 'RECHARGE_GIFT' || item.changeType === 'UNFREEZE'
                return (
                  <List.Item>
                    <List.Item.Meta
                      avatar={
                        <Tag color={isAdd ? 'green' : 'red'} style={{ fontSize: 18, padding: '4px 10px' }}>
                          {isAdd ? '+' : '-'}{Math.abs(item.changeAmount)}
                        </Tag>
                      }
                      title={
                        <Space>
                          <span>{changeTypeMap[item.changeType] || item.changeType}</span>
                          <Tag color="blue">{balanceTypeMap[item.balanceType] || item.balanceType}</Tag>
                        </Space>
                      }
                      description={
                        <>
                          <div>{item.businessNo || item.businessType || '-'}</div>
                          <div style={{ fontSize: 12, color: '#999' }}>
                            {dayjs(item.createTime).format('YYYY-MM-DD HH:mm')} · {item.operatorName || '系统'}
                          </div>
                          {item.remark && <div style={{ fontSize: 12 }}>{item.remark}</div>}
                        </>
                      }
                    />
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: 12, color: '#999' }}>余额</div>
                      <div style={{ fontWeight: 'bold' }}>{item.balanceAfter} 元</div>
                    </div>
                  </List.Item>
                )
              }}
            />
          </>
        )}
      </Drawer>
    </div>
  )
}

export default PrepaymentAccount
