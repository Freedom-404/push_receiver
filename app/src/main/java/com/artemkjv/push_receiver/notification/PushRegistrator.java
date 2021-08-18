package com.artemkjv.push_receiver.notification;

import android.content.Context;

public interface PushRegistrator {

   interface RegisteredHandler {
      void complete(String id, int status);
   }

   void registerForPush(Context context, String senderId, RegisteredHandler callback);
}