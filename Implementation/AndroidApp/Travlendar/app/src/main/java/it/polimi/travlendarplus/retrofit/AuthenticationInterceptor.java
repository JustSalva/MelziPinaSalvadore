package it.polimi.travlendarplus.retrofit;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class used to add an authentication header to the request sent to the server.
 * The token used is the one returned by the server during login/registration.
 */
public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer ".concat(authToken));

        Request request = builder.build();
        return chain.proceed(request);
    }
}
