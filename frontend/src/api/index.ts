import request from '@/utils/request'

export interface PageParams {
  pageNum?: number
  pageSize?: number
}

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function getVenueList(params: PageParams & { venueType?: string; keyword?: string }) {
  return request.get('/resource/venue/list', { params })
}

export function getVenue(id: number) {
  return request.get(`/resource/venue/${id}`)
}

export function addVenue(data: any) {
  return request.post('/resource/venue', data)
}

export function updateVenue(data: any) {
  return request.put('/resource/venue', data)
}

export function deleteVenue(id: number) {
  return request.delete(`/resource/venue/${id}`)
}

export function getOccupations(params: { venueId: number; startDate: string; endDate: string }) {
  return request.get('/resource/occupation', { params })
}

export function checkConflict(params: {
  venueId: number
  date: string
  startTime: string
  endTime: string
}) {
  return request.get('/resource/occupation/check', { params })
}

export function getTimeSlotList(venueId: number, usageType?: string) {
  return request.get('/resource/time-slot/list', { params: { venueId, usageType } })
}

export function getTicketOrderPage(params: PageParams & {
  orderStatus?: number
  payStatus?: number
  visitorName?: string
  phone?: string
}) {
  return request.get('/ticket/order/page', { params })
}

export function createTicketOrder(data: any) {
  return request.post('/ticket/order', data)
}

export function payTicketOrder(id: number, payType: string) {
  return request.post(`/ticket/order/${id}/pay`, null, { params: { payType } })
}

export function cancelTicketOrder(id: number) {
  return request.post(`/ticket/order/${id}/cancel`)
}

export function getMemberPage(params: PageParams & {
  keyword?: string
  memberLevel?: string
  status?: number
}) {
  return request.get('/member/account/page', { params })
}

export function registerMember(data: any) {
  return request.post('/member/account/register', data)
}

export function updateMember(data: any) {
  return request.put('/member/account', data)
}

export function rechargeMember(id: number, params: {
  principalAmount?: number
  giftAmount?: number
  remark?: string
}) {
  return request.post(`/member/account/${id}/recharge`, null, { params })
}

export function getSubAccounts(memberId: number) {
  return request.get(`/member/sub-account/${memberId}`)
}

export function getCardTypeList(cardType?: string) {
  return request.get('/member/card-type/list', { params: { cardType } })
}

export function getMemberCards(memberId: number, status?: number) {
  return request.get(`/member/card/list/${memberId}`, { params: { status } })
}

export function issueCard(params: {
  memberId: number
  subAccountId?: number
  cardTypeId: number
  payType: string
  saleId?: number
}) {
  return request.post('/member/card/issue', null, { params })
}

export function extendCard(id: number, days: number, reason?: string) {
  return request.post(`/member/card/${id}/extend`, null, { params: { days, reason } })
}

export function transferCard(id: number, params: {
  targetMemberId: number
  targetSubAccountId?: number
}) {
  return request.post(`/member/card/${id}/transfer`, null, { params })
}

export function getCoursePage(params: PageParams & {
  courseType?: string
  skillLevel?: string
  status?: number
}) {
  return request.get('/training/course/page', { params })
}

export function addCourse(data: any) {
  return request.post('/training/course', data)
}

export function updateCourse(data: any) {
  return request.put('/training/course', data)
}

export function getClassPage(params: PageParams & {
  courseId?: number
  coachId?: number
  classStatus?: number
  className?: string
}) {
  return request.get('/training/class/page', { params })
}

export function getClass(id: number) {
  return request.get(`/training/class/${id}`)
}

export function createClass(data: any) {
  return request.post('/training/class', data)
}

export function enrollStudent(classId: number, studentId: number, hours: number) {
  return request.post(`/training/class/${classId}/enroll`, null, { params: { studentId, hours } })
}

export function transferStudent(params: {
  fromClassId: number
  toClassId: number
  studentId: number
  hours: number
}) {
  return request.post('/training/class/transfer', null, { params })
}

export function suspendStudent(classId: number, params: {
  studentId: number
  startDate: string
  endDate: string
}) {
  return request.post(`/training/class/${classId}/suspend`, null, { params })
}

export function resumeStudent(classId: number, studentId: number) {
  return request.post(`/training/class/${classId}/resume`, null, { params: { studentId } })
}

export function getStudentPage(params: PageParams & {
  keyword?: string
  skillLevel?: string
  status?: number
}) {
  return request.get('/training/student/page', { params })
}

export function addStudent(data: any) {
  return request.post('/training/student', data)
}

export function updateStudent(data: any) {
  return request.put('/training/student', data)
}

export function getScheduleList(params: {
  classId?: number
  startDate?: string
  endDate?: string
}) {
  return request.get('/training/schedule/list', { params })
}

export function generateSchedules(classId: number) {
  return request.post(`/training/schedule/generate/${classId}`)
}

export function cancelSchedule(id: number) {
  return request.post(`/training/schedule/${id}/cancel`)
}

export function reschedule(id: number, params: {
  newDate: string
  newStartTime: string
  newEndTime: string
}) {
  return request.post(`/training/schedule/${id}/reschedule`, null, { params })
}

export function getAttendance(scheduleId: number) {
  return request.get(`/training/attendance/${scheduleId}`)
}

export function recordAttendance(params: {
  scheduleId: number
  studentId: number
  status: number
  remark?: string
}) {
  return request.post('/training/attendance', null, { params })
}

export function batchRecordAttendance(scheduleId: number, attendanceList: any[]) {
  return request.post('/training/attendance/batch', attendanceList, { params: { scheduleId } })
}
