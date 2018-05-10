# fybatis 概述

fybatis 是一个从零写起的类 MyBatis ORM 框架。用于学习 MyBatis 中优秀的编程思想。

**注意绝对不能商用。不承担任何风险责任。**

# v1.0.0 版本的思考

**通过一个 Mapper 接口，就可以操作数据库从而获取一个实体类。** 我们最简陋的版本就是要实现这样的一个简单的功能。

# v1.0.0 的实现方式

不是所有框架一开始都是很优雅的。想怎么来就怎么来。

不用考虑单一职责，不用考虑开闭原则。

就给你一副键盘，一个 IDE，就是撸代码，就是干！

当然我们还是要有一个会话的概念，每一次操作数据库就是一次会话，也就是一个 SqlSession。

可以理解为给数据库管理员打电话：喂，你好，给我来几条数据，条件为 xxx。管理员收到了之后，说：好的，就是这些东西给你，拜拜。我说：谢谢，拜拜（挂电话）。

# v1.0.0 的缺点

直接撸代码一般都是垃圾代码的一种生产过程，这里也不例外。

问题点整理：

1. 每有一条新的sql语句执行，就需要自己实现新的 SQL 语句操作 JDBC 的方法。每次都要写，很麻烦。

先考虑这么多吧，新加一个 feature，准备开始做 v1.1.0

# v1.1.0 实现过程

其实很简单，在 v1.0.0 的基础上，将 JDBC 部分的代码封装成几个对象。注意这里是多个对象。

如何拆分？其实也很简单，我们都知道 JDBC 操作是有几个过程的。这里可以参考**单一职责原则**，对每一个过程抽象一个对象。

最后我们可以分为四个过程。分别是：
1. 对参数的处理。
2. 对 SQL 语句的参数拼接。
3. 操作数据库。
4. 对返回值的处理。

## 具体细节

其次将数据库参考抽象出一个接口 Executor，再写一个简单处理的实现类 SimpleExecutor。

在 SimpleExecutor 的具体方法中，通过之前拆分的多个对象执行相应的操作。

# v1.1.0 的优缺点

**优点**：
1. 实现了动态对返回的实体类实例化和设置属性。
2. 抽象出了 Executor 和不同过程的 Handler，在一定程度做到了“单一职责”。也可以理解为“委托”，Executor 委托 Handler 做事。

**缺点**：
1. 没有解决硬编码的 Mapper 接口实现类。
2. 没有对配置项做统一的处理，目前逻辑都在 Executor 中。其实这部分并不属于 Executor 的功能。应该新增类来处理。

接下来引出 v1.2.0 来解决上述缺点。

# v1.2.0 的设计

针对以上的两个缺点，进一步改造代码。（其实，可以发现项目是越来越复杂，因为做的事情比较杂，需要做到单一职责和开闭原则，必然会生出好多类）

1. 针对需要硬编码的 Mapper 的接口实现类，可以考虑通过 JDK 的动态代理来实现（不清楚动态代理的同学，之后会单独写一个项目来手动实现 JDK 的动态代理）。
2. 针对对配置项的问题，可以新增一个 Configuration 类。目前流行两种形式的配置，一种是 XML，一种是 annotation。这里实现 annotation，不考虑 XML（因为当年被 Spring 的 applicationContext.xml 恶心到了，使用 Spring Boot 好太多了）

其实这一步挺难的。这里梳理一下思路。
1. 需要创建 Configuration 配置管理类和 MapperProxy 接口代理类
2. 需要创建一个注解 @Select
3. 因为 SqlSession 是唯一一个直接提供给用户操作的类，里面需要两个属性：configuration 和 executor。也需要两个方法，getMapper 和 selectOne。

解释一下为什么 SqlSession 中需要有 selectOne 方法。
1. 只有 SqlSession 拥有 Executor 对象操作数据库。
2. Executor 如果放在 MapperProxy 中，会让 MapperProxy 不纯粹，因为它只是一个代理类而已。
3. SqlSession 的意义本身就是数据库会话。本身可以操作数据库，所以 Executor 放在 SqlSession 中合情合理。

下面再来看一段代码：

