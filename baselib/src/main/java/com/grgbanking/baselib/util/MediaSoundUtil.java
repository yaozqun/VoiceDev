package com.grgbanking.baselib.util;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaSoundUtil {
    /**
     * @Title: playHintSound
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param context
     * @param resid   资源ID
     */
    public static void playHintSound(Context context,int resid){
        MediaPlayer mPlayer = MediaPlayer.create(context, resid); 
        if(mPlayer.isPlaying()){
            return;
        }
        mPlayer.start();
    }
    
}
