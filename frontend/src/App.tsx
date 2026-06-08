import { Routes, Route, Navigate } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import MainLayout from '@/layouts/MainLayout'
import Login from '@/pages/login'
import ProtectedRoute from '@/components/ProtectedRoute'
import Loading from '@/components/Loading'

const Dashboard = lazy(() => import('@/pages/dashboard'))
const VenueList = lazy(() => import('@/pages/resource/venue'))
const TimeSlotList = lazy(() => import('@/pages/resource/time-slot'))
const OccupationView = lazy(() => import('@/pages/resource/occupation'))
const TicketOrder = lazy(() => import('@/pages/ticket/order'))
const MemberList = lazy(() => import('@/pages/member/list'))
const CardTypeList = lazy(() => import('@/pages/member/card-type'))
const MemberCardList = lazy(() => import('@/pages/member/card'))
const CourseList = lazy(() => import('@/pages/training/course'))
const ClassList = lazy(() => import('@/pages/training/class'))
const StudentList = lazy(() => import('@/pages/training/student'))
const ScheduleList = lazy(() => import('@/pages/training/schedule'))
const AttendancePage = lazy(() => import('@/pages/training/attendance'))

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={
        <ProtectedRoute>
          <MainLayout />
        </ProtectedRoute>
      }>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={
          <Suspense fallback={<Loading />}><Dashboard /></Suspense>
        } />
        <Route path="resource/venue" element={
          <Suspense fallback={<Loading />}><VenueList /></Suspense>
        } />
        <Route path="resource/time-slot" element={
          <Suspense fallback={<Loading />}><TimeSlotList /></Suspense>
        } />
        <Route path="resource/occupation" element={
          <Suspense fallback={<Loading />}><OccupationView /></Suspense>
        } />
        <Route path="ticket/order" element={
          <Suspense fallback={<Loading />}><TicketOrder /></Suspense>
        } />
        <Route path="member/list" element={
          <Suspense fallback={<Loading />}><MemberList /></Suspense>
        } />
        <Route path="member/card-type" element={
          <Suspense fallback={<Loading />}><CardTypeList /></Suspense>
        } />
        <Route path="member/card" element={
          <Suspense fallback={<Loading />}><MemberCardList /></Suspense>
        } />
        <Route path="training/course" element={
          <Suspense fallback={<Loading />}><CourseList /></Suspense>
        } />
        <Route path="training/class" element={
          <Suspense fallback={<Loading />}><ClassList /></Suspense>
        } />
        <Route path="training/student" element={
          <Suspense fallback={<Loading />}><StudentList /></Suspense>
        } />
        <Route path="training/schedule" element={
          <Suspense fallback={<Loading />}><ScheduleList /></Suspense>
        } />
        <Route path="training/attendance" element={
          <Suspense fallback={<Loading />}><AttendancePage /></Suspense>
        } />
      </Route>
    </Routes>
  )
}

export default App
