/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.robocar.things.exception;

/**
 * Created by Keval on 15-May-17.
 * Exception to throw if GPIO is not initialized or initialization fails.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class GpioInitializationException extends RuntimeException {

    public GpioInitializationException() {
        super("Cannot initialize GPIO.");
    }
}
