package com.ryvk.drifthomeadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class MailSender {

    public static void sendEmail(final String recipient, final String subject, final String body, Context context, MailSenderProcess callback) {
        final String username = "raviduyashith123@gmail.com";
        final String password = "mpuqmchaiizminmk";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(body, "text/html; charset=utf-8");

                MimeMultipart mimeMultipart = new MimeMultipart();
                mimeMultipart.addBodyPart(mimeBodyPart);
                message.setContent(mimeMultipart);

                Transport.send(message);

                // Invoke success callback
                if (callback != null) {
                    ((Activity) context).runOnUiThread(callback::onCompletion);
                }

            } catch (MessagingException e) {
                Log.e("MailSender", "sendEmail failure: ", e);
                if (callback != null) {
                    ((Activity) context).runOnUiThread(() -> callback.onError(e));
                }
                AlertUtils.showAlert(context, "Error", "Email sending failed!");
            }
        }).start();
    }

    public interface MailSenderProcess {
        void onCompletion();
        void onError(Exception e);
    }
}
