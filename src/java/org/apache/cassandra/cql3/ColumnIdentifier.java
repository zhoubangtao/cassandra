/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.cql3;

import java.util.Locale;
import java.nio.ByteBuffer;

import org.apache.cassandra.cache.IMeasurableMemory;
import org.apache.cassandra.cql3.statements.Selectable;
import org.apache.cassandra.db.TypeSizes;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.cassandra.utils.ObjectSizes;

/**
 * Represents an identifer for a CQL column definition.
 */
public class ColumnIdentifier implements Selectable, Comparable<ColumnIdentifier>, IMeasurableMemory
{
    public final ByteBuffer bytes;
    private final String text;

    public ColumnIdentifier(String rawText, boolean keepCase)
    {
        this.text = keepCase ? rawText : rawText.toLowerCase(Locale.US);
        this.bytes = ByteBufferUtil.bytes(this.text);
    }

    public ColumnIdentifier(ByteBuffer bytes, AbstractType type)
    {
        this.bytes = bytes;
        this.text = type.getString(bytes);
    }

    @Override
    public final int hashCode()
    {
        return bytes.hashCode();
    }

    @Override
    public final boolean equals(Object o)
    {
        // Note: it's worth checking for reference equality since we intern those
        // in SparseCellNameType
        if (this == o)
            return true;

        if(!(o instanceof ColumnIdentifier))
            return false;
        ColumnIdentifier that = (ColumnIdentifier)o;
        return bytes.equals(that.bytes);
    }

    @Override
    public String toString()
    {
        return text;
    }

    public long memorySize()
    {
        return ObjectSizes.getFieldSize(2 * ObjectSizes.getReferenceSize())
             + ObjectSizes.getSize(bytes)
             + TypeSizes.NATIVE.sizeof(text);
    }

    public int compareTo(ColumnIdentifier other)
    {
        if (this == other)
            return 0;

        return bytes.compareTo(other.bytes);
    }
}
