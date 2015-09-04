package com.seatgeek.placesautocomplete.network;

import android.net.Uri;

import com.seatgeek.placesautocomplete.json.PlacesApiJsonParser;
import com.seatgeek.placesautocomplete.model.PlacesApiException;
import com.seatgeek.placesautocomplete.model.PlacesApiResponse;
import com.seatgeek.placesautocomplete.model.Status;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class OkHttpPlacesHttpClient extends AbstractPlacesHttpClient {
    private final OkHttpClient mOkHttpClient;

    OkHttpPlacesHttpClient(PlacesApiJsonParser parser) {
        super(parser);
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(15L, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(15L, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(15L, TimeUnit.SECONDS);
    }

    @Override
    protected <T extends PlacesApiResponse> T executeNetworkRequest(final Uri uri, final ResponseHandler<T> responseHandler) throws IOException {
        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        T body = responseHandler.handleStreamResult(response.body().byteStream());

        Status status = body.status;

        if (status != null && !status.isSuccessful()) {
            String err = body.error_message;
            throw new PlacesApiException(err != null ? err : "Unknown Places Api Error");
        } else {
            return body;
        }
    }
}
