package com.yiqi.choose.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检查网络状态工具类
 * 需要 android.permission.ACCESS_NETWORK_STATE 权限
 * @return
 */
public class NetWork {
	public static final int RESULT_OK = 200;
	public static final int RESULT_EMPTY = 300;
	public static final int RESULT_ERROR = 400;

	private static ConnectivityManager cwjManager = null;

	private static void getConnectivityManager(Context context) {
		// 创建连接网络管理对象
		if (cwjManager == null) {
			cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
	}

	/**
	 * 检查是否存在可用的网络
	 * 需要 android.permission.ACCESS_NETWORK_STATE 权限
	 * @return
	 */
//	public static boolean checkNetWorkStatus(Context context) {
//		boolean netSataus = false;
//		getConnectivityManager(context);
//		if (cwjManager.getActiveNetworkInfo() != null) {
//			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
//		}
//		JiaXingApp.Net_state = netSataus;
//		return netSataus;
//	}

//	/**
//	 * 判断当前网络是否可用，并给与toast提示
//	 * 需要 android.permission.ACCESS_NETWORK_STATE 权限
//	 * @param context
//	 * @return
//	 */
//	public static boolean judgeNetWork(Context context) {
//		if (!checkNetWorkStatus(context)) {
//			Toast.makeText(context, "当前手机网络不可用!", Toast.LENGTH_LONG).show();
//			return false;
//		}
//		return true;
//	}

	/**
	 * 判断是否存在网络，如果没有则以Dialog提示
	 * 需要 android.permission.ACCESS_NETWORK_STATE 权限
	 * @param ctx
	 * @return
	 */
//	public static boolean judgeDialog(Context ctx) {
//		// 检测网络状态
//		if (!checkNetWorkStatus(ctx)) {
//			final Context context = ctx;
//			AlertDialog.Builder builder = new Builder(ctx);
//			builder.setMessage("您的网络没有开启或暂不可用!");
//			builder.setTitle("提示");
//			builder.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//							((Activity) context).finish();
//						}
//					});
//			builder.create().show();
//			return false;
//		}
//		return true;
//	}

	/**
	 * 检查代理是否是cnwap接入
	 * 需要 android.permission.ACCESS_NETWORK_STATE 权限
	 * @param context
	 * @return
	 */
	public static boolean isCNWAPNetWork(Context context) {
		boolean netSataus = false;
		getConnectivityManager(context);
		NetworkInfo ni = cwjManager.getActiveNetworkInfo();
		if (ni != null
				&& ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE
				&& ("cmwap".equalsIgnoreCase((ni.getExtraInfo())) || "cmnet"
						.equalsIgnoreCase((ni.getExtraInfo())))) {
			netSataus = true;
		}
		return netSataus;
	}

}