```java
SqlSession sqlSession = new SqlSession(new SimpleExecutor(), new Configuration());
IStudentMapper studentMapper = sqlSession.getMapper(IStudentMapper.class);
Student student = studentMapper.selectStuById(1);
```

来解释一下这段代码的调用逻辑。
1. 第一行，将 Configuration 和 Executor 都传入 SqlSession 的构造方法。生成一个 SqlSession 实例。
2. 第二行，调用 SqlSession 的 getMapper 方法，传入需要被代理的接口；接着调用 Configuration 的 getMapper 方法，接着就是 JDK 生成代理类，返回。
3. 第三行，使用 JDK 生成的代理类，调用 selectStuById 方法，实际上会调用 MapperProxy 的 invoke 方法（这些都是动态代理的知识，不细说），而 MapperProxy 中的 invoke 方法最后调用 SqlSession 的 selectOne方法。
4. 回到 SqlSession 中，会发现 1-3 步，从 SqlSession 开始，走过了 Configuration 和 MapperProxy 最后又回到了 SqlSession 中。这时，SqlSession 调用 Executor 的 query 方法，执行 SQL，返回结果。

补充说明 MapperProxy 类中的 invoke 方法的一个判断条件：

```java
if(method.getDeclaringClass().getName().equals(interfaces.getClass().getDeclaringClass().getName())){
    return sqlSession.selectOne();
}
```

为什么需要这句判断呢？
1. 因为这里的动态代理不是正常的动态代理。被代理的接口没有实现类。
2. 也就是说 IStudentMapper 的 selectStuById 方法没有去实现。
3. 当代理的方法来自 IStudentMapper，我们需要手动的让它去调用数据库的相关方法。否则会找不到方法。

继续补充说明为什么绕了一大圈，又回到了 SqlSession？
1. 结合 SqlSession 为什么需要 selectOne 方法说明。
2. 其实就是为了动态代理 IStudentMapper 所做出的牺牲。虽然有点绕，但是层次清晰，而且不用自己手动写实现类了。优点远大于缺点。

# v1.2.0 的优缺点

为什么说 v1.2.0 几乎是一个里程碑版本？
1. 可以通过注解的形式，进行配置 Mapper 接口（也就是我们要操作数据库的接口），不需要再手写其实现类。
2. 增加了 Configuration 类，将配置的动作全都交给了这个类，再次体现了单一职责。

为什么说 v1.2.0 是几乎，而不是完全是一个里程碑？
* 因为我太菜了，写了一天，才写出这个效果，还是没能达到上生产环境的要求，而只是一个 demo 级别的产物。
* MapperProxy 类中的 invoke 方法中，获取接口对应的 SQL 语句和返回值类型时是写死的。 
```java
String sql = String.valueOf(mapperMathedMapList.get("com.faynely.fybatis.IStudentMapper").get(0).get("selectStuById").get("sql"));
Class clazz = (Class) mapperMathedMapList.get("com.faynely.fybatis.IStudentMapper").get(0).get("selectStuById").get("returnType");
```
* 在 Configuration 进行配置加载的时候，有一个 mapperMethodMapList，这个 Map 描述的配置有限，不清晰。目前是这个结构：
```text
{"com.faynely,fybatis.IStudentMapper" -> ["selectStuById" -> {"sql", "select * from student where id = %d"}, {"returnType" -> "com.faynely.fybatis.Student"}, ...]}
```

看着很累，需要再次用到面向对象的思想，将其分离出几个对象来描述。
将上述的问题解决了，就是我们的 v1.2.1。其实也就是小功能的提升，大的框架都已经完成了，就差最后一步。

# v1.2.1 的设计思路

针对上面的这个问题。
> MapperProxy 类中的 invoke 方法中，获取接口对应的 SQL 语句和返回值类型时是写死的。 

那么这样就需要在 MapperProxy 类加一个当前操作的 Mapper 接口的 Class 对象。这样可以通过 Class 对象去之前的 mapperMethodMapList 中获取 sql 和 returnType。

针对这个问题。
> 在 Configuration 进行配置加载的时候，有一个 mapperMethodMapList，这个 Map 描述的配置有限，不清晰

这样的结构一个不清晰，一个不合理。这个 Map 需要解决的就是把 Mapper 接口的*全限定名*加*方法名*和方法对应的 SQL 和返回类型做一个映射。

