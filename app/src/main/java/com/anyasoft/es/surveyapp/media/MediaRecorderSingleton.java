package com.anyasoft.es.surveyapp.media;

import android.media.MediaRecorder;

import com.anyasoft.es.surveyapp.ESurvey;
import com.anyasoft.es.surveyapp.logger.L;

import java.io.IOException;

public class MediaRecorderSingleton {


    private static  MediaRecorderSingleton mediaRecorderSingleton = null;
    private MediaRecorder recorder;

    private MediaRecorderSingleton() throws IOException {


        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(ESurvey.DIR_PATH+"/"+ESurvey.FILE_NAME);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.prepare();
    }//
    public void destroyObject(){
        if(null != recorder) {
            try {
                recorder.stop();
            }
            catch (Exception e){
                L.e(e+"");
            }//
            finally {
                recorder.release();
            }


        }//if()
        mediaRecorderSingleton = null;

    }//

    public MediaRecorder getRecorder(){
        return recorder;
    }//getRecorder()
    public static MediaRecorderSingleton getInstance() throws IOException {
        if( null == mediaRecorderSingleton){
           mediaRecorderSingleton =  new MediaRecorderSingleton();

        }//if()

        return mediaRecorderSingleton;
    }//getInstance()

}
