import { useState, useEffect } from 'react'
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  Select,
  DatePicker,
  InputNumber,
  message,
  Tag,
  Drawer,
  Descriptions,
  Card,
  Statistic,
  Row,
  Col,
} from 'antd'
import {
  PlusOutlined,
  EditOutlined,
  SearchOutlined,
  WalletOutlined,
  GiftOutlined,
  UserOutlined,
  ReloadOutlined,
} from '@ant-design/icons'
import { getMemberPage, registerMember, updateMember, rechargeMember, getMemberCards } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'

interface MemberItem {
  id: number
  memberNo: string
  memberName: string
  phone: string
  memberLevel: string
  principalBalance: number
  giftBalance: number
  status: number
  createTime: string
}

const levelMap: Record<string, { text: string; color: string }> = {
  NORMAL: { text: '普通', color: 'default' },
  SILVER: { text: '银卡', color: 'blue' },
  GOLD: { text: '金卡', color: 'gold' },
  PLATINUM: { text: '铂金', color: 'purple' },
}

function MemberList() {
  const [list, setList] = useState<MemberItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingItem, setEditingItem] = useState<MemberItem | null>(null)
  const [form] = Form.useForm()
  const [detailVisible, setDetailVisible] = useState(false)
  const [detailItem, setDetailItem] = useState<MemberItem | null>(null)
  const [rechargeVisible, setRechargeVisible] = useState(false)
  const [rechargeForm] = Form.useForm()
  const [cardList, setCardList] = useState<any[]>([])

  const fetchData = async (page = 1, pageSize = 10, params?: any) => {
    setLoading(true)
    try {
      const res = await getMemberPage({ pageNum: page, pageSize, ...params })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch member list:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleSearch = () => {
    const values = searchForm.getFieldsValue()
    fetchData(1, pagination.pageSize, values)
  }

  const handleReset = () => {
    searchForm.resetFields()
    fetchData(1, pagination.pageSize)
  }

  const handleAdd = () => {
    setEditingItem(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: MemberItem) => {
    setEditingItem(record)
    form.setFieldsValue({
      ...record,
      birthday: record.birthday ? dayjs(record.birthday) : undefined,
    })
    setModalVisible(true)
  }

  const handleDetail = async (record: MemberItem) => {
    setDetailItem(record)
    setDetailVisible(true)
    try {
      const res = await getMemberCards(record.id)
      setCardList(res || [])
    } catch (error) {
      console.error('Failed to fetch member cards:', error)
    }
  }

  const handleRecharge = (record: MemberItem) => {
    setDetailItem(record)
    rechargeForm.resetFields()
    setRechargeVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const submitData = {
        ...values,
        birthday: values.birthday ? values.birthday.format('YYYY-MM-DD') : undefined,
      }
      if (editingItem) {
        await updateMember({ id: editingItem.id, ...submitData })
        message.success('修改成功')
      } else {
        await registerMember(submitData)
        message.success('注册成功')
      }
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const handleRechargeSubmit = async () => {
    try {
      const values = await rechargeForm.validateFields()
      if (detailItem) {
        await rechargeMember(detailItem.id, values)
        message.success('充值成功')
        setRechargeVisible(false)
        fetchData(pagination.current, pagination.pageSize)
      }
    } catch (error) {
      console.error('Recharge failed:', error)
    }
  }

  const columns: ColumnsType<MemberItem> = [
    { title: '会员编号', dataIndex: 'memberNo', key: 'memberNo', width: 120 },
    { title: '会员姓名', dataIndex: 'memberName', key: 'memberName', width: 100 },
    { title: '手机号', dataIndex: 'phone', key: 'phone', width: 120 },
    {
      title: '会员等级',
      dataIndex: 'memberLevel',
      key: 'memberLevel',
      width: 80,
      render: (level) => {
        const info = levelMap[level] || { text: level, color: 'default' }
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    {
      title: '储值本金',
      dataIndex: 'principalBalance',
      key: 'principalBalance',
      width: 100,
      render: (val) => (
        <span style={{ color: '#52c41a' }}>¥{val?.toFixed(2) || '0.00'}</span>
      ),
    },
    {
      title: '赠送余额',
      dataIndex: 'giftBalance',
      key: 'giftBalance',
      width: 100,
      render: (val) => (
        <span style={{ color: '#1890ff' }}>¥{val?.toFixed(2) || '0.00'}</span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '正常' : '冻结'}
        </Tag>
      ),
    },
    {
      title: '注册时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleDetail(record)}>
            详情
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" size="small" icon={<ReloadOutlined />} onClick={() => handleRecharge(record)}>
            充值
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">会员列表</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增会员
        </Button>
      </div>

      <div className="search-bar">
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Form.Item name="keyword" label="关键词">
            <Input placeholder="姓名/手机号/会员号" style={{ width: 200 }} allowClear />
          </Form.Item>
          <Form.Item name="memberLevel" label="会员等级">
            <Select placeholder="请选择" style={{ width: 120 }} allowClear>
              <Select.Option value="NORMAL">普通</Select.Option>
              <Select.Option value="SILVER">银卡</Select.Option>
              <Select.Option value="GOLD">金卡</Select.Option>
              <Select.Option value="PLATINUM">铂金</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择" style={{ width: 100 }} allowClear>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>冻结</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" icon={<SearchOutlined />} htmlType="submit">
                搜索
              </Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </div>

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
        title={editingItem ? '编辑会员' : '新增会员'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="memberName" label="会员姓名" rules={[{ required: true, message: '请输入会员姓名' }]}>
            <Input placeholder="请输入会员姓名" />
          </Form.Item>
          <Form.Item name="phone" label="手机号" rules={[{ required: true, message: '请输入手机号' }]}>
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item name="idCard" label="身份证号">
            <Input placeholder="请输入身份证号" />
          </Form.Item>
          <Form.Item name="gender" label="性别">
            <Select placeholder="请选择">
              <Select.Option value={1}>男</Select.Option>
              <Select.Option value={0}>女</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="birthday" label="生日">
            <DatePicker style={{ width: '100%' }} placeholder="请选择生日" />
          </Form.Item>
          <Form.Item name="memberLevel" label="会员等级">
            <Select placeholder="请选择会员等级">
              <Select.Option value="NORMAL">普通</Select.Option>
              <Select.Option value="SILVER">银卡</Select.Option>
              <Select.Option value="GOLD">金卡</Select.Option>
              <Select.Option value="PLATINUM">铂金</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="address" label="地址">
            <Input placeholder="请输入地址" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="会员详情"
        width={600}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {detailItem && (
          <>
            <Card style={{ marginBottom: 16 }}>
              <Descriptions title="基本信息" column={2} size="small">
                <Descriptions.Item label="会员编号">{detailItem.memberNo}</Descriptions.Item>
                <Descriptions.Item label="会员姓名">{detailItem.memberName}</Descriptions.Item>
                <Descriptions.Item label="手机号">{detailItem.phone}</Descriptions.Item>
                <Descriptions.Item label="会员等级">
                  {levelMap[detailItem.memberLevel]?.text || detailItem.memberLevel}
                </Descriptions.Item>
              </Descriptions>
            </Card>
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={12}>
                <Card>
                  <Statistic
                    title="储值本金"
                    value={detailItem.principalBalance || 0}
                    precision={2}
                    prefix={<WalletOutlined />}
                    valueStyle={{ color: '#52c41a' }}
                  />
                </Card>
              </Col>
              <Col span={12}>
                <Card>
                  <Statistic
                    title="赠送余额"
                    value={detailItem.giftBalance || 0}
                    precision={2}
                    prefix={<GiftOutlined />}
                    valueStyle={{ color: '#1890ff' }}
                  />
                </Card>
              </Col>
            </Row>
            <Card title="会员卡列表">
              <Table
                size="small"
                rowKey="id"
                dataSource={cardList}
                pagination={false}
                columns={[
                  { title: '卡号', dataIndex: 'cardNo', key: 'cardNo' },
                  { title: '卡种', dataIndex: 'cardName', key: 'cardName' },
                  { title: '类型', dataIndex: 'cardType', key: 'cardType' },
                  { title: '状态', dataIndex: 'status', key: 'status',
                    render: (s) => s === 1 ? '正常' : s === 2 ? '已过期' : s === 3 ? '已用完' : '冻结'
                  },
                  { title: '有效期', dataIndex: 'validEndDate', key: 'validEndDate' },
                ]}
              />
            </Card>
          </>
        )}
      </Drawer>

      <Modal
        title="会员充值"
        open={rechargeVisible}
        onOk={handleRechargeSubmit}
        onCancel={() => setRechargeVisible(false)}
        width={400}
      >
        <Form form={rechargeForm} layout="vertical">
          <Form.Item name="principalAmount" label="储值本金">
            <InputNumber
              style={{ width: '100%' }}
              prefix="¥"
              min={0}
              step={100}
              placeholder="请输入充值金额"
            />
          </Form.Item>
          <Form.Item name="giftAmount" label="赠送金额">
            <InputNumber
              style={{ width: '100%' }}
              prefix="¥"
              min={0}
              step={10}
              placeholder="请输入赠送金额"
            />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default MemberList
