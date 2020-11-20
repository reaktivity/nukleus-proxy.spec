/**
 * Copyright 2016-2020 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.nukleus.proxy.internal;

import java.nio.ByteBuffer;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.reaktivity.specification.nukleus.proxy.internal.types.control.ProxyRouteExFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.stream.ProxyBeginExFW;

public final class ProxyFunctions
{
    @Function
    public static ProxyRouteExBuilder routeEx()
    {
        return new ProxyRouteExBuilder();
    }

    @Function
    public static ProxyBeginExBuilder beginEx()
    {
        return new ProxyBeginExBuilder();
    }

    @Function
    public static ProxyBeginExMatcherBuilder matchBeginEx()
    {
        return new ProxyBeginExMatcherBuilder();
    }

    public static final class ProxyRouteExBuilder
    {
        private final ProxyRouteExFW.Builder routeExRW;

        private ProxyRouteExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[1024 * 8]);
            this.routeExRW = new ProxyRouteExFW.Builder().wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public byte[] build()
        {
            final ProxyRouteExFW routeEx = routeExRW.build();
            final byte[] array = new byte[routeEx.sizeof()];
            routeEx.buffer().getBytes(routeEx.offset(), array);
            return array;
        }
    }

    public static final class ProxyBeginExBuilder
    {
        private final ProxyBeginExFW.Builder beginExRW;

        private ProxyBeginExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[1024 * 8]);
            this.beginExRW = new ProxyBeginExFW.Builder().wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public ProxyBeginExBuilder typeId(
            int typeId)
        {
            beginExRW.typeId(typeId);
            return this;
        }

        public byte[] build()
        {
            final ProxyBeginExFW beginEx = beginExRW.build();
            final byte[] array = new byte[beginEx.sizeof()];
            beginEx.buffer().getBytes(beginEx.offset(), array);
            return array;
        }
    }

    public static final class ProxyBeginExMatcherBuilder
    {
        private final DirectBuffer bufferRO = new UnsafeBuffer();

        private final ProxyBeginExFW beginExRO = new ProxyBeginExFW();

        private Integer typeId;

        public ProxyBeginExMatcherBuilder typeId(
            int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public BytesMatcher build()
        {
            return typeId != null ? this::match : buf -> null;
        }

        private ProxyBeginExFW match(
            ByteBuffer byteBuf) throws Exception
        {
            if (!byteBuf.hasRemaining())
            {
                return null;
            }

            bufferRO.wrap(byteBuf);
            final ProxyBeginExFW beginEx = beginExRO.tryWrap(bufferRO, byteBuf.position(), byteBuf.capacity());

            if (beginEx != null &&
                matchTypeId(beginEx))
            {
                byteBuf.position(byteBuf.position() + beginEx.sizeof());
                return beginEx;
            }

            throw new Exception(beginEx.toString());
        }

        private boolean matchTypeId(
            ProxyBeginExFW beginEx)
        {
            return typeId == beginEx.typeId();
        }
    }

    public static class Mapper extends FunctionMapperSpi.Reflective
    {
        public Mapper()
        {
            super(ProxyFunctions.class);
        }

        @Override
        public String getPrefixName()
        {
            return "proxy";
        }
    }

    private ProxyFunctions()
    {
        // utility
    }
}
