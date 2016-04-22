/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.psi.impl;

import com.goide.psi.GoArrayOrSliceType;
import com.goide.psi.GoChannelType;
import com.goide.psi.GoMapType;
import com.goide.psi.GoType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoTypeUtil {
  public static boolean isIterable(@NotNull GoType type) {
    return type instanceof GoArrayOrSliceType ||
           type instanceof GoMapType ||
           type instanceof GoChannelType ||
           isString(type);
  }

  public static boolean isString(@Nullable GoType type) {
    return type != null && type.textMatches("string") && GoPsiImplUtil.builtin(type);
  }
}
