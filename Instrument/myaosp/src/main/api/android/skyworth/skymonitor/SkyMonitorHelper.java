package android.skyworth.skymonitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.skyworth.skymonitor.SkyMonitorManager;
import java.util.List;
import android.util.Log;

public class SkyMonitorHelper {
	private final static String TAG = "SkyMonitor_Helper";
    private Context mContext;
    private SkyMonitorManager mSkyMonitorManager;
	
    public SkyMonitorHelper(){

    }
	
    /**
     *
     * @param context
     * @see
     */
    public SkyMonitorHelper(Context context){
        this.mContext = context;
        mSkyMonitorManager = (SkyMonitorManager) mContext.getSystemService(Context.SKYMONITOR_SERVICE);
		Log.i(TAG,"SkyMonitorHelper: mSkyMonitorManager="+mSkyMonitorManager);
    }
  
	public void testMethod(){
		Log.i(TAG,"testMethod: ");
		mSkyMonitorManager.testMethod();
	}
}