package com.smartstay.tenant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Service
public class OtpService {

    @Value("${SMS_API_KEY}")
    String SMS_API_KEY;
    @Value("${SMS_SENDER_ID}")
    String SMS_SENDER_ID;
    @Value("${SMS_CHANNEL}")
    String SMS_CHANNEL;

    @Value("${SMS_DCS}")
    String SMS_DCS;

    public void sendOtp(String mobileNo,String message) {

        try {
            String requestUrl = "https://www.smsgatewayhub.com/api/mt/SendSMS?" +
                    "APIKey=" + URLEncoder.encode(SMS_API_KEY, StandardCharsets.UTF_8) +
                    "&senderid=" + URLEncoder.encode(SMS_SENDER_ID, StandardCharsets.UTF_8) +
                    "&channel=" + URLEncoder.encode(SMS_CHANNEL, StandardCharsets.UTF_8) +
                    "&DCS=" + URLEncoder.encode(SMS_DCS, StandardCharsets.UTF_8) +
                    "&flashsms=" + URLEncoder.encode("0", StandardCharsets.UTF_8) +
                    "&number=" + URLEncoder.encode(mobileNo, StandardCharsets.UTF_8) +
                    "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8) +
                    "&route=" + URLEncoder.encode("", StandardCharsets.UTF_8);

            URL url = URI.create(requestUrl).toURL();
            HttpURLConnection uc = (HttpURLConnection)url.openConnection();

            uc.disconnect();
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
