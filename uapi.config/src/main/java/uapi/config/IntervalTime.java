/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.config;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.StringHelper;

import java.util.concurrent.TimeUnit;

/**
 * IntervalTime time, support below string
 *
 * 1ms  == 1 millisecond
 * 1s   == 1 second
 * 1m   == 1 minute
 * 1h   == 1 hour
 * 1d   == 1 day
 */
public class IntervalTime {

    private static final String UNIT_MS         = "ms";
    private static final String UNIT_SECOND     = "s";
    private static final String UNIT_MINUTE     = "m";
    private static final String UNIT_HOUR       = "h";
    private static final String UNIT_DAY        = "d";

    private static final long MS_OF_SECOND          = 1000;
    private static final long SECOND_OF_MINUTE      = 60;
    private static final long MINUTE_OF_HOUR        = 60;
    private static final long HOUR_OF_DAY           = 24;

    private static final long MS_OF_MINUTE          = MS_OF_SECOND * SECOND_OF_MINUTE;
    private static final long MS_OF_HOUR            = MS_OF_MINUTE * MINUTE_OF_HOUR;
    private static final long MS_OF_DAY             = MS_OF_HOUR * HOUR_OF_DAY;

    private static final long SECOND_OF_HOUR        = SECOND_OF_MINUTE * MINUTE_OF_HOUR;
    private static final long SECOND_OF_DAY         = SECOND_OF_HOUR * HOUR_OF_DAY;

    private static final long MINUTE_OF_DAY         = MINUTE_OF_HOUR * HOUR_OF_DAY;

    public static IntervalTime parse(String intervalString) {
        ArgumentChecker.required(intervalString, "intervalString");
        StringBuilder numberBuffer = new StringBuilder();
        StringBuilder unitBuffer = new StringBuilder();
        IntervalTime intervalTime = new IntervalTime();
        boolean lastNumber = false;
        for (int i = 0; i < intervalString.length(); i++) {
            char c = intervalString.charAt(i);
            if (c >= '0' && c <='9') {
                if (! lastNumber) {
                    checkBuffer(intervalTime, numberBuffer, unitBuffer);
                }
                numberBuffer.append(c);
                lastNumber = true;
            } else {
                if (i == 0) {
                    // the first char must be number
                    throw new InvalidArgumentException(intervalString, InvalidArgumentException.InvalidArgumentType.FORMAT);
                }
                if (lastNumber) {
                    checkBuffer(intervalTime, numberBuffer, unitBuffer);
                }
                unitBuffer.append(c);
                lastNumber = false;
            }
        }
        checkBuffer(intervalTime, numberBuffer, unitBuffer);
        if (numberBuffer.length() > 0 || unitBuffer.length() > 0) {
            throw new InvalidArgumentException(intervalString, InvalidArgumentException.InvalidArgumentType.FORMAT);
        }
        return intervalTime;
    }

    private static void checkBuffer(IntervalTime iTime, StringBuilder numBuf, StringBuilder unitBuf) {
        String unitStr = unitBuf.toString();
        String numStr = numBuf.toString();
        if (numStr.length() > 0 && unitStr.length() > 0) {
            long interval = Long.parseLong(numStr);
            if (UNIT_MS.equalsIgnoreCase(unitStr)) {
                iTime.add(interval, TimeUnit.MILLISECONDS);
            } else if (UNIT_SECOND.equalsIgnoreCase(unitStr)) {
                iTime.add(interval, TimeUnit.SECONDS);
            } else if (UNIT_MINUTE.equalsIgnoreCase(unitStr)) {
                iTime.add(interval, TimeUnit.MINUTES);
            } else if (UNIT_HOUR.equalsIgnoreCase(unitStr)) {
                iTime.add(interval, TimeUnit.HOURS);
            } else if (UNIT_DAY.equalsIgnoreCase(unitStr)) {
                iTime.add(interval, TimeUnit.DAYS);
            } else {
                throw new InvalidArgumentException("Unsupported time unit - {}", unitStr);
            }
            StringHelper.clear(numBuf, unitBuf);
        }
    }

    private long _interval;
    private TimeUnit _unit;

    public IntervalTime() {
        this._interval = 0;
        this._unit = TimeUnit.DAYS;
    }

