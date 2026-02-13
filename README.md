# AuraTab 后端

AuraTab 是一个轻量级浏览器起始页的后端服务，支持通过 WebDAV 同步书签数据。

## 功能特性

- 支持 WebDAV 书签同步（主要支持坚果云等服务）
- XBEL 格式解析与导出
- 本地设置管理
- 书签增删改查 API

## 支持的 WebDAV 服务

### 坚果云

- 基础 URL: `https://dav.jianguoyun.com/dav/`
- 需要使用账户用户名和密码进行认证
- 支持应用专用密码（推荐）
- 适配请求频率限制

## API 接口

- `GET /api/settings` - 获取应用设置
- `PUT /api/settings` - 保存应用设置
- `POST /api/webdav/test` - 测试 WebDAV 连接
- `GET /api/bookmarks/pull` - 从 WebDAV 拉取书签
- `POST /api/bookmarks/push` - 推送书签到 WebDAV

## 配置文件

应用设置保存在 `./data/settings.json` 文件中：

```json
{
  "schemaVersion": 1,
  "webdav": {
    "baseUrl": "https://dav.jianguoyun.com/dav/",
    "username": "your-email@example.com",
    "password": "your-password"
  },
  "remote": {
    "path": "/qinghome/bookmarks.xbel",
    "format": "xbel"
  },
  "sync": {
    "preferRemoteOnConflict": true
  }
}
```

## 构建与运行

使用 Maven 构建项目：

```bash
mvn clean package
java -jar target/auratab-backend-1.0.0.jar
```

## 特别说明

- 为避免 WebDAV 服务商（特别是坚果云）的请求频率限制，我们在请求之间添加了适当的延迟
- 支持坚果云等主流 WebDAV 服务提供商的特定配置
- 推荐使用应用专用密码而非账户主密码进行认证