package com.sdwfqin.quickseed.ui.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.ToastUtils;
import com.sdwfqin.quickseed.R;
import com.sdwfqin.quickseed.base.SampleBaseActivity;
import com.sdwfqin.quickseed.view.QuickWindowFloatView;

import butterknife.OnClick;

/**
 * 悬浮窗与截图Demo
 * <p>
 *
 * @author 张钦
 * @date 2020/4/10
 */
public class WindowFloatAndScreenshotActivity extends SampleBaseActivity {

    /**
     * 截图权限
     */
    public static final int REQUEST_MEDIA_PROJECTION = 18;
    /**
     * 悬浮窗
     */
    public static final int REQUEST_ALERT = 19;

    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected int getLayout() {
        return R.layout.activity_window_float_and_screenshot;
    }

    @Override
    protected void initEventAndData() {
        mTopBar.setTitle("悬浮窗与截图");
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
    }

    @OnClick(R.id.btn_screenshot)
    public void onViewClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_ALERT);
            } else {
                requestCapturePermission();
            }
        } else {
            requestCapturePermission();
        }
    }

    private void requestCapturePermission() {
        //获取截屏的管理器
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private void showFloat(MediaProjection mediaProjection) {
        QuickWindowFloatView quickWindowFloatView = new QuickWindowFloatView(mContext, mediaProjection);
        quickWindowFloatView.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    MediaProjection mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data);
                    showFloat(mMediaProjection);
                }
                break;
            case REQUEST_ALERT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        requestCapturePermission();
                    } else {
                        ToastUtils.showShort("ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝");
                    }
                }
        }
    }
}