    public IntervalTime(final long interval, TimeUnit unit) {
        ArgumentChecker.checkLong(interval, "interval", 0L, Long.MAX_VALUE);
        ArgumentChecker.required(unit, "unit");
        this._interval = interval;
        this._unit = unit;
    }

    public long milliseconds() {
        return this._unit.toMillis(this._interval);
    }

    public long seconds() {
        return this._unit.toSeconds(this._interval);
    }

    public long minutes() {
        return this._unit.toMinutes(this._interval);
    }

    public long hours() {
        return this._unit.toHours(this._interval);
    }

    public long days() {
        return this._unit.toDays(this._interval);
    }

    public void add(final long interval, final TimeUnit unit) {
        ArgumentChecker.checkLong(interval, "interval", 0L, Long.MAX_VALUE);
        ArgumentChecker.required(unit, "unit");
        switch (this._unit) {
            case DAYS:
                if (unit == TimeUnit.DAYS) {
                    this._interval += interval;
                } else if (unit == TimeUnit.HOURS) {
                    this._interval += (this._interval * HOUR_OF_DAY + interval);
                    this._unit = unit;
                } else if (unit == TimeUnit.MINUTES) {
                    this._interval = this._interval * HOUR_OF_DAY * MINUTE_OF_HOUR + interval;
                    this._unit = unit;
                } else if (unit == TimeUnit.SECONDS) {
                    this._interval = this._interval * HOUR_OF_DAY * MINUTE_OF_HOUR * SECOND_OF_MINUTE + interval;
                    this._unit = unit;
                } else if (unit == TimeUnit.MILLISECONDS) {
                    this._interval = this._interval * HOUR_OF_DAY * MINUTE_OF_HOUR * SECOND_OF_MINUTE * MS_OF_SECOND + interval;
                    this._unit = unit;
                } else {
                    throw new KernelException("Unsupported time unit - {}", unit);
                }
                break;
            case HOURS:
                if (unit == TimeUnit.DAYS) {
                    this._interval += (interval * HOUR_OF_DAY);
                } else if (unit == TimeUnit.HOURS) {
                    this._interval += interval;
                } else if (unit == TimeUnit.MINUTES) {
                    this._interval = this._interval * MINUTE_OF_HOUR + interval;
                    this._unit = unit;
                } else if (unit == TimeUnit.SECONDS) {
                    this._interval = this._interval * MINUTE_OF_HOUR * SECOND_OF_MINUTE + interval;
                    this._unit = unit;
                } else if (unit == TimeUnit.MILLISECONDS) {
                    this._interval = this._interval * MINUTE_OF_HOUR * SECOND_OF_MINUTE * MS_OF_SECOND + interval;
                    this._unit = unit;
                } else {
                    throw new KernelException("Unsupported time unit - {}", unit);
                }
                break;
            case MINUTES:
                if (unit == TimeUnit.DAYS) {
                    this._interval += (interval * HOUR_OF_DAY * MINUTE_OF_HOUR);
                } else if (unit == TimeUnit.HOURS) {
                    this._interval += (interval * MINUTE_OF_HOUR);
                } else if (unit == TimeUnit.MINUTES) {
                    this._interval += interval;
                } else if (unit == TimeUnit.SECONDS) {
                    this._interval = this._interval * SECOND_OF_MINUTE + interval;
                    this._unit = unit;
                } else if (unit == TimeUnit.MILLISECONDS) {
                    this._interval = this._interval * SECOND_OF_MINUTE * MS_OF_SECOND + interval;
                    this._unit = unit;
                } else {
                    throw new KernelException("Unsupported time unit - {}", unit);
                }
                break;
            case SECONDS:
                if (unit == TimeUnit.DAYS) {
                    this._interval += (interval * HOUR_OF_DAY * MINUTE_OF_HOUR * SECOND_OF_MINUTE);
                } else if (unit == TimeUnit.HOURS) {
                    this._interval += (interval * MINUTE_OF_HOUR * SECOND_OF_MINUTE);
                } else if (unit == TimeUnit.MINUTES) {
                    this._interval += (interval * SECOND_OF_MINUTE);
                } else if (unit == TimeUnit.SECONDS) {
                    this._interval += interval;
                } else if (unit == TimeUnit.MILLISECONDS) {
                    this._interval = this._interval * MS_OF_SECOND + interval;
                    this._unit = unit;
                } else {
                    throw new KernelException("Unsupported time unit - {}", unit);
                }
                break;
            case MILLISECONDS:
                if (unit == TimeUnit.DAYS) {
                    this._interval += (interval * HOUR_OF_DAY * MINUTE_OF_HOUR * SECOND_OF_MINUTE * MS_OF_SECOND);
                } else if (unit == TimeUnit.HOURS) {
                    this._interval += (interval * MINUTE_OF_HOUR * SECOND_OF_MINUTE * MS_OF_SECOND);
                } else if (unit == TimeUnit.MINUTES) {
                    this._interval += (interval * SECOND_OF_MINUTE * MS_OF_SECOND);
                } else if (unit == TimeUnit.SECONDS) {
                    this._interval += (interval * MS_OF_SECOND);
                } else if (unit == TimeUnit.MILLISECONDS) {
                    this._interval += interval;
                } else {
                    throw new KernelException("Unsupported time unit - {}", unit);
                }
                break;
            default:
                throw new KernelException("Unsupported time unit - {}", this._unit);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof IntervalTime)) {
            return false;
        }
        IntervalTime other = (IntervalTime) obj;
        return this._interval == other._interval && this._unit == other._unit;
    }

    @Override
    public int hashCode() {
        int result = (int) (_interval ^ (_interval >>> 32));
        result = 31 * result + _unit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        long interval = this._interval;
        if (this._unit == TimeUnit.MILLISECONDS) {
            if (interval >= MS_OF_DAY) {
                long days = interval / MS_OF_DAY;
                interval %= MS_OF_DAY;
                buffer.append(days).append(UNIT_DAY);
            }
            if (interval >= MS_OF_HOUR) {
                long hours = interval / MS_OF_HOUR;
                interval %= MS_OF_HOUR;
                buffer.append(hours).append(UNIT_HOUR);
            }
            if (interval >= MS_OF_MINUTE) {
                long minutes = interval / MS_OF_MINUTE;
                interval %= MS_OF_MINUTE;
                buffer.append(minutes).append(UNIT_MINUTE);
            }
            if (interval >= MS_OF_SECOND) {
                long seconds = interval / MS_OF_SECOND;
                interval %= MS_OF_SECOND;
                buffer.append(seconds).append(UNIT_SECOND);
            }
            if (interval > 0) {
                buffer.append(interval).append(UNIT_MS);
            }
        } else if (this._unit == TimeUnit.SECONDS) {
            if (interval >= SECOND_OF_DAY) {
                long days = interval / SECOND_OF_DAY;
                interval %= SECOND_OF_DAY;
                buffer.append(days).append(UNIT_DAY);
            }
            if (interval >= SECOND_OF_HOUR) {
                long hours = interval / SECOND_OF_HOUR;
                interval %= SECOND_OF_HOUR;
                buffer.append(hours).append(UNIT_HOUR);
            }
            if (interval >= SECOND_OF_MINUTE) {
                long minutes = interval / SECOND_OF_MINUTE;
                interval %= SECOND_OF_MINUTE;
                buffer.append(minutes).append(UNIT_MINUTE);
            }
            if (interval > 0) {
                buffer.append(interval).append(UNIT_SECOND);
            }
        } else if (this._unit == TimeUnit.MINUTES) {
            if (interval >= MINUTE_OF_DAY) {
                long days = interval / MINUTE_OF_DAY;
                interval %= MINUTE_OF_DAY;
                buffer.append(days).append(UNIT_DAY);
            }
            if (interval >= MINUTE_OF_HOUR) {
                long hours = interval / MINUTE_OF_HOUR;
                interval %= MINUTE_OF_HOUR;
                buffer.append(hours).append(UNIT_HOUR);
            }
            if (interval > 0) {
                buffer.append(interval).append(UNIT_MINUTE);
            }
        } else if (this._unit == TimeUnit.HOURS) {
            if (interval >= HOUR_OF_DAY) {
                long days = interval / HOUR_OF_DAY;
                interval %= HOUR_OF_DAY;
                buffer.append(days).append(UNIT_DAY);
            }
            if (interval > 0) {
                buffer.append(interval).append(UNIT_HOUR);
            }
        } else if (this._unit == TimeUnit.DAYS) {
            buffer.append(interval).append(UNIT_DAY);
        } else {
            throw new KernelException("Unsupported time unit - {}", this._unit);
        }
        return buffer.toString();
    }
}
