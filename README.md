# Android动态权限申请工具（包括悬浮窗）

标签（空格分隔）： Android

---

原文链接：https://www.zybuluo.com/Tyhj/note/1289791

为了保证APP正常运行，**动态权限申请**是android比较常用的功能，由于每次都需要做申请、等待返回还有拒绝反馈等操作，比较麻烦，所以集成了一个比较简单的动态权限申请库


### 集成方法：
Step 1. Add the JitPack repository to your build file
```
//Add it in your root build.gradle at the end of repositories:
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
//Add the dependency
dependencies {
	        implementation 'com.github.tyhjh:PermissionUtil:v1.0.2'
	}
```

### 调用方法
传入需要申请的权限，然后在回调中执行成功或者失败的操作，用法如下：
```java
PermissonUtil.checkPermission(MainActivity.this, new PermissionListener() {
                    @Override
                    public void havePermission() {
                        Toast.makeText(MainActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestPermissionFail() {
                        Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                }, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE});
```
以上的代码，实际效果如下




### 需要动态申请的相关权限
```xml
<!-- 联系人权限 -->
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <!-- 电话权限 -->
    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
    <uses-permission android:name="android.permission.USE_SIP" />
    <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS" />
    <uses-permission android:name="com.android.voicemail.permission.ADD_VOICEMAIL" />

    <!-- 日历权限 -->
    <uses-permission android:name="android.permission.READ_CALENDAR" />
    <uses-permission android:name="android.permission.WRITE_CALENDAR" />

    <!-- 相机权限 -->
    <uses-permission android:name="android.permission.CAMERA" />

    <!-- 传感器权限 -->
    <uses-permission android:name="android.permission.SENSORS" />

    <!-- 位置权限 -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <!-- 存储权限 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <!-- 录音权限 -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <!-- 短信权限 -->
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.RECEIVE_WAP_PUSH" />
    <uses-permission android:name="android.permission.RECEIVE_MMS" />
    <uses-permission android:name="android.permission.RECEIVE_SMS" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.READ_CELL_BROADCASTS" />
```

### 具体流程
先判断每个权限是否已经拿到，都拿到的话就返回正确，没有的话再进入**PermissionsActivity**去申请权限
```java
 public static void   checkPermission(Activity activity, PermissionListener listener, String... permissions) {
        permissionListener = listener;
        mPermissionsChecker = new PermissionsChecker(activity);
        // 缺少权限时, 进入权限配置页面
        if (permissions != null && mPermissionsChecker.lacksPermissions(permissions)) {
            PermissionsActivity.startActivityForResult(activity, PERMISSIONS_REQUEST_CODE, permissions);
            activity.overridePendingTransition(0, 0);
            return;
        }
        permissonResult(true);
    }
```
#### 判断是否已获取权限
```java
// 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
```


#### 在PermissionsActivity中取申请每个权限
```java

  @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            String[] permissions = getPermissions();
            if (mChecker.lacksPermissions(permissions)) {
                requestPermissions(permissions); // 请求权限
            } else {
                allPermissionsGranted(); // 全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

/**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }
```
在**onResume**中取申请每个权限，在**onRequestPermissionsResult**方法中判断权限是否获取完成。获取完权限则反馈完成，进行完成的操作；没完全获取则弹窗提示手动打开，点击设置到应用设置界面设置权限，返回时候会在**onResume**中再次判断权限；点击取消则获取失败，进行反馈，执行失败后操作


```java
  // 请求权限
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }
```


### 悬浮窗权限申请
使用方法
```java
FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this);
```

悬浮窗权限有点特殊，而且根据不同的手机还不一样，所以有些麻烦，也是网上找到的一个很好的工具，适配了很多的机型，这里单独写了出来，也添加到我的权限申请的工具里面


### 总结
其实就是新建一个**Activity**，将主题设置为透明并且取消切换动画，让用户感觉在同一个界面内，然后在这里面来处理权限申请，并且返回处理结果

项目地址：https://github.com/tyhjh/PermissionUtil
参考文章：https://blog.csdn.net/self_study/article/details/52859790


