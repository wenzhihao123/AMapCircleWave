# AMapCircleWave
Android开发——高德地图波纹扩散效果动效及自定义缩放、定位控件
### 引言
最近项目有在用高德地图，顺便记录下一些知识。项目需要做地图上显示一个车辆位置，这个位置需要波纹扩散效果，这个高德地图也提供了demo，但是看着效果不算很棒，比起iOS平台的要差老远了。

[高德地图demo地址](http://lbs.amap.com/dev/demo/location-circle#iOS)

Android平台下载了例子瞅了一眼，原理就是AMap.addCircle(CircleOptions options)，加上圆形之后，在一定的时间范围内动态改变圆的半径，说白了也就是动画，官方demo使用Timer+TimerTask来实现的动画。因为不能满足设计师的需求，而且不带渐变色，因此我决定用属性动画做了。


来张动态的效果图：

![动态效果图](http://upload-images.jianshu.io/upload_images/2018489-5073096c698a26d5.gif?imageMogr2/auto-orient/strip)

实现原理：添加了三个圆形，每个圆形一开始设置透明，每个圆形开始执行动画有个时间间隔，动态改变每个圆形半径及填充颜色的透明度即可。^_^

详情请见： [简书博客](http://www.jianshu.com/p/fbbee03c6968)  