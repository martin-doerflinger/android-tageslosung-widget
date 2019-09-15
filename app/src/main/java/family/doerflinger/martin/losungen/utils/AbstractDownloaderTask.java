package family.doerflinger.martin.losungen.utils;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public abstract class AbstractDownloaderTask extends AsyncTask<Void, Void, String[]> {
    protected String[] doInBackground(Void... params) {
        HttpsURLConnection con = null;
        String[] result = new String[5];
        try {
            URL url = new URL("https://www.losungen.de/die-losungen/");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.connect();

            if (con.getResponseCode() == 200) {
                InputStream in = new BufferedInputStream(con.getInputStream());
                Document doc = Jsoup.parse(IOUtils.toString(in, "UTF-8"));
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy");
                result[0] = dateFormat.format(date);
                result[1] = doc.getElementsByClass("losungen-verse1txt").first().text();
                result[2] = doc.getElementsByClass("losungen-verse1verse").first().text();
                result[3] = doc.getElementsByClass("losungen-verse2txt").first().text();
                result[4] = doc.getElementsByClass("losungen-verse2verse").first().text();
            }
        } catch (IOException e) {
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return result;
    }

    public abstract void onPostExecute(String[] result);
}
