-- =====================================================
-- 游泳馆管理系统数据库初始化脚本
-- =====================================================

CREATE DATABASE IF NOT EXISTS swim_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE swim_db;

-- =====================================================
-- 系统管理表
-- =====================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    avatar VARCHAR(256) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    position VARCHAR(64) COMMENT '职位',
    department_id BIGINT COMMENT '部门ID',
    employee_no VARCHAR(32) COMMENT '工号',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(256) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(64) COMMENT '菜单编码',
    path VARCHAR(128) COMMENT '路由路径',
    component VARCHAR(256) COMMENT '组件路径',
    icon VARCHAR(64) COMMENT '图标',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort INT DEFAULT 0 COMMENT '排序',
    menu_type VARCHAR(16) COMMENT '菜单类型 M-目录 C-菜单 F-按钮',
    permission VARCHAR(128) COMMENT '权限标识',
    visible TINYINT DEFAULT 1 COMMENT '是否显示 0-隐藏 1-显示',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =====================================================
-- 资源管理表
-- =====================================================

CREATE TABLE IF NOT EXISTS venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(64) NOT NULL COMMENT '场地名称',
    venue_type VARCHAR(32) NOT NULL COMMENT '场地类型 POOL-泳池 TRAINING_POOL-训练池 STAND-看台 ROOM-配套用房',
    venue_code VARCHAR(32) NOT NULL COMMENT '场地编码',
    location VARCHAR(128) COMMENT '位置',
    area DECIMAL(10,2) COMMENT '面积',
    capacity INT COMMENT '容纳人数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-启用',
    description TEXT COMMENT '描述',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_venue_code (venue_code),
    KEY idx_venue_type (venue_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

CREATE TABLE IF NOT EXISTS time_slot_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(64) NOT NULL COMMENT '模板名称',
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    usage_type VARCHAR(32) NOT NULL COMMENT '用途类型 PUBLIC-大众游泳 TRAINING-培训 RENTAL-租赁 EVENT-赛事',
    price DECIMAL(10,2) COMMENT '价格',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    KEY idx_venue_id (venue_id),
    KEY idx_usage_type (usage_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时段模板表';

CREATE TABLE IF NOT EXISTS venue_occupation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    occupation_date DATE NOT NULL COMMENT '占用日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    usage_type VARCHAR(32) NOT NULL COMMENT '用途类型 PUBLIC-大众游泳 TRAINING-培训 RENTAL-租赁 EVENT-赛事',
    business_type VARCHAR(32) NOT NULL COMMENT '业务类型 TICKET-散客票 MEMBER-会员 TRAINING_CLASS-培训班 RENTAL_ORDER-租赁订单 EVENT_ORDER-赛事订单',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    business_no VARCHAR(64) COMMENT '业务单号',
    lock_status TINYINT DEFAULT 0 COMMENT '锁定状态 0-正常 1-整片锁定',
    status TINYINT DEFAULT 1 COMMENT '状态 0-已取消 1-有效',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    KEY idx_venue_date (venue_id, occupation_date),
    KEY idx_business (business_type, business_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地占用记录表';

CREATE TABLE IF NOT EXISTS ticket_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    ticket_type VARCHAR(32) NOT NULL COMMENT '票种',
    visit_date DATE NOT NULL COMMENT '入场日期',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    pay_type VARCHAR(32) COMMENT '支付方式',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-待支付 1-已支付 2-已退款',
    pay_time DATETIME COMMENT '支付时间',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态 0-已取消 1-待使用 2-已使用 3-已过期',
    visitor_name VARCHAR(64) COMMENT '游客姓名',
    visitor_phone VARCHAR(20) COMMENT '游客手机号',
    sale_id BIGINT COMMENT '销售员ID',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_venue_date (venue_id, visit_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='散客票订单表';

CREATE TABLE IF NOT EXISTS rental_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    customer_name VARCHAR(64) NOT NULL COMMENT '客户名称',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    rental_type VARCHAR(32) COMMENT '租赁类型',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    total_hours DECIMAL(10,2) COMMENT '总时长',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    deposit_amount DECIMAL(10,2) DEFAULT 0 COMMENT '押金',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-待支付 1-已支付 2-已退款',
    pay_time DATETIME COMMENT '支付时间',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态 0-已取消 1-待确认 2-已确认 3-进行中 4-已完成',
    sales_staff_id BIGINT COMMENT '招商人员ID',
    operation_staff_id BIGINT COMMENT '运维人员ID',
    purpose TEXT COMMENT '租赁用途',
    remark VARCHAR(256) COMMENT '备注',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态 0-待审批 1-审批通过 2-审批拒绝',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_venue_date (venue_id, start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地租赁订单表';

CREATE TABLE IF NOT EXISTS event_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    event_name VARCHAR(128) NOT NULL COMMENT '赛事名称',
    event_type VARCHAR(32) COMMENT '赛事类型',
    organizer VARCHAR(128) COMMENT '主办方',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    venue_id BIGINT NOT NULL COMMENT '主场地ID',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-待支付 1-已支付 2-已退款',
    pay_time DATETIME COMMENT '支付时间',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态 0-已取消 1-待确认 2-已确认 3-进行中 4-已完成',
    participant_count INT COMMENT '参与人数',
    equipment_fee DECIMAL(10,2) DEFAULT 0 COMMENT '设备使用费',
    labor_subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '临时劳务补贴',
    equipment_loss DECIMAL(10,2) DEFAULT 0 COMMENT '设备损耗扣款',
    remark VARCHAR(256) COMMENT '备注',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态 0-待审批 1-审批通过 2-审批拒绝',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_event_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事订单表';

-- =====================================================
-- 会员管理表
-- =====================================================

CREATE TABLE IF NOT EXISTS member_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_no VARCHAR(32) NOT NULL COMMENT '会员编号',
    member_name VARCHAR(64) NOT NULL COMMENT '会员姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    id_card VARCHAR(32) COMMENT '身份证号',
    avatar VARCHAR(256) COMMENT '头像',
    gender TINYINT COMMENT '性别 0-女 1-男',
    birthday DATE COMMENT '生日',
    address VARCHAR(256) COMMENT '地址',
    principal_balance DECIMAL(10,2) DEFAULT 0 COMMENT '储值本金余额',
    gift_balance DECIMAL(10,2) DEFAULT 0 COMMENT '赠送余额',
    total_recharge DECIMAL(10,2) DEFAULT 0 COMMENT '累计充值',
    total_consumption DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费',
    member_level VARCHAR(32) DEFAULT 'NORMAL' COMMENT '会员等级',
    status TINYINT DEFAULT 1 COMMENT '状态 0-冻结 1-正常',
    source_type VARCHAR(32) COMMENT '来源渠道',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_member_no (member_no),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员主账户表';

CREATE TABLE IF NOT EXISTS member_sub_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT '主账户ID',
    sub_name VARCHAR(64) NOT NULL COMMENT '子账户姓名',
    sub_phone VARCHAR(20) COMMENT '子账户手机号',
    id_card VARCHAR(32) COMMENT '身份证号',
    relation VARCHAR(32) COMMENT '与主账户关系',
    birthday DATE COMMENT '生日',
    gender TINYINT COMMENT '性别 0-女 1-男',
    avatar VARCHAR(256) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0-冻结 1-正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    KEY idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员子账户表';

CREATE TABLE IF NOT EXISTS card_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_name VARCHAR(64) NOT NULL COMMENT '卡种名称',
    card_type VARCHAR(32) NOT NULL COMMENT '卡类型 TIME-限时卡 COUNT-次卡',
    card_code VARCHAR(32) NOT NULL COMMENT '卡种编码',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    valid_days INT COMMENT '有效天数',
    total_count INT COMMENT '总次数',
    gift_principal DECIMAL(10,2) DEFAULT 0 COMMENT '赠送本金',
    gift_amount DECIMAL(10,2) DEFAULT 0 COMMENT '赠送金额',
    gift_hours DECIMAL(10,2) DEFAULT 0 COMMENT '赠送课时',
    venue_scope VARCHAR(256) COMMENT '适用场地',
    time_scope VARCHAR(256) COMMENT '适用时段',
    description TEXT COMMENT '卡种说明',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-启用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_card_code (card_code),
    KEY idx_card_type (card_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡种表';

CREATE TABLE IF NOT EXISTS member_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_no VARCHAR(32) NOT NULL COMMENT '卡号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    sub_account_id BIGINT COMMENT '子账户ID',
    card_type_id BIGINT NOT NULL COMMENT '卡种ID',
    card_name VARCHAR(64) COMMENT '卡种名称',
    card_type VARCHAR(32) COMMENT '卡类型',
    buy_price DECIMAL(10,2) NOT NULL COMMENT '购买价格',
    pay_type VARCHAR(32) COMMENT '支付方式',
    valid_start_date DATE NOT NULL COMMENT '有效期开始',
    valid_end_date DATE NOT NULL COMMENT '有效期结束',
    total_count INT COMMENT '总次数',
    remaining_count INT COMMENT '剩余次数',
    total_hours DECIMAL(10,2) COMMENT '总课时',
    remaining_hours DECIMAL(10,2) COMMENT '剩余课时',
    status TINYINT DEFAULT 1 COMMENT '状态 0-冻结 1-正常 2-已过期 3-已用完',
    activation_time DATETIME COMMENT '激活时间',
    sale_id BIGINT COMMENT '销售员ID',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_card_no (card_no),
    KEY idx_member_id (member_id),
    KEY idx_card_type_id (card_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡片表';

CREATE TABLE IF NOT EXISTS member_account_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_no VARCHAR(64) NOT NULL COMMENT '流水号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    sub_account_id BIGINT COMMENT '子账户ID',
    card_id BIGINT COMMENT '卡片ID',
    account_type VARCHAR(32) NOT NULL COMMENT '账户类型 PRINCIPAL-本金 GIFT-赠金 HOURS-课时 COUNT-次数',
    change_type VARCHAR(32) NOT NULL COMMENT '变动类型',
    change_amount DECIMAL(10,2) NOT NULL COMMENT '变动金额/数量',
    before_balance DECIMAL(10,2) NOT NULL COMMENT '变动前余额',
    after_balance DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    business_type VARCHAR(32) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    business_no VARCHAR(64) COMMENT '业务单号',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    UNIQUE KEY uk_log_no (log_no),
    KEY idx_member_id (member_id),
    KEY idx_account_type (account_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员账户流水表';

CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    approval_no VARCHAR(64) NOT NULL COMMENT '审批单号',
    business_type VARCHAR(32) NOT NULL COMMENT '业务类型',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    business_no VARCHAR(64) COMMENT '业务单号',
    applicant_id BIGINT COMMENT '申请人ID',
    applicant_name VARCHAR(64) COMMENT '申请人姓名',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(64) COMMENT '审批人姓名',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态 0-待审批 1-审批通过 2-审批拒绝',
    approval_opinion VARCHAR(512) COMMENT '审批意见',
    approval_time DATETIME COMMENT '审批时间',
    apply_reason VARCHAR(512) COMMENT '申请原因',
    extra_info TEXT COMMENT '扩展信息',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_approval_no (approval_no),
    KEY idx_business (business_type, business_id),
    KEY idx_approval_status (approval_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- =====================================================
-- 培训管理表
-- =====================================================

CREATE TABLE IF NOT EXISTS coach (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '系统用户ID',
    coach_no VARCHAR(32) NOT NULL COMMENT '教练编号',
    coach_name VARCHAR(64) NOT NULL COMMENT '教练姓名',
    phone VARCHAR(20) COMMENT '手机号',
    gender TINYINT COMMENT '性别 0-女 1-男',
    avatar VARCHAR(256) COMMENT '头像',
    coach_level VARCHAR(32) DEFAULT 'JUNIOR' COMMENT '教练等级',
    specialty VARCHAR(256) COMMENT '特长项目',
    certificate VARCHAR(256) COMMENT '资质证书',
    teaching_age INT COMMENT '教龄',
    status TINYINT DEFAULT 1 COMMENT '状态 0-离职 1-在职',
    hourly_rate DECIMAL(10,2) COMMENT '课时费标准',
    description TEXT COMMENT '简介',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_coach_no (coach_no),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练表';

CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(64) NOT NULL COMMENT '课程名称',
    course_type VARCHAR(32) NOT NULL COMMENT '课程类型 REGULAR-常规班 PRIVATE-私教班 INTENSIVE-集训班',
    course_code VARCHAR(32) NOT NULL COMMENT '课程编码',
    age_range VARCHAR(32) COMMENT '适用年龄',
    skill_level VARCHAR(32) COMMENT '技能水平',
    total_hours DECIMAL(10,2) NOT NULL COMMENT '总课时',
    student_count INT DEFAULT 0 COMMENT '学员人数',
    max_student INT COMMENT '最大人数',
    venue_id BIGINT NOT NULL COMMENT '上课场地ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    course_introduction TEXT COMMENT '课程介绍',
    syllabus TEXT COMMENT '课程大纲',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-启用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT='更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_course_code (course_code),
    KEY idx_course_type (course_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

CREATE TABLE IF NOT EXISTS class_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(64) NOT NULL COMMENT '班级名称',
    class_no VARCHAR(32) NOT NULL COMMENT '班级编号',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(64) COMMENT '课程名称',
    course_type VARCHAR(32) COMMENT '课程类型',
    coach_id BIGINT NOT NULL COMMENT '主教练ID',
    coach_name VARCHAR(64) COMMENT '主教练姓名',
    assistant_coach_id BIGINT COMMENT '助理教练ID',
    venue_id BIGINT NOT NULL COMMENT '上课场地ID',
    class_date_start DATE NOT NULL COMMENT '开课日期',
    class_date_end DATE NOT NULL COMMENT '结课日期',
    total_hours DECIMAL(10,2) NOT NULL COMMENT '总课时',
    completed_hours DECIMAL(10,2) DEFAULT 0 COMMENT '已上课时',
    remaining_hours DECIMAL(10,2) COMMENT '剩余课时',
    student_count INT DEFAULT 0 COMMENT '学员人数',
    max_student INT COMMENT '最大人数',
    class_status TINYINT DEFAULT 1 COMMENT '班级状态 0-已取消 1-待开课 2-进行中 3-已结课',
    week_day VARCHAR(32) COMMENT '上课星期',
    class_start_time TIME COMMENT '上课开始时间',
    class_end_time TIME COMMENT '上课结束时间',
    price DECIMAL(10,2) NOT NULL COMMENT '班级价格',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_class_no (class_no),
    KEY idx_course_id (course_id),
    KEY idx_coach_id (coach_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(32) NOT NULL COMMENT '学员编号',
    student_name VARCHAR(64) NOT NULL COMMENT '学员姓名',
    gender TINYINT COMMENT '性别 0-女 1-男',
    birthday DATE COMMENT '生日',
    age INT COMMENT '年龄',
    member_id BIGINT COMMENT '关联会员ID',
    sub_account_id BIGINT COMMENT '关联子账户ID',
    guardian_name VARCHAR(64) COMMENT '监护人姓名',
    guardian_phone VARCHAR(20) COMMENT '监护人电话',
    id_card VARCHAR(32) COMMENT '身份证号',
    avatar VARCHAR(256) COMMENT '头像',
    skill_level VARCHAR(32) DEFAULT 'BEGINNER' COMMENT '技能水平',
    health_condition VARCHAR(256) COMMENT '健康状况',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停学 1-在学 2-毕业',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_student_no (student_no),
    KEY idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

CREATE TABLE IF NOT EXISTS class_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL COMMENT '班级ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    student_name VARCHAR(64) COMMENT '学员姓名',
    enroll_time DATETIME COMMENT '报名时间',
    enrollment_type VARCHAR(32) DEFAULT 'NORMAL' COMMENT '报名类型',
    enrolled_hours DECIMAL(10,2) NOT NULL COMMENT '报名课时',
    consumed_hours DECIMAL(10,2) DEFAULT 0 COMMENT '已消耗课时',
    remaining_hours DECIMAL(10,2) COMMENT '剩余课时',
    student_status TINYINT DEFAULT 1 COMMENT '学员状态 0-已退班 1-在班 2-休学中',
    suspension_start DATE COMMENT '休学开始日期',
    suspension_end DATE COMMENT '休学结束日期',
    source_class_id BIGINT COMMENT '转班前班级ID',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_class_student (class_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学员关联表';

CREATE TABLE IF NOT EXISTS class_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_no VARCHAR(64) NOT NULL COMMENT '排课编号',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    coach_id BIGINT NOT NULL COMMENT '教练ID',
    venue_id BIGINT NOT NULL COMMENT '场地ID',
    class_date DATE NOT NULL COMMENT '上课日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    class_hours DECIMAL(10,2) NOT NULL COMMENT '课时数',
    schedule_status TINYINT DEFAULT 1 COMMENT '排课状态 0-已取消 1-待上课 2-已完成 3-已补课',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补课 0-否 1-是',
    original_schedule_id BIGINT COMMENT '原排课ID',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    attendance_count INT DEFAULT 0 COMMENT '出勤人数',
    absent_count INT DEFAULT 0 COMMENT '缺勤人数',
    leave_count INT DEFAULT 0 COMMENT '请假人数',
    teaching_content TEXT COMMENT '教学内容',
    coach_remark VARCHAR(512) COMMENT '教练备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_schedule_no (schedule_no),
    KEY idx_class_date (class_id, class_date),
    KEY idx_coach_date (coach_id, class_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课记录表';

CREATE TABLE IF NOT EXISTS student_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL COMMENT '排课ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    student_name VARCHAR(64) COMMENT '学员姓名',
    attendance_status TINYINT NOT NULL COMMENT '考勤状态 1-出勤 2-缺勤 3-请假 4-迟到 5-早退',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    hours_consumed DECIMAL(10,2) DEFAULT 0 COMMENT '消耗课时',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补课 0-否 1-是',
    makeup_schedule_id BIGINT COMMENT '补课排课ID',
    need_makeup TINYINT DEFAULT 0 COMMENT '是否需要补课 0-否 1-是',
    makeup_status TINYINT DEFAULT 0 COMMENT '补课状态 0-无需补课 1-待补课 2-已补课',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_schedule_student (schedule_id, student_id),
    KEY idx_student_id (student_id),
    KEY idx_need_makeup (need_makeup)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员考勤表';

CREATE TABLE IF NOT EXISTS hours_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_no VARCHAR(64) NOT NULL COMMENT '流水号',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    class_id BIGINT COMMENT '班级ID',
    schedule_id BIGINT COMMENT '排课ID',
    card_id BIGINT COMMENT '会员卡ID',
    hours_type VARCHAR(32) NOT NULL COMMENT '课时类型',
    change_type VARCHAR(32) NOT NULL COMMENT '变动类型',
    change_hours DECIMAL(10,2) NOT NULL COMMENT '变动课时数',
    before_hours DECIMAL(10,2) NOT NULL COMMENT '变动前课时',
    after_hours DECIMAL(10,2) NOT NULL COMMENT '变动后课时',
    business_type VARCHAR(32) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    business_no VARCHAR(64) COMMENT '业务单号',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    UNIQUE KEY uk_log_no (log_no),
    KEY idx_student_id (student_id),
    KEY idx_class_id (class_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课时流水表';

CREATE TABLE IF NOT EXISTS makeup_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_no VARCHAR(64) NOT NULL COMMENT '补课计划编号',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    student_name VARCHAR(64) COMMENT '学员姓名',
    original_schedule_id BIGINT NOT NULL COMMENT '原排课ID',
    original_class_id BIGINT COMMENT '原班级ID',
    target_class_id BIGINT COMMENT '目标班级ID',
    target_schedule_id BIGINT COMMENT '目标排课ID',
    makeup_status TINYINT DEFAULT 0 COMMENT '补课状态 0-待安排 1-已安排 2-已完成 3-已取消',
    deadline DATE COMMENT '补课截止日期',
    arrangement_time DATETIME COMMENT '安排时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(256) COMMENT '取消原因',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_plan_no (plan_no),
    KEY idx_student_id (student_id),
    KEY idx_makeup_status (makeup_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补课计划表';

-- =====================================================
-- 财务管理表
-- =====================================================

CREATE TABLE IF NOT EXISTS order_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    order_type VARCHAR(32) NOT NULL COMMENT '订单类型',
    order_source VARCHAR(32) DEFAULT 'COUNTER' COMMENT '订单来源',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_type VARCHAR(32) COMMENT '支付方式',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-待支付 1-已支付 2-部分退款 3-全额退款',
    pay_time DATETIME COMMENT '支付时间',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态 0-已取消 1-待付款 2-已付款 3-已完成',
    member_id BIGINT COMMENT '会员ID',
    sub_account_id BIGINT COMMENT '子账户ID',
    sale_id BIGINT COMMENT '销售员ID',
    sale_name VARCHAR(64) COMMENT '销售员姓名',
    remark VARCHAR(256) COMMENT '备注',
    settle_status TINYINT DEFAULT 0 COMMENT '结算状态 0-未结算 1-已结算',
    settle_time DATETIME COMMENT '结算时间',
    daily_close_date DATE COMMENT '日结日期',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_type (order_type),
    KEY idx_pay_status (pay_status),
    KEY idx_settle_status (settle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS payment_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pay_no VARCHAR(64) NOT NULL COMMENT '支付流水号',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    pay_type VARCHAR(32) NOT NULL COMMENT '支付方式',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_status TINYINT DEFAULT 1 COMMENT '支付状态 0-失败 1-成功 2-已退款',
    pay_time DATETIME COMMENT '支付时间',
    third_pay_no VARCHAR(128) COMMENT '第三方支付号',
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    refund_time DATETIME COMMENT '退款时间',
    refund_reason VARCHAR(256) COMMENT '退款原因',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    UNIQUE KEY uk_pay_no (pay_no),
    KEY idx_order_no (order_no),
    KEY idx_pay_type (pay_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

CREATE TABLE IF NOT EXISTS daily_close (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    close_date DATE NOT NULL COMMENT '日结日期',
    ticket_count INT DEFAULT 0 COMMENT '散客票订单数',
    ticket_amount DECIMAL(12,2) DEFAULT 0 COMMENT '散客票金额',
    member_card_count INT DEFAULT 0 COMMENT '办卡订单数',
    member_card_amount DECIMAL(12,2) DEFAULT 0 COMMENT '办卡金额',
    training_count INT DEFAULT 0 COMMENT '培训订单数',
    training_amount DECIMAL(12,2) DEFAULT 0 COMMENT '培训金额',
    rental_count INT DEFAULT 0 COMMENT '租赁订单数',
    rental_amount DECIMAL(12,2) DEFAULT 0 COMMENT '租赁金额',
    event_count INT DEFAULT 0 COMMENT '赛事订单数',
    event_amount DECIMAL(12,2) DEFAULT 0 COMMENT '赛事金额',
    total_count INT DEFAULT 0 COMMENT '总订单数',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    cash_amount DECIMAL(12,2) DEFAULT 0 COMMENT '现金金额',
    wechat_amount DECIMAL(12,2) DEFAULT 0 COMMENT '微信金额',
    alipay_amount DECIMAL(12,2) DEFAULT 0 COMMENT '支付宝金额',
    card_amount DECIMAL(12,2) DEFAULT 0 COMMENT '刷卡金额',
    member_amount DECIMAL(12,2) DEFAULT 0 COMMENT '会员余额金额',
    refund_count INT DEFAULT 0 COMMENT '退款笔数',
    refund_amount DECIMAL(12,2) DEFAULT 0 COMMENT '退款金额',
    close_status TINYINT DEFAULT 1 COMMENT '日结状态 0-待日结 1-已日结',
    close_time DATETIME COMMENT '日结时间',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_close_date (close_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日结表';

CREATE TABLE IF NOT EXISTS cost_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cost_date DATE NOT NULL COMMENT '成本日期',
    cost_type VARCHAR(32) NOT NULL COMMENT '成本类型',
    cost_category VARCHAR(64) COMMENT '成本明细分类',
    amount DECIMAL(12,2) NOT NULL COMMENT '金额',
    unit VARCHAR(32) COMMENT '计量单位',
    quantity DECIMAL(10,2) COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    related_department VARCHAR(64) COMMENT '关联部门',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    KEY idx_cost_date (cost_date),
    KEY idx_cost_type (cost_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本核算表';

-- =====================================================
-- 人事薪酬表
-- =====================================================

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_no VARCHAR(32) NOT NULL COMMENT '员工编号',
    user_id BIGINT COMMENT '系统用户ID',
    employee_name VARCHAR(64) NOT NULL COMMENT '员工姓名',
    gender TINYINT COMMENT '性别 0-女 1-男',
    phone VARCHAR(20) COMMENT '手机号',
    id_card VARCHAR(32) COMMENT '身份证号',
    department_id BIGINT COMMENT '部门ID',
    position VARCHAR(64) COMMENT '职位',
    position_type VARCHAR(32) NOT NULL COMMENT '岗位类型',
    entry_date DATE COMMENT '入职日期',
    leave_date DATE COMMENT '离职日期',
    status TINYINT DEFAULT 1 COMMENT '状态 0-离职 1-在职',
    base_salary DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    performance_coefficient DECIMAL(5,2) DEFAULT 1.00 COMMENT '绩效系数',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_employee_no (employee_no),
    KEY idx_user_id (user_id),
    KEY idx_position_type (position_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE IF NOT EXISTS commission_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(64) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(32) NOT NULL COMMENT '规则编码',
    rule_type VARCHAR(32) NOT NULL COMMENT '规则类型',
    calculation_type VARCHAR(32) NOT NULL COMMENT '计算类型 RATIO-比例 FIXED-固定金额 TIERED-阶梯',
    rule_value DECIMAL(10,4) COMMENT '规则值',
    tier_config TEXT COMMENT '阶梯配置(JSON)',
    effective_date_start DATE COMMENT '生效开始日期',
    effective_date_end DATE COMMENT '生效结束日期',
    season VARCHAR(32) COMMENT '适用季节',
    activity_id BIGINT COMMENT '关联活动ID',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-启用',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_rule_code (rule_code),
    KEY idx_rule_type (rule_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金规则表';

CREATE TABLE IF NOT EXISTS salary_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    salary_no VARCHAR(64) NOT NULL COMMENT '薪酬单号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(64) COMMENT '员工姓名',
    position_type VARCHAR(32) COMMENT '岗位类型',
    salary_month VARCHAR(7) NOT NULL COMMENT '薪酬月份',
    base_salary DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    performance_salary DECIMAL(10,2) DEFAULT 0 COMMENT '绩效工资',
    commission_amount DECIMAL(10,2) DEFAULT 0 COMMENT '提成金额',
    bonus_amount DECIMAL(10,2) DEFAULT 0 COMMENT '奖金',
    subsidy_amount DECIMAL(10,2) DEFAULT 0 COMMENT '补贴',
    deduction_amount DECIMAL(10,2) DEFAULT 0 COMMENT '扣款',
    total_salary DECIMAL(10,2) NOT NULL COMMENT '应发工资',
    actual_salary DECIMAL(10,2) NOT NULL COMMENT '实发工资',
    settle_status TINYINT DEFAULT 0 COMMENT '结算状态 0-待发放 1-已发放',
    settle_time DATETIME COMMENT '发放时间',
    remark VARCHAR(256) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_salary_no (salary_no),
    KEY idx_employee_id (employee_id),
    KEY idx_salary_month (salary_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬记录表';
