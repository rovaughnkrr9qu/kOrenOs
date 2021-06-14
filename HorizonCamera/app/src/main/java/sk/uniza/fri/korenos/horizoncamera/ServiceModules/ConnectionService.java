package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;

/**
 * Created by Markos on 26. 11. 2016.
 */

public class ConnectionService {

    private static String IMAGE_TAG_JSON_NAME = "imageData";

    private static AsyncTask<String, Void, Boolean> sendTask = new AsyncTask<String, Void, Boolean>() {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                DataOutputStream printout;
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                //conn.setDoInput(true);
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                printout = new DataOutputStream(conn.getOutputStream());
                printout.writeBytes(URLEncoder.encode(params[1],"UTF-8"));
                printout.flush();
                printout.close();

                /*DataInputStream input = new DataInputStream(conn.getInputStream());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                input.(in);*/

            }catch (MalformedURLException e){
                System.out.println("chyba 1");
                e.printStackTrace();
                return false;
            }
            catch (IOException e){
                System.out.println("chyba 2");
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            System.out.println(aBoolean.booleanValue()+" TUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU 4");
        }
    };

    public static boolean sendData(String urlString, JSONObject JSONdata){
        sendTask.execute(urlString, "message");                                 //naprav to tak, ako to ma byt
        return false;
    }

    public static JSONObject convertPhotoData(List<Frame> listOfFrame, List<String> frameFullPath){
        if(listOfFrame == null || frameFullPath == null){
            return null;
        }
        if(listOfFrame.size() != frameFullPath.size()){
            return null;
        }
        JSONArray dataArray = null;

        dataArray = new JSONArray();
        if(dataArray == null){
            return null;
        }

        JSONObject frameJSONData = null;
        try {
            for(int i = 0; i < listOfFrame.size(); i++){
                frameJSONData = new JSONObject();

                for(int j = 0; j < Frame.COLUMN_NAMES.length; j++){
                    frameJSONData.accumulate(Frame.COLUMN_NAMES[j], listOfFrame.get(i).getDataOfColumn(j));
                }
                frameJSONData.accumulate(IMAGE_TAG_JSON_NAME, serialiseImageToString(frameFullPath.get(i)));
                dataArray.put(frameJSONData);
            }

            frameJSONData = new JSONObject();
            frameJSONData.put("horizonCameraData", dataArray);
        }catch (JSONException e){
        }

        return frameJSONData;
    }

    private static String serialiseImageToString(String imagePath){
        Bitmap savedImage = DataOperationServices.getSavedImage(imagePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        savedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteImageArray = outputStream.toByteArray();
        return Base64.encodeToString(byteImageArray, Base64.DEFAULT);
    }

    private static byte[] deserialiseImageFromString(String serialisedImage){
        return  Base64.decode(serialisedImage.getBytes(), Base64.DEFAULT);
    }
}
