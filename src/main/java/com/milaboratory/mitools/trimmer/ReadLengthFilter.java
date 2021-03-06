/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.mitools.trimmer;

import cc.redberry.primitives.Filter;
import com.milaboratory.core.io.sequence.SingleRead;

public class ReadLengthFilter implements Filter<SingleRead>, java.io.Serializable {
    final int minLength;

    public ReadLengthFilter(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean accept(SingleRead singleRead) {
        return singleRead.getData().size() >= minLength;
    }
}
