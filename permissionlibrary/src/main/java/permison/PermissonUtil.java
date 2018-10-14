package permison;

import android.app.Activity;

import permison.listener.PermissionListener;

/**
 * Created by Tyhj on 2018/4/2.
 */

public class PermissonUtil {

    private static int PERMISSIONS_REQUEST_CODE = 101;

    private static PermissionListener permissionListener;

    public static int getPermissionsRequestCode() {
        return PERMISSIONS_REQUEST_CODE;
    }

    public static void setPermissionsRequestCode(int permissionsRequestCode) {
        PERMISSIONS_REQUEST_CODE = permissionsRequestCode;
    }

    public static PermissionsChecker mPermissionsChecker; // 权限检测器

    public static void checkPermission(Activity activity, PermissionListener listener, String... permissions) {
        permissionListener = listener;
        mPermissionsChecker = new PermissionsChecker(activity);
        // 缺少权限时, 进入权限配置页面
        if (permissions != null && mPermissionsChecker.lacksPermissions(permissions)) {
            PermissionsActivity.startActivityForResult(activity, PERMISSIONS_REQUEST_CODE, permissions);
            return;
        }
        permissonResult(true);
    }


    public static void permissonResult(boolean succ) {
        if (succ && permissionListener != null) {
            permissionListener.havePermission();
        } else if (!succ && permissionListener != null) {
            permissionListener.requestPermissionFail();
        }
    }

}
