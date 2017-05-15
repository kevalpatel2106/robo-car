package com.kevalpatel2106.robocar.things.exception;

/**
 * Created by Keval on 15-May-17.
 * Exception to throw if GPIO is not initialized or initialization fails.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public class GpoInitializationException extends RuntimeException {

    public GpoInitializationException() {
        super("Cannot initialize GPIO.");
    }
}
