package de.isb.mjelen.itkons.generator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.LogisticDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

@RequiredArgsConstructor
public abstract class Distribution {

  protected final DistributionType type;
  protected final int wertMin;
  protected final int wertMax;

  public static Distribution buildNormal(int min, int max) {
    return new NormalBaksDistribution(DistributionType.NORMAL, min, max);
  }

  public static Distribution buildUniform(int min, int max) {
    return new UniformBaksDistribution(DistributionType.LINEAR, min, max);
  }

  public static Distribution buildLogistic(int min, int max) {
    return new LogisticBaksDistribution(DistributionType.LOGISTIC, min, max);
  }

  public static Distribution buildLogNormal(int min, int max) {
    return new LogNormalBaksDistribution(DistributionType.LOG_NORMAL, min, max);
  }

  public static Distribution buildExponential(int min, int max) {
    return new ExponentialBaksDistribution(DistributionType.LOG_EXPONENTIAL, min, max);
  }

  public abstract int nextValue();

  private enum DistributionType {
    NORMAL, LINEAR, LOGISTIC, LOG_NORMAL, LOG_EXPONENTIAL;
  }

  private static class ExponentialBaksDistribution extends Distribution {

    private final ExponentialDistribution dist;

    private ExponentialBaksDistribution(DistributionType type, int wertMin, int wertMax) {
      super(type, wertMin, wertMax);
      dist = new ExponentialDistribution(1.0);
    }

    @Override
    public int nextValue() {
      double sample = dist.sample();
      sample = Math.min(sample, 5.0);
      sample = sample / 5 * (wertMax - wertMin);
      return (int) Math.round(sample);
    }
  }

  private static class LogNormalBaksDistribution extends Distribution {

    private final LogNormalDistribution dist;

    private LogNormalBaksDistribution(DistributionType type, int wertMin, int wertMax) {
      super(type, wertMin, wertMax);
      dist = new LogNormalDistribution(0.0, 0.5);
    }

    @Override
    public int nextValue() {
      double sample = dist.sample();
      sample = Math.min(sample, 4.0);
      sample = sample / 4 * (wertMax - wertMin);
      return (int) Math.round(sample);
    }
  }

  private static class LogisticBaksDistribution extends Distribution {

    private final LogisticDistribution dist;

    private LogisticBaksDistribution(DistributionType type, int wertMin, int wertMax) {
      super(type, wertMin, wertMax);
      dist = new LogisticDistribution(5.0, 2.0);
    }

    @Override
    public int nextValue() {
      double sample = dist.sample();
      sample = Math.abs(sample + 5);
      sample = Math.min(sample, 20.0);
      sample = sample / 20 * (wertMax - wertMin);
      return (int) Math.round(sample);
    }
  }

  private static class NormalBaksDistribution extends Distribution {

    private final NormalDistribution dist;

    private NormalBaksDistribution(DistributionType type, int wertMin, int wertMax) {
      super(type, wertMin, wertMax);
      dist = new NormalDistribution(0.0, 1.0);
    }

    @Override
    public int nextValue() {
      double sample = dist.sample();
      sample = Math.abs(sample + 3);
      sample = Math.min(sample, 6.0);
      sample = sample / 6 * (wertMax - wertMin);
      return (int) Math.round(sample);
    }
  }

  private static class UniformBaksDistribution extends Distribution {

    private final UniformIntegerDistribution dist;

    private UniformBaksDistribution(DistributionType type, int wertMin, int wertMax) {
      super(type, wertMin, wertMax);
      dist = new UniformIntegerDistribution(wertMin, wertMax);
    }

    @Override
    public int nextValue() {
      return dist.sample();
    }
  }
}
