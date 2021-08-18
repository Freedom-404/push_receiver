package com.artemkjv.push_receiver.notification;

import android.util.Base64;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class PushRegistratorFCM extends PushRegistratorAbstractGoogle {

   private static final String FCM_DEFAULT_PROJECT_ID = "push-test-19590";

   private static final String FCM_DEFAULT_APP_ID = "1:910789166102:android:6b88b8bc65db02e29b6137";

   private static final String FCM_DEFAULT_API_KEY_BASE64 = "QUl6YVN5QzlwLVJ1WFhzMEdBX2l1eWNNakYwcTlFcXgxYXVHbnY0";

   private static final String FCM_APP_NAME = "Push";

   private FirebaseApp firebaseApp;

   @Override
   String getProviderName() {
      return "FCM";
   }

   @WorkerThread
   @Override
   String getToken(String senderId) throws ExecutionException, InterruptedException, IOException {
      initFirebaseApp(senderId);

      try {
         return getTokenWithClassFirebaseMessaging();
      } catch (NoClassDefFoundError | NoSuchMethodError e) {
         // Class or method wil be missing at runtime if firebase-message older than 21.0.0 is used.
         Log.e("Info",
            "FirebaseMessaging.getToken not found, attempting to use FirebaseInstanceId.getToken"
         );
      }

      // Fallback for firebase-message versions older than 21.0.0
      return getTokenWithClassFirebaseInstanceId(senderId);
   }

   // This method is only used if firebase-message older than 21.0.0 is in the app
   // We are using reflection here so we can compile with firebase-message:22.0.0 and newer
   //   - This version of Firebase has completely removed FirebaseInstanceId
   @Deprecated
   @WorkerThread
   private String getTokenWithClassFirebaseInstanceId(String senderId) throws IOException {
      // The following code is equivalent to:
      //   FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance(firebaseApp);
      //   return instanceId.getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE);
      Exception exception;
      try {
         Class<?> FirebaseInstanceIdClass = Class.forName("com.google.firebase.iid.FirebaseInstanceId");
         Method getInstanceMethod = FirebaseInstanceIdClass.getMethod("getInstance", FirebaseApp.class);
         Object instanceId = getInstanceMethod.invoke(null, firebaseApp);
         Method getTokenMethod = instanceId.getClass().getMethod("getToken", String.class, String.class);
         Object token = getTokenMethod.invoke(instanceId, senderId, "FCM");
         return (String) token;
      } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
         exception = e;
      }

      throw new Error("Reflection error on FirebaseInstanceId.getInstance(firebaseApp).getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE)", exception);
   }

   @WorkerThread
   private String getTokenWithClassFirebaseMessaging() throws ExecutionException, InterruptedException {
      // We use firebaseApp.get(FirebaseMessaging.class) instead of FirebaseMessaging.getInstance()
      //   as the latter uses the default Firebase app. We need to use a custom Firebase app as
      //   the senderId is provided at runtime.
      FirebaseMessaging fcmInstance = firebaseApp.get(FirebaseMessaging.class);
      // FirebaseMessaging.getToken API was introduced in firebase-messaging:21.0.0
      Task<String> tokenTask = fcmInstance.getToken();
      return Tasks.await(tokenTask);
   }

   private void initFirebaseApp(String senderId) {
      if (firebaseApp != null)
         return;

//      OneSignalRemoteParams.Params remoteParams = OneSignal.getRemoteParams();
      FirebaseOptions firebaseOptions =
         new FirebaseOptions.Builder()
            .setGcmSenderId(senderId)
            .setApplicationId(getAppId(/*remoteParams*/))
            .setApiKey(getApiKey(/*remoteParams*/))
            .setProjectId(getProjectId(/*remoteParams*/))
            .build();
      firebaseApp = FirebaseApp.initializeApp(AppContextKeeper.appContext, firebaseOptions);
   }

   private static @NonNull String getAppId(/*OneSignalRemoteParams.Params remoteParams*/) {
//      if (remoteParams.fcmParams.appId != null)
//         return remoteParams.fcmParams.appId;
      return FCM_DEFAULT_APP_ID;
   }

   private static @NonNull String getApiKey(/*OneSignalRemoteParams.Params remoteParams*/) {
//      if (remoteParams.fcmParams.apiKey != null)
//         return remoteParams.fcmParams.apiKey;
      return new String(Base64.decode(FCM_DEFAULT_API_KEY_BASE64, Base64.DEFAULT));
   }

   private static @NonNull String getProjectId(/*OneSignalRemoteParams.Params remoteParams*/) {
//      if (remoteParams.fcmParams.projectId != null)
//         return remoteParams.fcmParams.projectId;
      return FCM_DEFAULT_PROJECT_ID;
   }
}
