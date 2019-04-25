/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.reflect;

import org.apache.commons.lang.RandomStringUtils;
import org.gradle.internal.hash.HashCode;
import org.gradle.internal.hash.HashFunction;
import org.gradle.internal.hash.Hasher;
import org.gradle.internal.hash.Hashing;
import org.gradle.internal.hash.PrimitiveHasher;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Benchmark              (hashSize)   Mode  Cnt        Score       Error  Units
 * HashingBenchmark.md5         1024  thrpt    5  1212734.919 ±  2363.204  ops/s
 * HashingBenchmark.md5        65536  thrpt    5    29263.640 ±   445.377  ops/s
 * HashingBenchmark.md5     67108864  thrpt    5       28.775 ±     0.280  ops/s
 * HashingBenchmark.sha1        1024  thrpt    5   822945.914 ± 12295.094  ops/s
 * HashingBenchmark.sha1       65536  thrpt    5    18905.470 ±   305.588  ops/s
 * HashingBenchmark.sha1    67108864  thrpt    5       19.748 ±     0.586  ops/s
 **/
@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = SECONDS)
public class HashingBenchmark {
    private static final HashCode DUMMY_HASH = HashCode.fromInt(1);
    private static final String DUMMY_STRING = "dummy";
    private static final byte[] DUMMY_BYTES = DUMMY_STRING.getBytes();

    @Param({"1024", "65536", "67108864"})
    int hashSize;

    byte[] input;

    HashFunction sha1, md5;

    @Setup
    public void setup() {
        input = RandomStringUtils.random(hashSize).getBytes();

        sha1 = Hashing.sha1();
        md5 = Hashing.md5();
    }

    @Benchmark
    public void sha1(Blackhole bh) {
        bh.consume(sha1.hashBytes(input));
        bh.consume(putStuff(sha1.defaultHasher()).hash());
        bh.consume(putStuff(sha1.primitiveHasher()).hash());
    }

    @Benchmark
    public void md5(Blackhole bh) {
        bh.consume(md5.hashBytes(input));
        bh.consume(putStuff(md5.defaultHasher()).hash());
        bh.consume(putStuff(md5.primitiveHasher()).hash());
    }

    private Hasher putStuff(Hasher hasher) {
        hasher.putBoolean(true);
        hasher.putDouble(1D);
        hasher.putInt(1);
        hasher.putInt(Integer.MAX_VALUE);
        hasher.putLong(1L);
        hasher.putLong(Long.MAX_VALUE);
        hasher.putByte((byte) 1);
        hasher.putBytes(DUMMY_BYTES);
        hasher.putHash(DUMMY_HASH);
        hasher.putString(DUMMY_STRING);
        hasher.putNull();
        return hasher;
    }

    private PrimitiveHasher putStuff(PrimitiveHasher hasher) {
        hasher.putBoolean(true);
        hasher.putDouble(1D);
        hasher.putInt(1);
        hasher.putInt(Integer.MAX_VALUE);
        hasher.putLong(1L);
        hasher.putLong(Long.MAX_VALUE);
        hasher.putByte((byte) 1);
        hasher.putBytes(DUMMY_BYTES);
        hasher.putHash(DUMMY_HASH);
        hasher.putString(DUMMY_STRING);
        return hasher;
    }
}
