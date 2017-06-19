package com.app.ptt.comnha.Interfaces;

import com.app.ptt.comnha.Modules.PlaceAttribute;

/**
 * Created by cuong on 10/5/2016.
 */

public interface LocationFinderListener {
     void onLocationFinderStart();
     void onLocationFinderSuccess(PlaceAttribute placeAttribute);
     void onGeocodingFinderSuccess(String address);
}
