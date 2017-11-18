package io.ahababo.bot.skills.util;

import io.ahababo.bot.Bot;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class EmotionDetector {
    private static final Logger logger = LoggerFactory.getLogger(EmotionDetector.class);
    private static final String TELEGRAM_FILE_API = "https://api.telegram.org/bot%s/getFile?file_id=%s";
    private static final String TELEGRAM_IMAGE_API = "https://api.telegram.org/file/bot%s/%s";
    private static final String AZURE_COGNITION_API = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
    private static final String AZURE_COGNITION_KEY = "3521efef1d1646eeb40a128921297811";

    private static String sendRequest(HttpUriRequest request) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public static Result[] detect(Bot context, String fileId) throws Exception {
        // Fetch image path from Telegram
        URIBuilder filePathUri = new URIBuilder(String.format(TELEGRAM_FILE_API, context.getBotToken(), fileId));
        HttpGet filePathReq = new HttpGet(filePathUri.build());
        JSONObject response = new JSONObject(sendRequest(filePathReq));
        String publicFilePath = response.getJSONObject("result").getString("file_path");

        // Fetch emotions from Azure
        URIBuilder imageUri = new URIBuilder(String.format(TELEGRAM_IMAGE_API, context.getBotToken(), publicFilePath));
        URIBuilder emotionUri = new URIBuilder(AZURE_COGNITION_API);
        HttpPost emotionRequest = new HttpPost(emotionUri.build());
        emotionRequest.setHeader("Content-Type", "application/json");
        emotionRequest.setHeader("Ocp-Apim-Subscription-Key", AZURE_COGNITION_KEY);

        StringEntity requestEntity = new StringEntity("{ \"url\": \"" + imageUri.build().toString() + "\" }");
        emotionRequest.setEntity(requestEntity);
        String azureResponse = sendRequest(emotionRequest);
        logger.info(azureResponse);
        JSONArray emotionArray = new JSONArray(azureResponse);
        Result[] results = new Result[emotionArray.length()];
        for (int i = 0; i < results.length; i++) {
            JSONObject emotionEntry = emotionArray.getJSONObject(i).getJSONObject("scores");
            results[i] = new Result(emotionEntry.getDouble("anger"),
                    emotionEntry.getDouble("contempt"),
                    emotionEntry.getDouble("disgust"),
                    emotionEntry.getDouble("fear"),
                    emotionEntry.getDouble("happiness"),
                    emotionEntry.getDouble("neutral"),
                    emotionEntry.getDouble("sadness"),
                    emotionEntry.getDouble("surprise"));
        }
        return results;
    }

    public static class Result {
        public final double anger, contempt, disgust, fear, happiness, neutral, sadness, surprise;

        private Result(double anger, double contempt, double disgust, double fear, double happiness, double neutral, double sadness, double surprise) {
            this.anger = anger;
            this.contempt = contempt;
            this.disgust = disgust;
            this.fear = fear;
            this.happiness = happiness;
            this.neutral = neutral;
            this.sadness = sadness;
            this.surprise = surprise;
        }

        public double[] rawValues() {
            return new double[]{
                    anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
            };
        }

        public double similarTo(Result r) {
            double[] x = r.rawValues(), y = rawValues();
            double a = 0;
            for (int i = 0; i < x.length; i++) {
                a += x[i] * y[i];
            }
            double b = 0;
            for (int i = 0; i < x.length; i++) {
                b += x[i] * x[i];
            }
            b = Math.sqrt(b);
            double c = 0;
            for (int i = 0; i < y.length; i++) {
                c += y[i] * y[i];
            }
            c = Math.sqrt(c);
            return a / (b * c);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(" ");
            if (anger > 0.8) builder.append("You are angry. ");
            if (contempt > 0.8) builder.append("You are contempt. ");
            if (disgust > 0.8) builder.append("You are disgusted. ");
            if (fear > 0.8) builder.append("You have fear. ");
            if (happiness > 0.8) builder.append("You are happy. ");
            if (neutral > 0.8) builder.append("You look neutral. ");
            if (sadness > 0.8) builder.append("You look sad. ");
            if (surprise > 0.8) builder.append("You look surprised. ");
            return builder.toString();
        }
    }
}
