package com.support.robigroup.ututor.utils


import android.content.*
import android.os.Build
import android.support.v7.app.AlertDialog
import android.util.Log
import android.support.v4.content.ContextCompat.startActivity
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.ComponentName
import android.content.Intent
import com.support.robigroup.ututor.R


class XiomiHuaweiSecurityUtills {

    private fun showEnableNotificationDialog(context: Context, intent: Intent) {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(context)
        }
        builder.setTitle(context.getString(R.string.permission_autostart_title))
                .setMessage(context.getString(R.string.permission_autostart_description))
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialogInterface, i ->
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("Notification Request", "Failed to launch AutoStart Screen ", e)
                    } catch (e: Exception) {
                        Log.e("Notification Request", "Failed to launch AutoStart Screen ", e)
                    }
                })
                .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun addAutoStartup(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = android.os.Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
            }

            val list = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("exc", e.toString())
        }
    }
}
