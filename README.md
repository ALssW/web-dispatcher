# WebDispatcher
>基于本人在学习 SpringMVC 时对其原理的一些猜想与参考的实现框架
 
在学习 SpringMVC 时我就曾有过这种想法，为什么使用 SpringMVC 后可以不再依赖 Servlet 就能够完成一个接口对请求的接收与处理？

在之后我了解到 SpringMVC 底层依然是基于 Servlet 实现的，并产生了“我也可以试一试”的想法，于是这个框架诞生了。

WebDispatcher 是基于我早期学习 SpringMVC 时的一些原理猜想与其底层的了解而编写的一个类 SpringMVC 框架，属于我的一个练习小框架。

此为我使用该框架编写的一个完整项目：
[chess-game-demo](https://github.com/ALssW/chess-game-demo)

# 已实现
目前该框架实现了以下功能
* 基于注解的请求转发
* 参数转换
* MySql 数据库连接
* 类 MybatisPlus 的 SQL 编写功能
* 分页插件
* 日志输出（非文件）

# 待实现
* 请求线程池化
* 拦截器
* 更多的 SQL Wrapper


