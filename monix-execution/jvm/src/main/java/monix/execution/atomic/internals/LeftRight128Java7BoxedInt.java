/*
 * Copyright (c) 2016 by its authors. Some rights reserved.
 * See the project homepage at: https://sincron.org
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

package monix.execution.atomic.internals;

import monix.execution.misc.UnsafeAccess;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

abstract class LeftRight128Java7BoxedIntImpl extends LeftPadding56
  implements monix.execution.atomic.internals.BoxedInt {

  public volatile int value;
  private static final long OFFSET;
  private static final Unsafe UNSAFE = (Unsafe) UnsafeAccess.UNSAFE;

  static {
    UnsafeAccess.checkIfUnsafeIsAvailable();
    try {
      Field field = LeftRight128Java7BoxedIntImpl.class.getDeclaredField("value");
      OFFSET = UNSAFE.objectFieldOffset(field);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  LeftRight128Java7BoxedIntImpl(int initialValue) {
    UnsafeAccess.checkIfUnsafeIsAvailable();
    this.value = initialValue;
  }

  public int volatileGet() {
    return value;
  }

  public void volatileSet(int update) {
    value = update;
  }

  public void lazySet(int update) {
    UNSAFE.putOrderedInt(this, OFFSET, update);
  }

  public boolean compareAndSet(int current, int update) {
    return UNSAFE.compareAndSwapInt(this, OFFSET, current, update);
  }

  public int getAndSet(int update) {
    int current = value;
    while (!UNSAFE.compareAndSwapInt(this, OFFSET, current, update))
      current = value;
    return current;
  }

  public int getAndAdd(int delta) {
    int current;
    do {
      current = UNSAFE.getIntVolatile(this, OFFSET);
    } while (!UNSAFE.compareAndSwapInt(this, OFFSET, current, current+delta));
    return current;
  }
}

final class LeftRight128Java7BoxedInt extends LeftRight128Java7BoxedIntImpl {
  public volatile long r1, r2, r3, r4, r5, r6, r7 = 11;
  @Override public long sum() {
    return p1 + p2 + p3 + p4 + p5 + p6 + p7 +
      r1 + r2 + r3 + r4 + r5 + r6 + r7;
  }

  LeftRight128Java7BoxedInt(int initialValue) {
    super(initialValue);
  }
}