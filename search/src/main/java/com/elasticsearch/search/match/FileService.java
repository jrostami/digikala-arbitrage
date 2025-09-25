package com.elasticsearch.search.match;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FileService {
    List<String> userAgent = Lists.newArrayList(
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"
    );
    @Value("${file.downloadPathBase}")
    private String downloadPathBase;
    @Value("${file.downloadTries}")
    private Integer downloadTries;

    //@PostConstruct
    public void init() {
        downloadFileWithLocalCheck("http://162.223.91.117:3000/api/1.0/content-proxy?url=https://funko.com/dw/image/v2/BGTS_PRD/on/demandware.static/-/Sites-funko-master-catalog/default/dw17d9701c/images/funko/70826-1.png");
        System.out.println("salam");
    }

    public Boolean downloadFileWithLocalCheck(String url) {
        if (urlFileExist(url))
            return true;
        return downloadDirectFile(url, downloadTries);
    }

    public Boolean downloadDirectFile(String url, Integer tries) {

        for (Integer i = 0; i < tries; i++) {
            try {
                FileUtils.copyURLToFile(
                        new URL(url),
                        new File(downloadPathBase + encodeUrl(url)), 1000, 1000);
                Thread.sleep(1000L);
                return true;
            } catch (Exception e) {
                try {
                    System.out.println("download failed " + i);
                    DownloadImage(url, downloadPathBase + encodeUrl(url), i);
                    return true;
                } catch (Exception e2) {
                    System.out.println("second download failed " + i);
                }
            }
        }

        return false;
    }


    public Boolean isEncodableUrl(String url) {
        try {
            encodeUrl(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUrlFileFullPath(String url) {
        try {
            String encodeUrl = encodeUrl(url);
            return getDiskPath(encodeUrl);
        } catch (Exception e) {

        }
        return null;
    }

    private String getDiskPath(String filename) {
        return downloadPathBase + filename;
    }

    public String encodeUrl(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
    }

    public void deleteFiles(List<String> urls) {
        if (urls == null || urls.isEmpty())
            return;
        for (String url : urls) {
            deleteFile(url);
        }
    }

    public void deleteFile(String url) {
        try {
            String filePath = getUrlFileFullPath(url);
            if (fileExist(filePath)) {
                File file = new File(filePath);
                file.delete();
            }
        } catch (Exception e) {
        }
    }

    public Boolean urlFileExist(String url) {
        try {
            String filePath = getUrlFileFullPath(url);
            if (filePath == null)
                return false;
            return fileExist(filePath);
        } catch (Exception e) {
        }
        return false;
    }

    private Boolean fileExist(String filename) {
        File f = new File(filename);
        return f.isFile();
    }

    public boolean DownloadImage(String search, String path, Integer i) throws IOException {

        // This will get input data from the server
        InputStream inputStream = null;

        // This will read the data from the server;
        OutputStream outputStream = null;


        // This will open a socket from client to server
        URL url = new URL(search);
        // This socket type will allow to set user_agent
        URLConnection con = url.openConnection();
        con.setReadTimeout(1000);
        con.setConnectTimeout(1000);
        // Setting the user agent
        con.setRequestProperty("User-Agent", userAgent.get(i));

        // Requesting input data from server
        inputStream = con.getInputStream();

        // Open local file writer
        outputStream = new FileOutputStream(path);

        // Limiting byte written to file per loop
        byte[] buffer = new byte[2048];

        // Increments file size
        int length;

        // Looping until server finishes
        while ((length = inputStream.read(buffer)) != -1) {
            // Writing data
            outputStream.write(buffer, 0, length);
        }


        // closing used resources
        // The computer will not be able to use the image
        // This is a must

        outputStream.close();
        inputStream.close();
        return true;
    }
}