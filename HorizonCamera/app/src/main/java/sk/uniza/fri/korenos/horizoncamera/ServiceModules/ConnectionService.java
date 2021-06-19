package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;

/**
 * Created by Markos on 26. 11. 2016.
 */

public class ConnectionService {

    private static final String IMAGE_TAG_JSON_NAME = "imageData";

    private static final String CONNECTION_STATE_NO_INTERNET_CONNECTION = "noInternetConnection";
    private static final String CONNECTION_STATE_DATA_NOT_SEND = "dataNotSend";
    private static final String CONNECTION_STATE_DATA_SENT = "dataSent";

    private Context applicationContext = null;

    private AsyncTask<String, Void, String> sendTask = new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... params) {
            if (!connectionCheck()) {
                return CONNECTION_STATE_NO_INTERNET_CONNECTION;
            }

            BufferedReader reader = null;
            String text = null;

            try {
                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(params[1]);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                text = sb.toString();
                System.out.println(text);
            } catch (IOException ex) {
                ex.printStackTrace();
                return CONNECTION_STATE_DATA_NOT_SEND;
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
                return CONNECTION_STATE_DATA_SENT;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result){
                case CONNECTION_STATE_DATA_SENT:  Toast.makeText(applicationContext, "Data sent successfully", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_STATE_DATA_NOT_SEND:  Toast.makeText(applicationContext, "Error- Data has not been send", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_STATE_NO_INTERNET_CONNECTION:  Toast.makeText(applicationContext, "Connection not found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private boolean connectionCheck(){
            ConnectivityManager connectivity = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    };

    public ConnectionService(String urlString, JSONObject JSONdata, Context context) {
        applicationContext = context;

        sendTask.execute(urlString, JSONdata.toString());
    }

    public static void sendData(String urlString, List<Frame> listOfFrame, List<String> frameFullPath, Context context) {
        new ConnectionService(urlString, convertPhotoData(listOfFrame, frameFullPath), context);
    }

    public static JSONObject convertPhotoData(List<Frame> listOfFrame, List<String> frameFullPath) {
        if (listOfFrame == null || frameFullPath == null) {
            return null;
        }
        if (listOfFrame.size() != frameFullPath.size()) {
            return null;
        }
        JSONArray dataArray = null;

        dataArray = new JSONArray();
        if (dataArray == null) {
            return null;
        }

        JSONObject frameJSONData = null;
        try {
            for (int i = 0; i < listOfFrame.size(); i++) {
                frameJSONData = new JSONObject();

                for (int j = 0; j < Frame.COLUMN_NAMES.length; j++) {
                    frameJSONData.accumulate(Frame.COLUMN_NAMES[j], listOfFrame.get(i).getDataOfColumn(j));
                }
                frameJSONData.accumulate(IMAGE_TAG_JSON_NAME, serialiseImageToString(frameFullPath.get(i)));
                dataArray.put(frameJSONData);
            }

            frameJSONData = new JSONObject();
            frameJSONData.put("horizonCameraData", dataArray);
        } catch (JSONException e) {
        }

        return frameJSONData;
    }

    private static String serialiseImageToString(String imagePath) {
        Bitmap savedImage = DataOperationServices.getSavedImage(imagePath);

        if(savedImage != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            savedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] byteImageArray = outputStream.toByteArray();
            return Base64.encodeToString(byteImageArray, Base64.DEFAULT);
        }
        return "";
    }

    private static byte[] deserialiseImageFromString(String serialisedImage) {
        return Base64.decode(serialisedImage.getBytes(), Base64.DEFAULT);
    }
}
