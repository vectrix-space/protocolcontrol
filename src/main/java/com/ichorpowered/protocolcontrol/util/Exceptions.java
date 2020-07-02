/*
 * This file is part of ProtocolControl, licensed under the MIT License (MIT).
 *
 * Copyright (c) IchorPowered <http://ichorpowered.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ichorpowered.protocolcontrol.util;

import java.util.function.Consumer;
import net.kyori.indigo.DetailedReport;
import net.kyori.mu.function.ThrowingRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

public final class Exceptions {
  /**
   * Executes the {@link ThrowingRunnable} and when encountered an
   * exception, constructs a {@link DetailedReport} with the specified
   * message, creating the specified category appending a {@link Class}
   * source and sending the {@link DetailedReport} in the specified
   * {@link Logger}.
   *
   * @param throwingRunnable The throwing runnable function
   * @param logger The logger
   * @param source The class source
   * @param category The root category name
   * @param message The root message
   */
  public static void catchingReport(final @NonNull ThrowingRunnable<Throwable> throwingRunnable,
                                    final @NonNull Logger logger, final @NonNull Class<?> source,
                                    final @NonNull String category, final @NonNull String message) {
    try {
      throwingRunnable.throwingRun();
    } catch(final Throwable throwable) {
      Exceptions.catchingReport(
        throwable,
        logger,
        source,
        category,
        message
      );
    }
  }

  /**
   * Takes the specified {@link Throwable}, constructs a {@link DetailedReport}
   * with the specified message, creating the specified category appending a
   * {@link Class} source and sending the {@link DetailedReport} in the specified
   * {@link Logger}.
   *
   * @param throwable The throwable
   * @param logger The logger
   * @param source The class source
   * @param category The root category name
   * @param message The root message
   */
  public static void catchingReport(final @NonNull Throwable throwable, @NonNull final Logger logger,
                                    final @NonNull Class<?> source, final @NonNull String category,
                                    final @NonNull String message) {
    final DetailedReport report = DetailedReport.create(message, throwable);
    report.category(category).detail("class", source);

    Exceptions.printReport(logger, report);
  }

  /**
   * Executes the {@link ThrowingRunnable} and when encountered an
   * exception, constructs a {@link DetailedReport} with the specified
   * message, creating the specified category appending a {@link Class}
   * source, passing the {@link DetailedReport} to the specified report
   * filler function and sending the {@link DetailedReport} in the
   * specified {@link Logger}.
   *
   * @param throwingRunnable The throwing runnable function
   * @param logger The logger
   * @param source The class source
   * @param category The root category name
   * @param message The root message
   * @param reportFiller The report filler function
   */
  public static void catchingReport(final @NonNull ThrowingRunnable<Throwable> throwingRunnable,
                                    final @NonNull Logger logger, final @NonNull Class<?> source,
                                    final @NonNull String category, final @NonNull String message,
                                    final @NonNull Consumer<DetailedReport> reportFiller) {
    try {
      throwingRunnable.throwingRun();
    } catch(final Throwable throwable) {
      Exceptions.catchingReport(
        throwable,
        logger,
        source,
        category,
        message,
        reportFiller
      );
    }
  }

  /**
   * Takes the specified {@link Throwable}, constructs a {@link DetailedReport}
   * with the specified message, creating the specified category appending a
   * {@link Class} source, passing the {@link DetailedReport} to the specified
   * report filler function and sending the {@link DetailedReport} in the
   * specified {@link Logger}.
   *
   * @param throwable The throwable
   * @param logger The logger
   * @param source The class source
   * @param category The root category name
   * @param message The root message
   * @param reportFiller The report filler function
   */
  public static void catchingReport(final @NonNull Throwable throwable, final @NonNull Logger logger,
                                    final @NonNull Class<?> source, final @NonNull String category,
                                    final @NonNull String message, final @NonNull Consumer<DetailedReport> reportFiller) {
    final DetailedReport report = DetailedReport.create(message, throwable);
    report.category(category).detail("class", source);

    reportFiller.accept(report);

    Exceptions.printReport(logger, report);
  }

  /**
   * Prints the specified {@link DetailedReport} to the specified
   * {@link Logger}.
   *
   * @param logger The logger
   * @param report The report
   */
  public static void printReport(final @NonNull Logger logger, final @NonNull DetailedReport report) {
    logger.error(String.format("%s:\n %s", report.message(), report.toString()));
  }
}
