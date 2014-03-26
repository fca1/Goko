/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2013, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;

import ch.qos.logback.core.spi.ContextAwareBase;

abstract public class EncoderBase<E> extends ContextAwareBase implements Encoder<E> {

  protected boolean started;

  protected OutputStream outputStream;
  
  public void init(OutputStream os) throws IOException {
    this.outputStream = os;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    started = true;
  }

  public void stop() {
    started = false;
  }
}  

