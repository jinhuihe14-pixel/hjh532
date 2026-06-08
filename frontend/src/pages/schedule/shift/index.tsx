import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, TimePicker, Select, message } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { getShiftList, addShift, updateShift, deleteShift } from '@/api'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'

interface ShiftItem {
  id: number
  shiftName: string
  startTime: string
  endTime: string
  shiftType: string
  status: number
  description: string
}

const shiftTypeMap: Record<string, string> = {
  MORNING: '早班',
  MIDDLE: '中班',
  EVENING: '晚班',
  NIGHT: '夜班',
  FULL_DAY: '全天',
}

function ShiftTemplate() {
  const [list, setList] = useState<ShiftItem[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingItem, setEditingItem] = useState<ShiftItem | null>(null)
  const [form] = Form.useForm()

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getShiftList()
      setList(res || [])
    } catch (error) {
      console.error('Failed to fetch shift list:', error)
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

  const handleEdit = (record: ShiftItem) => {
    setEditingItem(record)
    form.setFieldsValue({
      ...record,
      startTime: record.startTime ? dayjs(record.startTime, 'HH:mm:ss') : null,
      endTime: record.endTime ? dayjs(record.endTime, 'HH:mm:ss') : null,
    })
    setModalVisible(true)
  }

  const handleDelete = (record: ShiftItem) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除班次"${record.shiftName}"吗？`,
      onOk: async () => {
        try {
          await deleteShift(record.id)
          message.success('删除成功')
          fetchData()
        } catch (error) {
          console.error('Failed to delete shift:', error)
        }
      },
    })
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const data = {
        ...values,
        startTime: values.startTime ? values.startTime.format('HH:mm:ss') : null,
        endTime: values.endTime ? values.endTime.format('HH:mm:ss') : null,
      }
      if (editingItem) {
        await updateShift({ ...editingItem, ...data })
        message.success('修改成功')
      } else {
        await addShift(data)
        message.success('添加成功')
      }
      setModalVisible(false)
      fetchData()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const columns: ColumnsType<ShiftItem> = [
    { title: '班次名称', dataIndex: 'shiftName', key: 'shiftName' },
    {
      title: '班次类型',
      dataIndex: 'shiftType',
      key: 'shiftType',
      render: (type) => shiftTypeMap[type] || type,
    },
    { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
    { title: '结束时间', dataIndex: 'endTime', key: 'endTime' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (status === 1 ? '启用' : '停用'),
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
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
        <div className="page-title">班次模板</div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增班次
        </Button>
      </div>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={false}
      />
      <Modal
        title={editingItem ? '编辑班次' : '新增班次'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={500}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="shiftName" label="班次名称" rules={[{ required: true, message: '请输入班次名称' }]}>
            <Input placeholder="请输入班次名称，如早班、晚班" />
          </Form.Item>
          <Form.Item name="shiftType" label="班次类型" rules={[{ required: true, message: '请选择班次类型' }]}>
            <Select placeholder="请选择班次类型">
              <Select.Option value="MORNING">早班</Select.Option>
              <Select.Option value="MIDDLE">中班</Select.Option>
              <Select.Option value="EVENING">晚班</Select.Option>
              <Select.Option value="NIGHT">夜班</Select.Option>
              <Select.Option value="FULL_DAY">全天</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="startTime" label="开始时间" rules={[{ required: true, message: '请选择开始时间' }]}>
            <TimePicker style={{ width: '100%' }} format="HH:mm" placeholder="请选择开始时间" />
          </Form.Item>
          <Form.Item name="endTime" label="结束时间" rules={[{ required: true, message: '请选择结束时间' }]}>
            <TimePicker style={{ width: '100%' }} format="HH:mm" placeholder="请选择结束时间" />
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

export default ShiftTemplate
