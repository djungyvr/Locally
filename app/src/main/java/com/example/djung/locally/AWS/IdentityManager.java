package com.example.djung.locally.AWS;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.example.djung.locally.Presenter.ThreadUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The identity manager keeps track of the current sign-in provider and is responsible
 * for caching credentials.
 */
public class IdentityManager {

    /**
     * Allows the application to get asynchronous response with user's
     * unique identifier.
     */
    public interface IdentityHandler {
        /**
         * Handles the user's unique identifier.
         * @param identityId Amazon Cognito Identity ID which uniquely identifies
         *                   the user.
         */
        public void handleIdentityID(final String identityId);

        /**
         * Handles any error that might have occurred while getting the user's
         * unique identifier from Amazon Cognito.
         * @param exception exception
         */
        public void handleError(final Exception exception);
    }

    /** Log tag. */
    private static final String LOG_TAG = IdentityManager.class.getSimpleName();

    /** Cognito caching credentials provider. */
    private CognitoCachingCredentialsProvider credentialsProvider;

    /** Executor service for obtaining credentials in a background thread. */
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Constructor. Initializes the cognito credentials provider.
     * @param appContext the application context.
     * @param clientConfiguration the client configuration options such as retries and timeouts.
     */
    public IdentityManager(final Context appContext, final ClientConfiguration clientConfiguration) {
        Log.d(LOG_TAG, "IdentityManager init");
        initializeCognito(appContext, clientConfiguration);
        // Ensures that userID is cached.
        getUserID(null);
    }

    private void initializeCognito(final Context context, final ClientConfiguration clientConfiguration) {
        credentialsProvider =
                new CognitoCachingCredentialsProvider(context,
                        AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID,
                        AwsConfiguration.AMAZON_COGNITO_REGION,
                        clientConfiguration
                );

    }

    /**
     * @return true if the cached Cognito credentials are expired, otherwise false.
     */
    public boolean areCredentialsExpired() {

        final Date credentialsExpirationDate =
                credentialsProvider.getSessionCredentitalsExpiration();

        if (credentialsExpirationDate == null) {
            Log.d(LOG_TAG, "Credentials are EXPIRED.");
            return true;
        }

        long currentTime = System.currentTimeMillis() -
                (long)(SDKGlobalConfiguration.getGlobalTimeOffset() * 1000);

        final boolean credsAreExpired =
                (credentialsExpirationDate.getTime() - currentTime) < 0;

        Log.d(LOG_TAG, "Credentials are " + (credsAreExpired ? "EXPIRED." : "OK"));

        return credsAreExpired;
    }

    /**
     * @return the Cognito credentials provider.
     */
    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    /**
     * Gets the cached unique identifier for the user.
     * @return the cached unique identifier for the user.
     */
    public String getCachedUserID() {
        return getCredentialsProvider().getCachedIdentityId();
    }

    /**
     * Gets the user's unique identifier. This method can be called from
     * any thread.
     * @param handler handles the unique identifier for the user
     */
    public void getUserID(final IdentityHandler handler) {

        new Thread(new Runnable() {
            Exception exception = null;

            @Override
            public void run() {
                String identityId = null;

                try {
                    // Retrieve the user identity on the background thread.
                    identityId = getCredentialsProvider().getIdentityId();
                } catch (final Exception exception) {
                    this.exception = exception;
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                } finally {

                    if (handler == null) {
                        return;
                    }

                    final String result = identityId;

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (exception != null) {
                                handler.handleError(exception);
                                return;
                            }

                            handler.handleIdentityID(result);
                        }
                    });
                }
            }
        }).start();
    }
}