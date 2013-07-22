/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.commons.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.isis.core.commons.exceptions.IsisException;

public final class ArrayUtil {

    private ArrayUtil() {
    }

    public static Object[] getObjectAsObjectArray(final Object option) {
        final Class<?> arrayType = option.getClass().getComponentType();
        if (!arrayType.isPrimitive()) {
            return (Object[]) option;
        }
        if (arrayType == char.class) {
            return ArrayUtil.convertCharToCharacterArray(option);
        } else {
            return convertPrimitiveToObjectArray(arrayType, option);
        }
    }

    private static Object[] convertPrimitiveToObjectArray(final Class<?> arrayType, final Object originalArray) {
        Object[] convertedArray;
        try {
            final Class<?> wrapperClass = WrapperUtils.wrap(arrayType);
            final Constructor<?> constructor = wrapperClass.getConstructor(new Class[] { String.class });
            final int len = Array.getLength(originalArray);
            convertedArray = (Object[]) Array.newInstance(wrapperClass, len);
            for (int i = 0; i < len; i++) {
                convertedArray[i] = constructor.newInstance(new Object[] { Array.get(originalArray, i).toString() });
            }
        } catch (final NoSuchMethodException e) {
            throw new IsisException(e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            throw new IsisException(e);
        } catch (final IllegalArgumentException e) {
            throw new IsisException(e);
        } catch (final InstantiationException e) {
            throw new IsisException(e);
        } catch (final IllegalAccessException e) {
            throw new IsisException(e);
        } catch (final InvocationTargetException e) {
            throw new IsisException(e);
        }
        return convertedArray;
    }

    public static Object[] convertCharToCharacterArray(final Object originalArray) {
        final char[] original = (char[]) originalArray;
        final int len = original.length;
        final Character[] converted = new Character[len];
        for (int i = 0; i < converted.length; i++) {
            converted[i] = Character.valueOf(original[i]);
        }
        return converted;
    }

    public static <T> T[] combine(final T[]... arrays) {
        final List<T> combinedList = new ArrayList<T>();
        for (final T[] array : arrays) {
            for (final T t : array) {
                combinedList.add(t);
            }
        }
        return combinedList.toArray(arrays[0]); // using 1st element of arrays
                                                // to specify the type
    }

    public static String[] append(final String[] args, final String... moreArgs) {
        final ArrayList<String> argList = new ArrayList<String>();
        argList.addAll(Arrays.asList(args));
        argList.addAll(Arrays.asList(moreArgs));
        return argList.toArray(new String[] {});
    }

}
