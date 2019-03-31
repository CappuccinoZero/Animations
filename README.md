# Animations
[![](https://jitpack.io/v/Zhouyulin1220/Animations.svg)](https://jitpack.io/#Zhouyulin1220/Animations)
### 持续更新
#### 这是一个动画库，包含我在空闲时间学习做的一些自定义控件，方便以后使用

## 
## 使用
#### Project build.gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#### Module build.gradle
```
dependencies {
	        implementation 'com.github.Zhouyulin1220:Animations:1.0'
	}
```
## 
## 介绍
### WaveView 波浪
#### 属性描述
属性名|值|描述
-|:-:|:-
waveSpeed|integer|流动速度
backgroundTint|color|背景颜色
firstColor|color|第一个波浪颜色
secondColor|color|第二个波浪颜色
waveWidth|dimension|波浪宽度
waveHeight|dimension|波兰高度
parcent|float[0.0-1.0]|占容器量
randomWave|boolean|是否随机高度
single|boolean|单层绘制或双层绘制
container|enum(rect,circle,heart)|容器形状(矩形,圆形,心形)

#### 方法描述
方法名|返回|描述
-|:-:|-
start( )|void|开始动画
pause( )|void|结束动画
stop( )|void|停止动画
addParcent(float num)|void|增加百分百高度,无动画
public void setParcent(int parcent)|void|设置百分百高度，无动画
public void addAnimationParcent(float num)|void|增加百分百高度,有动画
public void setAnimationParcent(int parcent)|void|设置百分百高度，有动画

##
### FlowRect 流动矩形
#### 属性描述
属性名|值|描述
-|:-:|:-
rectHeight|dimension|矩形高度
spaceX|dimension|水平间距
spaceY|dimension|竖直间距
translationX|dimension|水平偏移距离(正为左)
backgroundColor|color|背景颜色
startColor|color|渐变开始颜色
centerColor|color|渐变中间颜色
endColor|color|渐变结束颜色
FlowSpeed|integer|流动速度
colorGradient|Boolean|是否渐变,无渐变只有startcolor有效
flowRadius|integer|矩形圆角数值
#### 方法描述
方法名|返回|描述
-|:-:|-
start( )|void|开始动画
pause( )|void|结束动画
stop( )|void|停止动画

##
### RippleView 波纹涟漪
#### 属性描述
属性名|值|描述
-|:-:|:-
color|color|波纹颜色
speed|integer|扩散速度
density|integer|波纹间距
isFill|boolean|是否填充
isGradient|boolean|是否渐变
gradient|enum(deep,shallow)|渐变程度
locate|enum(9个位置)|圆心位置
rateOfChange|enum(none,slow,normal,high)|波纹从浅到深效果
style|enum(in,out)|波纹风格
#### 方法描述
方法名|返回|描述
-|:-:|-
start( )|void|开始动画
pause( )|void|结束动画
stop( )|void|停止动画


