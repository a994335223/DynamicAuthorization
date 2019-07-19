### DynamicAuthorization Android 动态授权
集成：

1. Add it in your root build.gradle at the end of repositories
```javascript
		allprojects {
			repositories {
			...
			maven { url 'https://jitpack.io' }
			}
		}
```

2. Add the dependency
```javascript
		dependencies {
	  		implementation 'com.github.a994335223:DynamicAuthorization:v1.0'
		}
```

使用方法：

单条必须赋予权限
```javascript
   PermissionsUtil.requestPermission(getApplication(),true, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权失败", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
```
单条权限申请 必须授权
```javascript
   PermissionsUtil.requestPermission(getApplication(),true, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权失败", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
```
单条权限申请 可拒绝赋予权限
```javascript
   PermissionsUtil.requestPermission(getApplication(),false, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权失败", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
```
多条权限申请 必须授权
```javascript
  PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("标题头:", "内容", "取消", "确认",true);
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                JSONArray arr = null;
                try {
                    arr = getContactInfo(MainActivity.this);
                    if (arr.length() == 0) {
                        Toast.makeText(MainActivity.this, "请确认通讯录不为空且有访问权限", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, arr.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了读取通讯录权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_CONTACTS//把需要的权限加到这里
        }, true, tip);
```
多条权限申请 可拒绝赋予权限
```javascript
  PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("标题头:", "内容", "取消", "确认",false);
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                JSONArray arr = null;
                try {
                    arr = getContactInfo(MainActivity.this);
                    if (arr.length() == 0) {
                        Toast.makeText(MainActivity.this, "请确认通讯录不为空且有访问权限", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, arr.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了读取通讯录权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_CONTACTS//把需要的权限加到这里
        }, true, tip);
```




