import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, message, Card } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import { getCustomerPage, addCustomer, updateCustomer, deleteCustomer } from '@/api'
import type { ColumnsType } from 'antd/es/table'

interface CustomerItem {
  id: number
  customerName: string
  customerCode: string
  customerType: string
  customerLevel: string
  contactPerson: string
  contactPhone: string
  discountRate: number
  status: number
  address: string
  remark: string
}

const customerTypeMap: Record<string, string> = {
  ENTERPRISE: '企业',
  SCHOOL: '学校',
  GOVERNMENT: '政府机构',
  OTHER: '其他',
}

const customerLevelMap: Record<string, string> = {
  NORMAL: '普通',
  SILVER: '银牌',
  GOLD: '金牌',
  PLATINUM: '铂金',
}

function GroupCustomer() {
  const [list, setList] = useState<CustomerItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [keyword, setKeyword] = useState('')
  const [customerType, setCustomerType] = useState<string | undefined>()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingItem, setEditingItem] = useState<CustomerItem | null>(null)
  const [form] = Form.useForm()

  const fetchData = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res: any = await getCustomerPage({ pageNum: page, pageSize, keyword, customerType })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch customer list:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [keyword, customerType])

  const handleSearch = () => {
    fetchData(1, pagination.pageSize)
  }

  const handleAdd = () => {
    setEditingItem(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: CustomerItem) => {
    setEditingItem(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = (record: CustomerItem) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除团体客户"${record.customerName}"吗？`,
      onOk: async () => {
        try {
          await deleteCustomer(record.id)
          message.success('删除成功')
          fetchData(pagination.current, pagination.pageSize)
        } catch (error) {
          console.error('Failed to delete customer:', error)
        }
      },
    })
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingItem) {
        await updateCustomer({ ...editingItem, ...values })
        message.success('修改成功')
      } else {
        await addCustomer(values)
        message.success('添加成功')
      }
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<CustomerItem> = [
    { title: '企业名称', dataIndex: 'customerName', key: 'customerName' },
    { title: '客户编码', dataIndex: 'customerCode', key: 'customerCode' },
    {
      title: '客户类型',
      dataIndex: 'customerType',
      key: 'customerType',
      render: (type) => customerTypeMap[type] || type,
    },
    {
      title: '客户等级',
      dataIndex: 'customerLevel',
      key: 'customerLevel',
      render: (level) => customerLevelMap[level] || level,
    },
    { title: '联系人', dataIndex: 'contactPerson', key: 'contactPerson' },
    { title: '联系电话', dataIndex: 'contactPhone', key: 'contactPhone' },
    {
      title: '优惠折扣',
      dataIndex: 'discountRate',
      key: 'discountRate',
      render: (rate) => rate ? `${(rate * 100).toFixed(0)}折` : '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (status === 1 ? '正常' : '停用'),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <div className="page-header">
        <div className="page-title">团体客户</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增客户
        </Button>
      </div>

      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="请输入企业名称/联系人"
            style={{ width: 220 }}
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
            allowClear
          />
          <Select
            placeholder="客户类型"
            style={{ width: 140 }}
            value={customerType}
            onChange={(val) => setCustomerType(val)}
            allowClear
          >
            <Select.Option value="ENTERPRISE">企业</Select.Option>
            <Select.Option value="SCHOOL">学校</Select.Option>
            <Select.Option value="GOVERNMENT">政府机构</Select.Option>
            <Select.Option value="OTHER">其他</Select.Option>
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
        title={editingItem ? '编辑团体客户' : '新增团体客户'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="customerName" label="企业名称" rules={[{ required: true, message: '请输入企业名称' }]}>
            <Input placeholder="请输入企业名称" />
          </Form.Item>
          <Form.Item name="customerCode" label="客户编码">
            <Input placeholder="系统自动生成，可留空" />
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="customerType" label="客户类型" rules={[{ required: true, message: '请选择客户类型' }]} style={{ flex: 1 }}>
              <Select placeholder="请选择客户类型">
                <Select.Option value="ENTERPRISE">企业</Select.Option>
                <Select.Option value="SCHOOL">学校</Select.Option>
                <Select.Option value="GOVERNMENT">政府机构</Select.Option>
                <Select.Option value="OTHER">其他</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="customerLevel" label="客户等级" style={{ flex: 1 }}>
              <Select placeholder="请选择客户等级">
                <Select.Option value="NORMAL">普通</Select.Option>
                <Select.Option value="SILVER">银牌</Select.Option>
                <Select.Option value="GOLD">金牌</Select.Option>
                <Select.Option value="PLATINUM">铂金</Select.Option>
              </Select>
            </Form.Item>
          </div>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="contactPerson" label="联系人" rules={[{ required: true, message: '请输入联系人' }]} style={{ flex: 1 }}>
              <Input placeholder="请输入联系人姓名" />
            </Form.Item>
            <Form.Item name="contactPhone" label="联系电话" rules={[{ required: true, message: '请输入联系电话' }]} style={{ flex: 1 }}>
              <Input placeholder="请输入联系电话" />
            </Form.Item>
          </div>
          <Form.Item name="discountRate" label="优惠折扣">
            <InputNumber style={{ width: '100%' }} min={0} max={1} step={0.05} placeholder="例如：0.9表示9折" />
          </Form.Item>
          <Form.Item name="address" label="地址">
            <Input placeholder="请输入地址" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>停用</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default GroupCustomer
