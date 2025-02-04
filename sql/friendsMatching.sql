create table usercenter.user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)      null comment '用户名',
    userAccount  varchar(256)      null comment '账号',
    avatarUrl    varchar(1024)     null comment '用户头像',
    gender       tinyint           null comment '性别',
    userPassword varchar(512)      not null comment '密码',
    phone        varchar(128)      null comment '手机号',
    email        varchar(256)      null comment '邮箱',
    userStatus   int     default 0 not null,
    createTime   datetime          null,
    updateTime   datetime          null,
    isDelete     tinyint default 0 null,
    role         int     default 0 not null comment '用户角色 0-普通用户，1-管理员',
    tags         varchar(1024)     null comment '用户标签  json列表',
    constraint user_pk
        unique (userAccount)
);

