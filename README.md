# HuaWei-2019
HuaWei 2019年编程大赛 西北赛区初赛 36名

![Image text](https://raw.githubusercontent.com/DaiQing1995/HuaWei-2019/master/score.png)

思路大概是：控制在全地图上的车辆数目，按照每一个时间单位重新进行一次调度（模拟过程），道路权重可以动态调整（稀释权重），另外进行了流量控制（根据进入出去的车辆数目进行记录）。薄弱的地方大概在于没有死锁检测。
程序本地吞吐量可以达到非常高的量，可惜与线上判题器不一致，后来给我带来了较大麻烦。

这一次参加比赛时间大概用了3天时间（周日周一周二敲代码，周二下午5小时高铁出差,晚上宾馆里测试到1点）吧，正式初赛那天一个上午都没有成功过，后来发现是好像是jdk版本的不一致，换了JDK后，试着提交了下，下午3点15成功提交，后面总共提交成功3次，要是还有点时间的话，结果可能能好很多。因为我是基本没有了调参机会。

这次比赛整体来讲，相较后来的中兴捧月，招商银行CERES，还是费了较大心血的，心里还是感觉有些可惜。最后感谢队友的支持。
