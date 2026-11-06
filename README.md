# MedLink · 门诊住院一体化管理平台（后端）

基于 Spring Boot 3 与 MyBatis-Plus 实现的医院门诊、住院业务后端，覆盖挂号、退号、收费、退费、住院登记、住院费用记账与结算、基础字典与患者账户等模块，共 8 个控制器、25 个 REST 接口、12 张业务表。

## 功能模块

| 模块 | 接口前缀 | 主要能力 |
| --- | --- | --- |
| 挂号 | `/api/register` | 建卡挂号、按科室与医生排班挂号、挂号记录查询 |
| 退号 | `/api/back-register` | 退号申请与状态回写 |
| 收费 | `/api/charge` | 收费开单、收费明细落库、发票生成 |
| 退费 | `/api/refund` | 可退金额试算、退费提交、退费记录查询 |
| 住院登记 | `/api/inpatient-register` | 入院登记、床位与科室分配、住院信息查询 |
| 住院费用 | `/api/inpatient-fees` | 预交金缴纳、费用记账、费用台账与余额结算 |
| 基础字典 | `/api/dictionaries` | 科室、医生、收费项目目录的维护与查询 |
| 患者账户 | `/api/settings/profile` | 患者基本信息与账户资料维护 |

## 技术栈

- Java 17、Spring Boot 3.3.4
- MyBatis-Plus 3.5.11（含 `mybatis-plus-jsqlparser`）
- MySQL 8（生产配置）、H2 内存库（测试配置）
- Lombok、Bean Validation、Maven
- JUnit 5 + Spring Boot Test

## 关键实现

**金额精度统一。** 所有金额字段使用 `BigDecimal`，计算与落库前统一保留两位小数，避免浮点误差在收费、退费、住院台账之间传导。

**退费链路的强一致。** 退费提交在 `@Transactional` 事务内完成退费主单、退费明细与发票状态回写；通过 `SELECT ... FOR UPDATE` 对退费单与发票行加悲观锁，串行化同一张发票的并发退费，避免重复红冲。

**可退额度校验。** 按「开单数量 − 已退数量」实时计算可退上限，不做冗余字段；退费金额不得超过原收费金额，校验在 Service 层前置完成并抛出业务异常。

**住院费用台账。** 以预交金为基础，按「预交金 + 缴费 − 费用 − 退费」实时汇总台账余额，余额不足时拦截后续记账与退费操作。

**统一返回与异常。** 全部接口返回 `ApiResult` 统一结构；`ApiExceptionHandler` 做全局异常拦截，业务异常与系统异常分别返回可读错误码与提示。

## 快速开始

1. 建库

```sql
CREATE DATABASE hospital_system DEFAULT CHARSET utf8mb4;
```

2. 导入表结构。12 张表的实体定义见 `src/main/java/com/neuedu/HospitalBackend/entity`，对应表名由 `@TableName` 标注；建表与初始化数据脚本暂未随仓库提供，需按实体结构自行建表或从本地库导出。

3. 修改 `src/main/resources/application-mysql.yml` 中的数据库地址与账号密码（仓库内为本地演示配置）。

4. 启动

```bash
mvn spring-boot:run
```

服务默认端口 `8080`，接口统一以 `/api` 开头。

## 目录结构

```
src/main/java/com/neuedu/HospitalBackend
├── common/         统一返回结构 ApiResult
├── controller/     8 个控制器 + 全局异常处理
├── dto/            请求/响应对象
├── entity/         12 张表的实体映射
├── mapper/         MyBatis-Plus Mapper
└── service/        业务接口与实现
```

## 测试

```bash
mvn test
```

测试使用 H2 内存库（`application-test.yml`，MySQL 兼容模式），用例覆盖退费、收费、字典、住院登记与住院费用等主链路。测试模式依赖 `classpath:schema.sql` 与 `data.sql`，如需运行请先补充这两个脚本。

## 说明

- 仓库内 `application-mysql.yml` 的账号密码为本地演示用，请勿直接用于生产环境。
- 前端页面暂未包含在本仓库，接口按 REST 方式提供，可直接对接任意前端或接口调试工具。
