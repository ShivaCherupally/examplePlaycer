/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sdk.response;

import com.citrus.sdk.TransactionResponse;

/**
 * Created by salil on 12/5/15.
 */
public class CitrusError extends CitrusResponse {
    private final TransactionResponse transactionResponse;

    public CitrusError(String message, Status status) {
        super(message, status);
        transactionResponse = null;
    }

    public CitrusError(String message, Status status, TransactionResponse transactionResponse) {
        super(message, status);
        this.transactionResponse = transactionResponse;
    }

    public CitrusError(String message, String rawResponse, Status status, TransactionResponse transactionResponse) {
        super(message, rawResponse, status);
        this.transactionResponse = transactionResponse;
    }

    public TransactionResponse getTransactionResponse() {
        return transactionResponse;
    }
}
