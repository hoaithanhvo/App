package com.example.nidecsnipeit.network;
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    private String URL = "192.168.0.44";

    public RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void setServerURL(String serverURL) {
        URL = serverURL;
    }

    public void getServerURL(String serverURL) {
        return URL;
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    //This method can be adapted to be used however needed for example swapping GET for POST, supplying a json object as the body instead of an empty new JsonObject();
    public void getItemRequestByAssetTag(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/bytag/" + assetTag;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResult(error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void checkinItem(int assetID, CheckinItemModel checkinItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkin";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResult(error);
            }
        }){

            @Override
            protected Map getParams()
            {
                Map params = new HashMap();
                params.put("status", checkinItem.status);
                params.put("name", checkinItem.name);
                params.put("note", checkinItem.note);
                params.put("location", checkinItem.localtion);

                return params;
            }

        };

        requestQueue.addToRequestQueue(jsonObjectRequest);
    }
}