package it.uniba.di.ivu.sms16.gruppo10.justpizza.util;

import android.location.Location;
import android.support.annotation.Nullable;

/**
 * Interfaccia di utilità per comunicare con i fragment
 * Created by Utente on 08/07/2016.
 */
public interface LocationProvider {
    /**
     * @return The current Location if any or null if not
     */
    @Nullable
    Location getLocation();
}
