package eu.fiskur.fiskurdatagov.providers;

import com.squareup.otto.Bus;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class BusProvider{

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
