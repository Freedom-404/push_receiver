package com.artemkjv.push_receiver.notification;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

// PushRegistratorFCM extend this class
// Only getToken() needs to be implement for FCM
// This performs error handling and retry logic for FCM
abstract class PushRegistratorAbstractGoogle implements PushRegistrator {
   private RegisteredHandler registeredHandler;

   private static int REGISTRATION_RETRY_COUNT = 5;
   private static int REGISTRATION_RETRY_BACKOFF_MS = 10_000;

   abstract String getProviderName();
   abstract String getToken(String senderId) throws Throwable;

   @Override
   public void registerForPush(Context context, String senderId, RegisteredHandler callback) {
      registeredHandler = callback;

      if (isValidProjectNumber(senderId, callback))
         internalRegisterForPush(senderId);
   }

   private void internalRegisterForPush(String senderId) {
      try {
         if (/*OSUtils.isGMSInstalledAndEnabled()*/ true)
            registerInBackground(senderId);
         else {
//            GooglePlayServicesUpgradePrompt.showUpdateGPSDialog();
            Log.e("Error", "'Google Play services' app not installed or disabled on the device.");
            registeredHandler.complete(null, UserState.PUSH_STATUS_OUTDATED_GOOGLE_PLAY_SERVICES_APP);
         }
      } catch (Throwable t) {
         Log.e(
            "Error",
            "Could not register with "
              + getProviderName() +
              " due to an issue with your AndroidManifest.xml or with 'Google Play services'.",
            t
         );
         registeredHandler.complete(null, UserState.PUSH_STATUS_FIREBASE_FCM_INIT_ERROR);
      }
   }

   private Thread registerThread;
   private synchronized void registerInBackground(final String senderId) {
      // If any thread is still running, don't create a new one
      if (registerThread != null && registerThread.isAlive())
         return;

      registerThread = new Thread(new Runnable() {
         public void run() {
            for (int currentRetry = 0; currentRetry < REGISTRATION_RETRY_COUNT; currentRetry++) {
               boolean finished = attemptRegistration(senderId, currentRetry);
               if (finished)
                  return;
               OSUtils.sleep(REGISTRATION_RETRY_BACKOFF_MS * (currentRetry + 1));
            }
         }
      });
      registerThread.start();
   }

   private boolean firedCallback;
   private boolean attemptRegistration(String senderId, int currentRetry) {
      try {
         String registrationId = getToken(senderId);
         Log.e("Error", "Device registered, push token = " + registrationId);
         registeredHandler.complete(registrationId, UserState.PUSH_STATUS_SUBSCRIBED);
         return true;
      } catch (IOException e) {
         if (!"SERVICE_NOT_AVAILABLE".equals(e.getMessage())) {
            Log.e("Error", "Error Getting " + getProviderName() + " Token", e);
            if (!firedCallback)
               registeredHandler.complete(null, UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION);
            return true;
         }
         else {
            if (currentRetry >= (REGISTRATION_RETRY_COUNT - 1))
               Log.e("Error", "Retry count of " + REGISTRATION_RETRY_COUNT + " exceed! Could not get a " + getProviderName() + " Token.", e);
            else {
               Log.e("Error", "'Google Play services' returned SERVICE_NOT_AVAILABLE error. Current retry count: " + currentRetry, e);
               if (currentRetry == 2) {
                  // Retry 3 times before firing a null response and continuing a few more times.
                  registeredHandler.complete(null, UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_SERVICE_NOT_AVAILABLE);
                  firedCallback = true;
                  return true;
               }
            }
         }
      } catch (Throwable t) {
         Log.e("Error", "Unknown error getting " + getProviderName() + " Token", t);
         registeredHandler.complete(null, UserState.PUSH_STATUS_FIREBASE_FCM_ERROR_MISC_EXCEPTION);
         return true;
      }

      return false;
   }

   private boolean isValidProjectNumber(String senderId, PushRegistrator.RegisteredHandler callback) {
      boolean isProjectNumberValidFormat;
      try {
         Float.parseFloat(senderId);
         isProjectNumberValidFormat = true;
      } catch(Throwable t) {
         isProjectNumberValidFormat = false;
      }

      if (!isProjectNumberValidFormat) {
         Log.e("Error", "Missing Google Project number!\nPlease enter a Google Project number / Sender ID on under App Settings > Android > Configuration on the OneSignal dashboard.");
         callback.complete(null, UserState.PUSH_STATUS_INVALID_FCM_SENDER_ID);
         return false;
      }
      return true;
   }
}
