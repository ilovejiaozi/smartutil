# smartutil
reusable Java components(可复用的java组建,扩展自apache commons),支持java 1.6+(包含16)

相比较Apache Commons 新增的内容如下：

**1 SmartStringUtil , 扩展自StringUtils, 新增功能如下:**
 
  （1） 字符串分割并转换为数组

        eg : `List<Integer> splitConverToIntList(String str, String split) `, 将字符串分割并转为int类型的List

  （2）大写首字母: upperCaseFirstChar

**2 SmartCollectionUtil 扩展自CollectionUtils ，新增功能如下：**

    验证是否为空的map

**3 SmartDateUtil  线程安全的DateUtil, 主要包含parse和format方法**

**4 SmartRandomUtil  扩展自RandomUtils  新增内容如下：，**

  （1）在范围内随机n个不同的随机数

      randomDiffInRange(final long startInclusive, final long endExclusive, int randomCount) 

  （2）乱顺序，重新排列List

      rearrange(List<T> list)

  （3）打乱顺序，并从中随机选取几个

      randomDiffInRearrange(List<K> list, int totalRandomCount)

**5 SmartXmlUtil 读取xml的工具类，方便你快速读取xml，并且支持数据类型转换。**
  
[SmartXmlUtil说明](https://github.com/ilovejiaozi/smartutil/wiki/SmartXmlUtil-%E8%AF%BB%E5%8F%96xml%E5%B7%A5%E5%85%B7%E7%B1%BB)


**6 SmartFileUtil 和 SmartZipFileUtil**

**7 SmartExcelReader  读取Excel工具类，使用poi读取,支持合并单元格数据**

**8.SmartEventBus  一套完整的事件分发和监听, 包括支持注解和接口来监听事件，并支持设置监听者优先级，也支持同步和异步事件。**

**9 SmartMemoryUtil  获取java对象在内存中所暂用内存大小的工具类**

**10 多线程工具类，包含定时调度，多线程队列处理器，多线程数据定时循环处理器等，具体见:**

https://github.com/ilovejiaozi/smartutil/wiki/%E5%A4%9A%E7%BA%BF%E7%A8%8B%E5%B7%A5%E5%85%B7%E7%B1%BB

