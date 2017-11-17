package org.apache.geode.examples.protobuf;/*
                                            * Licensed to the Apache Software Foundation (ASF) under
                                            * one or more contributor license agreements. See the
                                            * NOTICE file distributed with this work for additional
                                            * information regarding copyright ownership. The ASF
                                            * licenses this file to You under the Apache License,
                                            * Version 2.0 (the "License"); you may not use this file
                                            * except in compliance with the License. You may obtain
                                            * a copy of the License at
                                            *
                                            * http://www.apache.org/licenses/LICENSE-2.0
                                            *
                                            * Unless required by applicable law or agreed to in
                                            * writing, software distributed under the License is
                                            * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
                                            * CONDITIONS OF ANY KIND, either express or implied. See
                                            * the License for the specific language governing
                                            * permissions and limitations under the License.
                                            */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.geode.internal.protocol.protobuf.v1.BasicTypes.*;
import org.apache.geode.internal.protocol.protobuf.v1.ClientProtocol;
import org.apache.geode.internal.protocol.protobuf.v1.ConnectionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.ConnectionAPI.HandshakeRequest;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI;

public class Example {
  public static void main(String[] args) throws IOException {

    Socket socket = new Socket("localhost", 40404);
    OutputStream outputStream = socket.getOutputStream();
    InputStream inputStream = socket.getInputStream();
    outputStream.write(0x6E);
    outputStream.write(0x01);

    ClientProtocol.Message
        handsgakeMessage =
        ClientProtocol.Message.newBuilder()
            .setRequest(ClientProtocol.Request.newBuilder().setHandshakeRequest(HandshakeRequest.newBuilder().setMajorVersion(1).setMinorVersion(1))).build();

    handsgakeMessage.writeDelimitedTo(outputStream);
    outputStream.flush();

    ConnectionAPI.HandshakeResponse response =
        ConnectionAPI.HandshakeResponse.parseDelimitedFrom(inputStream);

    System.out.println("Got back: " + response);

    ClientProtocol.Message putMessage = ClientProtocol.Message.newBuilder().setRequest(
        ClientProtocol.Request.newBuilder().setPutRequest(RegionAPI.PutRequest.newBuilder()
        .setEntry(
            Entry.newBuilder().setKey(EncodedValue.newBuilder().setStringResult("key1"))
                .setValue(EncodedValue.newBuilder().setStringResult("value1")))
        .setRegionName("example-region"))).build();


    putMessage.writeDelimitedTo(outputStream);
    outputStream.flush();
    RegionAPI.PutResponse putResponse = RegionAPI.PutResponse.parseDelimitedFrom(inputStream);
    System.out.println("Got back: " + putResponse);
    socket.close();

  }

}