可以优化成如下结构：
```text
{"com.faynely.fybatis.IStudentMapper.selectStuById" -> {"select * from student where id = %d", "com.faynely.fybatis.Student"} }
```

这个 Map 的值可以作为一个对象，我们叫做 MapperData。

```java
public class MapperData{
    private String sql;
    private Class returnType;
}
```
# v1.2.1 的遗留问题

1. 直接 new SqlSession 时，构造方法有两个参数需要自己手写，面向用户不友好，需要改进成工厂模式去获取一个实例。
2. MapperProxy 中的 TODO（第50行），目前只支持一个参数，明显不符合规范。

# v1.2.2 的设计

针对：
> 直接 new SqlSession 时，构造方法有两个参数需要自己手写，面向用户不友好，需要改进成工厂模式去获取一个实例。

需要自己定义一个工厂类，将实例化的操作封装成一个方法。

针对：
> MapperProxy 中的 TODO（第50行），目前只支持一个参数，明显不符合规范。

将传 SQL 参数的地方均设为数组参数，利用 String.format 方法，传入数组元素，自动进行设置值。

# v1.2.2 总结

当版本到达了 v1.2.2 已经完成了一个里程碑的开发。推荐参考的同学们可以先完成 v1.2.2 的开发。

目前可以完美支持一般的 SQL 操作。不过也要参考一定的操作规范，比如数据库中的字段名和实体类的属性名要一致。


# v1.2.2-M1 的总结

**已经实现的核心功能**
1. 可以动态地将 Mapper 接口对应到 SQL 语句。用户通过 Mapper 接口可以直接操作数据库进行简单查询。
2. 通过向用户暴露 SqlSession 接口，提供 getMapper 方法，获取可以直接操作数据库的 Mapper 实现类。提供给用户使用。

# v1.2.2-M1 未完成的功能

1. 还没有 TypeHandler 类型转换功能。
2. 还没有增加事务。
3. 还没有增加 plugin 的功能。
4. 还没有增加缓存功能。

一点一点完善。你会发现最原始的硬编码 JDBC 操作数据库，到现在的相对优雅地实现了 MyBatis 的核心功能，就可以看出，一个框架不是一蹴而就的，是不停地根据新需求完善出来的。

# v1.3.0 的功能计划

1. 增加 MyBatis 的 plugin 功能。
2. 增加 Executor 缓存的功能。（针对同一条语句，在一个 session 中进行缓存）

# v1.3.0 的设计思路

针对：
> 增加 MyBatis 的 plugin 功能。

plugin 的作用和拦截器一样。作用于在 Executor 操作数据库时。那也就是说要代理 Executor，而且还得支持多个 plugin。也就是需要对代理后的对象做代理。

针对：
> 增加 Executor 缓存的功能。（针对同一条语句，在一个 session 中进行缓存）

对 Ececutor 的子类做拓展，也就是用装饰器模式，增加一个支持缓存的 Executor。

## 插件的设计思想

作用：插件的作用在于 Executor 执行数据库语句前做额外的操作。

如何实现：使用动态代理，对 Executor 进行多重动态代理。

运行流程：
1. 通过 PluginProxy.wrap() 生成 Executor 动态代理对象，其中 InvocationHandler 为 new PluginProxy(目标对象， 插件)
2. 这样生成的代理类，调用时，肯定会执行 InvocationHandler 中的 invoke 方法，也就是 PluginProxy 中的 invoke 方法。
3. 在 invoke 方法中调用插件的拦截方法，并在插件拦截方法中，传入目标对象的实例，方法和参数，进行调用目标对象需要执行的方法。

举例：（若有 PluginA 和 PluginB 两个插件。）

1. 首先对 Executor 生成代理类。
第一次生成，其中 PluginProxy(Executor 对象， PluginA)
第二次生成，其中 PluginProxy(第一次生成的代理类， PLuginB)

2. 调用时，拿到第二次生成的代理类，执行 invoke 方法后，进入 PluginB 的拦截方法中，执行拦截方法代码，回调反射执行第一次生成的代理类的方法。
3. 这时再次进入 invoke 方法，继而进入 PluginA 的拦截方法中，执行拦截方法代码，回调 Executor 类对应的方法。
