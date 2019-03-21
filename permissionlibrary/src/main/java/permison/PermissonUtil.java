package permison;

import android.app.Activity;
import android.content.Context;

import permison.listener.PermissionListener;

/**
 * Created by Tyhj on 2018/4/2.
 */

public class PermissonUtil {

    private static int PERMISSIONS_REQUEST_CODE = 101;

    private static PermissionListener permissionListener;

    /**
     * 权限检测器
     */
    static PermissionsChecker mPermissionsChecker;

    public static void checkPermission(Activity activity, PermissionListener listener, String... permissions) {
        permissionListener = listener;
        Context context = activity.getApplicationContext();
        mPermissionsChecker = new PermissionsChecker(context);
        // 缺少权限时, 进入权限配置页面
        if (permissions != null && mPermissionsChecker.lacksPermissions(permissions)) {
            PermissionsActivity.startActivityForResult(activity, PERMISSIONS_REQUEST_CODE, permissions);
            return;
        }
        permissonResult(true);
    }


    static void permissonResult(boolean succ) {
        if (succ && permissionListener != null) {
            permissionListener.havePermission();
        } else if (!succ && permissionListener != null) {
            permissionListener.requestPermissionFail();
        }
        permissionListener=null;
    }

}
