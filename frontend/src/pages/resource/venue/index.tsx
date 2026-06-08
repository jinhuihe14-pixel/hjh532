import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { getVenueList, addVenue, updateVenue, deleteVenue } from '@/api'
import type { ColumnsType } from 'antd/es/table'

interface VenueItem {
  id: number
  venueName: string
  venueCode: string
  venueType: string
  location: string
  area: number
  capacity: number
  status: number
  description: string
}

const venueTypeMap: Record<string, string> = {
  POOL: '泳池',
  TRAINING_POOL: '训练池',
  STAND: '看台',
  ROOM: '配套用房',
}

function VenueList() {
  const [list, setList] = useState<VenueItem[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [modalVisible, setModalVisible] = useState(false)
  const [editingItem, setEditingItem] = useState<VenueItem | null>(null)
  const [form] = Form.useForm()

  const fetchData = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res = await getVenueList({ pageNum: page, pageSize })
      setList(res.records || [])
      setPagination({
        current: res.current || page,
        pageSize: res.size || pageSize,
        total: res.total || 0,
      })
    } catch (error) {
      console.error('Failed to fetch venue list:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleAdd = () => {
    setEditingItem(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: VenueItem) => {
    setEditingItem(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = (record: VenueItem) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除场地"${record.venueName}"吗？`,
      onOk: async () => {
        try {
          await deleteVenue(record.id)
          message.success('删除成功')
          fetchData(pagination.current, pagination.pageSize)
        } catch (error) {
          console.error('Failed to delete venue:', error)
        }
      },
    })
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingItem) {
        await updateVenue({ ...editingItem, ...values })
        message.success('修改成功')
      } else {
        await addVenue(values)
        message.success('添加成功')
      }
      setModalVisible(false)
      fetchData(pagination.current, pagination.pageSize)
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<VenueItem> = [
    { title: '场地名称', dataIndex: 'venueName', key: 'venueName' },
    { title: '场地编码', dataIndex: 'venueCode', key: 'venueCode' },
    {
      title: '场地类型',
      dataIndex: 'venueType',
      key: 'venueType',
      render: (type) => venueTypeMap[type] || type,
    },
    { title: '位置', dataIndex: 'location', key: 'location' },
    { title: '面积(㎡)', dataIndex: 'area', key: 'area' },
    { title: '容纳人数', dataIndex: 'capacity', key: 'capacity' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (status === 1 ? '启用' : '停用'),
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
        <div className="page-title">场地管理</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增场地
        </Button>
      </div>
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
        title={editingItem ? '编辑场地' : '新增场地'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="venueName" label="场地名称" rules={[{ required: true, message: '请输入场地名称' }]}>
            <Input placeholder="请输入场地名称" />
          </Form.Item>
          <Form.Item name="venueCode" label="场地编码" rules={[{ required: true, message: '请输入场地编码' }]}>
            <Input placeholder="请输入场地编码" />
          </Form.Item>
          <Form.Item name="venueType" label="场地类型" rules={[{ required: true, message: '请选择场地类型' }]}>
            <Select placeholder="请选择场地类型">
              <Select.Option value="POOL">泳池</Select.Option>
              <Select.Option value="TRAINING_POOL">训练池</Select.Option>
              <Select.Option value="STAND">看台</Select.Option>
              <Select.Option value="ROOM">配套用房</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="location" label="位置">
            <Input placeholder="请输入位置" />
          </Form.Item>
          <Form.Item name="area" label="面积(㎡)">
            <InputNumber style={{ width: '100%' }} placeholder="请输入面积" />
          </Form.Item>
          <Form.Item name="capacity" label="容纳人数">
            <InputNumber style={{ width: '100%' }} placeholder="请输入容纳人数" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>停用</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default VenueList
